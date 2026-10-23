package ch.zizka.luanti.mavenizer

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request

class ContentDbClient {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://content.luanti.org/api"

    /** GET with retries on rate limiting (429) and server errors; returns null for 404 and other client errors. */
    private fun get(url: String): String? {
        var delayMs = 2000L
        repeat(6) { attempt ->
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            response.use {
                if (it.isSuccessful) return it.body?.string()
                if (it.code != 429 && it.code < 500) return null
                val wait = it.header("Retry-After")?.toLongOrNull()?.times(1000) ?: delayMs
                println("HTTP ${it.code} for $url, retrying in ${wait / 1000}s (attempt ${attempt + 1})")
                Thread.sleep(wait)
                delayMs *= 2
            }
        }
        println("Giving up on $url")
        return null
    }

    /** @param game optional `author/name` of a game, to only list packages supporting it, e.g. `ryvnf/mineclonia`. */
    fun fetchAllPackages(game: String? = null): List<Package> {
        val url = if (game != null) "$baseUrl/packages/?type=mod&game=$game" else "$baseUrl/packages/"
        val body = get(url) ?: return emptyList()
        return json.decodeFromString<List<Package>>(body)
            .filter { it.type.equals("MOD", ignoreCase = true) } // Only Mavenize MODs
    }

    fun fetchPackageDetails(author: String, name: String): PackageDetails? {
        val url = "$baseUrl/packages/$author/$name/"
        try {
            val body = get(url) ?: return null
            val details = json.decodeFromString<PackageDetails>(body)
            // The package detail endpoint doesn't inline the release list; fetch it separately.
            return details.copy(releases = fetchReleases(author, name))
        } catch (e: Exception) {
            println("Error fetching details for $author/$name: ${e.message}")
            return null
        }
    }

    /** Required and optional mods from mod.conf, with the ContentDB packages providing each of them. */
    fun fetchDependencies(author: String, name: String): List<ModDependency> {
        try {
            val body = get("$baseUrl/packages/$author/$name/dependencies/") ?: return emptyList()
            return json.decodeFromString<Map<String, List<ModDependency>>>(body).values.firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching dependencies for $author/$name: ${e.message}")
            return emptyList()
        }
    }

    fun fetchReleases(author: String, name: String): List<Release> {
        val url = "$baseUrl/packages/$author/$name/releases/"
        try {
            val body = get(url) ?: return emptyList()
            return json.decodeFromString<List<Release>>(body)
        } catch (e: Exception) {
            println("Error fetching releases for $author/$name: ${e.message}")
            return emptyList()
        }
    }
}