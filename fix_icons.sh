#!/bin/bash
# Clean up old icons
find app/src/main/res -name "app_icon*" -delete
find app/src/main/res -name "anime_icon*" -delete
find app/src/main/res -name "my_custom*" -delete
find app/src/main/res -name "custom_anime*" -delete

# Source image
IMG="file_00000000d6b08211afa0219b69ef51cf-1.png"

# Resize for adaptive foreground (drawable-anydpi-v26)
mkdir -p app/src/main/res/drawable
convert "$IMG" -resize 432x432 app/src/main/res/drawable/ic_launcher_foreground.png

# Background color
cat << 'INNER_EOF' > app/src/main/res/drawable/ic_launcher_background.xml
<?xml version="1.0" encoding="utf-8"?>
<color xmlns:android="http://schemas.android.com/apk/res/android" name="ic_launcher_background">#000000</color>
INNER_EOF

# Adaptive Icon XMLs
mkdir -p app/src/main/res/mipmap-anydpi-v26
cat << 'INNER_EOF' > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:color="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
INNER_EOF

cp app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml

# Legacy icons
mkdir -p app/src/main/res/mipmap-xxxhdpi
convert "$IMG" -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
cp app/src/main/res/mipmap-xxxhdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

mkdir -p app/src/main/res/mipmap-xxhdpi
convert "$IMG" -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
cp app/src/main/res/mipmap-xxhdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png

mkdir -p app/src/main/res/mipmap-xhdpi
convert "$IMG" -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
cp app/src/main/res/mipmap-xhdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher_round.png

mkdir -p app/src/main/res/mipmap-hdpi
convert "$IMG" -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-hdpi/ic_launcher_round.png

mkdir -p app/src/main/res/mipmap-mdpi
convert "$IMG" -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
cp app/src/main/res/mipmap-mdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png

echo "Done"
