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
            WebsiteModel("Anime salt", "https://animesalt.me/"),
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
            WebsiteModel("Prime Video", "https://flixhub.studio/prime/index"),
            WebsiteModel("Netflix", "https://flixhub.studio/netflix/index"),
            WebsiteModel("Crunchyroll", "https://flixhub.studio/crunchyroll/index"),
            WebsiteModel("Disney+", "https://flixhub.studio/disney/index"),
            WebsiteModel("Hotstar", "https://flixhub.studio/hotstar/index"),
            WebsiteModel("Zee5", "https://flixhub.studio/zee5/index"),
            WebsiteModel("Sony LIV", "https://flixhub.studio/sonyliv/index")
        ),
        "SERVER 2" to listOf(
            WebsiteModel("Prime Video", "https://pantyflix.com/platforms/9/movie"),
            WebsiteModel("Netflix", "https://pantyflix.com/platforms/8/movie"),
            WebsiteModel("Crunchyroll", "https://pantyflix.com/platforms/283/movie"),
            WebsiteModel("Disney+", "https://pantyflix.com/platforms/337/movie"),
            WebsiteModel("Apple TV", "https://pantyflix.com/platforms/350/movie"),
            WebsiteModel("Hulu", "https://pantyflix.com/platforms/15/movie"),
            WebsiteModel("MGM+", "https://pantyflix.com/platforms/34/movie"),
            WebsiteModel("HBO MAX", "https://pantyflix.com/platforms/1899/movie"),
            WebsiteModel("Peacock", "https://pantyflix.com/platforms/387/movie"),
            WebsiteModel("Shudder", "https://pantyflix.com/platforms/99/movie")
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
