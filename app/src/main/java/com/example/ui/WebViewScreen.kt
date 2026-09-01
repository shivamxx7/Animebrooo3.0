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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.material3.CircularProgressIndicator
import android.webkit.WebSettings
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.WebsiteRepository
import java.io.ByteArrayInputStream
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import android.media.AudioManager

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect

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
    var isLocked by remember { mutableStateOf(false) }
    var showSettingsFab by remember { mutableStateOf(false) }
    var showControlPanel by remember { mutableStateOf(false) }
    var fabTimer by remember { mutableIntStateOf(0) }

    LaunchedEffect(showSettingsFab, fabTimer, showControlPanel) {
        if (showSettingsFab && !showControlPanel) {
            delay(3000)
            showSettingsFab = false
        }
    }

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

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent(androidx.compose.ui.input.pointer.PointerEventPass.Initial)
                        if (event.changes.any { it.pressed && !it.previousPressed }) {
                            showSettingsFab = true
                            fabTimer++
                        }
                    }
                }
            }
    ) {
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
                                                requestedDomain.contains("1shows.org") ||
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
                                    if (!window.__targetBlankFix) {
                                        window.__targetBlankFix = setInterval(function() {
                                            document.querySelectorAll('a[target="_blank"]').forEach(function(a) {
                                                if (a.href && a.href.includes(window.location.hostname)) {
                                                    a.removeAttribute('target');
                                                }
                                            });
                                        }, 500);
                                    }
                                    
                                    if (window.location.href.indexOf('animeflix') !== -1) {
                                        if (!window.__afFixInterval) {
                                            window.__afFixInterval = setInterval(function() {
                                                var existingStyle = document.getElementById('af-fix');
                                                if (!existingStyle) {
                                                    var style = document.createElement('style');
                                                    style.id = 'af-fix';
                                                    style.innerHTML = 'html, body { overflow-y: auto !important; overflow-x: hidden !important; }';
                                                    document.head.appendChild(style);
                                                }
                                            }, 500);
                                        }
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
                                    override fun shouldOverrideUrlLoading(v: WebView?, request: WebResourceRequest?): Boolean {
                                        val requestedUrl = request?.url.toString()
                                        val requestedHost = request?.url?.host ?: ""
                                        
                                        val isAd = adDomains.any { requestedUrl.contains(it) } || requestedUrl.contains("youtube.com")
                                        val isAllowedDomain = WebsiteRepository.allowedDomains.any { requestedHost.contains(it) } ||
                                                requestedHost.contains("1shows.org") ||
                                                requestedHost.contains("skyflixer.fun")
                                                
                                        // Allow same-domain or explicitly whitelisted domains to open in main view
                                        val currentHost = try { java.net.URI(view.url ?: "").host?.removePrefix("www.") ?: "" } catch(e: Exception) { "" }
                                        val isSameDomain = currentHost.isNotEmpty() && requestedHost.contains(currentHost)
                                        
                                        if (!isAd && (isAllowedDomain || isSameDomain)) {
                                            view.loadUrl(requestedUrl)
                                        }
                                        return true // Cancel loading in the dummy WebView
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
                            customView?.layoutParams = FrameLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                android.view.Gravity.CENTER
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

        // --- NEW CONTROL SYSTEM OVERLAYS ---
        if (isLocked) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            showSettingsFab = true
                            fabTimer++
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ -> change.consume() }
                    }
            )
        }

        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = showSettingsFab && !showControlPanel,
                enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 48.dp)
            ) {
                IconButton(
                    onClick = {
                        if (isLocked) {
                            isLocked = false
                            showSettingsFab = true
                        } else {
                            showControlPanel = true
                            showSettingsFab = false
                        }
                    },
                    modifier = Modifier
                        .background(Color(0xFF222222).copy(alpha = 0.9f), CircleShape)
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = if (isLocked) Icons.Default.Lock else Icons.Default.Settings,
                        contentDescription = if (isLocked) "Unlock" else "Settings",
                        tint = Color.White
                    )
                }
            }

            AnimatedVisibility(
                visible = showControlPanel,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            showControlPanel = false
                            showSettingsFab = true
                            fabTimer++
                        },
                    contentAlignment = Alignment.Center
                ) {
                    ControlPanelContent(
                        onLock = {
                            isLocked = true
                            showControlPanel = false
                            showSettingsFab = true
                            fabTimer++
                        },
                        onRotate = {
                            activity?.let {
                                it.requestedOrientation = if (it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                } else {
                                    ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                }
                            }
                        },
                        onBack = {
                            showControlPanel = false
                            if (customView != null) {
                                customViewCallback?.onCustomViewHidden()
                                customView = null
                                customViewCallback = null
                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                activity?.let { a ->
                                    val windowInsetsController = WindowCompat.getInsetsController(a.window, a.window.decorView)
                                    windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
                                }
                            } else if (webView?.canGoBack() == true) {
                                webView?.goBack()
                            } else {
                                onBack()
                            }
                        },
                        onHome = {
                            showControlPanel = false
                            onBack()
                        },
                        context = context,
                        activity = activity
                    )
                }
            }
        }
        // --- END NEW CONTROL SYSTEM ---

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

@Composable
fun ControlPanelContent(
    onLock: () -> Unit,
    onRotate: () -> Unit,
    onBack: () -> Unit,
    onHome: () -> Unit,
    context: Context,
    activity: Activity?
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF161616).copy(alpha = 0.95f)),
        modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {}
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ControlActionRow(icon = Icons.Default.WbSunny, text = "Brightness", onClick = null)
                ControlActionRow(icon = Icons.Default.VolumeUp, text = "Volume", onClick = null)
                ControlActionRow(icon = Icons.Default.Lock, text = "Lock", onClick = onLock)
                ControlActionRow(icon = Icons.Default.ScreenRotation, text = "Rotate", onClick = onRotate)
                ControlActionRow(icon = Icons.Default.ArrowBack, text = "Back", onClick = onBack)
                ControlActionRow(icon = Icons.Default.Home, text = "Home", onClick = onHome)
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                var brightness by remember { mutableStateOf(activity?.window?.attributes?.screenBrightness?.takeIf { it >= 0f } ?: 0.5f) }
                CustomVerticalSlider(
                    value = brightness,
                    onValueChange = {
                        brightness = it
                        val layoutParams = activity?.window?.attributes
                        layoutParams?.screenBrightness = it
                        activity?.window?.attributes = layoutParams
                    },
                    activeColor = Color(0xFFFFB300),
                    icon = Icons.Default.WbSunny
                )

                val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
                val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat() }
                var volume by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume) }
                CustomVerticalSlider(
                    value = volume,
                    onValueChange = {
                        volume = it
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (it * maxVolume).toInt(), 0)
                    },
                    activeColor = Color(0xFF2979FF),
                    icon = Icons.Default.VolumeUp
                )
            }
        }
    }
}

@Composable
fun ControlActionRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, onClick: (() -> Unit)?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .size(48.dp)
                .background(if (onClick != null) Color(0xFF2A2A2A) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = text, tint = Color.White, modifier = Modifier.size(24.dp))
        }
        Text(text = text, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun CustomVerticalSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    var sliderHeight by remember { mutableIntStateOf(0) }
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .width(48.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF2A2A2A))
            .onSizeChanged { sliderHeight = it.height }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val deltaY = change.position.y
                    if (sliderHeight > 0) {
                        val newValue = 1f - (deltaY / sliderHeight).coerceIn(0f, 1f)
                        onValueChange(newValue)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    if (sliderHeight > 0) {
                        val newValue = 1f - (offset.y / sliderHeight).coerceIn(0f, 1f)
                        onValueChange(newValue)
                    }
                }
            }
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(value)
                .background(activeColor)
        )
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
                .size(24.dp)
        )
    }
}
