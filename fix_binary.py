import os
import sys
import struct

try:
    from PIL import Image, ImageDraw
except ImportError:
    import subprocess
    subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
    from PIL import Image, ImageDraw

src_img = "app/logo_source.png"
base_res_dir = "app/src/main/res"
densities = {
    "mdpi": {"legacy": 48, "fg": 108},
    "hdpi": {"legacy": 72, "fg": 162},
    "xhdpi": {"legacy": 96, "fg": 216},
    "xxhdpi": {"legacy": 144, "fg": 324},
    "xxxhdpi": {"legacy": 192, "fg": 432}
}

def verify_png(path):
    with open(path, 'rb') as f:
        header = f.read(4)
        if header != b'\x89PNG': # \x89 \x50 \x4e \x47
            return False, header.hex()
        return True, header.hex()

with Image.open(src_img) as img:
    img = img.convert("RGBA")
    for density, sizes in densities.items():
        dir_path = os.path.join(base_res_dir, f"mipmap-{density}")
        os.makedirs(dir_path, exist_ok=True)
        
        # Legacy Square
        leg_sz = sizes["legacy"]
        bg = Image.new("RGBA", (leg_sz, leg_sz), (0,0,0,255))
        ratio = min(leg_sz / img.width, leg_sz / img.height) * 0.9
        new_w, new_h = int(img.width * ratio), int(img.height * ratio)
        resized = img.resize((new_w, new_h), Image.Resampling.LANCZOS)
        bg.paste(resized, ((leg_sz - new_w)//2, (leg_sz - new_h)//2), resized)
        sq_path = os.path.join(dir_path, "ic_launcher.png")
        bg.save(sq_path, format="PNG")
        
        # Legacy Round
        mask = Image.new("L", (leg_sz, leg_sz), 0)
        draw = ImageDraw.Draw(mask)
        draw.ellipse((0, 0, leg_sz, leg_sz), fill=255)
        round_img = bg.copy()
        round_img.putalpha(mask)
        rd_path = os.path.join(dir_path, "ic_launcher_round.png")
        round_img.save(rd_path, format="PNG")
        
        # Foreground
        fg_sz = sizes["fg"]
        fg_bg = Image.new("RGBA", (fg_sz, fg_sz), (0,0,0,0))
        ratio_fg = min(fg_sz / img.width, fg_sz / img.height) * 0.62
        new_w_fg, new_h_fg = int(img.width * ratio_fg), int(img.height * ratio_fg)
        resized_fg = img.resize((new_w_fg, new_h_fg), Image.Resampling.LANCZOS)
        fg_bg.paste(resized_fg, ((fg_sz - new_w_fg)//2, (fg_sz - new_h_fg)//2), resized_fg)
        fg_path = os.path.join(dir_path, "ic_launcher_foreground.png")
        fg_bg.save(fg_path, format="PNG")

print("Verifying PNG Headers...")
all_valid = True
for density in densities.keys():
    dir_path = os.path.join(base_res_dir, f"mipmap-{density}")
    for f in ["ic_launcher.png", "ic_launcher_round.png", "ic_launcher_foreground.png"]:
        path = os.path.join(dir_path, f)
        is_valid, hex_header = verify_png(path)
        if not is_valid:
            all_valid = False
            print(f"CORRUPTED: {path} - Header: {hex_header}")
        else:
            print(f"OK: {path} - Header: {hex_header} - Size: {os.path.getsize(path)} bytes")

if all_valid:
    print("SUCCESS: All PNGs have valid 89504e47 headers.")
else:
    sys.exit("FAILURE: Some PNGs are corrupted.")
