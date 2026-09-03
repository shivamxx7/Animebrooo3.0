import re

with open("app/src/main/java/com/example/ui/WebViewScreen.kt", "r") as f:
    content = f.read()

imports_to_add = """
import android.provider.Settings
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
"""
content = content.replace("import android.annotation.SuppressLint", imports_to_add + "import android.annotation.SuppressLint")

target = """                var brightness by remember { mutableStateOf(activity?.window?.attributes?.screenBrightness?.takeIf { it >= 0f } ?: 0.5f) }
                CustomVerticalSlider(
                    value = brightness,
                    onValueChange = {
                        brightness = it
                        val layoutParams = activity?.window?.attributes
                        layoutParams?.screenBrightness = it
                        activity?.window?.attributes = layoutParams
                    },"""

replacement = """                val getSystemBrightness = {
                    try {
                        Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255f
                    } catch (e: Exception) {
                        0.5f
                    }
                }
                var brightness by remember { mutableStateOf(activity?.window?.attributes?.screenBrightness?.takeIf { it >= 0f } ?: getSystemBrightness()) }
                
                DisposableEffect(context) {
                    val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
                        override fun onChange(selfChange: Boolean) {
                            super.onChange(selfChange)
                            brightness = getSystemBrightness()
                            val layoutParams = activity?.window?.attributes
                            if (layoutParams != null) {
                                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                                activity?.window?.attributes = layoutParams
                            }
                        }
                    }
                    context.contentResolver.registerContentObserver(
                        Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS),
                        false,
                        observer
                    )
                    onDispose {
                        context.contentResolver.unregisterContentObserver(observer)
                    }
                }
                
                CustomVerticalSlider(
                    value = brightness,
                    onValueChange = {
                        brightness = it
                        val layoutParams = activity?.window?.attributes
                        layoutParams?.screenBrightness = it
                        activity?.window?.attributes = layoutParams
                    },"""

if target in content:
    content = content.replace(target, replacement)
    print("Replaced brightness logic successfully.")
else:
    print("Target not found! Check the source code.")

with open("app/src/main/java/com/example/ui/WebViewScreen.kt", "w") as f:
    f.write(content)
