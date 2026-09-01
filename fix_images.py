import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

# Replace painterResource for logo_aniwave
content = re.sub(
    r'Image\(\s*painter\s*=\s*painterResource\(id\s*=\s*R\.drawable\.logo_aniwave\),',
    r'AsyncImage(\n                    model = R.drawable.logo_aniwave,',
    content
)

# Replace painterResource for img_rem
content = re.sub(
    r'Image\(\s*painter\s*=\s*painterResource\(id\s*=\s*R\.drawable\.img_rem\),',
    r'AsyncImage(\n                    model = R.drawable.img_rem,',
    content
)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(content)

print("Fixed Image -> AsyncImage")
