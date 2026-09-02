import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

# Add import for SocialWebViewScreen
if "import com.example.ui.SocialWebViewScreen" not in content:
    content = content.replace(
        "import com.example.ui.WebViewScreen",
        "import com.example.ui.WebViewScreen\nimport com.example.ui.SocialWebViewScreen"
    )

# Add route for social_webview/{url}
route_pattern = re.compile(
    r'composable\("webview/\{url\}"\) \{ backStackEntry ->\n.*?WebViewScreen\(\n.*?url = url,\n.*?onBack = \{ navController.popBackStack\(\) \}\n.*?\}\n.*?\n.*\}',
    re.DOTALL
)

social_route = """composable("webview/{url}") { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val url = try {
                String(Base64.decode(encodedUrl, Base64.URL_SAFE or Base64.NO_WRAP), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                ""
            }
            if (url.isNotEmpty()) {
                WebViewScreen(
                    url = url,
                    onBack = { navController.popBackStack() }
                )
            }
        }
        composable("social_webview/{url}") { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url") ?: ""
            val url = try {
                String(Base64.decode(encodedUrl, Base64.URL_SAFE or Base64.NO_WRAP), StandardCharsets.UTF_8)
            } catch (e: Exception) {
                ""
            }
            if (url.isNotEmpty()) {
                SocialWebViewScreen(
                    url = url,
                    onBack = { navController.popBackStack() }
                )
            }
        }"""

new_content = route_pattern.sub(social_route, content)

# Check if HomeScreen is taking a new callback, no we can just use the existing setup, but add onSocialLinkClick
# Wait, HomeScreen doesn't take onSocialLinkClick yet. Let's add it.

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(new_content)
