package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.material.icons.filled.Clear
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.R
import coil.compose.AsyncImage
import com.example.data.WebsiteModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import com.example.data.WebsiteRepository

@Composable
fun HomeScreen(onWebsiteClick: (String) -> Unit) {
    var selectedTabIndex by androidx.compose.runtime.saveable.rememberSaveable { mutableIntStateOf(0) }
    
    var expandedCategory by remember { androidx.compose.runtime.mutableStateOf<String?>(null) }
    var expandedWebsites by remember { androidx.compose.runtime.mutableStateOf<List<WebsiteModel>?>(null) }
    var originBounds by remember { androidx.compose.runtime.mutableStateOf<androidx.compose.ui.geometry.Rect?>(null) }
    var isSidebarOpen by remember { androidx.compose.runtime.mutableStateOf(false) }

    val currentCategories = when (selectedTabIndex) {
        0 -> WebsiteRepository.animeCategories
        1 -> WebsiteRepository.ottCategories
        else -> emptyMap()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection(onWebsiteClick = onWebsiteClick, onMenuClick = { isSidebarOpen = true })
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
            ) {
                items(
                    items = currentCategories.entries.toList(),
                    key = { it.key }
                ) { (categoryName, websites) ->
                    CategoryRow(
                        categoryName = categoryName, 
                        websites = websites, 
                        onWebsiteClick = onWebsiteClick,
                        onViewAllClick = { bounds ->
                            expandedCategory = categoryName
                            expandedWebsites = websites
                            originBounds = bounds
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        BottomNavigationBar(
            selectedIndex = selectedTabIndex,
            onIndexSelected = { 
                if (it == 2) {
                    onWebsiteClick("https://www.1shows.org/")
                } else {
                    selectedTabIndex = it 
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
        
        ViewAllModal(
            isOpen = expandedCategory != null,
            categoryName = expandedCategory,
            websites = expandedWebsites,
            originBounds = originBounds,
            onClose = {
                expandedCategory = null
            },
            onWebsiteClick = onWebsiteClick
        )
        
        SidebarOverlay(isOpen = isSidebarOpen, onClose = { isSidebarOpen = false }, onWebsiteClick = onWebsiteClick)
    }
}

@Composable
fun HeaderSection(onWebsiteClick: (String) -> Unit, onMenuClick: () -> Unit) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clickable { onMenuClick() }
                .background(Color(0xFF161618), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "An",
                color = Color(0xFFFF6D00),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "ı",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .offset(y = (-8).dp)
                        .size(5.dp)
                        .background(Color(0xFFFF6D00), CircleShape)
                )
            }
            Text(
                text = "meBro",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .clickable { 
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse("https://t.me/animebroig"))
                    context.startActivity(intent)
                }
                .background(Color(0xFF161618))
                .border(1.dp, Color(0xFF27272A), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_telegram),
                contentDescription = "Telegram",
                tint = androidx.compose.ui.graphics.Color.Unspecified,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun CategoryRow(
    categoryName: String, 
    websites: List<WebsiteModel>, 
    onWebsiteClick: (String) -> Unit,
    onViewAllClick: (androidx.compose.ui.geometry.Rect) -> Unit
) {
    val accentColor = when {
        categoryName.contains("HINDI") -> Color(0xFF00E5FF)
        categoryName.contains("ENG") -> Color(0xFFD500F9)
        else -> Color(0xFF00E5FF)
    }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .background(Color(0xFF161618), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = categoryName,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            var bounds by remember { androidx.compose.runtime.mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

            Row(
                modifier = Modifier
                    .onGloballyPositioned { layoutCoordinates ->
                        bounds = layoutCoordinates.boundsInRoot()
                    }
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        onViewAllClick(bounds)
                    }
                    .background(Color(0xFF161618), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(12.dp))
                    .padding(start = 12.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "View All", color = Color(0xFFA1A1AA), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFA1A1AA),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        val pagerState = androidx.compose.foundation.pager.rememberPagerState(
            initialPage = if (websites.size > 1) 1 else 0,
            pageCount = { websites.size }
        )
        
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val cardWidth = 150.dp
        val horizontalPadding = (screenWidth - cardWidth) / 2

        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val website = websites[page]
            
            WebsiteCard(
                website = website,
                accentColor = accentColor,
                isCenter = true,
                modifier = Modifier.graphicsLayer {
                    val pageOffset = kotlin.math.abs((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                    val fraction = pageOffset.coerceIn(0f, 1f)
                    
                    val scale = (1 - fraction) * 1f + fraction * 0.82f
                    val itemAlpha = (1 - fraction) * 1f + fraction * 0.5f
                    
                    scaleX = scale
                    scaleY = scale
                    this.alpha = itemAlpha
                },
                onClick = { onWebsiteClick(website.url) }
            )
        }
    }
}

@Composable
fun WebsiteCard(
    website: WebsiteModel,
    accentColor: Color,
    isCenter: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor = Color(0xFF1C1A14)
    val textColor = Color.White
    val domain = website.url.replace("https://", "").replace("http://", "").substringBefore("/")
    
    val ottDomains = mapOf(
        "Netflix" to "netflix.com",
        "Crunchyroll" to "crunchyroll.com",
        "Prime Video" to "primevideo.com",
        "Prime" to "primevideo.com",
        "Disney+" to "disneyplus.com",
        "Apple TV" to "tv.apple.com",
        "Hulu" to "hulu.com",
        "MGM+" to "mgmplus.com",
        "HBO" to "hbo.com",
        "Hotstar" to "hotstar.com",
        "Zee5" to "zee5.com",
        "Sony LIV" to "sonyliv.com"
    )
    
    val actualDomain = ottDomains[website.name] ?: domain
    val faviconUrl = "https://www.google.com/s2/favicons?domain=${actualDomain}&sz=256"
    
    Card(
        modifier = modifier
            .width(150.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isAniwave = website.name.equals("Aniwave", ignoreCase = true)
            if (isAniwave) {
                AsyncImage(
                    model = R.drawable.logo_aniwave,
                    contentDescription = website.name,
                    modifier = Modifier.size(76.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = faviconUrl,
                    contentDescription = website.name,
                    modifier = Modifier.size(76.dp),
                    contentScale = ContentScale.Fit
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = website.name,
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedIndex: Int,
    onIndexSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        "ANIME" to Color(0xFF4FC3F7),
        "OTT" to Color(0xFFB388FF),
        "TV SHOWS" to Color(0xFFFF5252)
    )

    Row(
        modifier = modifier
            .fillMaxWidth(0.80f)
            .height(66.dp)
            .background(Color(0xFF111111), RoundedCornerShape(33.dp))
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(33.dp))
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEachIndexed { index, (title, color) ->
            val isSelected = selectedIndex == index
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onIndexSelected(index) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))
                
                Box(
                    modifier = Modifier.height(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val scale by animateFloatAsState(if (isSelected) 1.15f else 1.0f, animationSpec = tween(250))
                    val alpha by animateFloatAsState(if (isSelected) 1.0f else 0.5f, animationSpec = tween(250))
                    val glowAlpha by animateFloatAsState(if (isSelected) 0.8f else 0.0f, animationSpec = tween(250))

                    Box(
                        modifier = Modifier
                            .scale(scale)
                            .graphicsLayer { this.alpha = alpha },
                        contentAlignment = Alignment.Center
                    ) {
                        // Blurred Glow Background
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .graphicsLayer { this.alpha = glowAlpha }
                                .blur(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            IconGraphic(index, color)
                        }

                        // Foreground Icon
                        IconGraphic(index, color)
                    }
                }
                
                Text(
                    text = title,
                    color = if (isSelected) Color.White else Color(0xFF888888),
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
                
                // Active indicator dot/bar
                Spacer(modifier = Modifier.height(2.dp))
                val indicatorAlpha by animateFloatAsState(if (isSelected) 1f else 0f, animationSpec = tween(250))
                Box(
                    modifier = Modifier
                        .offset(y = (-2).dp)
                        .size(width = 12.dp, height = 2.dp)
                        .graphicsLayer { this.alpha = indicatorAlpha }
                        .background(color, RoundedCornerShape(1.dp))
                        .blur(0.5.dp)
                )
            }
        }
    }
}

@Composable
fun IconGraphic(index: Int, baseColor: Color) {
    val size = 26.dp
    when (index) {
        0 -> { // ANIME (Rem)
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = R.drawable.img_rem,
                    contentDescription = "Rem",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }
        1 -> { // OTT (TV)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
                val corner = 3.dp.toPx()
                // TV Box
                drawRoundRect(
                    color = baseColor,
                    topLeft = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 7.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 14.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx())
                )
                // Antennas
                drawLine(baseColor, androidx.compose.ui.geometry.Offset(12.dp.toPx(), 7.dp.toPx()), androidx.compose.ui.geometry.Offset(6.dp.toPx(), 3.dp.toPx()), strokeWidth = 1.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(baseColor, androidx.compose.ui.geometry.Offset(12.dp.toPx(), 7.dp.toPx()), androidx.compose.ui.geometry.Offset(18.dp.toPx(), 3.dp.toPx()), strokeWidth = 1.5.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                // Play Button
                val path = Path().apply {
                    moveTo(10.dp.toPx(), 10.dp.toPx())
                    lineTo(15.dp.toPx(), 14.dp.toPx())
                    lineTo(10.dp.toPx(), 18.dp.toPx())
                    close()
                }
                drawPath(path, baseColor, style = androidx.compose.ui.graphics.drawscope.Fill)
            }
        }
        2 -> { // TV SHOWS (Film Strip)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
                val brush = Brush.verticalGradient(listOf(Color(0xFFFF5252), Color(0xFFC51162)))
                val corner = 2.dp.toPx()
                // Film strip border
                drawRoundRect(
                    brush = brush,
                    topLeft = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 2.dp.toPx()),
                    size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 20.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner, corner),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
                // Top holes
                drawCircle(brush, radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(6.dp.toPx(), 5.dp.toPx()))
                drawCircle(brush, radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 5.dp.toPx()))
                drawCircle(brush, radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(18.dp.toPx(), 5.dp.toPx()))
                // Bottom holes
                drawCircle(brush, radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(6.dp.toPx(), 19.dp.toPx()))
                drawCircle(brush, radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 19.dp.toPx()))
                drawCircle(brush, radius = 1.dp.toPx(), center = androidx.compose.ui.geometry.Offset(18.dp.toPx(), 19.dp.toPx()))
                // Play button in center
                val path = Path().apply {
                    moveTo(9.dp.toPx(), 8.dp.toPx())
                    lineTo(16.dp.toPx(), 12.dp.toPx())
                    lineTo(9.dp.toPx(), 16.dp.toPx())
                    close()
                }
                drawPath(path, brush, style = androidx.compose.ui.graphics.drawscope.Fill)
            }
        }
    }
}

@Composable
fun ViewAllModal(
    isOpen: Boolean,
    categoryName: String?,
    websites: List<WebsiteModel>?,
    originBounds: androidx.compose.ui.geometry.Rect?,
    onClose: () -> Unit,
    onWebsiteClick: (String) -> Unit
) {
    if (!isOpen) return
    
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF161618))
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categoryName ?: "",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    androidx.compose.material3.IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF27272A), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(130.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(websites ?: emptyList()) { website -> 
                         WebsiteCard(
                            website = website,
                            accentColor = Color(0xFF00E5FF),
                            isCenter = false,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onWebsiteClick(website.url) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarOverlay(isOpen: Boolean, onClose: () -> Unit, onWebsiteClick: (String) -> Unit) {
    var showAboutModal by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showSiteIssuesModal by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showHowToUseModal by remember { androidx.compose.runtime.mutableStateOf(false) }
    var showSocialModal by remember { androidx.compose.runtime.mutableStateOf(false) }

    AnimatedVisibility(
        visible = isOpen,
        enter = fadeIn(tween(400, easing = androidx.compose.animation.core.FastOutSlowInEasing)),
        exit = fadeOut(tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = onClose
                )
        )
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInHorizontally(
            initialOffsetX = { -it - 100 },
            animationSpec = tween(400, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -it - 100 },
            animationSpec = tween(300, easing = androidx.compose.animation.core.FastOutLinearInEasing)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .padding(top = 20.dp, bottom = 20.dp, end = 24.dp)
                .width(280.dp)
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                .clip(RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                .background(Color(0xFF101014))
                .border(1.dp, Color(0xFF27272A).copy(alpha = 0.5f), RoundedCornerShape(topEnd = 32.dp, bottomEnd = 32.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -10) {
                            onClose()
                        }
                    }
                }
                .clickable(
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                    indication = null,
                    onClick = {} // Consume clicks inside sidebar
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "An",
                            color = Color(0xFFFF6D00),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "ı",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Box(
                                modifier = Modifier
                                    .offset(y = (-9).dp)
                                    .size(5.dp)
                                    .background(Color(0xFFFF6D00), CircleShape)
                            )
                        }
                        Text(
                            text = "meBro",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    androidx.compose.material3.IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF161618), CircleShape)
                            .border(1.dp, Color(0xFF27272A), CircleShape)
                    ) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Clear,
                            contentDescription = "Close Menu",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(48.dp))
                
                // Options
                SidebarOption(text = "About AnimeBro", onClick = { showAboutModal = true })
                SidebarOption(text = "Site Issues", onClick = { showSiteIssuesModal = true })
                SidebarOption(text = "Social Media", onClick = { showSocialModal = true })
                SidebarOption(text = "How to Use", onClick = { showHowToUseModal = true })
            }
        }
    }
    
    // Modals
    if (showAboutModal) {
        InfoModal(
            title = "About AnimeBro",
            content = "AnimeBro is an app created for anime and web series fans. It brings anime and web series websites together in one simple place so users can easily access their preferred sites. The app may feel a little confusing when you use it for the first time, but once you understand how it works, it becomes much easier and more convenient to use. All important app updates, announcements, and information are shared through our Telegram channel. If you face any problem with AnimeBro, you can report it through the comments on our Telegram channel so we can look into it. AnimeBro is designed to work without requesting unnecessary device permissions. For your security, always download AnimeBro only from our official Telegram source or another source that you trust.",
            onClose = { showAboutModal = false }
        )
    }
    
    if (showSiteIssuesModal) {
        InfoModal(
            title = "Site Issues",
            content = "If your favourite anime or web series site is not working, please check our Telegram channel for the latest updates. If a site is missing, temporarily unavailable, or has not been updated yet, please wait around 3–4 days because fixing or restoring a site may take some time. If a site stops working, you can report the issue in the comments on our Telegram channel so it can be checked and fixed as soon as possible.",
            onClose = { showSiteIssuesModal = false }
        )
    }
    
    if (showHowToUseModal) {
        InfoModal(
            title = "How to Use",
            content = "AnimeBro makes it easy to find and watch your favorite anime and web series. Simply select a category from the home screen, choose a website from the list, and use that website to find and watch your desired content.",
            onClose = { showHowToUseModal = false }
        )
    }
    
    if (showSocialModal) {
        val context = LocalContext.current
        SocialModal(
            onClose = { showSocialModal = false },
            onLinkClick = { link ->
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(link))
                context.startActivity(intent)
                showSocialModal = false
            }
        )
    }
}

@Composable
fun SidebarOption(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .background(Color(0xFF1C1C1E).copy(alpha = 0.4f))
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = Color(0xFFE4E4E7),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
        Icon(
            androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFA1A1AA),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
fun InfoModal(title: String, content: String, onClose: () -> Unit) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF161618))
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    androidx.compose.material3.IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF27272A), CircleShape)
                    ) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Clear,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = content,
                    color = Color(0xFFA1A1AA),
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun SocialModal(onClose: () -> Unit, onLinkClick: (String) -> Unit) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onClose,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFF161618))
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(24.dp))
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Social Media",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    androidx.compose.material3.IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFF27272A), CircleShape)
                    ) {
                        Icon(
                            androidx.compose.material.icons.Icons.Default.Clear,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                // Instagram
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onLinkClick("https://instagram.com/animebro.ig") }
                        .background(Color(0xFF27272A).copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_instagram),
                        contentDescription = "Instagram",
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "@animebro.ig",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Telegram
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onLinkClick("https://t.me/animebroig") }
                        .background(Color(0xFF27272A).copy(alpha = 0.5f))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_telegram),
                        contentDescription = "Telegram",
                        tint = androidx.compose.ui.graphics.Color.Unspecified,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Telegram Channel",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
