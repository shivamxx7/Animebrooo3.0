import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

pattern = re.compile(
    r"val pagerState = androidx\.compose\.foundation\.pager\.rememberPagerState\(.*?onClick = \{ onWebsiteClick\(website\.url\) \}\n            \)\n        \}\n    \}\n\}",
    re.DOTALL
)

replacement = """val listState = rememberLazyListState(initialFirstVisibleItemIndex = if (websites.size > 1) 1 else 0)
        
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        val cardWidth = 150.dp
        val horizontalPadding = (screenWidth - cardWidth) / 2

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            flingBehavior = androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior(lazyListState = listState),
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
}"""

new_content = pattern.sub(replacement, content)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(new_content)
