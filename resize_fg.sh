#!/bin/bash
IMG="app/src/main/res/drawable-xxxhdpi/anime_logo_foreground.png"

convert "$IMG" -resize 108x108 app/src/main/res/drawable-mdpi/anime_logo_foreground.png
convert "$IMG" -resize 162x162 app/src/main/res/drawable-hdpi/anime_logo_foreground.png
convert "$IMG" -resize 216x216 app/src/main/res/drawable-xhdpi/anime_logo_foreground.png
convert "$IMG" -resize 324x324 app/src/main/res/drawable-xxhdpi/anime_logo_foreground.png

echo "Done"
