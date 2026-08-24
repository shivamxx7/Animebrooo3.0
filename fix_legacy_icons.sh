#!/bin/bash
SRC="app/src/main/res/drawable-xxxhdpi/anime_logo_foreground.png"

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
            "app/src/main/res/mipmap-${density}/anime_logo.png"

    convert -size ${size}x${size} canvas:black \
            \( "$SRC" -resize ${size}x${size} \) \
            -gravity center -composite \
            "app/src/main/res/mipmap-${density}/anime_logo_round.png"
done
echo "Legacy icons replaced successfully."
