import os
import urllib.request

output_dir = r"E:\Chill_pops\src\main\resources\static\images\products"
os.makedirs(output_dir, exist_ok=True)

photos = {
    "cup_vanilla.jpg": "https://images.unsplash.com/photo-1570197788417-0e82375c9371?w=500&auto=format&fit=crop&q=80",
    "cup_strawberry.jpg": "https://images.unsplash.com/photo-1567206563064-6f60f40a2b57?w=500&auto=format&fit=crop&q=80",
    "cup_butterscotch.jpg": "https://images.unsplash.com/photo-1580915411954-282cb1b0d780?w=500&auto=format&fit=crop&q=80",
    "cup_caramel.jpg": "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&auto=format&fit=crop&q=80",
    
    "pop_mango.jpg": "https://images.unsplash.com/photo-1505394033641-40c6ad1178d7?w=500&auto=format&fit=crop&q=80",
    "pop_orange.jpg": "https://images.unsplash.com/photo-1576506295286-5cda18df43e7?w=500&auto=format&fit=crop&q=80",
    "pop_kalakhatta.jpg": "https://images.unsplash.com/photo-1488900128323-21503983a07e?w=500&auto=format&fit=crop&q=80",
    "pop_watermelon.jpg": "https://images.unsplash.com/photo-1506084868230-bb9d95c24759?w=500&auto=format&fit=crop&q=80",
    
    "choc_belgian.jpg": "https://images.unsplash.com/photo-1579954115545-a95591f28bfc?w=500&auto=format&fit=crop&q=80",
    "choc_brownie.jpg": "https://images.unsplash.com/photo-1629385739335-c335e9cb4e31?w=500&auto=format&fit=crop&q=80",
    "choc_mint.jpg": "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=500&auto=format&fit=crop&q=80",
    "choc_hazelnut.jpg": "https://images.unsplash.com/photo-1587314168485-3236d6710814?w=500&auto=format&fit=crop&q=80",
    
    "fruit_mango.jpg": "https://images.unsplash.com/photo-1590080875515-8a3a8dc5735e?w=500&auto=format&fit=crop&q=80",
    "fruit_sitaphal.jpg": "https://images.unsplash.com/photo-1501443762994-82bd5dace89a?w=500&auto=format&fit=crop&q=80",
    "fruit_coconut.jpg": "https://images.unsplash.com/photo-1549395156-e0c1fe6fc7a5?w=500&auto=format&fit=crop&q=80",
    "fruit_blueberry.jpg": "https://images.unsplash.com/photo-1497034825429-c343d7c6a68f?w=500&auto=format&fit=crop&q=80",
    
    "sundae_dbc.jpg": "https://images.unsplash.com/photo-1551024709-8f23befc6f87?w=500&auto=format&fit=crop&q=80",
    "sundae_banana.jpg": "https://images.unsplash.com/photo-1568254183919-78a4f43a2877?w=500&auto=format&fit=crop&q=80",
    "sundae_berry.jpg": "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500&auto=format&fit=crop&q=80",
    "sundae_caramel.jpg": "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=500&auto=format&fit=crop&q=80",
    
    "shake_oreo.jpg": "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=500&auto=format&fit=crop&q=80",
    "shake_kitkat.jpg": "https://images.unsplash.com/photo-1579954115545-a95591f28bfc?w=500&auto=format&fit=crop&q=80",
    "shake_belgian.jpg": "https://images.unsplash.com/photo-1541658016709-82535e94bc69?w=500&auto=format&fit=crop&q=80",
    "shake_strawberry.jpg": "https://images.unsplash.com/photo-1579954115563-e72bf1381629?w=500&auto=format&fit=crop&q=80",
    
    "special_rajbhog.jpg": "https://images.unsplash.com/photo-1582293041079-7814c2f12063?w=500&auto=format&fit=crop&q=80",
    "special_kesar.jpg": "https://images.unsplash.com/photo-1516559828984-fb3b99548b21?w=500&auto=format&fit=crop&q=80",
    "special_cottoncandy.jpg": "https://images.unsplash.com/photo-1505394033641-40c6ad1178d7?w=500&auto=format&fit=crop&q=80",
    "special_bubblegum.jpg": "https://images.unsplash.com/photo-1517256064527-09c73fc73e38?w=500&auto=format&fit=crop&q=80"
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

print("All photos processed.")
