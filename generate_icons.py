import os
import sys
import subprocess

# Ensure Pillow is installed
try:
    from PIL import Image, ImageDraw
except ImportError:
    subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
    from PIL import Image, ImageDraw

src_img = "25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-2.png"
if not os.path.exists(src_img):
    src_img = "25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b.png"
if not os.path.exists(src_img):
    src_img = "file_00000000d6b08211afa0219b69ef51cf.png"

print(f"Using source image: {src_img}")

base_res_dir = "app/src/main/res"
densities = {
    "mdpi": {"legacy": 48, "fg": 108},
    "hdpi": {"legacy": 72, "fg": 162},
    "xhdpi": {"legacy": 96, "fg": 216},
    "xxhdpi": {"legacy": 144, "fg": 324},
    "xxxhdpi": {"legacy": 192, "fg": 432}
}

with Image.open(src_img) as img:
    img = img.convert("RGBA")
    
    def create_centered(target_size, bg_color):
        bg = Image.new("RGBA", (target_size, target_size), bg_color)
        ratio = min(target_size/img.width, target_size/img.height)
        new_w, new_h = int(img.width * ratio), int(img.height * ratio)
        resized = img.resize((new_w, new_h), Image.Resampling.LANCZOS)
        offset = ((target_size - new_w)//2, (target_size - new_h)//2)
        bg.paste(resized, offset, resized if resized.mode == 'RGBA' else None)
        return bg

    for density, sizes in densities.items():
        dir_path = os.path.join(base_res_dir, f"mipmap-{density}")
        os.makedirs(dir_path, exist_ok=True)
        
        # Square
        legacy_size = sizes["legacy"]
        legacy_square = create_centered(legacy_size, (0, 0, 0, 255))
        legacy_square.save(os.path.join(dir_path, "ic_launcher.png"), format="PNG")
        
        # Round
        mask = Image.new("L", (legacy_size, legacy_size), 0)
        draw = ImageDraw.Draw(mask)
        draw.ellipse((0, 0, legacy_size, legacy_size), fill=255)
        legacy_round = legacy_square.copy()
        legacy_round.putalpha(mask)
        legacy_round.save(os.path.join(dir_path, "ic_launcher_round.png"), format="PNG")
        
        # Foreground Adaptive (Transparent)
        fg_size = sizes["fg"]
        fg_img = create_centered(fg_size, (0, 0, 0, 0))
        fg_img.save(os.path.join(dir_path, "ic_launcher_foreground.png"), format="PNG")

print("Validating generated files...")
for density in densities.keys():
    dir_path = os.path.join(base_res_dir, f"mipmap-{density}")
    for f in ["ic_launcher.png", "ic_launcher_round.png", "ic_launcher_foreground.png"]:
        path = os.path.join(dir_path, f)
        size = os.path.getsize(path)
        print(f"{path}: {size} bytes")
        if size == 0:
            sys.exit(f"ERROR: {path} is 0 bytes!")
            
print("SUCCESS: All PNGs generated correctly using Python Pillow.")
