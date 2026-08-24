#!/bin/bash
SRC="25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-1.png"

# Delete any old anime_logo stuff to be clean
rm -rf app/src/main/res/mipmap-*/anime_logo*
rm -rf app/src/main/res/drawable/anime_logo*

# We will overwrite ic_launcher completely
echo "Generating Adaptive Foregrounds for ic_launcher..."
mkdir -p app/src/main/res/drawable-mdpi
mkdir -p app/src/main/res/drawable-hdpi
mkdir -p app/src/main/res/drawable-xhdpi
mkdir -p app/src/main/res/drawable-xxhdpi
mkdir -p app/src/main/res/drawable-xxxhdpi

convert "$SRC" -resize 108x108 app/src/main/res/drawable-mdpi/ic_launcher_foreground.png
convert "$SRC" -resize 162x162 app/src/main/res/drawable-hdpi/ic_launcher_foreground.png
convert "$SRC" -resize 216x216 app/src/main/res/drawable-xhdpi/ic_launcher_foreground.png
convert "$SRC" -resize 324x324 app/src/main/res/drawable-xxhdpi/ic_launcher_foreground.png
convert "$SRC" -resize 432x432 app/src/main/res/drawable-xxxhdpi/ic_launcher_foreground.png

echo "Generating Legacy Icons for ic_launcher..."
for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
    case $density in
        mdpi) size=48 ;;
        hdpi) size=72 ;;
        xhdpi) size=96 ;;
        xxhdpi) size=144 ;;
        xxxhdpi) size=192 ;;
    esac
    
    convert -size ${size}x${size} canvas:black \
            \( "$SRC" -resize ${size}x${size} \) \
            -gravity center -composite \
            "app/src/main/res/mipmap-${density}/ic_launcher.png"

    convert -size ${size}x${size} canvas:black \
            \( "$SRC" -resize ${size}x${size} \) \
            -gravity center -composite \
            "app/src/main/res/mipmap-${density}/ic_launcher_round.png"
done

# Ensure the XMLs use the foreground we generated
cat << 'XML' > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:color="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
XML

cp app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml

echo "Done"
