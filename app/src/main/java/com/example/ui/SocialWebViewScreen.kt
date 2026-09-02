package com.example.ui

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun SocialWebViewScreen(url: String, onBack: () -> Unit) {
    var webView: WebView? by remember { mutableStateOf(null) }
    val context = LocalContext.current

    BackHandler {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBack()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    
                    // Added user agent mimicking mobile browser for better compatibility
                    settings.userAgentString = settings.userAgentString.replace("; wv", "")

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val requestUrl = request?.url?.toString() ?: return false
                            
                            // If it's a standard web URL, load it in this WebView
                            if (requestUrl.startsWith("http://") || requestUrl.startsWith("https://")) {
                                return false 
                            }
                            
                            // Otherwise, it's a custom scheme like tg:// or intent://
                            try {
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
                            }
                            
                            return false
                        }
                    }
                    
                    loadUrl(url)
                    webView = this
                }
            }
        )
    }
}
