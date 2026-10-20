package ch.zizka.luanti.mavenizer

import java.io.File

/**
 * Usage: mavenizer [--local] <author>/<name>[/<releaseId>] ...   -> mavenize one or more packages
 *                                                               (given release, or latest if omitted)
 *        mavenizer [--local]                                   -> crawl and sync all packages from ContentDB
 *
 * With --local, the artifacts are only installed into the local ~/.m2 instead of being deployed.
 */
fun main(args: Array<String>) {
    // Configuration
    val targetDir = File("generated-mods-project")
    val gpgKeyName = System.getenv("GPG_KEYNAME") ?: "73BA361CA92D829800730DE207838E30786B5257"

    val local = "--local" in args
    val packages = args.filter { it != "--local" }
    val manager = RepoManager(targetDir, gpgKeyName, local)

    if (packages.isNotEmpty()) {
        packages.forEach { arg ->
            val parts = arg.split("/")
            require(parts.size in 2..3) { "Expected <author>/<name>[/<releaseId>], got: $arg" }
            manager.syncOne(parts[0], parts[1], parts.getOrNull(2)?.toInt())
        }
    } else {
        manager.syncAll()
    }
}
