package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.pager.HorizontalPager
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF09090B))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeaderSection()
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
            ) {
                items(WebsiteRepository.categories.entries.toList()) { (categoryName, websites) ->
                    CategoryRow(categoryName, websites, onWebsiteClick)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
        BottomNavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
fun HeaderSection() {
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
                .background(Color(0xFF161618), RoundedCornerShape(14.dp))
                .border(1.dp, Color(0xFF27272A), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
        }
    }
}

@Composable
fun CategoryRow(categoryName: String, websites: List<WebsiteModel>, onWebsiteClick: (String) -> Unit) {
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
            
            Row(
                modifier = Modifier
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
        
        val listState = rememberLazyListState()
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val cardWidth = 150.dp
        val horizontalPadding = (screenWidth - cardWidth) / 2

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            flingBehavior = rememberSnapFlingBehavior(lazyListState = listState),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(websites.size) { index ->
                val website = websites[index]
                
                WebsiteCard(
                    website = website,
                    accentColor = accentColor,
                    isCenter = true,
                    modifier = Modifier.graphicsLayer {
                        val layoutInfo = listState.layoutInfo
                        val visibleItem = layoutInfo.visibleItemsInfo.find { it.index == index }
                        if (visibleItem != null) {
                            val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportSize.width / 2f
                            val itemCenter = visibleItem.offset + visibleItem.size / 2f
                            val distance = kotlin.math.abs(viewportCenter - itemCenter)
                            
                            val maxDistance = visibleItem.size.toFloat()
                            val fraction = (distance / maxDistance).coerceIn(0f, 1f)
                            
                            val scale = (1 - fraction) * 1f + fraction * 0.82f
                            val itemAlpha = (1 - fraction) * 1f + fraction * 0.5f
                            
                            scaleX = scale
                            scaleY = scale
                            this.alpha = itemAlpha
                        } else {
                            scaleX = 0.82f
                            scaleY = 0.82f
                            this.alpha = 0.5f
                        }
                    },
                    onClick = { onWebsiteClick(website.url) }
                )
            }
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
    val isKissanime = website.name.equals("Kissanime", ignoreCase = true)
    val isAniwave = website.name.equals("Aniwave", ignoreCase = true)
    val faviconUrl = "https://www.google.com/s2/favicons?domain=${domain}&sz=256"
    
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
            if (isKissanime) {
                AsyncImage(
                    model = R.drawable.kissanime_logo,
                    contentDescription = website.name,
                    modifier = Modifier.size(76.dp).clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else if (isAniwave) {
                AsyncImage(
                    model = R.drawable.aniwave_logo,
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
fun BottomNavigationBar(modifier: Modifier = Modifier) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    
    val items = listOf(
        "ANIME" to Color(0xFF4FC3F7),
        "OTT" to Color(0xFFB388FF),
        "PREMIUM" to Color(0xFFFFD54F),
        "WATCHLIST" to Color(0xFFFF5252),
        "PROFILE" to Color(0xFF448AFF)
    )

    Row(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .height(60.dp)
            .background(Color(0xFF111111), RoundedCornerShape(30.dp))
            .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(30.dp))
            .padding(horizontal = 4.dp),
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
                    ) { selectedIndex = index },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Box(
                    modifier = Modifier.height(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val scale by animateFloatAsState(if (isSelected) 1.1f else 0.95f, animationSpec = tween(250))
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
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.offset(y = (-4).dp)
                )
                
                // Active indicator dot/bar
                Spacer(modifier = Modifier.height(2.dp))
                val indicatorAlpha by animateFloatAsState(if (isSelected) 1f else 0f, animationSpec = tween(250))
                Box(
                    modifier = Modifier
                        .offset(y = (-4).dp)
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
    val size = 24.dp
    when (index) {
        0 -> { // ANIME (Rem)
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = "https://i.postimg.cc/SQW8Mc40/1000106315-Photoroom.png",
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
        2 -> { // PREMIUM (Crown)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
                val brush = Brush.verticalGradient(listOf(Color(0xFFFFD54F), Color(0xFFFF8F00)))
                val path = Path().apply {
                    moveTo(2.dp.toPx(), 15.dp.toPx())
                    lineTo(2.dp.toPx(), 5.dp.toPx())
                    lineTo(7.dp.toPx(), 10.dp.toPx())
                    lineTo(12.dp.toPx(), 3.dp.toPx())
                    lineTo(17.dp.toPx(), 10.dp.toPx())
                    lineTo(22.dp.toPx(), 5.dp.toPx())
                    lineTo(22.dp.toPx(), 15.dp.toPx())
                    close()
                }
                drawPath(path, brush, style = androidx.compose.ui.graphics.drawscope.Fill)
                drawRoundRect(brush, topLeft = androidx.compose.ui.geometry.Offset(2.dp.toPx(), 17.dp.toPx()), size = androidx.compose.ui.geometry.Size(20.dp.toPx(), 3.dp.toPx()), cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()))
            }
        }
        3 -> { // WATCHLIST (Bookmark)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
                val brush = Brush.verticalGradient(listOf(Color(0xFFFF5252), Color(0xFFC51162)))
                val path = Path().apply {
                    moveTo(5.dp.toPx(), 2.dp.toPx())
                    lineTo(19.dp.toPx(), 2.dp.toPx())
                    lineTo(19.dp.toPx(), 22.dp.toPx())
                    lineTo(12.dp.toPx(), 16.dp.toPx())
                    lineTo(5.dp.toPx(), 22.dp.toPx())
                    close()
                }
                drawPath(path, brush, style = androidx.compose.ui.graphics.drawscope.Fill)
            }
        }
        4 -> { // PROFILE (Person)
            androidx.compose.foundation.Canvas(modifier = Modifier.size(size)) {
                val brush = Brush.verticalGradient(listOf(Color(0xFF40C4FF), Color(0xFF2962FF)))
                drawCircle(brush, radius = 4.5.dp.toPx(), center = androidx.compose.ui.geometry.Offset(12.dp.toPx(), 7.5.dp.toPx()))
                val path = Path().apply {
                    moveTo(4.dp.toPx(), 21.dp.toPx())
                    quadraticBezierTo(4.dp.toPx(), 13.dp.toPx(), 12.dp.toPx(), 13.dp.toPx())
                    quadraticBezierTo(20.dp.toPx(), 13.dp.toPx(), 20.dp.toPx(), 21.dp.toPx())
                    close()
                }
                drawPath(path, brush, style = androidx.compose.ui.graphics.drawscope.Fill)
            }
        }
    }
}
