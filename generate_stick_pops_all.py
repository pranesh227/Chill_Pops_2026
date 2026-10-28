import os

products_dir = r"E:\Chill_pops\src\main\resources\static\images\products"
os.makedirs(products_dir, exist_ok=True)
images_dir = r"E:\Chill_pops\src\main\resources\static\images"
os.makedirs(images_dir, exist_ok=True)

logo_svg = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 280 80" width="280" height="80">
  <defs>
    <linearGradient id="brandGrad" x1="0%" y1="0%" x2="100%" y2="100%">
      <stop offset="0%" stop-color="#FF6584"/>
      <stop offset="100%" stop-color="#FF8DA1"/>
    </linearGradient>
    <linearGradient id="popStick" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#FDE68A"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
    <filter id="softShadow" x="-10%" y="-10%" width="130%" height="130%">
      <feDropShadow dx="2" dy="4" stdDeviation="3" flood-opacity="0.15" flood-color="#FF6584"/>
    </filter>
  </defs>
  <!-- Stick Icon -->
  <g transform="translate(15, 10)" filter="url(#softShadow)">
    <rect x="16" y="38" width="10" height="22" rx="5" fill="url(#popStick)"/>
    <path d="M6,40 L36,40 C38,40 40,38 40,34 L40,18 C40,7 31,0 21,0 C11,0 2,7 2,18 L2,34 C2,38 4,40 6,40 Z" fill="url(#brandGrad)"/>
    <path d="M10,12 C14,8 20,8 26,14" stroke="#FFFFFF" stroke-width="3" stroke-linecap="round" fill="none" opacity="0.8"/>
    <circle cx="30" cy="20" r="3.5" fill="#FFFFFF" opacity="0.9"/>
  </g>
  <!-- Text Brand -->
  <text x="68" y="42" font-family="'Fredoka', 'Nunito', 'Segoe UI', sans-serif" font-weight="800" font-size="32" fill="#FF477E" letter-spacing="-0.5">CHILL<tspan fill="#0284C7"> POPS</tspan></text>
  <text x="70" y="60" font-family="'Nunito', sans-serif" font-weight="700" font-size="11" fill="#64748B" letter-spacing="2.5">STICK ICE CREAMS &amp; POS</text>
</svg>"""

with open(os.path.join(images_dir, "logo.svg"), "w", encoding="utf-8") as f:
    f.write(logo_svg)

def make_stick_pop(filename, title, c1, c2, c3, drip, stick_len=55, sprinkles=False, choco_dip=False):
    sprinkle_elements = ""
    if sprinkles:
        sprinkle_elements = """
        <rect x="75" y="50" width="6" height="3" rx="1.5" fill="#FDE047" transform="rotate(25 75 50)"/>
        <rect x="115" y="65" width="6" height="3" rx="1.5" fill="#60A5FA" transform="rotate(-35 115 65)"/>
        <rect x="85" y="90" width="6" height="3" rx="1.5" fill="#F43F5E" transform="rotate(45 85 90)"/>
        <rect x="110" y="110" width="6" height="3" rx="1.5" fill="#34D399" transform="rotate(-15 110 110)"/>
        <rect x="92" y="75" width="6" height="3" rx="1.5" fill="#A855F7" transform="rotate(60 92 75)"/>
        """
    choco_elements = ""
    if choco_dip:
        choco_elements = """
        <path d="M62,65 Q80,75 100,68 Q120,72 138,62 L138,28 C138,15 125,5 100,5 C75,5 62,15 62,28 Z" fill="#3E2723" opacity="0.95"/>
        <circle cx="115" cy="42" r="2.5" fill="#D7CCC8"/>
        <circle cx="85" cy="35" r="2" fill="#D7CCC8"/>
        <circle cx="98" cy="52" r="2" fill="#D7CCC8"/>
        """
    svg = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 230" width="200" height="230">
  <defs>
    <linearGradient id="popGrad_{filename[:6]}" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="{c1}"/>
      <stop offset="50%" stop-color="{c2}"/>
      <stop offset="100%" stop-color="{c3}"/>
    </linearGradient>
    <linearGradient id="woodStick" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0%" stop-color="#FEF08A"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
    <filter id="shadow" x="-15%" y="-15%" width="130%" height="130%">
      <feDropShadow dx="0" dy="6" stdDeviation="6" flood-opacity="0.15" flood-color="#0F172A"/>
    </filter>
  </defs>
  <!-- Background Aura -->
  <circle cx="100" cy="110" r="90" fill="#FFF9F5"/>
  <!-- Stick -->
  <rect x="90" y="145" width="20" height="{stick_len}" rx="10" fill="url(#woodStick)"/>
  <line x1="98" y1="155" x2="98" y2="190" stroke="#B45309" stroke-width="1.5" opacity="0.4" stroke-linecap="round"/>
  <!-- Popsicle Body -->
  <g filter="url(#shadow)">
    <rect x="62" y="25" width="76" height="125" rx="38" fill="url(#popGrad_{filename[:6]})"/>
    <!-- Glossy Highlight -->
    <path d="M76,45 L76,120" stroke="#FFFFFF" stroke-width="5" stroke-linecap="round" opacity="0.4"/>
    <circle cx="116" cy="45" r="6" fill="#FFFFFF" opacity="0.6"/>
    {choco_elements}
    <!-- Delicious Drip -->
    <path d="M124,115 C124,128 136,128 136,115 C136,110 124,110 124,115 Z" fill="{drip}" opacity="0.9"/>
    {sprinkle_elements}
  </g>
  <!-- Brand text badge -->
  <rect x="45" y="206" width="110" height="20" rx="10" fill="#FFE4E6" opacity="0.85"/>
  <text x="100" y="220" text-anchor="middle" font-family="'Fredoka', 'Nunito', sans-serif" font-size="10.5" font-weight="800" fill="#E11D48" letter-spacing="0.5">STICK ICE CREAM</text>
</svg>"""
    with open(os.path.join(products_dir, filename), "w", encoding="utf-8") as f:
        f.write(svg)

items = [
    ("pop_mango.svg", "Classic Mango Pop", "#FDE047", "#FACC15", "#EA580C", "#C2410C", 55, False, False),
    ("pop_orange.svg", "Orange Splash Pop", "#FED7AA", "#FB923C", "#EA580C", "#C2410C", 55, False, False),
    ("pop_strawberry.svg", "Strawberry Chill Pop", "#FCE7F3", "#F472B6", "#E11D48", "#BE123C", 55, True, False),
    ("pop_pineapple.svg", "Pineapple Twist Pop", "#FEF08A", "#FACC15", "#CA8A04", "#A16207", 55, False, False),
    ("pop_rosemilk.svg", "Rose Milk Pop", "#FFE4E6", "#FDA4AF", "#F43F5E", "#E11D48", 55, False, False),
    ("pop_blueberry.svg", "Blueberry Blast Pop", "#DDD6FE", "#8B5CF6", "#4C1D95", "#2E1065", 55, False, False),
    ("pop_chocolate.svg", "Chocolate Crunch Stick", "#78350F", "#451A03", "#1C1917", "#0F0E0D", 55, True, True),
    ("pop_blackcurrant.svg", "Blackcurrant Freeze Pop", "#C084FC", "#9333EA", "#581C87", "#3B0764", 55, False, False),
    ("pop_butterscotch.svg", "Butterscotch Delight Stick", "#FEF08A", "#F59E0B", "#D97706", "#B45309", 55, True, False),
    ("pop_belgian_choc.svg", "Belgian Chocolate Stick", "#582F0E", "#3E2723", "#271003", "#1A0801", 55, False, True),
    ("pop_mixed_berry.svg", "Mixed Berry Blast Pop", "#F472B6", "#BE185D", "#831843", "#500724", 55, True, False),
    ("pop_mango_cream.svg", "Premium Mango Cream Stick", "#FEF08A", "#FBBF24", "#F97316", "#EA580C", 55, True, False),
    ("pop_double_choc.svg", "Double Chocolate Chill Stick", "#451A03", "#2E1065", "#1C1917", "#0A0A0A", 55, True, True),
    ("pop_kesar_badam.svg", "Kesar Badam Stick", "#FEF3C7", "#FDE68A", "#F59E0B", "#D97706", 55, True, False),
    ("pop_watermelon.svg", "Watermelon Breeze Pop", "#86EFAC", "#F87171", "#DC2626", "#991B1B", 55, False, False),
    ("pop_kalakhatta.svg", "Tangy Kala Khatta Stick", "#E9D5FF", "#A855F7", "#6B21A8", "#4C1D95", 55, False, False),
    ("pop_malai_kulfi.svg", "Malai Kulfi Stick", "#FFFBEB", "#FEF3C7", "#FDE68A", "#F59E0B", 55, True, False),
    ("pop_roasted_almond.svg", "Roasted Almond Stick", "#FDE68A", "#D97706", "#78350F", "#451A03", 55, True, True)
]

for item in items:
    make_stick_pop(*item)

print(f"Successfully generated logo and {len(items)} stick ice cream images.")
