package com.example.data

data class WebsiteModel(
    val name: String,
    val url: String
)

object WebsiteRepository {
    val categories = mapOf(
        "JAP + HINDI + SUB" to listOf(
            WebsiteModel("Anime world india", "https://watchanimeworld.pro/"),
            WebsiteModel("Toonstream", "https://toon-stream.site/home"),
            WebsiteModel("Anime salt", "https://animesalt.me/")
        ),
        "JAP + ENG + SUB" to listOf(
            WebsiteModel("Yeanime", "https://yenime.net/"),
            WebsiteModel("Reanime", "https://reanime.to/")
        ),
        "JAP + SUB" to listOf(
            WebsiteModel("Aniwave", "https://aniwaves.ru/home"),
            WebsiteModel("Aniwatch", "https://aniwatch.co.at/"),
            WebsiteModel("9anime", "https://9anime.org.lv/"),
            WebsiteModel("Kissanime", "https://kissanime.com.cv/")
        )
    )

    val allowedDomains = categories.values.flatten().map { getDomain(it.url) }.toSet()

    fun getDomain(url: String): String {
        return try {
            val uri = java.net.URI(url)
            uri.host?.removePrefix("www.") ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
