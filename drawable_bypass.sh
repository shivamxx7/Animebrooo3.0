#!/bin/bash
set -e

SRC="25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b-1.png"

echo "1. CREATING DRAWABLE FOLDER AND COPYING LOGO..."
mkdir -p app/src/main/res/drawable
# Using convert to ensure it is a clean, resized 512x512 PNG, or just simply copy it.
# Simple copy is safest to preserve exact original image:
cp "$SRC" app/src/main/res/drawable/app_logo.png

echo "2. DELETING MIPMAP FOLDERS TO PREVENT ANY CACHE FALLBACK..."
rm -rf app/src/main/res/mipmap-*

echo "Done."
