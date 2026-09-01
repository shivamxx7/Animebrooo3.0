import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

pattern = re.compile(
    r"val listState = rememberLazyListState\(\).*?onClick = \{ onWebsiteClick\(website\.url\) \}\n                \)\n            \}\n        \}\n    \}\n}",
    re.DOTALL
)

replacement = """val pagerState = androidx.compose.foundation.pager.rememberPagerState(
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
}"""

new_content = pattern.sub(replacement, content)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(new_content)
