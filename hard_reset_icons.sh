#!/bin/bash
set -e

SRC="25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-1.png"

echo "1. DELETING ADAPTIVE ICONS..."
rm -rf app/src/main/res/mipmap-anydpi-v26/

echo "2 & 3. PREPARING LOGO AND INJECTING RAW PNGs..."
densities=("mdpi:48" "hdpi:72" "xhdpi:96" "xxhdpi:144" "xxxhdpi:192")

for entry in "${densities[@]}"; do
    IFS=":" read -r density size <<< "$entry"
    mkdir -p "app/src/main/res/mipmap-$density"
    
    # Create square icon (anime_icon.png)
    convert -size ${size}x${size} canvas:black \
        \( "$SRC" -resize ${size}x${size} \) \
        -gravity center -composite \
        "app/src/main/res/mipmap-$density/anime_icon.png"
        
    # Create round icon (anime_icon_round.png) - using a circular mask
    convert -size ${size}x${size} xc:none -fill white -draw "circle $((size/2)),$((size/2)) $((size/2)),0" mask.png
    convert -size ${size}x${size} canvas:black \
        \( "$SRC" -resize ${size}x${size} \) \
        -gravity center -composite \
        mask.png -alpha off -compose CopyOpacity -composite \
        "app/src/main/res/mipmap-$density/anime_icon_round.png"
    rm mask.png
done

echo "Done generating PNGs."
