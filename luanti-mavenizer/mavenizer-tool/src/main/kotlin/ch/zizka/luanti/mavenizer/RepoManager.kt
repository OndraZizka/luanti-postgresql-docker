package ch.zizka.luanti.mavenizer

import org.luanti.mavenizer.ContentDbClient
import java.io.File
import java.util.concurrent.TimeUnit

class RepoManager(
    private val workingDir: File,
    private val gpgKeyName: String,
    private val localInstall: Boolean = false,
) {
    private val client = ContentDbClient()
    private val pomGen = PomGenerator()
    private val groupIdPrefix = "ch.zizka.luanti.mods"
    private val contentDbBase = "https://content.luanti.org/packages"

    fun syncAll() {
        println("--- Starting Sync ---")
        val packages = client.fetchAllPackages()
        println("Found ${packages.size} mods.")

        packages.forEach { pkg ->
            processPackage(pkg.author, pkg.name)
        }
    }

    /** Mavenize and deploy a single ContentDB package: the given release, or the latest one if [releaseId] is null. */
    fun syncOne(author: String, name: String, releaseId: Int? = null) {
        processPackage(author, name, releaseId)
    }

    private fun processPackage(author: String, name: String, releaseId: Int? = null) {
        val details = client.fetchPackageDetails(author, name)
        if (details == null) {
            println("Could not fetch details for $author/$name")
            return
        }

        val groupId = "$groupIdPrefix.${sanitizeSegment(author)}"
        val artifactId = sanitizeSegment(name)
        val moduleDir = File(workingDir, "$author/$name")
        val contentDbUrl = "$contentDbBase/$author/$name/"

        val latestRelease = if (releaseId != null) details.releases.firstOrNull { it.id == releaseId }
                            else details.releases.maxByOrNull { it.id }
        if (latestRelease == null) {
            println("No ${if (releaseId != null) "release $releaseId" else "releases"} found for $author/$name")
            return
        }

        val version = latestRelease.id.toString()
        if (isDeployed(groupId, artifactId, version)) {
            println("$groupId:$artifactId:$version already deployed, skipping.")
            return
        }

        println(">>> New Version Found: $groupId:$artifactId:$version")
        pomGen.generatePom(
            ModPomInfo(
                groupId = groupId,
                artifactId = artifactId,
                version = version,
                title = details.title ?: name,
                description = details.short_description ?: (details.title ?: name),
                contentDbUrl = contentDbUrl,
                downloadUrl = "${contentDbUrl}releases/${latestRelease.id}/download/",
                license = details.license,
                scmUrl = details.repo,
                author = author,
                gpgKeyName = gpgKeyName,
            ),
            moduleDir,
        )
        deploy(moduleDir, version)
    }

    private fun deploy(moduleDir: File, version: String) {
        // GPG_PASSPHRASE is read by ~/.m2/settings.xml (gpg.passphrase = ${env.GPG_PASSPHRASE}).
        // It is never written to disk or passed as a command-line argument here.
        val process = ProcessBuilder(if (localInstall) listOf("mvn", "clean", "install", "-Dgpg.skip=true") else listOf("mvn", "clean", "deploy", "-P", "ossrh"))
            .directory(moduleDir)
            .redirectErrorStream(true)
            .start()

        process.inputStream.bufferedReader().lines().forEach { println(it) }
        val completed = process.waitFor(10, TimeUnit.MINUTES)
        if (!completed || process.exitValue() != 0) {
            println("Failed to deploy $moduleDir version $version")
        } else {
            println("Successfully deployed version $version")
        }
    }

    // A simple heuristic to check if we already processed this.
    // Ideally, query your Maven Repo (Nexus/Artifactory) API.
    private fun isDeployed(g: String, a: String, v: String): Boolean {
        return false
    }

    private fun sanitizeSegment(input: String) = input.replace(Regex("[^A-Za-z0-9_\\-.]"), "_")
}
