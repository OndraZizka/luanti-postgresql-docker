package ch.zizka.luanti.mavenizer

import java.io.File

fun main(args: Array<String>) {
    // Configuration
    val targetDir = File("generated-mods-project")
    val gpgKeyName = System.getenv("GPG_KEYNAME") ?: "73BA361CA92D829800730DE207838E30786B5257"

    val manager = RepoManager(targetDir, gpgKeyName)

    // Usage: mavenizer <author>/<name>   -> mavenize + deploy the latest release of one package
    //        mavenizer                   -> crawl and sync all packages from ContentDB
    if (args.isNotEmpty()) {
        val (author, name) = args[0].split("/", limit = 2).let {
            require(it.size == 2) { "Expected argument in the form <author>/<name>, got: ${args[0]}" }
            it[0] to it[1]
        }
        manager.syncOne(author, name)
    } else {
        manager.syncAll()
    }
}