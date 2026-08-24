#!/bin/bash
cat << 'INNER_EOF' > app/src/main/res/mipmap-anydpi-v26/anime_logo.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:color="@color/ic_launcher_background"/>
    <foreground android:drawable="@drawable/anime_logo_foreground"/>
</adaptive-icon>
INNER_EOF

cp app/src/main/res/mipmap-anydpi-v26/anime_logo.xml app/src/main/res/mipmap-anydpi-v26/anime_logo_round.xml

# Make sure foreground is in nodpi to prevent any scaling issues
mkdir -p app/src/main/res/drawable-nodpi
mv app/src/main/res/drawable-xxxhdpi/anime_logo_foreground.png app/src/main/res/drawable-nodpi/anime_logo_foreground.png

echo "Done"
