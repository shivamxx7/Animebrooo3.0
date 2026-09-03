import re

with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "r") as f:
    content = f.read()

old_load_logic = "loadUrl(url)"

new_load_logic = """                    var loadedInApp = false
                    if (url.contains("instagram.com")) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            intent.setPackage("com.instagram.android")
                            context.startActivity(intent)
                            loadedInApp = true
                            // Go back to previous screen since we opened external app
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                onBack()
                            }, 500)
                        } catch (e: Exception) {
                            loadedInApp = false
                        }
                    }
                    
                    if (!loadedInApp) {
                        loadUrl(url)
                    }"""

content = content.replace(old_load_logic, new_load_logic)

with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "w") as f:
    f.write(content)
