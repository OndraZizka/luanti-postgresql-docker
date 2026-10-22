package org.luanti.mavenizer

import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import ch.zizka.luanti.mavenizer.Package
import ch.zizka.luanti.mavenizer.ModDependency
import ch.zizka.luanti.mavenizer.PackageDetails
import ch.zizka.luanti.mavenizer.Release

class ContentDbClient {

    private val client = OkHttpClient()
    private val json = Json { ignoreUnknownKeys = true }
    private val baseUrl = "https://content.luanti.org/api"

    /** @param game optional `author/name` of a game, to only list packages supporting it, e.g. `ryvnf/mineclonia`. */
    fun fetchAllPackages(game: String? = null): List<Package> {
        val url = if (game != null) "$baseUrl/packages/?type=mod&game=$game" else "$baseUrl/packages/"
        val request = Request.Builder().url(url).build()
        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()
        return json.decodeFromString<List<Package>>(body)
            .filter { it.type.equals("MOD", ignoreCase = true) } // Only Mavenize MODs
    }

    fun fetchPackageDetails(author: String, name: String): PackageDetails? {
        val url = "$baseUrl/packages/$author/$name/"
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return null
            val body = response.body?.string() ?: return null
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
        val request = Request.Builder().url("$baseUrl/packages/$author/$name/dependencies/").build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()
            val body = response.body?.string() ?: return emptyList()
            return json.decodeFromString<Map<String, List<ModDependency>>>(body).values.firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching dependencies for $author/$name: ${e.message}")
            return emptyList()
        }
    }

    fun fetchReleases(author: String, name: String): List<Release> {
        val url = "$baseUrl/packages/$author/$name/releases/"
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return emptyList()
            val body = response.body?.string() ?: return emptyList()
            return json.decodeFromString<List<Release>>(body)
        } catch (e: Exception) {
            println("Error fetching releases for $author/$name: ${e.message}")
            return emptyList()
        }
    }
}