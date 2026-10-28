# -*- coding: utf-8 -*-
import sys
sys.stdout.reconfigure(encoding='utf-8') if hasattr(sys.stdout, 'reconfigure') else None
import urllib.parse
import json
import http.cookiejar
import sys

BASE_URL = "http://localhost:8080"
cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

def request(method, path, body=None, content_type="application/json"):
    url = BASE_URL + path
    data = None
    if body is not None:
        if isinstance(body, (dict, list)):
            data = json.dumps(body).encode("utf-8")
        elif isinstance(body, bytes):
            data = body
        elif isinstance(body, str):
            data = body.encode("utf-8")
    
    req = urllib.request.Request(url, data=data, method=method)
    if content_type:
        req.add_header("Content-Type", content_type)
    req.add_header("User-Agent", "ChillPopsTest/1.0")
    try:
        with opener.open(req) as resp:
            status = resp.status
            content = resp.read()
            return status, resp.headers, content
    except urllib.error.HTTPError as e:
        return e.code, e.headers, e.read()

print("--- STARTING CHILL POPS AUTOMATED VERIFICATION ---")

# 1. Test Static Homepage & Logo
status, headers, content = request("GET", "/")
assert status == 200, f"Failed GET /: {status}"
assert b"Cool Your Day with the Chill Pops Way!" in content, "Tagline missing in homepage"
assert b"Welcome to" in content and b"Chill Pops!" in content, "Welcome heading missing"
print("✓ [TEST 1 PASS] Homepage loaded with tagline and branding")

# 2. Test Get Products (Stick Ice Creams Only)
status, _, content = request("GET", "/api/products")
assert status == 200
products = json.loads(content.decode("utf-8"))
assert len(products) >= 14, f"Expected at least 14 stick ice cream flavours, got {len(products)}"
for p in products:
    price = float(p["price"])
    assert 10.0 <= price <= 50.0, f"Product {p['name']} has invalid price {price}"
    name_lower = p["name"].lower()
    desc_lower = (p["description"] or "").lower()
    # verify no cones, cups, sundaes, shakes, bowls
    for forbidden in ["cone", "cup", "sundae", "shake", "tub", "bowl"]:
        assert forbidden not in name_lower, f"Forbidden non-stick item found: {p['name']}"
print(f"✓ [TEST 2 PASS] Products list verified: {len(products)} stick ice creams strictly priced ₹10–₹50")

# 3. Test Admin Login
login_payload = {"username": "admin", "password": "admin123"}
status, _, content = request("POST", "/api/auth/login", login_payload)
assert status == 200, f"Login failed: {content}"
res = json.loads(content.decode("utf-8"))
assert res["success"] is True and res["user"]["role"] == "ROLE_OWNER"
print("✓ [TEST 3 PASS] Admin login works with role OWNER")

# 4. Test Add New Product with Price Validation (₹10–₹50)
invalid_low_product = {
    "name": "Invalid Cheap Stick",
    "categoryId": 1,
    "price": 5.00, # Below ₹10
    "description": "Too cheap",
    "imagePath": "/images/products/pop_mango.jpg"
}
status, _, content = request("POST", "/api/products", invalid_low_product)
assert status == 400, f"Expected 400 rejection for ₹5 price, got {status}: {content}"
print("✓ [TEST 4 PASS] Low price below ₹10 rejected by backend")

invalid_high_product = {
    "name": "Invalid Costly Stick",
    "categoryId": 1,
    "price": 75.00, # Above ₹50
    "description": "Too expensive",
    "imagePath": "/images/products/pop_mango.jpg"
}
status, _, content = request("POST", "/api/products", invalid_high_product)
assert status == 400, f"Expected 400 rejection for ₹75 price, got {status}: {content}"
print("✓ [TEST 5 PASS] High price above ₹50 rejected by backend")

valid_new_product = {
    "name": "Tropical Guava Chilli Pop",
    "categoryId": 1,
    "price": 25.00,
    "description": "Sweet pink guava popsicle with a pinch of red chilli rock salt.",
    "imagePath": "/images/products/pop_mango.jpg",
    "available": True,
    "stock": 100
}
status, _, content = request("POST", "/api/products", valid_new_product)
assert status == 201, f"Failed creating product: {content}"
created_p = json.loads(content.decode("utf-8"))
new_p_id = created_p["id"]
print(f"✓ [TEST 6 PASS] Admin created new stick flavour '{created_p['name']}' at ₹{created_p['price']}")

# 5. Test Edit Product
edit_payload = {
    "name": "Tropical Guava Chilli Pop (Special)",
    "categoryId": 1,
    "price": 30.00,
    "description": "Updated delicious pink guava stick.",
    "imagePath": "/images/products/pop_mango.jpg",
    "available": True,
    "stock": 90
}
status, _, content = request("PUT", f"/api/products/{new_p_id}", edit_payload)
assert status == 200
edited_p = json.loads(content.decode("utf-8"))
assert edited_p["price"] == 30.00
assert edited_p["name"] == "Tropical Guava Chilli Pop (Special)"
print("✓ [TEST 7 PASS] Admin edited product details and price successfully")

# 6. Test Toggle Availability and Soft Delete
status, _, _ = request("PATCH", f"/api/products/{new_p_id}/toggle-availability")
assert status == 200
status, _, content = request("GET", f"/api/products/{new_p_id}")
p_check = json.loads(content.decode("utf-8"))
assert p_check["available"] is False
print("✓ [TEST 8 PASS] Toggle product availability works")

status, _, _ = request("DELETE", f"/api/products/{new_p_id}")
assert status == 204
print("✓ [TEST 9 PASS] Soft delete product works")

# 7. Test Admin Settings (GST & UPI)
status, _, content = request("GET", "/api/settings")
assert status == 200
settings = json.loads(content.decode("utf-8"))
assert "Chill Pops" in settings["shopName"]
print(f"✓ [TEST 10 PASS] Current Settings loaded: GST Enabled = {settings['gstEnabled']}, Tax Rate = {settings['taxRate']}%, UPI = {settings['upiId']}")

# Test disabling GST first
settings_payload_no_gst = {
    "shopName": "Chill Pops Franchise",
    "upiId": "chillpops.billing@oksbi",
    "upiAccountName": "Chill Pops Express",
    "taxRate": 5.00,
    "gstEnabled": False, # DISABLED
    "shopAddress": "123 Popsicle Street, Chill Zone, Bengaluru 560001",
    "contactNumber": "+91 98765 43210",
    "logoPath": "/images/logo.svg",
    "invoiceFooter": "Cool your day the Chill Pops way!"
}
status, _, content = request("PUT", "/api/settings", settings_payload_no_gst)
assert status == 200
updated_s = json.loads(content.decode("utf-8"))
assert updated_s["gstEnabled"] is False
assert updated_s["upiId"] == "chillpops.billing@oksbi"
print("✓ [TEST 11 PASS] Admin Settings updated with GST DISABLED")

# 8. Test POS Bill Creation when GST is DISABLED
p1 = products[0]
p2 = products[1]
bill_req_no_gst = {
    "idempotencyKey": "TEST-NO-GST-" + str(SystemCurrent := 10001),
    "items": [
        {"productId": p1["id"], "quantity": 2},
        {"productId": p2["id"], "quantity": 1}
    ]
}
status, _, content = request("POST", "/api/billing/create", bill_req_no_gst)
assert status == 201, f"Create bill failed: {content}"
bill_no_gst = json.loads(content.decode("utf-8"))
expected_subtotal = (p1["price"] * 2) + (p2["price"] * 1)
assert float(bill_no_gst["subtotal"]) == float(expected_subtotal)
assert float(bill_no_gst["taxAmount"]) == 0.0, "Tax amount must be 0 when GST is disabled"
assert float(bill_no_gst["grandTotal"]) == float(expected_subtotal), "Grand total must equal subtotal when GST is disabled"
assert bill_no_gst["gstApplied"] is False
print(f"✓ [TEST 12 PASS] Bill created with GST DISABLED: Subtotal = ₹{bill_no_gst['subtotal']}, Tax = ₹{bill_no_gst['taxAmount']}, Grand Total = ₹{bill_no_gst['grandTotal']}")

# 9. Test Confirm Payment for Cash
confirm_cash = {"method": "CASH"}
status, _, content = request("POST", f"/api/billing/{bill_no_gst['id']}/confirm", confirm_cash)
assert status == 200
completed_cash_bill = json.loads(content.decode("utf-8"))
assert completed_cash_bill["status"] == "COMPLETED"
assert completed_cash_bill["paymentDetail"]["method"] == "CASH"
print("✓ [TEST 13 PASS] Cash payment confirmed, bill marked COMPLETED")

# 10. Test Now ENABLING GST in Settings and verify new bill calculates GST!
settings_payload_with_gst = settings_payload_no_gst.copy()
settings_payload_with_gst["gstEnabled"] = True
settings_payload_with_gst["taxRate"] = 5.00
status, _, content = request("PUT", "/api/settings", settings_payload_with_gst)
assert status == 200
s_with_gst = json.loads(content.decode("utf-8"))
assert s_with_gst["gstEnabled"] is True
print("✓ [TEST 14 PASS] Admin Settings updated with GST ENABLED at 5.00%")

bill_req_with_gst = {
    "idempotencyKey": "TEST-WITH-GST-" + str(SystemCurrent + 1),
    "items": [
        {"productId": p1["id"], "quantity": 2}, # ₹10 * 2 = ₹20
        {"productId": p2["id"], "quantity": 1}  # ₹10 * 1 = ₹10 => Subtotal = ₹30.00
    ]
}
status, _, content = request("POST", "/api/billing/create", bill_req_with_gst)
assert status == 201
bill_with_gst = json.loads(content.decode("utf-8"))
assert float(bill_with_gst["subtotal"]) == 30.00
# 5% of 30.00 = 1.50
assert float(bill_with_gst["taxAmount"]) == 1.50, f"Expected tax 1.50, got {bill_with_gst['taxAmount']}"
assert float(bill_with_gst["grandTotal"]) == 31.50, f"Expected grand total 31.50, got {bill_with_gst['grandTotal']}"
assert bill_with_gst["gstApplied"] is True
print(f"✓ [TEST 15 PASS] Bill created with GST ENABLED: Subtotal = ₹{bill_with_gst['subtotal']}, GST (5%) = ₹{bill_with_gst['taxAmount']}, Grand Total = ₹{bill_with_gst['grandTotal']}")

# 11. Verify Historic Invoice Immutability: bill_no_gst must still have 0 GST!
status, _, content = request("GET", f"/api/billing/{bill_no_gst['id']}")
assert status == 200
historic_bill = json.loads(content.decode("utf-8"))
assert float(historic_bill["taxAmount"]) == 0.0
assert historic_bill["gstApplied"] is False
print("✓ [TEST 16 PASS] Historical invoice immutability verified (past invoice retained 0 GST)")

# 12. Test UPI Dynamic QR Code Generation
status, headers, qr_bytes = request("GET", f"/api/billing/qr/{bill_with_gst['id']}", content_type=None)
assert status == 200
assert headers.get("Content-Type") == "image/png"
assert len(qr_bytes) > 500, "QR Code bytes too small"
print(f"✓ [TEST 17 PASS] Dynamic UPI QR code generated successfully ({len(qr_bytes)} bytes PNG)")

# 13. Test UPI Payment Confirmation
confirm_upi = {"method": "UPI", "upiReference": "UPI-REF-99887766"}
status, _, content = request("POST", f"/api/billing/{bill_with_gst['id']}/confirm", confirm_upi)
assert status == 200
completed_upi_bill = json.loads(content.decode("utf-8"))
assert completed_upi_bill["status"] == "COMPLETED"
assert completed_upi_bill["paymentDetail"]["method"] == "UPI"
assert completed_upi_bill["paymentDetail"]["upiReference"] == "UPI-REF-99887766"
print("✓ [TEST 18 PASS] UPI payment confirmed and verified by admin")

# 14. Test Invoice PDF Download
status, headers, pdf_bytes = request("GET", f"/api/billing/{bill_with_gst['id']}/pdf", content_type=None)
assert status == 200
assert "application/pdf" in headers.get("Content-Type", "")
assert len(pdf_bytes) > 1000
assert pdf_bytes.startswith(b"%PDF"), "Response is not a valid PDF"
print(f"✓ [TEST 19 PASS] Customer Invoice PDF generated via OpenPDF ({len(pdf_bytes)} bytes)")

# 15. Test Dashboard Metrics
status, _, content = request("GET", "/api/reports/dashboard")
assert status == 200
dash = json.loads(content.decode("utf-8"))
assert dash["totalOrders"] >= 2
assert float(dash["todaySales"]) >= (30.00 + 31.50)
print(f"✓ [TEST 20 PASS] Dashboard metrics verified: Orders = {dash['totalOrders']}, Today Sales = ₹{dash['todaySales']}, Best Seller = {dash['bestSellingFlavour']}")

# 16. Test Reports Excel Export (.xlsx)
status, headers, excel_bytes = request("GET", "/api/reports/export/excel", content_type=None)
assert status == 200
assert "spreadsheetml" in headers.get("Content-Type", "")
assert len(excel_bytes) > 2000
print(f"✓ [TEST 21 PASS] Sales Report Excel export verified via Apache POI ({len(excel_bytes)} bytes .xlsx)")

# 17. Test Reports PDF Export (.pdf)
status, headers, rep_pdf_bytes = request("GET", "/api/reports/export/pdf", content_type=None)
assert status == 200
assert "application/pdf" in headers.get("Content-Type", "")
assert rep_pdf_bytes.startswith(b"%PDF")
print(f"✓ [TEST 22 PASS] Sales Report PDF export verified ({len(rep_pdf_bytes)} bytes .pdf)")

print("\n🎉 ALL 22 AUTOMATED VERIFICATION CHECKS PASSED PERFECTLY! 🎉")
