package com.example.ui


import android.graphics.Bitmap
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
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
    var isLoading by remember { mutableStateOf(true) }
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
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                        }

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
                            }
                            
                            return false
                        }
                    }
                    
                                        var loadedInApp = false
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
                    }
                    webView = this
                }
            }
        )
        
        if (isLoading) {
            CircularProgressIndicator(
                color = Color(0xFFFF6D00),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
