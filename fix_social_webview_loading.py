import re

with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "r") as f:
    content = f.read()

# Make sure imports are added
imports_to_add = """
import android.graphics.Bitmap
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
"""

# replace first import block with imports_to_add + first import block
content = content.replace("import android.content.Intent", imports_to_add + "import android.content.Intent")

# add isLoading state
content = content.replace(
    "var webView: WebView? by remember { mutableStateOf(null) }",
    "var webView: WebView? by remember { mutableStateOf(null) }\n    var isLoading by remember { mutableStateOf(true) }"
)

# update WebViewClient
old_client = """                    webViewClient = object : WebViewClient() {"""

new_client = """                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }
"""
content = content.replace(old_client, new_client)

# update Box content to add loader
old_box = """        AndroidView(
            modifier = Modifier.fillMaxSize(),"""

new_box = """        AndroidView(
            modifier = Modifier.fillMaxSize(),"""

content = content.replace(old_box, new_box)

old_end = """        )
    }
}"""

new_end = """        )
        
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFFFF6D00),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}"""

content = content.replace(old_end, new_end)

with open("app/src/main/java/com/example/ui/SocialWebViewScreen.kt", "w") as f:
    f.write(content)
