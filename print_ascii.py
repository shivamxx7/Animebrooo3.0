import sys
from PIL import Image

def print_ascii(image_path):
    try:
        img = Image.open(image_path)
        img = img.convert('L') # Convert to grayscale
        width, height = img.size
        aspect_ratio = height/width
        new_width = 80
        new_height = int(aspect_ratio * new_width * 0.5)
        img = img.resize((new_width, new_height))
        
        pixels = img.getdata()
        chars = ["B","S","#","&","@","$","%","*","!",":","."]
        new_pixels = [chars[pixel//25] for pixel in pixels]
        new_pixels = ''.join(new_pixels)
        
        ascii_image = [new_pixels[index:index + new_width] for index in range(0, len(new_pixels), new_width)]
        ascii_image = "\n".join(ascii_image)
        print(ascii_image)
    except Exception as e:
        print(f"Error: {e}")

print_ascii(sys.argv[1])
