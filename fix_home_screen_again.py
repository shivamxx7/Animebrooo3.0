import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

# Fix SidebarOverlay signature
content = content.replace(
    "fun SidebarOverlay(isOpen: Boolean, onClose: () -> Unit, onWebsiteClick: (String) -> Unit) {",
    "fun SidebarOverlay(isOpen: Boolean, onClose: () -> Unit, onWebsiteClick: (String) -> Unit, onSocialLinkClick: (String) -> Unit = {}) {"
)

# Fix SidebarOverlay call in HomeScreen
content = content.replace(
    "SidebarOverlay(\n            isOpen = isSidebarOpen,",
    "SidebarOverlay(\n            isOpen = isSidebarOpen,\n            onSocialLinkClick = onSocialLinkClick,"
)

# And if there's a space/newline issue, let's use regex
call_pattern = re.compile(r"SidebarOverlay\(\s*isOpen = isSidebarOpen,\s*onClose = \{ isSidebarOpen = false \},\s*onWebsiteClick = onWebsiteClick\s*\)")

new_call = """SidebarOverlay(
            isOpen = isSidebarOpen,
            onClose = { isSidebarOpen = false },
            onWebsiteClick = onWebsiteClick,
            onSocialLinkClick = onSocialLinkClick
        )"""

content = call_pattern.sub(new_call, content)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(content)
