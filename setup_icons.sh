#!/bin/bash
mkdir -p app/src/main/res/mipmap-anydpi-v26
mkdir -p app/src/main/res/drawable

# Create solid black background drawable
cat << 'INNER_EOF' > app/src/main/res/drawable/ic_launcher_background.xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android" android:shape="rectangle">
    <solid android:color="#000000"/>
</shape>
INNER_EOF

# Create adaptive icon for standard launcher
cat << 'INNER_EOF' > app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
INNER_EOF

# Create adaptive icon for round launcher
cat << 'INNER_EOF' > app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
INNER_EOF

# Ensure foreground and legacy icons are properly set in all densities
img="file_00000000d6b08211afa0219b69ef51cf.png"
for dir in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
    mkdir -p app/src/main/res/mipmap-$dir
    cp $img app/src/main/res/mipmap-$dir/ic_launcher_foreground.png
    cp $img app/src/main/res/mipmap-$dir/ic_launcher.png
    cp $img app/src/main/res/mipmap-$dir/ic_launcher_round.png
done

echo "Icons configured properly."
