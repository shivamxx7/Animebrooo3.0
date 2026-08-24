#!/bin/bash
# Rename everything to anime_logo
cd app/src/main/res

# Rename mipmap PNGs
for dir in mipmap-*; do
  if [ -f "$dir/ic_launcher.png" ]; then
    mv "$dir/ic_launcher.png" "$dir/anime_logo.png"
  fi
  if [ -f "$dir/ic_launcher_round.png" ]; then
    mv "$dir/ic_launcher_round.png" "$dir/anime_logo_round.png"
  fi
done

# Rename XMLs
if [ -f "mipmap-anydpi-v26/ic_launcher.xml" ]; then
  mv "mipmap-anydpi-v26/ic_launcher.xml" "mipmap-anydpi-v26/anime_logo.xml"
fi
if [ -f "mipmap-anydpi-v26/ic_launcher_round.xml" ]; then
  mv "mipmap-anydpi-v26/ic_launcher_round.xml" "mipmap-anydpi-v26/anime_logo_round.xml"
fi

# Rename drawable
if [ -f "drawable/ic_launcher_foreground.png" ]; then
  mv "drawable/ic_launcher_foreground.png" "drawable/anime_logo_foreground.png"
fi

# Update references in XMLs
sed -i 's/ic_launcher_foreground/anime_logo_foreground/g' mipmap-anydpi-v26/anime_logo.xml mipmap-anydpi-v26/anime_logo_round.xml

# Go back to root
cd ../../../../
sed -i 's/@mipmap\/ic_launcher/@mipmap\/anime_logo/g' app/src/main/AndroidManifest.xml
echo "Done"
