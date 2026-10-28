import os

output_dir = r"E:\Chill_pops\src\main\resources\static\images\products"
os.makedirs(output_dir, exist_ok=True)

def pop_svg(c1, c2, c3, drip):
    return f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 220" width="200" height="220">
  <defs>
    <linearGradient id="g" x1="0" y1="0" x2="0" y2="1">
      <stop offset="0%" stop-color="{c1}"/>
      <stop offset="55%" stop-color="{c2}"/>
      <stop offset="100%" stop-color="{c3}"/>
    </linearGradient>
    <linearGradient id="wood" x1="0" y1="0" x2="1" y2="0">
      <stop offset="0%" stop-color="#FDE68A"/>
      <stop offset="100%" stop-color="#D97706"/>
    </linearGradient>
  </defs>
  <circle cx="100" cy="110" r="96" fill="#FFF8F0"/>
  <rect x="90" y="148" width="20" height="58" rx="10" fill="url(#wood)"/>
  <rect x="62" y="28" width="76" height="128" rx="38" fill="url(#g)"/>
  <path d="M78,52 L78,128" stroke="#FFF" stroke-width="5" stroke-linecap="round" opacity="0.45"/>
  <circle cx="118" cy="52" r="6" fill="#FFF" opacity="0.75"/>
  <path d="M124,118 C124,132 138,132 138,118 C138,112 124,112 124,118 Z" fill="{drip}" opacity="0.85"/>
  <text x="100" y="214" text-anchor="middle" font-family="Nunito, sans-serif" font-size="11" font-weight="800" fill="#FF6B8B">CHILL POPS</text>
</svg>
"""

flavours = {
    "pop_vanilla.svg": ("#FFF7D6", "#FDE68A", "#FCD34D", "#F59E0B"),
    "pop_orange.svg": ("#FDBA74", "#FB923C", "#F97316", "#EA580C"),
    "pop_cola.svg": ("#92400E", "#78350F", "#451A03", "#1C1917"),
    "pop_mango.svg": ("#FDE047", "#FACC15", "#EAB308", "#D97706"),
    "pop_watermelon.svg": ("#FCA5A5", "#F87171", "#34D399", "#059669"),
    "pop_strawberry.svg": ("#FBCFE8", "#F472B6", "#EC4899", "#BE185D"),
    "pop_kalakhatta.svg": ("#D8B4FE", "#A855F7", "#7E22CE", "#581C87"),
    "pop_blueberry.svg": ("#C4B5FD", "#818CF8", "#4F46E5", "#312E81"),
    "pop_chocolate.svg": ("#A16207", "#78350F", "#451A03", "#1C1917"),
    "pop_mint.svg": ("#A7F3D0", "#6EE7B7", "#10B981", "#047857"),
    "pop_bubblegum.svg": ("#93C5FD", "#60A5FA", "#F472B6", "#DB2777"),
    "pop_cottoncandy.svg": ("#F9A8D4", "#F472B6", "#7DD3FC", "#38BDF8"),
    "pop_rajbhog.svg": ("#FDE68A", "#FBBF24", "#F59E0B", "#B45309"),
}

for name, colors in flavours.items():
    with open(os.path.join(output_dir, name), "w", encoding="utf-8") as f:
        f.write(pop_svg(*colors))
    print(name)

print("Stick ice cream SVGs ready.")
