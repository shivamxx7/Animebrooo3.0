import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

content = content.replace(
    "fun HomeScreen(onWebsiteClick: (String) -> Unit) {",
    "fun HomeScreen(onWebsiteClick: (String) -> Unit, onSocialLinkClick: (String) -> Unit = {}) {"
)

modal_pattern = re.compile(
    r"SocialModal\(\n\s*onClose = \{ showSocialModal = false \},\n\s*onLinkClick = \{ link ->\n\s*val intent = android.content.Intent\(android.content.Intent.ACTION_VIEW, android.net.Uri.parse\(link\)\)\n\s*context.startActivity\(intent\)\n\s*showSocialModal = false\n\s*\}\n\s*\)",
    re.DOTALL
)

new_modal = """SocialModal(
            onClose = { showSocialModal = false },
            onLinkClick = { link ->
                onSocialLinkClick(link)
                showSocialModal = false
            }
        )"""

content = modal_pattern.sub(new_modal, content)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(content)
