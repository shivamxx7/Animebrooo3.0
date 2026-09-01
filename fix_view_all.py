import re

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "r") as f:
    content = f.read()

pattern = re.compile(
    r"@Composable\nfun ViewAllModal\(.*?fun SidebarOverlay",
    re.DOTALL
)

replacement = """@Composable
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
fun SidebarOverlay"""

new_content = pattern.sub(replacement, content)

with open("app/src/main/java/com/example/ui/HomeScreen.kt", "w") as f:
    f.write(new_content)
