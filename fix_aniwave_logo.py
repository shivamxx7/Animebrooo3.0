import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

target = """            val isAniwave = website.name.equals("Aniwave", ignoreCase = true)
            if (isAniwave) {
                AsyncImage(
                    model = R.drawable.logo_aniwave,
                    contentDescription = website.name,
                    modifier = Modifier.size(76.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {"""

replacement = """            val isAniwave = website.name.equals("Aniwave", ignoreCase = true)
            if (isAniwave) {
                Image(
                    painter = painterResource(id = R.drawable.logo_aniwave),
                    contentDescription = website.name,
                    modifier = Modifier.size(76.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {"""

if target in content:
    content = content.replace(target, replacement)
    print("Replaced logo logic successfully.")
else:
    print("Target not found! Check the source code.")

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(content)
