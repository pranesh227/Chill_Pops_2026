import os
import urllib.request

output_dir = r"E:\Chill_pops\src\main\resources\static\images\products"
os.makedirs(output_dir, exist_ok=True)

photos = {
    "pop_mango.jpg": "https://images.unsplash.com/photo-1505394033641-40c6ad1178d7?w=500&auto=format&fit=crop&q=80",
    "pop_orange.jpg": "https://images.unsplash.com/photo-1576506295286-5cda18df43e7?w=500&auto=format&fit=crop&q=80",
    "pop_strawberry.jpg": "https://images.unsplash.com/photo-1488900128323-21503983a07e?w=500&auto=format&fit=crop&q=80",
    "pop_watermelon.jpg": "https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=500&auto=format&fit=crop&q=80",
    "pop_chocolate.jpg": "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=500&auto=format&fit=crop&q=80",
    "pop_belgian_choc.jpg": "https://images.unsplash.com/photo-1579954115545-a95591f28bfc?w=500&auto=format&fit=crop&q=80",
    "pop_blueberry.jpg": "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=500&auto=format&fit=crop&q=80",
    "pop_mixed_berry.jpg": "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500&auto=format&fit=crop&q=80",
    "pop_butterscotch.jpg": "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&auto=format&fit=crop&q=80",
    "pop_rosemilk.jpg": "https://images.unsplash.com/photo-1567206563064-6f60f40a2b57?w=500&auto=format&fit=crop&q=80",
    "pop_kesar_badam.jpg": "https://images.unsplash.com/photo-1582293041079-7814c2f12063?w=500&auto=format&fit=crop&q=80",
    "pop_malai_kulfi.jpg": "https://images.unsplash.com/photo-1516559828984-fb3b99548b21?w=500&auto=format&fit=crop&q=80",
    "pop_pineapple.jpg": "https://images.unsplash.com/photo-1549395156-e0c1fe6fc7a5?w=500&auto=format&fit=crop&q=80",
    "pop_blackcurrant.jpg": "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=500&auto=format&fit=crop&q=80",
    "pop_mango_cream.jpg": "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=500&auto=format&fit=crop&q=80",
    "pop_double_choc.jpg": "https://images.unsplash.com/photo-1629385739335-c335e9cb4e31?w=500&auto=format&fit=crop&q=80",
    "pop_roasted_almond.jpg": "https://images.unsplash.com/photo-1541658016709-82535e94bc69?w=500&auto=format&fit=crop&q=80",
    "pop_kalakhatta.jpg": "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=500&auto=format&fit=crop&q=80"
}

headers = {'User-Agent': 'Mozilla/5.0'}

for fname, url in photos.items():
    dest = os.path.join(output_dir, fname)
    if not os.path.exists(dest) or os.path.getsize(dest) == 0:
        try:
            req = urllib.request.Request(url, headers=headers)
            with urllib.request.urlopen(req, timeout=10) as resp, open(dest, 'wb') as out_file:
                out_file.write(resp.read())
            print(f"Downloaded: {fname} ({os.path.getsize(dest)} bytes)")
        except Exception as e:
            print(f"Failed {fname}: {e}")

print("All stick ice cream photos downloaded.")
