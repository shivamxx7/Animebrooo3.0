package com.example.data

data class WebsiteModel(
    val name: String,
    val url: String
)

object WebsiteRepository {
    val animeCategories = mapOf(
        "JAP + HINDI + SUB" to listOf(
            WebsiteModel("Toonstream", "https://toon-stream.site/home"),
            WebsiteModel("Anime world india", "https://watchanimeworld.pro/"),
            WebsiteModel("Anime salt", "https://animesalt.cx/"),
            WebsiteModel("Blakiteanime", "https://www.blakiteanime.buzz/?m=1")
        ),
        "JAP + ENG + SUB" to listOf(
            WebsiteModel("Yeanime", "https://yenime.net/"),
            WebsiteModel("Reanime", "https://reanime.to/home"),
            WebsiteModel("Animeflix", "https://animeflix.app/"),
            WebsiteModel("Anistream", "https://anistream.one/home")
        ),
        "JAP + SUB" to listOf(
            WebsiteModel("Animepahe", "https://animepahe.pw/"),
            WebsiteModel("Aniwave", "https://aniwaves.ru/home"),
            WebsiteModel("Aniwatch", "https://aniwatch.co.at/"),
            WebsiteModel("9anime", "https://9anime.org.lv/"),
            WebsiteModel("Kissanime", "https://kissanime.com.cv/")
        )
    )

    val ottCategories = mapOf(
        "RECOMMENDED" to listOf(
            WebsiteModel("Prime Video", "https://flixeo.tv/platforms/9/movie"),
            WebsiteModel("Netflix", "https://flixeo.tv/platforms/8/movie"),
            WebsiteModel("Crunchyroll", "https://flixeo.tv/platforms/283/movie"),
            WebsiteModel("Disney+", "https://flixeo.tv/platforms/337/movie"),
            WebsiteModel("Apple TV", "https://flixeo.tv/platforms/350/movie"),
            WebsiteModel("Hulu", "https://flixeo.tv/platforms/15/movie"),
            WebsiteModel("MGM+", "https://flixeo.tv/platforms/34/movie"),
            WebsiteModel("HBO", "https://flixeo.tv/platforms/1899/movie")
        ),
        "SERVER 2" to listOf(
            WebsiteModel("Prime Video", "https://flixhub.studio/prime/index"),
            WebsiteModel("Netflix", "https://flixhub.studio/netflix/index"),
            WebsiteModel("Crunchyroll", "https://flixhub.studio/crunchyroll/index"),
            WebsiteModel("Disney+", "https://flixhub.studio/disney/index"),
            WebsiteModel("Hotstar", "https://flixhub.studio/hotstar/index"),
            WebsiteModel("Zee5", "https://flixhub.studio/zee5/index"),
            WebsiteModel("Sony LIV", "https://flixhub.studio/sonyliv/index")
        ),
        "SERVER 3" to listOf(
            WebsiteModel("Prime", "https://pixelflix.cc/studio/prime"),
            WebsiteModel("Netflix", "https://pixelflix.cc/studio/netflix"),
            WebsiteModel("Crunchyroll", "https://pixelflix.cc/studio/crunchyroll"),
            WebsiteModel("Disney+", "https://pixelflix.cc/studio/disney"),
            WebsiteModel("HBO", "https://pixelflix.cc/studio/hbo"),
            WebsiteModel("Hulu", "https://pixelflix.cc/studio/hulu")
        ),
        "SERVER 4" to listOf(
            WebsiteModel("Prime Video", "https://cinehd.vc/search?page=1&watch_provider=9"),
            WebsiteModel("Netflix", "https://cinehd.vc/search?page=1&watch_provider=8"),
            WebsiteModel("Crunchyroll", "https://cinehd.vc/search?page=1&watch_provider=283"),
            WebsiteModel("Disney+", "https://cinehd.vc/search?page=1&watch_provider=337"),
            WebsiteModel("Apple TV", "https://cinehd.vc/search?page=1&watch_provider=350"),
            WebsiteModel("Hulu", "https://cinehd.vc/search?page=1&watch_provider=15"),
            WebsiteModel("MGM+", "https://cinehd.vc/search?page=1&watch_provider=583")
        )
    )

    val categories = animeCategories + ottCategories

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
