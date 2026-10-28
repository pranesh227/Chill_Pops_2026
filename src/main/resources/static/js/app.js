/**
 * CHILL POPS - Ice Cream Franchise POS & Management System
 * Client Application Logic
 */

const app = {
  // State
  currentUser: null,
  products: [],
  categories: [],
  settings: {
    shopName: "Chill Pops",
    upiId: "chillpops@oksbi",
    upiAccountName: "Chill Pops Franchise",
    qrImagePath: null,
    taxRate: 5.0,
    gstEnabled: false,
    shopAddress: "123 Popsicle Street, Chill Zone, Bengaluru, Karnataka 560001",
    contactNumber: "+91 98765 43210",
    invoiceFooter: "Thank you for choosing Chill Pops! Cool your day the Chill Pops way!"
  },
  cart: [],
  selectedCategory: "ALL",
  activeBill: null,
  charts: {
    daily: null,
    category: null
  },

  // Modals
  modals: {},

  // =========================================================================
  // Initialization
  // =========================================================================
  async init() {
    this.initModals();
    await this.checkAuthStatus();
    await this.loadSettings();
    await this.loadCategories();
    await this.loadProducts();
    this.renderMenuPills();
    this.renderPosPills();
    this.renderProducts();
    this.renderPosProducts();
    this.updateCartUI();

    // Default dates for custom report
    const today = new Date().toISOString().split("T")[0];
    const firstDay = new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split("T")[0];
    const fromEl = document.getElementById("report-custom-from");
    const toEl = document.getElementById("report-custom-to");
    if (fromEl) fromEl.value = firstDay;
    if (toEl) toEl.value = today;
  },

  initModals() {
    const loginEl = document.getElementById("modal-login");
    if (loginEl) this.modals.login = new bootstrap.Modal(loginEl);

    const prodEl = document.getElementById("modal-product");
    if (prodEl) this.modals.product = new bootstrap.Modal(prodEl);

    const upiEl = document.getElementById("modal-upi");
    if (upiEl) this.modals.upi = new bootstrap.Modal(upiEl);

    const invEl = document.getElementById("modal-invoice");
    if (invEl) this.modals.invoice = new bootstrap.Modal(invEl);
  },

  // =========================================================================
  // Navigation & View Switching
  // =========================================================================
  switchView(viewName) {
    document.querySelectorAll(".app-view").forEach(el => el.style.display = "none");
    document.querySelectorAll(".top-navbar .nav-pill-btn").forEach(btn => btn.classList.remove("active"));

    if (viewName === "menu") {
      document.getElementById("view-menu").style.display = "block";
      document.getElementById("nav-btn-menu").classList.add("active");
    } else if (viewName === "pos") {
      document.getElementById("view-pos").style.display = "block";
      document.getElementById("nav-btn-pos").classList.add("active");
      this.renderPosProducts();
    } else if (viewName === "admin") {
      if (!this.currentUser || !this.currentUser.authenticated) {
        this.openLoginModal();
        return;
      }
      document.getElementById("view-admin").style.display = "block";
      document.getElementById("nav-btn-admin").classList.add("active");
      this.loadDashboardData();
    }
    window.scrollTo({ top: 0, behavior: "smooth" });
  },

  handleAdminClick() {
    if (!this.currentUser || !this.currentUser.authenticated) {
      this.openLoginModal();
    } else {
      this.switchView("admin");
    }
  },

  switchAdminTab(tabName) {
    document.querySelectorAll(".admin-subtab").forEach(el => el.style.display = "none");
    document.querySelectorAll(".admin-nav-item").forEach(btn => btn.classList.remove("active"));

    const tabEl = document.getElementById("admin-tab-" + tabName);
    const btnEl = document.getElementById("admin-tab-btn-" + tabName);
    if (tabEl) tabEl.style.display = "block";
    if (btnEl) btnEl.classList.add("active");

    if (tabName === "dash") this.loadDashboardData();
    if (tabName === "products") this.loadAdminProducts();
    if (tabName === "tx") this.loadTransactions();
    if (tabName === "reports") this.loadReportPreset("month");
    if (tabName === "settings") this.populateSettingsForm();
  },

  // =========================================================================
  // Authentication
  // =========================================================================
  async checkAuthStatus() {
    try {
      const res = await fetch("/api/auth/status");
      const data = await res.json();
      this.currentUser = data;
      this.updateAuthUI();
    } catch (e) {
      console.error("Auth status error:", e);
    }
  },

  updateAuthUI() {
    const authInfoEl = document.getElementById("auth-info");
    const loginBtnEl = document.getElementById("btn-login-modal");

    if (this.currentUser && this.currentUser.authenticated) {
      if (authInfoEl) authInfoEl.style.setProperty("display", "flex", "important");
      if (loginBtnEl) loginBtnEl.style.display = "none";
    } else {
      if (authInfoEl) authInfoEl.style.setProperty("display", "none", "important");
      if (loginBtnEl) loginBtnEl.style.display = "inline-flex";
    }
  },

  openLoginModal() {
    const errEl = document.getElementById("login-error");
    if (errEl) errEl.style.display = "none";
    this.modals.login.show();
  },

  fillDemoAdmin() {
    document.getElementById("login-username").value = "admin";
    document.getElementById("login-password").value = "admin123";
  },

  async submitLogin() {
    const username = document.getElementById("login-username").value.trim();
    const password = document.getElementById("login-password").value;
    const errEl = document.getElementById("login-error");

    try {
      const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password })
      });
      const data = await res.json();

      if (res.ok && data.success) {
        this.currentUser = data.user;
        this.updateAuthUI();
        this.modals.login.hide();
        this.switchView("admin");
      } else {
        errEl.innerText = data.message || "Invalid credentials";
        errEl.style.display = "block";
      }
    } catch (e) {
      errEl.innerText = "Connection error. Please try again.";
      errEl.style.display = "block";
    }
  },

  async logout() {
    try {
      await fetch("/api/auth/logout", { method: "POST" });
    } catch (e) {}
    this.currentUser = { authenticated: false };
    this.updateAuthUI();
    this.switchView("menu");
  },

  // =========================================================================
  // Settings Management (GST & UPI & Shop Info)
  // =========================================================================
  async loadSettings() {
    try {
      const res = await fetch("/api/settings");
      if (res.ok) {
        this.settings = await res.json();
        this.applySettingsToUI();
      }
    } catch (e) {
      console.error("Load settings error:", e);
    }
  },

  applySettingsToUI() {
    // Update footer info
    const footerEl = document.getElementById("footer-shop-details");
    if (footerEl) {
      footerEl.innerText = `${this.settings.shopAddress} | Phone: ${this.settings.contactNumber}`;
    }

    // Update cart tax display logic
    this.updateCartUI();
  },

  populateSettingsForm() {
    document.getElementById("setting-gst-enabled").checked = !!this.settings.gstEnabled;
    document.getElementById("setting-tax-rate").value = this.settings.taxRate;
    document.getElementById("setting-upi-id").value = this.settings.upiId || "";
    document.getElementById("setting-upi-name").value = this.settings.upiAccountName || "";
    document.getElementById("setting-shop-name").value = this.settings.shopName || "Chill Pops";
    document.getElementById("setting-contact").value = this.settings.contactNumber || "";
    document.getElementById("setting-address").value = this.settings.shopAddress || "";
    document.getElementById("setting-footer").value = this.settings.invoiceFooter || "";

    const previewImg = document.getElementById("setting-qr-preview");
    const previewLbl = document.getElementById("preview-upi-id-lbl");
    if (previewImg) {
      previewImg.src = this.settings.qrImagePath ? this.settings.qrImagePath : "/images/logo.svg";
    }
    if (previewLbl) {
      previewLbl.innerText = this.settings.upiId || "No UPI ID configured";
    }

    this.toggleGstInput();
  },

  toggleGstInput() {
    const isChecked = document.getElementById("setting-gst-enabled").checked;
    const txt = document.getElementById("gst-status-text");
    const rateInput = document.getElementById("setting-tax-rate");
    if (isChecked) {
      txt.innerText = "GST is Currently ENABLED";
      txt.className = "fw-bold text-success";
      rateInput.disabled = false;
    } else {
      txt.innerText = "GST is Currently DISABLED";
      txt.className = "fw-bold text-muted";
      rateInput.disabled = true;
    }
  },

  async saveSettings() {
    const shopName = document.getElementById("setting-shop-name").value.trim();
    const upiId = document.getElementById("setting-upi-id").value.trim();
    const upiAccountName = document.getElementById("setting-upi-name").value.trim();
    const taxRate = parseFloat(document.getElementById("setting-tax-rate").value) || 0;
    const gstEnabled = document.getElementById("setting-gst-enabled").checked;
    const shopAddress = document.getElementById("setting-address").value.trim();
    const contactNumber = document.getElementById("setting-contact").value.trim();
    const invoiceFooter = document.getElementById("setting-footer").value.trim();

    if (!shopName || !upiId) {
      alert("Shop Name and UPI ID are required.");
      return;
    }

    const payload = {
      shopName,
      upiId,
      upiAccountName,
      qrImagePath: this.settings.qrImagePath,
      taxRate,
      gstEnabled,
      shopAddress,
      contactNumber,
      logoPath: this.settings.logoPath || "/images/logo.svg",
      invoiceFooter
    };

    try {
      const res = await fetch("/api/settings", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
      });
      if (res.ok) {
        this.settings = await res.json();
        this.applySettingsToUI();
        alert("✨ Franchise Settings saved successfully!");
      } else {
        const err = await res.json();
        alert("Error saving settings: " + (err.message || "Failed"));
      }
    } catch (e) {
      alert("Network error while saving settings: " + e.message);
    }
  },

  async uploadCustomQr(input) {
    if (!input.files || input.files.length === 0) return;
    const file = input.files[0];
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("/api/settings/upload-qr", {
        method: "POST",
        body: formData
      });
      if (res.ok) {
        const data = await res.json();
        this.settings.qrImagePath = data.url;
        document.getElementById("setting-qr-preview").src = data.url;
        alert("QR code uploaded successfully!");
      } else {
        alert("Failed to upload QR image.");
      }
    } catch (e) {
      alert("Upload error: " + e.message);
    }
  },

  // =========================================================================
  // Product Catalog & Categories
  // =========================================================================
  async loadCategories() {
    try {
      const res = await fetch("/api/categories");
      if (res.ok) {
        this.categories = await res.json();
      }
    } catch (e) {
      console.error("Load categories error:", e);
    }
  },

  async loadProducts() {
    try {
      const res = await fetch("/api/products");
      if (res.ok) {
        this.products = await res.json();
      }
    } catch (e) {
      console.error("Load products error:", e);
    }
  },

  renderMenuPills() {
    const container = document.getElementById("menu-category-pills");
    if (!container) return;

    let html = `<button class="cat-filter-btn ${this.selectedCategory === 'ALL' ? 'active' : ''}" onclick="app.setCategoryFilter('ALL')">All Sticks (${this.products.length})</button>`;
    this.categories.forEach(c => {
      const count = this.products.filter(p => p.categoryId === c.id).length;
      html += `<button class="cat-filter-btn ${this.selectedCategory == c.id ? 'active' : ''}" onclick="app.setCategoryFilter(${c.id})">${c.name} (${count})</button>`;
    });
    container.innerHTML = html;
  },

  renderPosPills() {
    const container = document.getElementById("pos-category-pills");
    if (!container) return;

    let html = `<button class="cat-filter-btn btn-sm ${this.selectedCategory === 'ALL' ? 'active' : ''}" onclick="app.setCategoryFilter('ALL')">All</button>`;
    this.categories.forEach(c => {
      html += `<button class="cat-filter-btn btn-sm ${this.selectedCategory == c.id ? 'active' : ''}" onclick="app.setCategoryFilter(${c.id})">${c.name}</button>`;
    });
    container.innerHTML = html;
  },

  setCategoryFilter(catId) {
    this.selectedCategory = catId;
    this.renderMenuPills();
    this.renderPosPills();
    this.renderProducts();
    this.renderPosProducts();
  },

  filterProducts() {
    this.renderProducts();
  },

  filterPosProducts() {
    this.renderPosProducts();
  },

  getFilteredProducts(searchQuery) {
    return this.products.filter(p => {
      const matchCat = (this.selectedCategory === "ALL" || p.categoryId == this.selectedCategory);
      const q = (searchQuery || "").trim().toLowerCase();
      const matchSearch = !q || p.name.toLowerCase().includes(q) || (p.description && p.description.toLowerCase().includes(q));
      return matchCat && matchSearch;
    });
  },

  renderProducts() {
    const grid = document.getElementById("products-grid");
    if (!grid) return;

    const query = document.getElementById("menu-search-input") ? document.getElementById("menu-search-input").value : "";
    const list = this.getFilteredProducts(query);

    if (list.length === 0) {
      grid.innerHTML = `
        <div class="col-12 text-center py-5">
          <div class="fs-1 text-muted mb-2">🍧</div>
          <h5 class="text-muted">No stick ice creams found matching your search.</h5>
        </div>
      `;
      return;
    }

    grid.innerHTML = list.map(p => `
      <div class="col-xl-3 col-lg-4 col-md-6 col-sm-6">
        <div class="stick-product-card">
          <div class="product-img-wrap">
            <span class="category-tag">${p.categoryName}</span>
            <span class="stick-only-tag"><i class="fa-solid fa-popsicle me-1"></i>STICK</span>
            <img src="${p.imagePath}" alt="${p.name}" class="product-img" 
                 onerror="this.src='/images/products/pop_mango.svg'">
          </div>
          <div class="product-card-body">
            <div>
              <h5 class="product-title">${p.name}</h5>
              <p class="product-desc">${p.description || "Refreshing frozen treat on a stick."}</p>
            </div>
            <div class="product-price-bar">
              <span class="product-price">₹${parseFloat(p.price).toFixed(2)}</span>
              <button class="add-cart-btn" onclick="app.addToCart(${p.id})">
                <i class="fa-solid fa-plus"></i> Add
              </button>
            </div>
          </div>
        </div>
      </div>
    `).join("");
  },

  renderPosProducts() {
    const grid = document.getElementById("pos-products-grid");
    if (!grid) return;

    const query = document.getElementById("pos-search-input") ? document.getElementById("pos-search-input").value : "";
    const list = this.getFilteredProducts(query);

    grid.innerHTML = list.map(p => `
      <div class="col-md-4 col-sm-6 col-6">
        <div class="stick-product-card p-2 text-center h-100" style="cursor:pointer;" onclick="app.addToCart(${p.id})">
          <img src="${p.imagePath}" alt="${p.name}" class="img-fluid mb-2" 
               style="height:80px; object-fit:contain;" onerror="this.src='/images/products/pop_mango.svg'">
          <div class="fw-bold small text-truncate" title="${p.name}">${p.name}</div>
          <div class="text-primary fw-bold">₹${parseFloat(p.price).toFixed(2)}</div>
        </div>
      </div>
    `).join("");
  },

  // =========================================================================
  // Cart & POS Calculations
  // =========================================================================
  addToCart(productId) {
    const p = this.products.find(x => x.id === productId);
    if (!p) return;

    const existing = this.cart.find(it => it.product.id === productId);
    if (existing) {
      existing.quantity += 1;
    } else {
      this.cart.push({ product: p, quantity: 1 });
    }
    this.updateCartUI();

    // Pulse feedback on nav badge
    const countEl = document.getElementById("nav-cart-count");
    if (countEl) {
      countEl.classList.add("animate__animated", "animate__pulse");
      setTimeout(() => countEl.classList.remove("animate__animated", "animate__pulse"), 500);
    }
  },

  changeCartQty(productId, delta) {
    const item = this.cart.find(it => it.product.id === productId);
    if (!item) return;

    item.quantity += delta;
    if (item.quantity <= 0) {
      this.cart = this.cart.filter(it => it.product.id !== productId);
    }
    this.updateCartUI();
  },

  removeFromCart(productId) {
    this.cart = this.cart.filter(it => it.product.id !== productId);
    this.updateCartUI();
  },

  clearCart() {
    this.cart = [];
    this.updateCartUI();
  },

  updateCartUI() {
    const container = document.getElementById("cart-items-container");
    const countEl = document.getElementById("nav-cart-count");
    const subtotalEl = document.getElementById("cart-subtotal");
    const gstLineEl = document.getElementById("cart-gst-line");
    const gstLabelEl = document.getElementById("cart-gst-label");
    const gstAmountEl = document.getElementById("cart-gst-amount");
    const grandTotalEl = document.getElementById("cart-grand-total");

    const totalQty = this.cart.reduce((sum, it) => sum + it.quantity, 0);
    if (countEl) {
      countEl.innerText = totalQty;
      countEl.style.display = totalQty > 0 ? "inline-block" : "none";
    }

    if (!container) return;

    if (this.cart.length === 0) {
      container.innerHTML = `
        <div class="text-center py-4 text-muted">
          <div class="fs-2 mb-2 text-secondary"><i class="fa-solid fa-cart-arrow-down"></i></div>
          <p class="small mb-0 fw-bold">Active order is empty.</p>
          <span class="text-muted" style="font-size:0.75rem;">Click stick ice creams from the catalog to add.</span>
        </div>
      `;
      if (subtotalEl) subtotalEl.innerText = "₹0.00";
      if (gstLineEl) gstLineEl.style.display = "none";
      if (grandTotalEl) grandTotalEl.innerText = "₹0.00";
      return;
    }

    container.innerHTML = this.cart.map(it => `
      <div class="cart-item-row">
        <div class="cart-item-info">
          <div class="cart-item-title">${it.product.name}</div>
          <div class="cart-item-sub">₹${parseFloat(it.product.price).toFixed(2)} x ${it.quantity}</div>
        </div>
        <div class="cart-qty-stepper me-3">
          <button class="stepper-btn" onclick="app.changeCartQty(${it.product.id}, -1)">-</button>
          <span class="small fw-bold px-1">${it.quantity}</span>
          <button class="stepper-btn" onclick="app.changeCartQty(${it.product.id}, 1)">+</button>
        </div>
        <div class="fw-bold text-dark me-2">₹${(it.product.price * it.quantity).toFixed(2)}</div>
        <button class="btn btn-sm btn-link text-danger p-0" onclick="app.removeFromCart(${it.product.id})">
          <i class="fa-solid fa-xmark"></i>
        </button>
      </div>
    `).join("");

    // Financial Calculation using BigDecimal logic in JS
    const subtotal = this.cart.reduce((sum, it) => sum + (it.product.price * it.quantity), 0);
    let taxAmount = 0;
    let grandTotal = subtotal;

    if (this.settings.gstEnabled && this.settings.taxRate > 0) {
      taxAmount = (subtotal * this.settings.taxRate) / 100;
      grandTotal = subtotal + taxAmount;
      if (gstLineEl) gstLineEl.style.display = "flex";
      if (gstLabelEl) gstLabelEl.innerText = `GST (${this.settings.taxRate}%)`;
      if (gstAmountEl) gstAmountEl.innerText = `₹${taxAmount.toFixed(2)}`;
    } else {
      if (gstLineEl) gstLineEl.style.display = "none";
    }

    if (subtotalEl) subtotalEl.innerText = `₹${subtotal.toFixed(2)}`;
    if (grandTotalEl) grandTotalEl.innerText = `₹${grandTotal.toFixed(2)}`;
  },

  // =========================================================================
  // Bill Creation, Payment Flow & Invoicing
  // =========================================================================
  async generateBill() {
    if (this.cart.length === 0) {
      alert("Please add at least one stick ice cream to the cart before generating a bill.");
      return;
    }

    const idempotencyKey = "BILL-" + Date.now() + "-" + Math.random().toString(36).substring(2, 7);
    const items = this.cart.map(it => ({
      productId: it.product.id,
      quantity: it.quantity
    }));

    const btn = document.getElementById("btn-create-bill");
    if (btn) btn.disabled = true;

    try {
      const res = await fetch("/api/billing/create", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ idempotencyKey, items })
      });

      if (!res.ok) {
        const err = await res.json();
        alert("Failed to create bill: " + (err.message || "Error"));
        return;
      }

      this.activeBill = await res.json();
      const method = document.querySelector('input[name="paymentMethod"]:checked').value;

      if (method === "UPI") {
        this.openUpiModal(this.activeBill);
      } else {
        // Direct cash payment prompt
        if (confirm(`Confirm CASH payment receipt of ₹${parseFloat(this.activeBill.grandTotal).toFixed(2)} for ${this.activeBill.invoiceNumber}?`)) {
          await this.confirmPayment("CASH");
        }
      }
    } catch (e) {
      alert("Network error: " + e.message);
    } finally {
      if (btn) btn.disabled = false;
    }
  },

  openUpiModal(bill) {
    document.getElementById("upi-modal-inv").innerText = bill.invoiceNumber;
    document.getElementById("upi-modal-amount").innerText = `₹${parseFloat(bill.grandTotal).toFixed(2)}`;
    document.getElementById("upi-modal-vpa").innerText = this.settings.upiId;

    // Use custom uploaded QR if set, otherwise fetch dynamic NPCI QR
    const qrImg = document.getElementById("upi-modal-qr-img");
    if (this.settings.qrImagePath) {
      qrImg.src = this.settings.qrImagePath;
    } else {
      qrImg.src = `/api/billing/qr/${bill.id}?t=${Date.now()}`;
    }

    this.modals.upi.show();
  },

  closeUpiModal() {
    this.modals.upi.hide();
  },

  copyUpiId() {
    navigator.clipboard.writeText(this.settings.upiId).then(() => {
      alert("UPI ID copied: " + this.settings.upiId);
    });
  },

  async confirmPayment(method) {
    if (!this.activeBill) return;

    try {
      const res = await fetch(`/api/billing/${this.activeBill.id}/confirm`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ method, upiReference: "REF-" + Date.now() })
      });

      if (res.ok) {
        this.activeBill = await res.json();
        if (this.modals.upi) this.modals.upi.hide();
        this.clearCart();
        this.showInvoiceModal(this.activeBill);
      } else {
        const err = await res.json();
        alert("Payment confirmation failed: " + (err.message || "Error"));
      }
    } catch (e) {
      alert("Payment confirmation error: " + e.message);
    }
  },

  async cancelActiveBill() {
    if (!this.activeBill) return;
    if (!confirm("Are you sure you want to cancel this pending bill?")) return;

    try {
      const res = await fetch(`/api/billing/${this.activeBill.id}/cancel`, { method: "POST" });
      if (res.ok) {
        this.modals.upi.hide();
        alert("Bill " + this.activeBill.invoiceNumber + " has been cancelled.");
        this.activeBill = null;
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  },

  showInvoiceModal(bill) {
    const container = document.getElementById("invoice-receipt-container");
    if (!container) return;

    const formattedDate = new Date(bill.createdAt).toLocaleString("en-IN", { timeZone: "Asia/Kolkata" });
    const paymentMethod = bill.paymentDetail ? bill.paymentDetail.method : "PENDING";

    let taxRowHtml = "";
    if (bill.gstApplied && bill.taxAmount > 0) {
      taxRowHtml = `
        <tr>
          <td colspan="3" class="text-end">GST (${bill.taxRate}%):</td>
          <td class="text-end fw-bold">₹${parseFloat(bill.taxAmount).toFixed(2)}</td>
        </tr>
      `;
    }

    container.innerHTML = `
      <div class="text-center mb-3">
        <div class="invoice-brand-title">CHILL POPS</div>
        <div class="small fw-bold text-muted">"Cool Your Day with the Chill Pops Way!"</div>
        <div class="small text-muted">${this.settings.shopAddress}</div>
        <div class="small text-muted">Phone: ${this.settings.contactNumber}</div>
      </div>
      <div class="border-top border-bottom py-2 my-2 small">
        <div class="d-flex justify-content-between">
          <span><strong>INVOICE:</strong> ${bill.invoiceNumber}</span>
          <span><strong>STATUS:</strong> ${bill.status}</span>
        </div>
        <div class="d-flex justify-content-between">
          <span><strong>DATE:</strong> ${formattedDate}</span>
          <span><strong>PAY:</strong> ${paymentMethod}</span>
        </div>
      </div>
      <table class="invoice-table">
        <thead>
          <tr>
            <th>Flavour</th>
            <th class="text-center">Qty</th>
            <th class="text-end">Rate</th>
            <th class="text-end">Total</th>
          </tr>
        </thead>
        <tbody>
          ${bill.items.map(it => `
            <tr>
              <td>${it.productName}</td>
              <td class="text-center">${it.quantity}</td>
              <td class="text-end">₹${parseFloat(it.unitPrice).toFixed(2)}</td>
              <td class="text-end">₹${parseFloat(it.lineTotal).toFixed(2)}</td>
            </tr>
          `).join("")}
        </tbody>
        <tfoot>
          <tr>
            <td colspan="3" class="text-end pt-2">Subtotal:</td>
            <td class="text-end pt-2 fw-bold">₹${parseFloat(bill.subtotal).toFixed(2)}</td>
          </tr>
          ${taxRowHtml}
          <tr>
            <td colspan="3" class="text-end fs-6 fw-bold">Grand Total:</td>
            <td class="text-end fs-6 fw-bold text-danger">₹${parseFloat(bill.grandTotal).toFixed(2)}</td>
          </tr>
        </tfoot>
      </table>
      <div class="text-center mt-3 pt-2 border-top small text-muted">
        ${this.settings.invoiceFooter}
      </div>
    `;

    document.getElementById("btn-download-pdf").onclick = () => {
      window.open(`/api/billing/${bill.id}/pdf`, "_blank");
    };

    this.modals.invoice.show();
  },

  downloadActivePdf() {
    if (this.activeBill) {
      window.open(`/api/billing/${this.activeBill.id}/pdf`, "_blank");
    }
  },

  // =========================================================================
  // Admin Management - Products CRUD (Stick Ice Creams Only)
  // =========================================================================
  async loadAdminProducts() {
    try {
      const res = await fetch("/api/products?includeUnavailable=true");
      if (res.ok) {
        const list = await res.json();
        const tbody = document.getElementById("admin-products-table");
        if (!tbody) return;

        tbody.innerHTML = list.map(p => `
          <tr>
            <td>
              <img src="${p.imagePath}" alt="${p.name}" class="rounded" 
                   style="width:48px; height:48px; object-fit:contain;" onerror="this.src='/images/products/pop_mango.svg'">
            </td>
            <td>
              <div class="fw-bold">${p.name}</div>
              <div class="text-muted small">${p.description || ""}</div>
            </td>
            <td><span class="badge bg-light text-dark border">${p.categoryName}</span></td>
            <td><strong class="text-primary">₹${parseFloat(p.price).toFixed(2)}</strong></td>
            <td>
              <button class="btn btn-sm ${p.available ? 'btn-success' : 'btn-secondary'} rounded-pill py-0 px-2 fw-bold" 
                      onclick="app.toggleProductAvailability(${p.id})">
                ${p.available ? '<i class="fa-solid fa-check me-1"></i>Active' : '<i class="fa-solid fa-ban me-1"></i>Inactive'}
              </button>
            </td>
            <td>
              <button class="btn btn-sm btn-outline-primary rounded-pill me-1" onclick="app.editProduct(${p.id})">
                <i class="fa-solid fa-pen"></i> Edit
              </button>
              <button class="btn btn-sm btn-outline-danger rounded-pill" onclick="app.deleteProduct(${p.id}, '${p.name}')">
                <i class="fa-solid fa-trash"></i>
              </button>
            </td>
          </tr>
        `).join("");
      }
    } catch (e) {
      console.error("Admin products error:", e);
    }
  },

  openProductModal(p = null) {
    document.getElementById("product-modal-title").innerText = p ? "Edit Stick Ice Cream" : "Add Stick Ice Cream";
    document.getElementById("prod-id").value = p ? p.id : "";
    document.getElementById("prod-name").value = p ? p.name : "";
    document.getElementById("prod-price").value = p ? p.price : "20.00";
    document.getElementById("prod-desc").value = p ? (p.description || "") : "";
    document.getElementById("prod-image-path").value = p ? p.imagePath : "/images/products/pop_mango.jpg";
    document.getElementById("prod-image-preview").src = p ? p.imagePath : "/images/products/pop_mango.jpg";
    document.getElementById("prod-available").checked = p ? p.available : true;

    // Populate category dropdown
    const catSelect = document.getElementById("prod-category");
    catSelect.innerHTML = this.categories.map(c => `
      <option value="${c.id}" ${p && p.categoryId === c.id ? "selected" : ""}>${c.name}</option>
    `).join("");

    this.modals.product.show();
  },

  editProduct(id) {
    const p = this.products.find(x => x.id === id);
    if (p) this.openProductModal(p);
  },

  async uploadProductImage(input) {
    if (!input.files || input.files.length === 0) return;
    const file = input.files[0];
    const formData = new FormData();
    formData.append("file", file);

    try {
      const res = await fetch("/api/products/upload-image", { method: "POST", body: formData });
      if (res.ok) {
        const data = await res.json();
        document.getElementById("prod-image-path").value = data.url;
        document.getElementById("prod-image-preview").src = data.url;
      } else {
        alert("Failed to upload image.");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  },

  async saveProduct() {
    const id = document.getElementById("prod-id").value;
    const name = document.getElementById("prod-name").value.trim();
    const categoryId = parseInt(document.getElementById("prod-category").value);
    const price = parseFloat(document.getElementById("prod-price").value);
    const description = document.getElementById("prod-desc").value.trim();
    const imagePath = document.getElementById("prod-image-path").value;
    const available = document.getElementById("prod-available").checked;

    // Validate ₹10 to ₹50 price rule
    if (isNaN(price) || price < 10.0 || price > 50.0) {
      alert("Strict Franchise Rule: Stick ice creams must be priced between ₹10.00 and ₹50.00.");
      return;
    }

    const payload = {
      name,
      categoryId,
      price,
      description,
      imagePath,
      available,
      stock: 100
    };

    try {
      let res;
      if (id) {
        res = await fetch(`/api/products/${id}`, {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });
      } else {
        res = await fetch("/api/products", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });
      }

      if (res.ok) {
        this.modals.product.hide();
        await this.loadProducts();
        this.renderProducts();
        this.renderPosProducts();
        this.loadAdminProducts();
        alert("Stick ice cream saved successfully!");
      } else {
        const err = await res.json();
        alert("Failed to save: " + (err.message || "Error"));
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  },

  async toggleProductAvailability(id) {
    try {
      const res = await fetch(`/api/products/${id}/toggle-availability`, { method: "PATCH" });
      if (res.ok) {
        await this.loadProducts();
        this.renderProducts();
        this.renderPosProducts();
        this.loadAdminProducts();
      }
    } catch (e) {
      console.error(e);
    }
  },

  async deleteProduct(id, name) {
    if (!confirm(`Are you sure you want to delete "${name}"?`)) return;

    try {
      const res = await fetch(`/api/products/${id}`, { method: "DELETE" });
      if (res.ok) {
        await this.loadProducts();
        this.renderProducts();
        this.renderPosProducts();
        this.loadAdminProducts();
      }
    } catch (e) {
      alert("Delete failed: " + e.message);
    }
  },

  // =========================================================================
  // Admin Management - Dashboard & Analytics
  // =========================================================================
  async loadDashboardData() {
    try {
      const res = await fetch("/api/reports/dashboard");
      if (res.ok) {
        const data = await res.json();
        document.getElementById("metric-products").innerText = data.totalProducts;
        document.getElementById("metric-orders").innerText = data.totalOrders;
        document.getElementById("metric-today").innerText = `₹${parseFloat(data.todaySales).toFixed(2)}`;
        document.getElementById("metric-month").innerText = `₹${parseFloat(data.monthlyRevenue).toFixed(2)}`;
        document.getElementById("metric-quantity").innerText = data.totalQuantitySold;
        document.getElementById("metric-bestseller").innerText = data.bestSellingFlavour || "—";

        // Populate recent transactions
        const tbody = document.getElementById("dashboard-recent-table");
        if (tbody && data.recentTransactions) {
          tbody.innerHTML = data.recentTransactions.map(t => `
            <tr>
              <td><strong>${t.invoiceNumber}</strong></td>
              <td class="small text-muted">${new Date(t.createdAt).toLocaleString("en-IN", { timeZone: "Asia/Kolkata" })}</td>
              <td class="small">${t.items.map(it => `${it.productName} (${it.quantity})`).join(", ")}</td>
              <td><strong class="text-primary">₹${parseFloat(t.grandTotal).toFixed(2)}</strong></td>
              <td><span class="badge bg-light text-dark">${t.paymentDetail ? t.paymentDetail.method : 'N/A'}</span></td>
              <td>
                <span class="${t.status === 'COMPLETED' ? 'badge-status-completed' : (t.status === 'PENDING' ? 'badge-status-pending' : 'badge-status-cancelled')}">
                  ${t.status}
                </span>
              </td>
              <td>
                <button class="btn btn-sm btn-outline-primary rounded-pill py-0 px-2" onclick="app.viewPastInvoice(${t.id})">
                  View
                </button>
              </td>
            </tr>
          `).join("");
        }
      }
    } catch (e) {
      console.error("Dashboard error:", e);
    }
  },

  // =========================================================================
  // Admin Management - Transactions History
  // =========================================================================
  async loadTransactions() {
    const status = document.getElementById("tx-filter-status").value;
    const search = document.getElementById("tx-filter-search").value.trim();
    const from = document.getElementById("tx-filter-from").value;
    const to = document.getElementById("tx-filter-to").value;

    let url = `/api/transactions?`;
    if (status) url += `status=${encodeURIComponent(status)}&`;
    if (search) url += `search=${encodeURIComponent(search)}&`;
    if (from) url += `from=${encodeURIComponent(from)}&`;
    if (to) url += `to=${encodeURIComponent(to)}&`;

    try {
      const res = await fetch(url);
      if (res.ok) {
        const list = await res.json();
        const tbody = document.getElementById("admin-tx-table");
        if (!tbody) return;

        if (list.length === 0) {
          tbody.innerHTML = `<tr><td colspan="9" class="text-center py-4 text-muted">No transactions found matching filters.</td></tr>`;
          return;
        }

        tbody.innerHTML = list.map(t => `
          <tr>
            <td><strong>${t.invoiceNumber}</strong></td>
            <td class="small">${new Date(t.createdAt).toLocaleString("en-IN", { timeZone: "Asia/Kolkata" })}</td>
            <td class="small text-muted">${t.completedAt ? new Date(t.completedAt).toLocaleString("en-IN", { timeZone: "Asia/Kolkata" }) : "—"}</td>
            <td>₹${parseFloat(t.subtotal).toFixed(2)}</td>
            <td>₹${parseFloat(t.taxAmount).toFixed(2)}</td>
            <td><strong class="text-primary">₹${parseFloat(t.grandTotal).toFixed(2)}</strong></td>
            <td>
              <span class="${t.status === 'COMPLETED' ? 'badge-status-completed' : (t.status === 'PENDING' ? 'badge-status-pending' : 'badge-status-cancelled')}">
                ${t.status}
              </span>
            </td>
            <td class="small">${t.paymentDetail ? t.paymentDetail.method : '—'}</td>
            <td>
              <div class="btn-group btn-group-sm">
                <button class="btn btn-outline-primary" onclick="app.viewPastInvoice(${t.id})" title="View Receipt">
                  <i class="fa-solid fa-eye"></i>
                </button>
                <button class="btn btn-outline-danger" onclick="window.open('/api/billing/${t.id}/pdf', '_blank')" title="PDF">
                  <i class="fa-solid fa-file-pdf"></i>
                </button>
                ${t.status === 'PENDING' ? `
                  <button class="btn btn-outline-success" onclick="app.verifyPendingTx(${t.id})" title="Confirm Payment">
                    <i class="fa-solid fa-check"></i>
                  </button>
                ` : ''}
              </div>
            </td>
          </tr>
        `).join("");
      }
    } catch (e) {
      console.error("Transactions error:", e);
    }
  },

  async viewPastInvoice(id) {
    try {
      const res = await fetch(`/api/billing/${id}`);
      if (res.ok) {
        const bill = await res.json();
        this.showInvoiceModal(bill);
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  },

  async verifyPendingTx(id) {
    const method = prompt("Enter payment method to verify (CASH or UPI):", "UPI");
    if (!method) return;

    try {
      const res = await fetch(`/api/billing/${id}/confirm`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ method: method.toUpperCase(), upiReference: "MANUAL-" + Date.now() })
      });
      if (res.ok) {
        alert("Transaction confirmed as COMPLETED!");
        this.loadTransactions();
      } else {
        const err = await res.json();
        alert("Failed: " + err.message);
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  },

  // =========================================================================
  // Admin Management - Sales Reports & Charts
  // =========================================================================
  async loadReportPreset(preset) {
    let url = "/api/reports/today";
    if (preset === "yesterday") url = "/api/reports/yesterday";
    if (preset === "month") url = "/api/reports/month";

    try {
      const res = await fetch(url);
      if (res.ok) {
        const report = await res.json();
        this.renderReport(report);
      }
    } catch (e) {
      console.error("Report error:", e);
    }
  },

  async loadCustomReport() {
    const from = document.getElementById("report-custom-from").value;
    const to = document.getElementById("report-custom-to").value;
    if (!from || !to) {
      alert("Please select both start and end dates.");
      return;
    }

    try {
      const res = await fetch(`/api/reports/range?from=${from}&to=${to}`);
      if (res.ok) {
        const report = await res.json();
        this.renderReport(report);
      }
    } catch (e) {
      console.error("Custom report error:", e);
    }
  },

  renderReport(report) {
    document.getElementById("rep-orders").innerText = report.transactions;
    document.getElementById("rep-items").innerText = report.itemsSold;
    document.getElementById("rep-revenue").innerText = `₹${parseFloat(report.revenue).toFixed(2)}`;
    document.getElementById("rep-bestseller").innerText = report.bestSeller || "—";

    this.renderDailySalesChart(report.daily);
    this.renderCategoryChart(report.byCategory);
  },

  renderDailySalesChart(dailyMap) {
    const ctx = document.getElementById("chart-daily-sales");
    if (!ctx) return;

    const labels = Object.keys(dailyMap);
    const dataValues = Object.values(dailyMap).map(v => parseFloat(v));

    if (this.charts.daily) {
      this.charts.daily.destroy();
    }

    this.charts.daily = new Chart(ctx, {
      type: "bar",
      data: {
        labels: labels,
        datasets: [{
          label: "Daily Revenue (₹)",
          data: dataValues,
          backgroundColor: "#0F172A",
          hoverBackgroundColor: "#E11D48",
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { display: false }
        },
        scales: {
          x: {
            grid: { display: false }
          },
          y: {
            beginAtZero: true,
            grid: { color: "#F1F5F9" },
            ticks: {
              callback: val => "₹" + val
            }
          }
        }
      }
    });
  },

  renderCategoryChart(catMap) {
    const ctx = document.getElementById("chart-category-sales");
    if (!ctx) return;

    const labels = Object.keys(catMap);
    const dataValues = Object.values(catMap).map(v => parseFloat(v));

    if (this.charts.category) {
      this.charts.category.destroy();
    }

    this.charts.category = new Chart(ctx, {
      type: "doughnut",
      data: {
        labels: labels.length > 0 ? labels : ["No Sales"],
        datasets: [{
          data: dataValues.length > 0 ? dataValues : [1],
          backgroundColor: ["#E11D48", "#0284C7", "#059669", "#D97706", "#7C3AED"],
          borderWidth: 2,
          borderColor: "#FFFFFF"
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        cutout: "70%",
        plugins: {
          legend: { position: "bottom", labels: { boxWidth: 12, font: { size: 11 } } }
        }
      }
    });
  },

  exportReport(type) {
    const from = document.getElementById("report-custom-from").value;
    const to = document.getElementById("report-custom-to").value;
    let url = `/api/reports/export/${type}`;
    if (from && to) {
      url += `?from=${from}&to=${to}`;
    }
    window.open(url, "_blank");
  }
};

// Start application when DOM is loaded
document.addEventListener("DOMContentLoaded", () => {
  app.init();
});
