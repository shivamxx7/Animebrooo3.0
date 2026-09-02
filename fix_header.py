import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

# Update HeaderSection definition
content = content.replace(
    "fun HeaderSection(onWebsiteClick: (String) -> Unit, onMenuClick: () -> Unit) {",
    "fun HeaderSection(onWebsiteClick: (String) -> Unit, onMenuClick: () -> Unit, onSocialLinkClick: (String) -> Unit = {}) {"
)

# Update HeaderSection call
content = content.replace(
    "HeaderSection(onWebsiteClick = onWebsiteClick, onMenuClick = { isSidebarOpen = true })",
    "HeaderSection(onWebsiteClick = onWebsiteClick, onMenuClick = { isSidebarOpen = true }, onSocialLinkClick = onSocialLinkClick)"
)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(content)
