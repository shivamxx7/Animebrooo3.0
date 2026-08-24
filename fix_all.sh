#!/bin/bash
set -e

SRC="25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-1.png"

echo "Cleaning up..."
rm -rf app/src/main/res/mipmap-*/*
rm -rf app/src/main/res/drawable-*/ic_launcher*
rm -rf app/src/main/res/drawable-*/anime_logo*
rm -rf app/src/main/res/drawable-*/anime_bro*
rm -rf app/src/main/res/drawable-*/anime_icon*

echo "Creating new icon: anime_icon"

mkdir -p app/src/main/res/mipmap-anydpi-v26
mkdir -p app/src/main/res/drawable-mdpi
mkdir -p app/src/main/res/drawable-hdpi
mkdir -p app/src/main/res/drawable-xhdpi
mkdir -p app/src/main/res/drawable-xxhdpi
mkdir -p app/src/main/res/drawable-xxxhdpi

mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi

# Generate Foreground PNGs (108dp size for Adaptive Icon)
convert "$SRC" -resize 108x108 app/src/main/res/drawable-mdpi/anime_icon_foreground.png
convert "$SRC" -resize 162x162 app/src/main/res/drawable-hdpi/anime_icon_foreground.png
convert "$SRC" -resize 216x216 app/src/main/res/drawable-xhdpi/anime_icon_foreground.png
convert "$SRC" -resize 324x324 app/src/main/res/drawable-xxhdpi/anime_icon_foreground.png
convert "$SRC" -resize 432x432 app/src/main/res/drawable-xxxhdpi/anime_icon_foreground.png

# Generate Legacy mipmaps (48dp base size)
for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
    case $density in
        mdpi) size=48 ;;
        hdpi) size=72 ;;
        xhdpi) size=96 ;;
        xxhdpi) size=144 ;;
        xxxhdpi) size=192 ;;
    esac
    
    # Square legacy icon
    convert -size ${size}x${size} canvas:black \
            \( "$SRC" -resize ${size}x${size} \) \
            -gravity center -composite \
            "app/src/main/res/mipmap-${density}/anime_icon.png"

    # Round legacy icon (masked)
    convert -size ${size}x${size} xc:none -fill white -draw "circle $((size/2)),$((size/2)) $((size/2)),0" mask.png
    convert -size ${size}x${size} canvas:black \
            \( "$SRC" -resize ${size}x${size} \) \
            -gravity center -composite \
            mask.png -alpha off -compose CopyOpacity -composite \
            "app/src/main/res/mipmap-${density}/anime_icon_round.png"
    rm mask.png
done

# Generate Adaptive Icon XML
cat << 'XML' > app/src/main/res/mipmap-anydpi-v26/anime_icon.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:color="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/anime_icon_foreground"/>
</adaptive-icon>
XML

cp app/src/main/res/mipmap-anydpi-v26/anime_icon.xml app/src/main/res/mipmap-anydpi-v26/anime_icon_round.xml

# Generate a solid color background just to be safe
cat << 'XML' > app/src/main/res/values/colors.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#000000</color>
</resources>
XML

echo "Done"
