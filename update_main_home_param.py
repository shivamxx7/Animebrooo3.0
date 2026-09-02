import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    content = f.read()

content = content.replace(
"""            HomeScreen(
                onWebsiteClick = { url ->
                    val encodedUrl = Base64.encodeToString(url.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
                    navController.navigate("webview/$encodedUrl")
                }
            )""",
"""            HomeScreen(
                onWebsiteClick = { url ->
                    val encodedUrl = Base64.encodeToString(url.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
                    navController.navigate("webview/$encodedUrl")
                },
                onSocialLinkClick = { url ->
                    val encodedUrl = Base64.encodeToString(url.toByteArray(StandardCharsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
                    navController.navigate("social_webview/$encodedUrl")
                }
            )""")

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(content)
