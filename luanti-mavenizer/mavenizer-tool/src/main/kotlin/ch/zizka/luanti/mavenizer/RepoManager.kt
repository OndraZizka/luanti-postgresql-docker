package ch.zizka.luanti.mavenizer

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

/** Where to take the list of already mavenized packages from. */
enum class UpgradeSource(val argName: String) {
    LOCAL_REPO("localRepo"),
    MAVEN_CENTRAL("mavenCentral"),
    /** The packages of the game given by --game which are in the local repository. */
    MODS_FOR_GAME("modsForGame");

    companion object {
        fun fromArg(name: String) = entries.firstOrNull { it.argName == name }
            ?: throw IllegalArgumentException("Expected one of ${entries.map { it.argName }}, got: $name")
    }
}

class RepoManager(
    private val workingDir: File,
    private val gpgKeyName: String,
    private val localInstall: Boolean = false,
    private var withDependencies: Boolean = false,
) {
    private val client = ContentDbClient()
    private val pomGen = PomGenerator()
    private val groupIdPrefix = "ch.zizka.luanti"
    private val contentDbBase = "https://content.luanti.org/packages"

    private val processed = mutableSetOf<String>()   // "author/name/release" mavenized in this run
    private val failed = mutableSetOf<String>()
    private val inProgress = mutableSetOf<String>()   // "author/name", for cycle detection
    private var targets = emptySet<String>()          // "author/name" of the packages asked for
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

    private enum class Outcome { DONE, FAILED }

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
     * Looks up the already mavenized packages on ContentDB, and mavenizes the ones with a newer release
     * (with their dependencies). With dryRun, it only reports them.
     */
    fun upgradeExisting(source: UpgradeSource, game: String?, dryRun: Boolean) {
        val installed = when (source) {
            UpgradeSource.LOCAL_REPO -> installedPackages()
            UpgradeSource.MAVEN_CENTRAL -> centralPackages()
            UpgradeSource.MODS_FOR_GAME -> {
                requireNotNull(game) { "modsForGame needs --game=<author>/<name>" }
                val ofGame = client.fetchAllPackages(game).map { "${it.author}/${it.name}" }.toSet()
                installedPackages().filterKeys { it in ofGame }
            }
        }
        println("--- Checking ${installed.size} mavenized packages (${source.argName}) against ContentDB ---")
        val outdated = mutableListOf<Request>()
        for ((pkg, installedRelease) in installed) {
            val (author, name) = pkg.split("/", limit = 2)
            val latest = client.fetchPackageDetails(author, name)?.releases?.maxOfOrNull { it.id }
            when {
                latest == null -> println("Not found on ContentDB: $pkg")
                latest > installedRelease -> {
                    println("Update: $pkg $installedRelease -> $latest")
                    outdated += Request(author, name, latest)
                }
            }
        }
        println("--- ${outdated.size} of ${installed.size} packages have a newer release ---")
        if (dryRun || outdated.isEmpty()) return
        withDependencies = true
        run(outdated)
    }

    /** "author/name" -> its highest installed release id, taken from the POMs of ch.zizka.luanti.* in the local repo. */
    private fun installedPackages(): Map<String, Int> {
        val result = sortedMapOf<String, Int>()
        val urlRegex = Regex("<url>https://content\\.luanti\\.org/packages/([^/]+)/([^/]+)/</url>")
        File(localRepo, "ch/zizka/luanti").walkTopDown()
            .filter { it.isFile && it.extension == "pom" }
            .forEach { pom ->
                val release = pom.parentFile.name.substringBefore('.').toIntOrNull() ?: return@forEach
                val match = urlRegex.find(pom.readText()) ?: return@forEach
                val pkg = "${urlDecode(match.groupValues[1])}/${urlDecode(match.groupValues[2])}"
                result.merge(pkg, release, ::maxOf)
            }
        return result
    }

    /**
     * "author/name" -> highest release id published on Maven Central under ch.zizka.luanti.<type>.<author>,
     * found through the directory listings and maven-metadata.xml of repo1.maven.org.
     */
    private fun centralPackages(): Map<String, Int> {
        val base = "https://repo1.maven.org/maven2/ch/zizka/luanti"
        fun get(url: String) = runCatching {
            val response = http.send(java.net.http.HttpRequest.newBuilder(java.net.URI(url)).build(),
                java.net.http.HttpResponse.BodyHandlers.ofString())
            response.body().takeIf { response.statusCode() == 200 }
        }.getOrNull()
        fun subdirs(url: String) = Regex("href=\"([^\"/?.][^\"/]*)/\"").findAll(get(url) ?: "").map { it.groupValues[1] }.toList()

        val result = sortedMapOf<String, Int>()
        val urlRegex = Regex("<url>https://content\\.luanti\\.org/packages/([^/]+)/([^/]+)/</url>")
        for (type in PackageType.entries) for (author in subdirs("$base/${type.groupSegment}/")) {
            for (artifact in subdirs("$base/${type.groupSegment}/$author/")) {
                val dir = "$base/${type.groupSegment}/$author/$artifact"
                val versions = Regex("<version>([^<]+)</version>").findAll(get("$dir/maven-metadata.xml") ?: "")
                    .map { it.groupValues[1] }.toList()
                val latest = versions.filter { it.substringBefore('.').toIntOrNull() != null }
                    .maxByOrNull { it.substringBefore('.').toInt() } ?: continue
                val pom = get("$dir/$latest/$artifact-$latest.pom") ?: continue
                val match = urlRegex.find(pom) ?: continue
                result.merge("${urlDecode(match.groupValues[1])}/${urlDecode(match.groupValues[2])}",
                    latest.substringBefore('.').toInt(), ::maxOf)
            }
        }
        return result
    }

    private fun urlDecode(s: String) = java.net.URLDecoder.decode(s.replace("+", "%2B"), Charsets.UTF_8)

    /**
     * Packages are mavenized depth-first: the unavailable dependencies of a package (neither in the local repository
     * nor mavenized earlier in this run) are mavenized before it. Only dependencies which were asked for are
     * followed, unless withDependencies is set. A package left with unavailable dependencies is skipped, and those
     * are listed in missing-dependencies.txt.
     */
    private fun run(requests: List<Request>) {
        targets = requests.map { "${it.author}/${it.name}" }.toSet()
        requests.forEach { processPackage(it) }
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

        val release = if (releaseId != null) details.releases.firstOrNull { it.id == releaseId }
                            else details.releases.maxByOrNull { it.id }
        if (release == null) {
            println("No ${if (releaseId != null) "release $releaseId" else "releases"} found for $author/$packageName")
            return Outcome.FAILED
        }

        val key = "$author/$packageName/${release.id}"
        if (key in processed) return Outcome.DONE
        if (key in failed) return Outcome.FAILED
        inProgress += "$author/$packageName"
        try {
            return mavenize(details, type, release, key).also { if (it == Outcome.FAILED) failed += key }
        } finally {
            inProgress -= "$author/$packageName"
        }
    }

    private fun mavenize(details: PackageDetails, type: PackageType, release: Release, key: String): Outcome {
        val author = details.author
        val packageName = details.name
        val groupId = groupFor(author, type)
        val artifactId = sanitizeSegment(packageName)
        val moduleDir = File(workingDir, "$author/$packageName")
        val contentDbUrl = "$contentDbBase/${urlSegment(author)}/${urlSegment(packageName)}/"
        val version = effectiveVersion(groupId, artifactId, release.id)
        if (isDeployed(groupId, artifactId, version)) {
            println("$groupId:$artifactId:$version already deployed, skipping.")
            processed += key // counts as available for the packages depending on it
            return Outcome.DONE
        }

        val allResolved =
            if (type != PackageType.MOD) ResolvedDependencies()
            else resolveDependencies(author, packageName, details)

        // Maven can't build a dependency cycle: the edge back to a package being mavenized is left out.
        val cyclic = allResolved.refs.filterKeys { it.substringBeforeLast('/') in inProgress }
        val resolved = if (cyclic.isEmpty()) allResolved else allResolved.copy(
            dependencies = allResolved.dependencies.filter { d -> cyclic.values.none { it.groupId == d.groupId && it.artifactId == d.artifactId } },
            refs = allResolved.refs - cyclic.keys,
            unresolved = allResolved.unresolved + cyclic.keys.map { "${it.substringBeforeLast('/')}: left out, dependency cycle" },
        )

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
        for ((depKey, dep) in refs.filter { (k, d) -> !isAvailable(k, d) }) {
            val (depAuthor, depName, depRelease) = depKey.split("/")
            if (withDependencies || "$depAuthor/$depName" in targets) processPackage(Request(depAuthor, depName, depRelease.toInt()))
            if (!isAvailable(depKey, dep)) {
                println("Skipping $author/$packageName: dependency $depKey is not available")
                return Outcome.FAILED
            }
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
                downloadUrl = "${contentDbUrl}releases/${release.id}/download/",
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
                releaseDate = release.release_date?.substringBefore('T'),
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
        processed += key
        return Outcome.DONE
    }

    private data class ResolvedDependencies(
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
        if (!completed) {
            println("Timed out after 10m while deploying $moduleDir version $version")
            process.destroy()
            return false
        }
        if (process.exitValue() != 0) {
            println("Failed to deploy $moduleDir version $version")
            return false
        }
        println("Successfully deployed version $version")
        return true
    }

    /** On Central the version can't be published again. Local installs are always redone, as they are cheap. */
    private fun isDeployed(g: String, a: String, v: String) = !localInstall && isOnCentral(g, a, v)

    private fun urlSegment(s: String) = java.net.URLEncoder.encode(s, Charsets.UTF_8).replace("+", "%20")

    private fun sanitizeSegment(input: String) = input.replace(Regex("[^A-Za-z0-9_\\-.]"), "_")
}
