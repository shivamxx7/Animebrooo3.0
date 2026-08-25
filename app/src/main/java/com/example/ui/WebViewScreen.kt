package com.example.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CircularProgressIndicator
import android.webkit.WebSettings
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.WebsiteRepository
import java.io.ByteArrayInputStream

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(url: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context.findActivity()

    var webView: WebView? by remember { mutableStateOf(null) }
    var customView: View? by remember { mutableStateOf(null) }
    var isLoading by remember { mutableStateOf(true) }
    var customViewCallback: WebChromeClient.CustomViewCallback? by remember { mutableStateOf(null) }

    val adDomains = listOf(
        "doubleclick.net", "googleadservices.com", "adsafeprotected.com", "popads.net"
    )

    BackHandler {
        if (customView != null) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            customViewCallback?.onCustomViewHidden()
            customView = null
            customViewCallback = null
        } else if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBack()
        }
    }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    webView = this
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.databaseEnabled = true
                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    settings.mediaPlaybackRequiresUserGesture = false
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                            super.onPageStarted(view, url, favicon)
                            isLoading = true
                        }

                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val requestedUrl = request?.url.toString()
                            val requestedHost = request?.url?.host ?: return true
                            
                            val isAllowed = WebsiteRepository.allowedDomains.any { allowed ->
                                requestedHost.contains(allowed.removePrefix("www."))
                            }
                            return !isAllowed
                        }

                        override fun shouldInterceptRequest(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): WebResourceResponse? {
                            val requestedUrl = request?.url.toString()
                            val isAd = adDomains.any { requestedUrl.contains(it) }
                            
                            if (isAd) {
                                return WebResourceResponse(
                                    "text/plain",
                                    "UTF-8",
                                    ByteArrayInputStream("".toByteArray())
                                )
                            }
                            return super.shouldInterceptRequest(view, request)
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            view?.evaluateJavascript(
                                "javascript:(function() { " +
                                "var elements = document.querySelectorAll('.ad-container, popup'); " +
                                "for (var i = 0; i < elements.length; i++) { " +
                                "elements[i].style.display = 'none'; " +
                                "} })()", null
                            )
                        }
                    }

                    webChromeClient = object : WebChromeClient() {
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress >= 70) {
                                isLoading = false
                            }
                        }

                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            super.onShowCustomView(view, callback)
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            customView = view
                            customViewCallback = callback
                        }

                        override fun onHideCustomView() {
                            super.onHideCustomView()
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            customView = null
                            customViewCallback = null
                        }
                    }

                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (isLoading) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF6D00))
            }
        }
        if (customView != null) {
            AndroidView(
                factory = { context ->
                    FrameLayout(context).apply {
                        setBackgroundColor(android.graphics.Color.BLACK)
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        addView(customView)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            webView?.destroy()
        }
    }
}
