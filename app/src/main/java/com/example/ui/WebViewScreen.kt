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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.CircularProgressIndicator
import android.webkit.WebSettings
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.WebsiteRepository
import java.io.ByteArrayInputStream
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally

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
    var isControlsVisible by remember { mutableStateOf(true) }

    val adDomains = listOf(
        "doubleclick.net", "googleadservices.com", "adsafeprotected.com", "popads.net",
        "adsterra.com", "onclickads.net", "exoclick.com", "hilltopads.net", "propellerads.com"
    )

    val isAnimeSite = remember(url) {
        WebsiteRepository.animeCategories.values.flatten().any { website ->
            val domain = WebsiteRepository.getDomain(website.url)
            domain.isNotEmpty() && url.contains(domain)
        }
    }

    BackHandler {
        if (customView != null) {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.let {
                val windowInsetsController = WindowCompat.getInsetsController(it.window, it.window.decorView)
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
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
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    settings.allowFileAccess = true
                    settings.allowContentAccess = true
                    settings.setSupportMultipleWindows(true)
                    
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        WebView.setWebContentsDebuggingEnabled(true)
                    }
                    
                    if (!isAnimeSite) {
                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 10; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/110.0.0.0 Mobile Safari/537.36"
                    }
                    
                    android.webkit.CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

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
                            
                            // Block intent:// and other external schemes
                            if (!requestedUrl.startsWith("http")) {
                                return true
                            }
                            
                            val requestedHost = request?.url?.host ?: return true

                            if (request.isForMainFrame) {
                                val currentHost = try { java.net.URI(view?.url ?: "").host?.removePrefix("www.") ?: "" } catch(e: Exception) { "" }
                                val requestedDomain = requestedHost.removePrefix("www.")
                                
                                val isAllowed = WebsiteRepository.allowedDomains.any { requestedDomain.contains(it) } || 
                                                (currentHost.isNotEmpty() && requestedDomain.contains(currentHost)) ||
                                                requestedDomain.contains("1flex.org") ||
                                                requestedDomain.contains("skyflixer.fun")
                                                
                                if (!isAllowed) {
                                    return true // Block main frame navigation to unverified 3rd-party domains
                                }
                            }
                            
                            return adDomains.any { requestedUrl.contains(it) }
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
                            
                            val js = """
                                javascript:(function() {
                                    if (window.location.href.indexOf('animeflix') !== -1) {
                                        var style = document.createElement('style');
                                        style.innerHTML = 'html, body, #___gatsby, #gatsby-focus-wrapper { height: auto !important; min-height: 100vh !important; overflow: visible !important; overflow-y: auto !important; }';
                                        document.head.appendChild(style);
                                    }
                                    if (!window.__searchFixApplied) {
                                        window.__searchFixApplied = true;
                                        document.addEventListener('keydown', function(e) {
                                            if (e.key === 'Enter' || e.keyCode === 13 || e.keyCode === 229) {
                                                var el = document.activeElement;
                                                if (el && el.tagName === 'INPUT') {
                                                    var form = el.closest('form');
                                                    if (form) {
                                                        var btn = form.querySelector('button[type="submit"]');
                                                        if (btn) { btn.click(); } else { form.submit(); }
                                                    } else {
                                                        // Fallback: try to find a nearby button
                                                        var btn = el.parentElement.querySelector('button');
                                                        if (btn) btn.click();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                })()
                            """.trimIndent()
                            
                            view?.evaluateJavascript(js, null)
                        }
                    }

                    webChromeClient = object : WebChromeClient() {

                        override fun onCreateWindow(
                            view: WebView?,
                            isDialog: Boolean,
                            isUserGesture: Boolean,
                            resultMsg: android.os.Message?
                        ): Boolean {
                            if (view != null && resultMsg != null) {
                                val dummyWebView = WebView(view.context)
                                dummyWebView.webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        return true // Cancel all loading inside dummy WebView
                                    }
                                }
                                val transport = resultMsg.obj as WebView.WebViewTransport
                                transport.webView = dummyWebView
                                resultMsg.sendToTarget()
                                return true
                            }
                            return false
                        }

                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress >= 70) {
                                isLoading = false
                            }
                        }

                        override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                            super.onShowCustomView(view, callback)
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            activity?.let {
                                val windowInsetsController = WindowCompat.getInsetsController(it.window, it.window.decorView)
                                windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
                            }
                            customView = view
                            customViewCallback = callback
                        }

                        override fun onHideCustomView() {
                            super.onHideCustomView()
                            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            activity?.let {
                                val windowInsetsController = WindowCompat.getInsetsController(it.window, it.window.decorView)
                                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                            }
                            customView = null
                            customViewCallback = null
                        }
                    }

                    loadUrl(url)
                }
            },
            modifier = Modifier.fillMaxSize().alpha(if (customView != null) 0f else 1f)
        )

        if (customView == null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                AnimatedVisibility(
                    visible = isControlsVisible,
                    enter = slideInHorizontally { -it } + expandHorizontally() + fadeIn(),
                    exit = slideOutHorizontally { -it } + shrinkHorizontally() + fadeOut()
                ) {
                    IconButton(
                        onClick = {
                            if (webView?.canGoBack() == true) {
                                webView?.goBack()
                            } else {
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Refresh and Home Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = isControlsVisible,
                        enter = slideInHorizontally { it } + expandHorizontally() + fadeIn(),
                        exit = slideOutHorizontally { it } + shrinkHorizontally() + fadeOut()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            IconButton(
                                onClick = { webView?.reload() },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Refresh",
                                    tint = Color.White
                                )
                            }
                            
                            IconButton(
                                onClick = {
                                    activity?.let {
                                        it.requestedOrientation = if (it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        } else {
                                            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Rotate Screen",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { onBack() },
                                modifier = Modifier
                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Home",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { isControlsVisible = !isControlsVisible },
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isControlsVisible) Icons.Default.KeyboardArrowRight else Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Toggle Controls",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        if (isLoading) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFF6D00))
            }
        }
        if (customView != null) {
            androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
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
                
                IconButton(
                    onClick = {
                        activity?.let {
                            it.requestedOrientation = if (it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                            } else {
                                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(24.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rotate Screen",
                        tint = Color.White
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity?.let {
                val windowInsetsController = WindowCompat.getInsetsController(it.window, it.window.decorView)
                windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
            }
            webView?.let {
                // Safely remove the WebView from its parent before destroying
                (it.parent as? ViewGroup)?.removeView(it)
                it.stopLoading()
                it.clearHistory()
                it.removeAllViews()
                it.destroy()
            }
        }
    }
}
