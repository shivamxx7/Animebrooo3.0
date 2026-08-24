#!/bin/bash
# Fix foreground image density
mkdir -p app/src/main/res/drawable-xxxhdpi
mv app/src/main/res/drawable/anime_logo_foreground.png app/src/main/res/drawable-xxxhdpi/anime_logo_foreground.png

# Fix background tag in adaptive icons
sed -i 's/android:color/android:drawable/g' app/src/main/res/mipmap-anydpi-v26/anime_logo.xml
sed -i 's/android:color/android:drawable/g' app/src/main/res/mipmap-anydpi-v26/anime_logo_round.xml

echo "Done"
