import urllib.request, uuid

boundary = uuid.uuid4().hex
lines = [
    f'--{boundary}'.encode(),
    b'Content-Disposition: form-data; name="file"; filename="test_pop.png"',
    b'Content-Type: image/png\r\n',
    b'\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x01\x00\x00\x00\x01\x08\x06\x00\x00\x00\x1f\x15c4\x00\x00\x00\nIDATx\x9cc\x00\x01\x00\x00\x05\x00\x01\r\n-\xb4\x00\x00\x00\x00IEND\xaeB`\x82',
    f'--{boundary}--\r\n'.encode()
]
body = b'\r\n'.join(lines)

req = urllib.request.Request('http://localhost:8080/api/products/upload-image', data=body, method='POST')
req.add_header('Content-Type', f'multipart/form-data; boundary={boundary}')
with urllib.request.urlopen(req) as resp:
    print('Product image upload result:', resp.read().decode())

req_qr = urllib.request.Request('http://localhost:8080/api/settings/upload-qr', data=body, method='POST')
req_qr.add_header('Content-Type', f'multipart/form-data; boundary={boundary}')
with urllib.request.urlopen(req_qr) as resp:
    print('QR upload result:', resp.read().decode())
