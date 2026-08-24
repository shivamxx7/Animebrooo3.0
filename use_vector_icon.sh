#!/bin/bash
set -e

echo "1. Deleting all potentially corrupted mipmap PNGs..."
rm -f app/src/main/res/mipmap-*/*.png
rm -f app/src/main/res/drawable/animebro_logo.png
rm -f app/src/main/res/drawable/app_logo.png
# Leave the vector drawable intact

echo "2. Updating adaptive icon XMLs to use the vector foreground..."
mkdir -p app/src/main/res/mipmap-anydpi-v26

cat << 'XML' > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_vector"/>
</adaptive-icon>
XML

cp app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml

echo "3. Setting background color to black..."
# Create or update colors.xml
mkdir -p app/src/main/res/values
cat << 'XML' > app/src/main/res/values/colors.xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#000000</color>
</resources>
XML

echo "4. Ensuring AndroidManifest points to mipmap..."
sed -i 's/@drawable\/animebro_logo/@mipmap\/ic_launcher/g' app/src/main/AndroidManifest.xml
sed -i 's/@drawable\/app_logo/@mipmap\/ic_launcher/g' app/src/main/AndroidManifest.xml

echo "Done."
