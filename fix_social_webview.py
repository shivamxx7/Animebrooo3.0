with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "r") as f:
    content = f.read()

import re

old_intent_logic = """                            try {
                                val intent = Intent.parseUri(requestUrl, Intent.URI_INTENT_SCHEME)
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    // If app not installed, try to open market or fallback URL
                                    val fallbackUrl = intent.getStringExtra("browser_fallback_url")
                                    if (fallbackUrl != null) {
                                        view?.loadUrl(fallbackUrl)
                                    } else {
                                        val marketIntent = Intent(Intent.ACTION_VIEW).apply {
                                            data = Uri.parse("market://details?id=" + intent.`package`)
                                        }
                                        if (marketIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(marketIntent)
                                        }
                                    }
                                }
                                return true
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }"""

new_intent_logic = """                            try {
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

content = content.replace(old_intent_logic, new_intent_logic)

with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "w") as f:
    f.write(content)
