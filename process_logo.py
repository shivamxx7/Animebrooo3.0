import sys
import subprocess

try:
    from PIL import Image, ImageDraw
except ImportError:
    subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
    from PIL import Image, ImageDraw

img_path = "file_00000000c1e8821195997f949abe88b0.png"
out_path = "app/src/main/res/drawable/logo_aniwave.png"

try:
    img = Image.open(img_path).convert("RGBA")
    
    # Crop to exact coordinates provided by user
    img = img.crop((47, 47, 1207, 1207))
    
    # Make corners transparent
    # The image is 1160x1160.
    # We will check the color of the corner pixels and flood fill with transparency (0,0,0,0).
    # Floodfill from 4 corners.
    ImageDraw.floodfill(img, (0, 0), (0, 0, 0, 0), thresh=30)
    ImageDraw.floodfill(img, (img.width-1, 0), (0, 0, 0, 0), thresh=30)
    ImageDraw.floodfill(img, (0, img.height-1), (0, 0, 0, 0), thresh=30)
    ImageDraw.floodfill(img, (img.width-1, img.height-1), (0, 0, 0, 0), thresh=30)
    
    img.save(out_path)
    print("SUCCESS: Logo processed and saved to", out_path)
except Exception as e:
    print("ERROR:", e)
