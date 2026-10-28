import os

output_dir = r"E:\Chill_pops\src\main\resources\static\images\products"
os.makedirs(output_dir, exist_ok=True)
images_dir = r"E:\Chill_pops\src\main\resources\static\images"
os.makedirs(images_dir, exist_ok=True)

def write_svg(filename, content):
    with open(os.path.join(output_dir, filename), "w", encoding="utf-8") as f:
        f.write(content.strip())

# Chill Pops Logo
logo_svg = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 240 70" width="240" height="70">
  <defs>
    <linearGradient id="popGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#FF6B8B"/>
      <stop offset="100%" stop-color="#FF8DA1"/>
    </linearGradient>
    <linearGradient id="stickGrad" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="#FCD34D"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
  </defs>
  <!-- Popsicle Icon -->
  <g transform="translate(10, 8)">
    <rect x="16" y="38" width="8" height="18" rx="4" fill="url(#stickGrad)"/>
    <path d="M8,40 L32,40 C34,40 36,38 36,34 L36,18 C36,8 28,0 20,0 C12,0 4,8 4,18 L4,34 C4,38 6,40 8,40 Z" fill="url(#popGrad)"/>
    <path d="M10,8 C14,8 14,20 18,20 C22,20 22,8 26,8 C28,8 30,12 30,12" stroke="#FFF" stroke-width="2.5" stroke-linecap="round" fill="none" opacity="0.7"/>
    <circle cx="28" cy="14" r="3" fill="#FFF" opacity="0.9"/>
  </g>
  <!-- Text Brand -->
  <text x="60" y="38" font-family="'Poppins', sans-serif" font-weight="900" font-size="28" fill="#FF477E" letter-spacing="-0.5">Chill<tspan fill="#38BDF8"> Pops</tspan></text>
  <text x="62" y="54" font-family="'Poppins', sans-serif" font-weight="600" font-size="9" fill="#94A3B8" letter-spacing="2">ICE CREAM &amp; POS</text>
</svg>"""

with open(os.path.join(images_dir, "logo.svg"), "w", encoding="utf-8") as f:
    f.write(logo_svg)

# Templates for different ice cream types:
# 1. Cup Template
def create_cup_svg(color1, color2, swirl_color, topping_color, label):
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">
  <defs>
    <radialGradient id="scoop1" cx="35%" cy="35%" r="65%">
      <stop offset="0%" stop-color="{color1}"/>
      <stop offset="100%" stop-color="{color2}"/>
    </radialGradient>
    <linearGradient id="cupGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#FFF0F3"/>
      <stop offset="100%" stop-color="#FCE7F3"/>
    </linearGradient>
  </defs>
  <!-- Background Glow -->
  <circle cx="100" cy="100" r="88" fill="#FFF8F0"/>
  <!-- Scoop -->
  <circle cx="100" cy="85" r="48" fill="url(#scoop1)"/>
  <!-- Swirl or highlights -->
  <path d="M75,70 Q100,50 125,70 Q100,85 75,70 Z" fill="{swirl_color}" opacity="0.6"/>
  <circle cx="85" cy="65" r="4" fill="#FFF" opacity="0.8"/>
  <circle cx="115" cy="75" r="3" fill="#FFF" opacity="0.8"/>
  <!-- Topping or Cherry -->
  <circle cx="100" cy="40" r="11" fill="{topping_color}"/>
  <path d="M100,32 Q115,18 120,24" stroke="#78350F" stroke-width="2" fill="none" stroke-linecap="round"/>
  <!-- Cup Body -->
  <path d="M52,105 L62,165 C63,169 66,172 70,172 L130,172 C134,172 137,169 138,165 L148,105 Z" fill="url(#cupGrad)" stroke="#FFB6C1" stroke-width="2"/>
  <rect x="48" y="100" width="104" height="10" rx="4" fill="#FF8DA1"/>
  <!-- Spoon -->
  <path d="M135,70 L155,120" stroke="#93C5FD" stroke-width="6" stroke-linecap="round"/>
  <!-- Ice Cream Texture Dots -->
  <circle cx="90" cy="95" r="2" fill="{color2}" opacity="0.5"/>
  <circle cx="110" cy="95" r="2.5" fill="{color2}" opacity="0.5"/>
</svg>"""

# 2. Popsicle Template
def create_pop_svg(c1, c2, c3, drip_color, label):
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">
  <defs>
    <linearGradient id="popGrad" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="{c1}"/>
      <stop offset="50%" stop-color="{c2}"/>
      <stop offset="100%" stop-color="{c3}"/>
    </linearGradient>
    <linearGradient id="wood" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0%" stop-color="#FDE68A"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
  </defs>
  <!-- Background Glow -->
  <circle cx="100" cy="100" r="88" fill="#F0FDFA"/>
  <!-- Wooden Stick -->
  <rect x="91" y="130" width="18" height="50" rx="9" fill="url(#wood)"/>
  <!-- Popsicle Bar -->
  <rect x="65" y="35" width="70" height="105" rx="35" fill="url(#popGrad)"/>
  <!-- Highlight bar -->
  <path d="M78,55 L78,120" stroke="#FFF" stroke-width="4" stroke-linecap="round" opacity="0.5"/>
  <!-- Fresh drop / drip -->
  <path d="M120,110 C120,122 132,122 132,110 C132,105 120,105 120,110 Z" fill="{drip_color}" opacity="0.8"/>
  <circle cx="85" cy="50" r="4" fill="#FFF" opacity="0.8"/>
</svg>"""

# 3. Sundae Template
def create_sundae_svg(c1, c2, syrup_color, cherry_color):
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">
  <defs>
    <radialGradient id="sun1" cx="35%" cy="35%" r="65%">
      <stop offset="0%" stop-color="{c1}"/>
      <stop offset="100%" stop-color="{c2}"/>
    </radialGradient>
    <linearGradient id="glass" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="#E0F2FE"/>
      <stop offset="100%" stop-color="#BAE6FD"/>
    </linearGradient>
  </defs>
  <!-- Background -->
  <circle cx="100" cy="100" r="88" fill="#FFF1F2"/>
  <!-- Glass Base & Stem -->
  <ellipse cx="100" cy="180" rx="30" ry="7" fill="url(#glass)"/>
  <rect x="96" y="145" width="8" height="35" fill="url(#glass)"/>
  <!-- Glass Bowl -->
  <path d="M55,95 Q55,150 100,150 Q145,150 145,95 Z" fill="url(#glass)" opacity="0.8" stroke="#7DD3FC" stroke-width="1.5"/>
  <!-- Scoops inside glass -->
  <circle cx="82" cy="85" r="28" fill="url(#sun1)"/>
  <circle cx="118" cy="85" r="28" fill="{c2}"/>
  <circle cx="100" cy="65" r="26" fill="{c1}"/>
  <!-- Whipped Cream & Syrup -->
  <path d="M75,60 Q100,30 125,60" fill="#FFF" stroke="#FFF" stroke-width="8" stroke-linecap="round"/>
  <path d="M85,55 Q95,75 105,55 Q115,75 125,60" stroke="{syrup_color}" stroke-width="3.5" fill="none" stroke-linecap="round"/>
  <!-- Cherry on top -->
  <circle cx="100" cy="30" r="9" fill="{cherry_color}"/>
  <path d="M100,24 Q112,12 118,16" stroke="#991B1B" stroke-width="2" fill="none" stroke-linecap="round"/>
  <!-- Wafer Stick -->
  <line x1="125" y1="50" x2="155" y2="20" stroke="#D97706" stroke-width="5" stroke-linecap="round"/>
</svg>"""

# 4. Milkshake Template
def create_shake_svg(drink_c1, drink_c2, straw_color, cream_color):
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">
  <defs>
    <linearGradient id="shakeGrad" x1="0" y1="0" x2="1" y2="1">
      <stop offset="0%" stop-color="{drink_c1}"/>
      <stop offset="100%" stop-color="{drink_c2}"/>
    </linearGradient>
  </defs>
  <circle cx="100" cy="100" r="88" fill="#FDF4FF"/>
  <!-- Straw -->
  <line x1="110" y1="75" x2="145" y2="15" stroke="{straw_color}" stroke-width="8" stroke-linecap="round"/>
  <line x1="110" y1="75" x2="145" y2="15" stroke="#FFF" stroke-width="2" stroke-linecap="round" stroke-dasharray="6,6"/>
  <!-- Glass Cup -->
  <path d="M68,75 L76,170 C77,175 80,178 85,178 L115,178 C120,178 123,175 124,170 L132,75 Z" fill="#E2E8F0" opacity="0.4"/>
  <!-- Drink Liquid -->
  <path d="M70,82 L77,168 C78,172 81,175 85,175 L115,175 C119,175 122,172 123,168 L130,82 Z" fill="url(#shakeGrad)"/>
  <!-- Whipped Cream Dome -->
  <path d="M68,78 C68,55 82,45 100,45 C118,45 132,55 132,78 Z" fill="{cream_color}"/>
  <!-- Sprinkles & Cherry -->
  <circle cx="100" cy="42" r="8" fill="#EF4444"/>
  <rect x="85" y="58" width="5" height="2" rx="1" fill="#F59E0B" transform="rotate(25 85 58)"/>
  <rect x="110" y="55" width="5" height="2" rx="1" fill="#EC4899" transform="rotate(-30 110 55)"/>
  <rect x="95" y="65" width="5" height="2" rx="1" fill="#10B981" transform="rotate(15 95 65)"/>
</svg>"""

# 5. Scoop / Special Cone Template
def create_cone_svg(c1, c2, cone_c, deco_c):
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200" width="200" height="200">
  <defs>
    <radialGradient id="scoopCone" cx="35%" cy="35%" r="65%">
      <stop offset="0%" stop-color="{c1}"/>
      <stop offset="100%" stop-color="{c2}"/>
    </radialGradient>
  </defs>
  <circle cx="100" cy="100" r="88" fill="#FEF3C7"/>
  <!-- Waffle Cone -->
  <polygon points="68,95 132,95 100,180" fill="{cone_c}"/>
  <line x1="75" y1="105" x2="115" y2="160" stroke="#B45309" stroke-width="1.5" opacity="0.5"/>
  <line x1="125" y1="105" x2="85" y2="160" stroke="#B45309" stroke-width="1.5" opacity="0.5"/>
  <!-- Big Delicious Scoop -->
  <circle cx="100" cy="78" r="42" fill="url(#scoopCone)"/>
  <path d="M62,88 Q100,105 138,88 Q100,96 62,88 Z" fill="{c2}"/>
  <!-- Decoration / Sprinkles -->
  <circle cx="85" cy="62" r="3.5" fill="{deco_c}"/>
  <circle cx="115" cy="68" r="4" fill="#FFF" opacity="0.8"/>
  <circle cx="100" cy="50" r="3" fill="{deco_c}"/>
</svg>"""

# Generation:
# Category 1: Cups
write_svg("cup_vanilla.svg", create_cup_svg("#FEF3C7", "#FDE68A", "#FFFBEB", "#DC2626", "Vanilla"))
write_svg("cup_strawberry.svg", create_cup_svg("#FBCFE8", "#F472B6", "#DB2777", "#BE185D", "Strawberry"))
write_svg("cup_butterscotch.svg", create_cup_svg("#FDE047", "#EAB308", "#CA8A04", "#854D0E", "Butterscotch"))
write_svg("cup_caramel.svg", create_cup_svg("#FDBA74", "#EA580C", "#9A3412", "#7C2D12", "Caramel"))

# Category 2: Popsicles
write_svg("pop_mango.svg", create_pop_svg("#FBBF24", "#F59E0B", "#D97706", "#D97706", "Mango"))
write_svg("pop_orange.svg", create_pop_svg("#FB923C", "#F97316", "#EA580C", "#EA580C", "Orange"))
write_svg("pop_kalakhatta.svg", create_pop_svg("#A855F7", "#7E22CE", "#581C87", "#581C87", "Kala Khatta"))
write_svg("pop_watermelon.svg", create_pop_svg("#F87171", "#EF4444", "#10B981", "#059669", "Watermelon"))

# Category 3: Chocolates
write_svg("choc_belgian.svg", create_cone_svg("#5B3A29", "#3E2723", "#D97706", "#F59E0B"))
write_svg("choc_brownie.svg", create_cup_svg("#78350F", "#451A03", "#290F02", "#EF4444", "Brownie"))
write_svg("choc_mint.svg", create_cone_svg("#6EE7B7", "#059669", "#D97706", "#3E2723"))
write_svg("choc_hazelnut.svg", create_cup_svg("#92400E", "#78350F", "#B45309", "#FDE68A", "Hazelnut"))

# Category 4: Fruit Ice Creams
write_svg("fruit_mango.svg", create_cone_svg("#FDE047", "#EAB308", "#D97706", "#EF4444"))
write_svg("fruit_sitaphal.svg", create_cup_svg("#ECFCCB", "#BEF264", "#A3E635", "#65A30D", "Sitaphal"))
write_svg("fruit_coconut.svg", create_cup_svg("#FFFFFF", "#F1F5F9", "#CBD5E1", "#0284C7", "Coconut"))
write_svg("fruit_blueberry.svg", create_cone_svg("#C084FC", "#7E22CE", "#D97706", "#FAF5FF"))

# Category 5: Sundaes
write_svg("sundae_dbc.svg", create_sundae_svg("#451A03", "#78350F", "#3E2723", "#DC2626"))
write_svg("sundae_banana.svg", create_sundae_svg("#FEF08A", "#F472B6", "#EA580C", "#DC2626"))
write_svg("sundae_berry.svg", create_sundae_svg("#F472B6", "#E11D48", "#9F1239", "#BE123C"))
write_svg("sundae_caramel.svg", create_sundae_svg("#FDE047", "#D97706", "#B45309", "#DC2626"))

# Category 6: Milkshakes
write_svg("shake_oreo.svg", create_shake_svg("#374151", "#1F2937", "#3B82F6", "#FFFFFF"))
write_svg("shake_kitkat.svg", create_shake_svg("#B45309", "#78350F", "#EF4444", "#FFFBEB"))
write_svg("shake_belgian.svg", create_shake_svg("#5B3A29", "#3E2723", "#F59E0B", "#FFF7ED"))
write_svg("shake_strawberry.svg", create_shake_svg("#F472B6", "#DB2777", "#10B981", "#FFF1F2"))

# Category 7: Special Ice Creams
write_svg("special_rajbhog.svg", create_cup_svg("#FEF08A", "#FACC15", "#EAB308", "#15803D", "Rajbhog"))
write_svg("special_kesar.svg", create_cone_svg("#FBBF24", "#D97706", "#D97706", "#16A34A"))
write_svg("special_cottoncandy.svg", create_cone_svg("#F472B6", "#38BDF8", "#FDE68A", "#FFFFFF"))
write_svg("special_bubblegum.svg", create_cup_svg("#38BDF8", "#0284C7", "#F472B6", "#FDE047", "Bubblegum"))

print("Successfully generated all SVGs!")
