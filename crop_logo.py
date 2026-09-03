import sys
import subprocess
try:
    from PIL import Image
except ImportError:
    subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
    from PIL import Image

img = Image.open("file_00000000c1e8821195997f949abe88b0-1.png")

left = 48
top = 48
right = 1488
bottom = 1508

cropped_img = img.crop((left, top, right, bottom))
cropped_img.save("app/src/main/res/drawable/logo_aniwave.png")
print("Image cropped and saved successfully!")
