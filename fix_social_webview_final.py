with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "r") as f:
    content = f.read()

old_logic = """                            try {
                                if (requestUrl.startsWith("intent://")) {
                                    val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    } else {
                                        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                        if (fallbackUrl != null) {
                                            view?.loadUrl(fallbackUrl)
                                        }
                                    }
                                } else {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                    context.startActivity(intent)
                                }
                                return true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }"""

new_logic = """                            try {
                                if (requestUrl.startsWith("intent://")) {
                                    val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: android.content.ActivityNotFoundException) {
                                        val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                        if (fallbackUrl != null) {
                                            view?.loadUrl(fallbackUrl)
                                        }
                                    }
                                } else {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                    try {
                                        context.startActivity(intent)
                                    } catch (e: android.content.ActivityNotFoundException) {
                                        // App not found
                                    }
                                }
                                return true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }"""

content = content.replace(old_logic, new_logic)

with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "w") as f:
    f.write(content)
