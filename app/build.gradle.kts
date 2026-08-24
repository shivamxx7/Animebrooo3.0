import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

import java.io.File
import java.awt.Color
import java.awt.RenderingHints
import java.awt.geom.Ellipse2D
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.roborazzi)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "com.aistudio.animebrowser.v2.qxyz"
    minSdk = 24
    targetSdk = 36
    versionCode = 102
    versionName = "2.1"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      signingConfig = signingConfigs.getByName("release")
    }
    debug { signingConfig = signingConfigs.getByName("debugConfig") }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  sourceSets {
    getByName("main") {
      res.srcDir("$projectDir/build/generated/res/custom_icons")
    }
  }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Some unused dependencies are commented out below instead of being removed.
// This makes it easy to add them back in the future if needed.
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  // implementation(libs.accompanist.permissions)
  implementation(libs.androidx.activity.compose)
  // implementation(libs.androidx.camera.camera2)
  // implementation(libs.androidx.camera.core)
  // implementation(libs.androidx.camera.lifecycle)
  // implementation(libs.androidx.camera.view)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  // implementation(libs.androidx.datastore.preferences)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  // implementation(libs.androidx.media3.exoplayer)
  // implementation(libs.androidx.media3.ui)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  // Uncomment to use Firestore:
  // implementation(libs.firebase.firestore)

  // Uncomment ALL FOUR of the following dependencies together to use Firebase Auth and Google
  // Sign-In via Credential Manager:
  // implementation(libs.firebase.auth)
  // implementation(libs.androidx.credentials)
  // implementation(libs.androidx.credentials.play.services)
  // implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  // implementation(libs.play.services.location)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  testImplementation(libs.roborazzi)
  testImplementation(libs.roborazzi.compose)
  testImplementation(libs.roborazzi.junit.rule)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
// Workaround for GitHub Actions CI build

tasks.register("generateAppIcons") {
    notCompatibleWithConfigurationCache("Reads directly from files and does custom I/O")
    doLast {
        val srcFile = file("$rootDir/25645c54235013738fff1a89b08cbb5af18fb18afa3a9115783037abc6e00d6b.png")
        if (!srcFile.exists()) throw GradleException("Source logo not found at ${srcFile.absolutePath}")
        val img = ImageIO.read(srcFile) ?: throw GradleException("Could not decode source logo")

        val densities = mapOf(
            "mdpi" to (48 to 108), "hdpi" to (72 to 162), "xhdpi" to (96 to 216),
            "xxhdpi" to (144 to 324), "xxxhdpi" to (192 to 432)
        )

        densities.forEach { (density, sizes) ->
            val (legacySize, fgSize) = sizes
            val dir = file("$projectDir/build/generated/res/custom_icons/mipmap-$density")
            dir.mkdirs()

            fun scaledDraw(g: java.awt.Graphics2D, canvas: Int, scale: Double) {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
                val ratio = minOf(canvas.toDouble() / img.width, canvas.toDouble() / img.height) * scale
                val w = (img.width * ratio).toInt(); val h = (img.height * ratio).toInt()
                g.drawImage(img, (canvas - w) / 2, (canvas - h) / 2, w, h, null)
            }

            val square = BufferedImage(legacySize, legacySize, BufferedImage.TYPE_INT_ARGB)
            val g1 = square.createGraphics()
            g1.color = Color.BLACK; g1.fillRect(0, 0, legacySize, legacySize)
            scaledDraw(g1, legacySize, 0.9); g1.dispose()
            ImageIO.write(square, "PNG", File(dir, "ic_launcher.png"))

            val round = BufferedImage(legacySize, legacySize, BufferedImage.TYPE_INT_ARGB)
            val g2 = round.createGraphics()
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            g2.clip = Ellipse2D.Float(0f, 0f, legacySize.toFloat(), legacySize.toFloat())
            g2.drawImage(square, 0, 0, null); g2.dispose()
            ImageIO.write(round, "PNG", File(dir, "ic_launcher_round.png"))

            val fg = BufferedImage(fgSize, fgSize, BufferedImage.TYPE_INT_ARGB)
            val g3 = fg.createGraphics()
            scaledDraw(g3, fgSize, 0.62); g3.dispose()
            ImageIO.write(fg, "PNG", File(dir, "ic_launcher_foreground.png"))
        }

        val check = file("$projectDir/build/generated/res/custom_icons/mipmap-xxxhdpi/ic_launcher.png")
        if (check.length() == 0L) throw GradleException("Icon generation ran but output is still empty")
        println("App icons regenerated dynamically (${check.length()} bytes for xxxhdpi ic_launcher.png)")
    }
}

tasks.named("preBuild") { dependsOn("generateAppIcons") }
