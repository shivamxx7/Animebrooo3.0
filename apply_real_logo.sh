#!/bin/bash
SRC="25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b.png"

echo "Generating Adaptive Foregrounds..."
convert "$SRC" -resize 108x108 app/src/main/res/drawable-mdpi/anime_logo_foreground.png
convert "$SRC" -resize 162x162 app/src/main/res/drawable-hdpi/anime_logo_foreground.png
convert "$SRC" -resize 216x216 app/src/main/res/drawable-xhdpi/anime_logo_foreground.png
convert "$SRC" -resize 324x324 app/src/main/res/drawable-xxhdpi/anime_logo_foreground.png
convert "$SRC" -resize 432x432 app/src/main/res/drawable-xxxhdpi/anime_logo_foreground.png

echo "Generating Legacy Icons..."
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

echo "Done applying real logo"
