
Build:

`mvn clean install`


Run:

```bash
java -cp target/luanti-mavenizer-1.0.0-SNAPSHOT.jar:target/lib/* org.luanti.mavenizer.MainKt
```


What happens:

* The tool creates a folder generated-mods-project.
* Inside, it creates folders like tenplus1/bakedclay.
* Inside that, it creates a pom.xml.
* It detects that release 31934 is missing.
* It runs mvn deploy in that folder.
* The pom.xml downloads the zip from ContentDB, unzips it into target/classes (so it becomes a JAR content), and uploads the resulting JAR to your defined Maven Repo.