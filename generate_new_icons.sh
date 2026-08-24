#!/bin/bash
set -e

SRC="25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-2.png"

# Verify source header to ensure it's not corrupt
HEADER=$(od -A n -t x1 -N 8 "$SRC" | tr -d ' \n')
if [ "$HEADER" != "89504e470d0a1a0a" ]; then
    echo "Source is corrupted: $HEADER"
    exit 1
fi
echo "Source is valid PNG binary."

echo "1. Deleting all old icon files..."
rm -f app/src/main/res/mipmap-*/*.png
rm -f app/src/main/res/mipmap-*/*.xml
rm -f app/src/main/res/drawable/animebro_logo.png
rm -f app/src/main/res/drawable/app_logo.png
rm -f app/src/main/res/drawable/ic_launcher_vector.xml

echo "2. Generating mipmap icon set at every density using ImageMagick..."
# Density: legacy_size:adaptive_size:scaled_logo_size
densities=("mdpi:48:108:72" "hdpi:72:162:108" "xhdpi:96:216:144" "xxhdpi:144:324:216" "xxxhdpi:192:432:288")

for entry in "${densities[@]}"; do
    IFS=":" read -r density size fg_size logo_scaled_size <<< "$entry"
    DIR="app/src/main/res/mipmap-$density"
    mkdir -p "$DIR"
    
    # 1. Square legacy icon
    convert -size ${size}x${size} canvas:black \
        \( "$SRC" -resize ${size}x${size}\> \) \
        -gravity center -composite \
        "$DIR/ic_launcher.png"
        
    # 2. Round legacy icon
    convert -size ${size}x${size} xc:none -fill white -draw "circle $((size/2)),$((size/2)) $((size/2)),0" mask.png
    convert -size ${size}x${size} canvas:black \
        \( "$SRC" -resize ${size}x${size}\> \) \
        -gravity center -composite \
        mask.png -alpha off -compose CopyOpacity -composite \
        "$DIR/ic_launcher_round.png"
        
    # 3. Adaptive foreground (transparent canvas)
    convert -size ${fg_size}x${fg_size} xc:transparent \
        \( "$SRC" -resize ${logo_scaled_size}x${logo_scaled_size}\> \) \
        -gravity center -composite \
        "$DIR/ic_launcher_foreground.png"
        
    rm -f mask.png
done

echo "3. Adding adaptive icon XML..."
mkdir -p app/src/main/res/mipmap-anydpi-v26
cat << 'XML' > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
XML
cp app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml

echo "4. Setting background color to black..."
mkdir -p app/src/main/res/values
cat << 'XML' > app/src/main/res/values/colors.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#000000</color>
</resources>
XML

echo "5. Fixing AndroidManifest roundIcon reference..."
sed -i 's/android:roundIcon="@mipmap\/ic_launcher"/android:roundIcon="@mipmap\/ic_launcher_round"/g' app/src/main/AndroidManifest.xml

echo "6. Verifying every PNG file..."
ALL_GOOD=1
find app/src/main/res -name "*.png" | while read -r img; do
    IMG_HEADER=$(od -A n -t x1 -N 8 "$img" | tr -d ' \n')
    if [ "$IMG_HEADER" != "89504e470d0a1a0a" ]; then
        echo "CORRUPTED FILE DETECTED: $img (Header: $IMG_HEADER)"
        ALL_GOOD=0
    else
        echo "VERIFIED OK: $img"
    fi
done

if [ $ALL_GOOD -eq 0 ]; then
    echo "ERROR: Corrupted PNGs detected!"
    exit 1
fi

echo "Cleaning build cache..."
gradle clean

echo "Done."
