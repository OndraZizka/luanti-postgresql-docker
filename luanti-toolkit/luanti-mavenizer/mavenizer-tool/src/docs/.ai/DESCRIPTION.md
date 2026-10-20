
# Project description

I would like to mavenize the all Luanti mods, in order to simplify creating a Luanti server docker image.

For docker operations, use Fabric8 docker maven plugin. https://dmp.fabric8.io/

The mods would get a groupId `ch.zizka.luanti.mods.<author>` and artifactId `mod-<author>-<id>`, and the version would be the ContentDB numerical ID.

For instance: Here's one mod:
https://content.luanti.org/packages/TenPlus1/bakedclay/
There's a button labeled "Download". The link leads to
`https://content.luanti.org/packages/TenPlus1/bakedclay/releases/31934/download/`
which is redirected to a zip file.

I would like to have a maven project with a module for each of mods in the ContentDB (content.luanti.org), and repackage each release and upload it to Maven Central.

So, create such project, and create a Kotlin package which will crawl through that server and create such a maven submodule for each Luanti mod. And then another package, which will check ContentDB for a new version to repackage. The maven submodule will then have a profile which will download the package as per a pre-set property, repackage, and if so, it will download it, create a jar, and upload to Maven Central.
