package ch.zizka.luanti.mavenizer

import java.io.File

/**
 * Usage: mavenizer [--local] <author>/<name>[/<releaseId>] ...   -> mavenize one or more packages
 *                                                               (given release, or latest if omitted)
 *        mavenizer [--local]                                   -> crawl and sync all packages from ContentDB
 *        mavenizer [--local] --game=<author>/<name>            -> sync all mods supporting that game, e.g. --game=ryvnf/mineclonia
 *
 * With --local, the artifacts are only installed into the local ~/.m2 instead of being deployed.
 */
fun main(args: Array<String>) {
    // Configuration
    val targetDir = File("generated-mods-project")
    val gpgKeyName = System.getenv("GPG_KEYNAME") ?: "73BA361CA92D829800730DE207838E30786B5257"

    val local = "--local" in args
    val game = args.firstOrNull { it.startsWith("--game=") }?.removePrefix("--game=")
    val packages = args.filter { !it.startsWith("--") }
    val manager = RepoManager(targetDir, gpgKeyName, local)

    if (packages.isEmpty())
        manager.syncAll(game)
    else
        manager.syncAll(packages.map { arg ->
            val parts = arg.split("/")
            require(parts.size in 2..3) { "Expected <author>/<name>[/<releaseId>], got: $arg" }
            Triple(parts[0], parts[1], parts.getOrNull(2)?.toInt())
        })
}
