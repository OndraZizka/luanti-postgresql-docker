package ch.zizka.luanti.mavenizer

import kotlinx.serialization.Serializable

@Serializable
data class Package(
    val author: String,
    val name: String,
    val title: String?,
    val type: String,
    val url: String? = null
)

@Serializable
data class PackageDetails(
    val author: String,
    val name: String,
    val title: String?,
    val short_description: String? = null,
    val license: String? = null,
    val repo: String? = null,
    val release: Int? = null, // id of the current/latest release
    val releases: List<Release> = emptyList()
)

@Serializable
data class Release(
    val id: Int,
    val title: String,
    val url: String? = null, // The download URL (relative), not used for downloading directly
    val commit: String? = null,
    val min_minetest_version: MinetestVersion? = null
)

@Serializable
data class MinetestVersion(
    val name: String? = null,
    val is_dev: Boolean = false,
    val protocol_version: Int? = null
)