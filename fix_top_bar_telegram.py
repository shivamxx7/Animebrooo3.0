import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

pattern = re.compile(
    r"\.clickable \{\s*val intent = android\.content\.Intent\(android\.content\.Intent\.ACTION_VIEW, android\.net\.Uri\.parse\(\"https://t\.me/animebroig\"\)\)\s*context\.startActivity\(intent\)\s*\}"
)

replacement = '.clickable { onSocialLinkClick("https://t.me/animebroig") }'

new_content = pattern.sub(replacement, content)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(new_content)
