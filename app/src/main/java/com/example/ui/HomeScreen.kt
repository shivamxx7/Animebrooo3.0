package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import coil.compose.AsyncImage
import com.example.data.WebsiteModel
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
            Box(contentAlignment = Alignment.TopCenter) {
                Text(
                    text = "ı",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(4.dp)
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
                    .padding(end = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(20.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                        .background(accentColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
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
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFFA1A1AA),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        
        val pagerState = rememberPagerState(pageCount = { websites.size })
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val cardWidth = 150.dp
        val horizontalPadding = (screenWidth - cardWidth) / 2
        
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            pageSpacing = 16.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val absOffset = kotlin.math.abs(pageOffset)
            
            fun lerp(start: Float, stop: Float, fraction: Float): Float = (1 - fraction) * start + fraction * stop
            val scale = lerp(0.82f, 1f, 1f - absOffset.coerceIn(0f, 1f))
            val alpha = lerp(0.5f, 1f, 1f - absOffset.coerceIn(0f, 1f))
            
            val isCenter = absOffset < 0.5f
            
            WebsiteCard(
                website = websites[page],
                accentColor = accentColor,
                isCenter = isCenter,
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    },
                onClick = { onWebsiteClick(websites[page].url) }
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
    val faviconUrl = "https://www.google.com/s2/favicons?domain=${domain}&sz=256"
    
    Card(
        modifier = modifier
            .width(150.dp)
            .height(180.dp)
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
            AsyncImage(
                model = faviconUrl,
                contentDescription = website.name,
                modifier = Modifier.size(76.dp),
                contentScale = ContentScale.Fit
            )
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
    Row(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .height(64.dp)
            .background(Color(0xFF161618).copy(alpha = 0.95f), RoundedCornerShape(32.dp))
            .border(1.dp, Color(0xFF27272A), RoundedCornerShape(32.dp))
            .padding(horizontal = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Home, contentDescription = "Home", tint = Color(0xFF00E5FF), modifier = Modifier.size(28.dp))
        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color(0xFF71717A), modifier = Modifier.size(28.dp))
        Icon(Icons.Default.Star, contentDescription = "Premium", tint = Color(0xFF71717A), modifier = Modifier.size(28.dp))
        Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color(0xFF71717A), modifier = Modifier.size(28.dp))
    }
}
