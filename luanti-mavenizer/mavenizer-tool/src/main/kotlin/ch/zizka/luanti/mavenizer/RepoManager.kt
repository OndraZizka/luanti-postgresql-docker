package ch.zizka.luanti.mavenizer

import org.luanti.mavenizer.ContentDbClient
import java.io.File
import java.util.concurrent.TimeUnit

/** ContentDB package types, with the ContentDB API name and the Maven groupId segment. */
enum class PackageType(val apiName: String, val groupSegment: String) {
    MOD("mod", "mods"),
    GAME("game", "games"),
    TEXTURE_PACK("txp", "texturepacks");

    companion object {
        fun fromApi(name: String?): PackageType? = entries.firstOrNull { it.apiName.equals(name, ignoreCase = true) }
    }
}

class RepoManager(
    private val workingDir: File,
    private val gpgKeyName: String,
    private val localInstall: Boolean = false,
) {
    private val client = ContentDbClient()
    private val pomGen = PomGenerator()
    private val groupIdPrefix = "ch.zizka.luanti"
    private val contentDbBase = "https://content.luanti.org/packages"

    private val processed = mutableSetOf<String>()
    private val referenced = sortedMapOf<String, PomDependency>() // "author/name/release" -> its Maven coordinates
    private val localRepo = File(System.getProperty("user.home"), ".m2/repository")

    private val centralCache = mutableMapOf<String, Boolean>()
    private val http = java.net.http.HttpClient.newHttpClient()

    /** True if this exact version is already on Maven Central, where releases are immutable. */
    private fun isOnCentral(group: String, artifact: String, version: String) =
        centralCache.getOrPut("$group:$artifact:$version") {
            val url = "https://repo1.maven.org/maven2/${group.replace('.', '/')}/$artifact/$version/$artifact-$version.pom"
            runCatching {
                http.send(java.net.http.HttpRequest.newBuilder(java.net.URI(url))
                    .method("HEAD", java.net.http.HttpRequest.BodyPublishers.noBody()).build(),
                    java.net.http.HttpResponse.BodyHandlers.discarding()).statusCode() == 200
            }.getOrDefault(false)
        }

    /** A version already published to Central (with older metadata) is republished as "<version>.1". */
    private fun effectiveVersion(group: String, artifact: String, releaseId: Int): String {
        val plain = releaseId.toString()
        return if (isOnCentral(group, artifact, plain)) "$plain.1" else plain
    }

    private enum class Outcome { DONE, DEFERRED, FAILED }

    private data class Request(val author: String, val name: String, val releaseId: Int? = null)

    fun syncAll(game: String? = null) {
        println("--- Starting Sync ---")
        val packages = client.fetchAllPackages(game)
        println("Found ${packages.size} mods.")
        run(packages.map { Request(it.author, it.name) })
    }

    /** Mavenize and deploy the given packages: the given release, or the latest one if the release id is null. */
    fun syncAll(requests: List<Triple<String, String, Int?>>) = run(requests.map { Request(it.first, it.second, it.third) })

    /**
     * A package whose dependencies are not available yet (neither in the local repository nor mavenized earlier
     * in this run) is deferred until they are. What remains unavailable is listed in missing-dependencies.txt;
     * the tool never mavenizes packages that were not asked for.
     */
    private fun run(requests: List<Request>) {
        var pending = requests
        while (pending.isNotEmpty()) {
            val deferred = pending.filter { processPackage(it) == Outcome.DEFERRED }
            if (deferred.size == pending.size) {
                println("--- ${deferred.size} packages left with unavailable dependencies: " +
                    deferred.joinToString { "${it.author}/${it.name}" } + " ---")
                break
            }
            pending = deferred
        }
        writeMissingDependencies()
    }

    private fun isAvailable(key: String, dep: PomDependency) =
        key in processed || File(localRepo, "${dep.groupId.replace('.', '/')}/${dep.artifactId}/${dep.version}" +
            "/${dep.artifactId}-${dep.version}.pom").isFile

    /** Lists the referenced packages which are not available yet. Feed this file back to the tool to mavenize them. */
    private fun writeMissingDependencies() {
        val missing = referenced.filter { (key, dep) -> !isAvailable(key, dep) }
        val file = File(workingDir, "missing-dependencies.txt")
        workingDir.mkdirs()
        file.writeText(missing.keys.joinToString("") { "$it\n" })
        println("--- ${missing.size} referenced packages not yet mavenized, listed in ${file.absolutePath} ---")
    }

    private fun groupFor(author: String, type: PackageType) =
        "$groupIdPrefix.${type.groupSegment}.${sanitizeSegment(author)}"

    private fun processPackage(request: Request): Outcome {
        val (author, packageName, releaseId) = request
        val details = client.fetchPackageDetails(author, packageName)
        if (details == null) {
            println("Could not fetch details for $author/$packageName")
            return Outcome.FAILED
        }

        val type = PackageType.fromApi(details.type)
        if (type == null) {
            println("Unsupported package type '${details.type}' of $author/$packageName")
            return Outcome.FAILED
        }
        val groupId = groupFor(author, type)
        val artifactId = sanitizeSegment(packageName)
        val moduleDir = File(workingDir, "$author/$packageName")
        val contentDbUrl = "$contentDbBase/$author/$packageName/"

        val latestRelease = if (releaseId != null) details.releases.firstOrNull { it.id == releaseId }
                            else details.releases.maxByOrNull { it.id }
        if (latestRelease == null) {
            println("No ${if (releaseId != null) "release $releaseId" else "releases"} found for $author/$packageName")
            return Outcome.FAILED
        }

        val version = effectiveVersion(groupId, artifactId, latestRelease.id)
        if (isDeployed(groupId, artifactId, version)) {
            println("$groupId:$artifactId:$version already deployed, skipping.")
            return Outcome.DONE
        }

        val resolved =
            if (type != PackageType.MOD) ResolvedDependencies()
            else resolveDependencies(author, packageName, details)

        // Each compatible game becomes a profile with a regular dependency, so it needn't exist for this build.
        // Games are mavenized as dummy packages, only when asked for explicitly.
        val allGames = details.game_support.filter { it.supports && it.game.release != null }.associate {
            "${it.game.author}/${it.game.name}/${it.game.release}" to PomDependency(
                groupFor(it.game.author, PackageType.GAME), sanitizeSegment(it.game.name),
                effectiveVersion(groupFor(it.game.author, PackageType.GAME), sanitizeSegment(it.game.name),
                    it.game.release!!), optional = false)
        }
        val refs = resolved.refs
        referenced.putAll(refs)
        val unavailable = refs.filter { (key, dep) -> !isAvailable(key, dep) }
        if (unavailable.isNotEmpty()) {
            println("Deferring $author/$packageName: waiting for ${unavailable.keys}")
            return Outcome.DEFERRED
        }

        println(">>> New Version Found: $groupId:$artifactId:$version")
        pomGen.generatePom(
            ModPomInfo(
                groupId = groupId,
                artifactId = artifactId,
                version = version,
                title = details.title ?: packageName,
                description = details.short_description ?: (details.title ?: packageName),
                contentDbUrl = contentDbUrl,
                downloadUrl = "${contentDbUrl}releases/${latestRelease.id}/download/",
                license = details.license,
                scmUrl = details.repo,
                author = author,
                gpgKeyName = gpgKeyName,
                longDescription = details.long_description,
                website = details.website,
                issueTrackerUrl = details.issue_tracker,
                forumUrl = details.forum_url,
                donateUrl = details.donate_url,
                devState = details.dev_state,
                releaseDate = latestRelease.release_date?.substringBefore('T'),
                tags = details.tags,
                maintainers = details.maintainers,
                mediaLicense = details.media_license?.takeIf { it != details.license },
                gameDependencies = allGames.values.toList(),
                dummy = type == PackageType.GAME,
                compatibleGames = allGames.keys.map { it.substringBeforeLast('/') },
                modDependencies = resolved.dependencies,
                requiredModNames = resolved.requiredNames,
                optionalModNames = resolved.optionalNames,
                unresolvedDependencies = resolved.unresolved,
            ),
            moduleDir,
        )
        if (!deploy(moduleDir, version)) return Outcome.FAILED
        processed += "$author/$packageName/${latestRelease.id}"
        return Outcome.DONE
    }

    private class ResolvedDependencies(
        val dependencies: List<PomDependency> = emptyList(),
        val refs: Map<String, PomDependency> = emptyMap(),
        val requiredNames: List<String> = emptyList(),
        val optionalNames: List<String> = emptyList(),
        val unresolved: List<String> = emptyList(),
    )

    /**
     * Turns the mod.conf dependencies into Maven dependencies on other ContentDB mods.
     * Mods provided by a game the package supports are skipped, as the game dependency covers them.
     * If a mod name is offered by several packages, the one named like the mod is taken.
     */
    private fun resolveDependencies(author: String, name: String, details: PackageDetails): ResolvedDependencies {
        val games = details.game_support.filter { it.supports }.map { "${it.game.author}/${it.game.name}" }.toSet()
        val modDeps = client.fetchDependencies(author, name)
        val result = linkedMapOf<String, PomDependency>()
        val refs = linkedMapOf<String, PomDependency>()
        val unresolved = mutableListOf<String>()

        for (dep in modDeps) {
            if (dep.packages.any { it in games }) continue
            val candidate = when {
                dep.packages.size == 1 -> dep.packages.first()
                else -> dep.packages.singleOrNull { it.substringAfter('/') == dep.name }
            }
            if (candidate == null) {
                unresolved += "${dep.name}${if (dep.is_optional) " (optional)" else ""}: " +
                    if (dep.packages.isEmpty()) "no ContentDB package" else "ambiguous, ${dep.packages.size} candidates"
                continue
            }
            val (depAuthor, depName) = candidate.split("/", limit = 2)
            val depDetails = client.fetchPackageDetails(depAuthor, depName)
            val depRelease = depDetails?.releases?.maxByOrNull { it.id }
            if (depDetails == null || depRelease == null || PackageType.fromApi(depDetails.type) != PackageType.MOD) {
                unresolved += "${dep.name}: $candidate is not a mavenizable mod"
                continue
            }
            val depGroup = groupFor(depAuthor, PackageType.MOD)
            val pomDep = PomDependency(depGroup, sanitizeSegment(depName),
                effectiveVersion(depGroup, sanitizeSegment(depName), depRelease.id), optional = dep.is_optional)
            refs["$candidate/${depRelease.id}"] = pomDep
            val key = "${pomDep.groupId}:${pomDep.artifactId}"
            result[key] = result[key]?.let { it.copy(optional = it.optional && pomDep.optional) } ?: pomDep
        }
        return ResolvedDependencies(
            result.values.toList(),
            refs,
            modDeps.filter { !it.is_optional }.map { it.name },
            modDeps.filter { it.is_optional }.map { it.name },
            unresolved,
        )
    }

    private fun deploy(moduleDir: File, version: String): Boolean {
        // The GPG passphrase is read by maven-gpg-plugin from the MAVEN_GPG_PASSPHRASE env var.
        // It is never written to disk or passed as a command-line argument here.
        val process = ProcessBuilder(if (localInstall) listOf("mvn", "clean", "install", "-Dgpg.skip=true") else listOf("mvn", "clean", "deploy", "-P", "ossrh"))
            .directory(moduleDir)
            .redirectErrorStream(true)
            .start()

        process.inputStream.bufferedReader().lines().forEach { println(it) }
        val completed = process.waitFor(10, TimeUnit.MINUTES)
        if (!completed || process.exitValue() != 0) {
            println("Failed to deploy $moduleDir version $version")
            return false
        }
        println("Successfully deployed version $version")
        return true
    }

    // A simple heuristic to check if we already processed this.
    // Ideally, query your Maven Repo (Nexus/Artifactory) API.
    private fun isDeployed(g: String, a: String, v: String): Boolean {
        return false
    }

    private fun sanitizeSegment(input: String) = input.replace(Regex("[^A-Za-z0-9_\\-.]"), "_")
}
