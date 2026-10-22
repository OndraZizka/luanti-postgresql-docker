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
    val type: String? = null,
    val long_description: String? = null,
    val website: String? = null,
    val issue_tracker: String? = null,
    val forum_url: String? = null,
    val donate_url: String? = null,
    val dev_state: String? = null,
    val media_license: String? = null,
    val tags: List<String> = emptyList(),
    val maintainers: List<String> = emptyList(),
    val supports_all_games: Boolean = false,
    val game_support: List<GameSupport> = emptyList(),
    val releases: List<Release> = emptyList()
)

@Serializable
data class GameSupport(
    val supports: Boolean = true,
    val game: GameRef
)

@Serializable
data class GameRef(
    val author: String,
    val name: String,
    val title: String? = null,
    val release: Int? = null
)

/** A mod name from mod.conf, with the ContentDB packages providing it. */
@Serializable
data class ModDependency(
    val name: String,
    val is_optional: Boolean = false,
    val packages: List<String> = emptyList()
)

@Serializable
data class Release(
    val id: Int,
    val title: String,
    val url: String? = null, // The download URL (relative), not used for downloading directly
    val commit: String? = null,
    val release_date: String? = null,
    val min_minetest_version: MinetestVersion? = null
)

@Serializable
data class MinetestVersion(
    val name: String? = null,
    val is_dev: Boolean = false,
    val protocol_version: Int? = null
)