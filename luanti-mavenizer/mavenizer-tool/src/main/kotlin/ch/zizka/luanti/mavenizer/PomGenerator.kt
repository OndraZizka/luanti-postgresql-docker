package ch.zizka.luanti.mavenizer

import java.io.File

/**
 * Metadata needed to render a Maven Central compliant pom.xml for a single
 * Luanti ContentDB mod release, published as a `zip`-typed artifact (no jar
 * repackaging, no sources/javadoc requirement).
 *
 * The pom.xml is regenerated fresh for every release (version and download
 * URL are baked in as literals) because Maven Central's Publisher API
 * validates the raw pom.xml file as staged, and does not resolve
 * property/CI-friendly placeholders such as ${revision} in it.
 */
data class ModPomInfo(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val title: String,
    val description: String,
    val contentDbUrl: String,
    val downloadUrl: String,
    val license: String?,
    val scmUrl: String?,
    val author: String,
    val gpgKeyName: String,
    val autoPublish: Boolean = false,
)

class PomGenerator {

    fun generatePom(modPomInfo: ModPomInfo, outputDir: File) {
        val licenseBlock = if (modPomInfo.license != null) """
    <licenses>
        <license>
            <name>${escape(modPomInfo.license)}</name>
        </license>
    </licenses>""" else ""

        val scmBlock = if (modPomInfo.scmUrl != null) """
    <scm>
        <url>${escape(modPomInfo.scmUrl)}</url>
        <connection>scm:git:${escape(modPomInfo.scmUrl)}</connection>
    </scm>""" else ""

        val pomContent = """
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>${modPomInfo.groupId}</groupId>
    <artifactId>${modPomInfo.artifactId}</artifactId>
    <version>${modPomInfo.version}</version>
    <packaging>pom</packaging>

    <name>${escape(modPomInfo.title)}</name>
    <description>${escape(modPomInfo.description)}</description>
    <url>${escape(modPomInfo.contentDbUrl)}</url>
$licenseBlock

    <developers>
        <developer>
            <id>${escape(modPomInfo.author)}</id>
            <name>${escape(modPomInfo.author)}</name>
        </developer>
    </developers>
$scmBlock

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <gpg.keyname>${modPomInfo.gpgKeyName}</gpg.keyname>
    </properties>

    <build>
        <plugins>
            <!-- Download the ContentDB release zip. -->
            <plugin>
                <groupId>com.googlecode.maven-download-plugin</groupId>
                <artifactId>download-maven-plugin</artifactId>
                <version>1.6.8</version>
                <executions>
                    <execution>
                        <id>download-mod</id>
                        <phase>generate-resources</phase>
                        <goals><goal>wget</goal></goals>
                        <configuration>
                            <url>${escape(modPomInfo.downloadUrl)}</url>
                            <outputFileName>mod.zip</outputFileName>
                            <outputDirectory>${'$'}{project.build.directory}/download</outputDirectory>
                        </configuration>
                    </execution>
                    <!-- Unpack the same zip (served from the plugin's download cache) to build the sources jar. -->
                    <execution>
                        <id>unpack-mod</id>
                        <phase>generate-resources</phase>
                        <goals><goal>wget</goal></goals>
                        <configuration>
                            <url>${escape(modPomInfo.downloadUrl)}</url>
                            <outputFileName>mod.zip</outputFileName>
                            <outputDirectory>${'$'}{project.build.directory}/mod-sources</outputDirectory>
                            <unpack>true</unpack>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Pack the Lua sources (and license/text files) of the mod into a -sources.jar. -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <executions>
                    <execution>
                        <id>sources-jar</id>
                        <phase>package</phase>
                        <goals><goal>jar</goal></goals>
                        <configuration>
                            <classifier>sources</classifier>
                            <classesDirectory>${'$'}{project.build.directory}/mod-sources</classesDirectory>
                            <includes>
                                <include>**/*.lua</include>
                                <include>**/*.md</include>
                                <include>**/*.txt</include>
                                <include>**/*.conf</include>
                                <include>**/*.json</include>
                                <include>**/LICENSE*</include>
                                <include>**/COPYING*</include>
                            </includes>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Attach the raw zip as the main (classifier-less) artifact of type "zip". -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>3.6.2</version>
                <executions>
                    <execution>
                        <id>attach-mod-zip</id>
                        <phase>package</phase>
                        <goals><goal>attach-artifact</goal></goals>
                        <configuration>
                            <artifacts>
                                <artifact>
                                    <file>${'$'}{project.build.directory}/download/mod.zip</file>
                                    <type>zip</type>
                                </artifact>
                            </artifacts>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <!-- Sign the pom and the zip with GPG, required by Maven Central. -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-gpg-plugin</artifactId>
                <version>3.2.8</version>
                <executions>
                    <execution>
                        <id>sign-artifacts</id>
                        <phase>verify</phase>
                        <goals><goal>sign</goal></goals>
                    </execution>
                </executions>
            </plugin>

            <!-- Publish to Maven Central via the Central Portal Publisher API. -->
            <plugin>
                <groupId>org.sonatype.central</groupId>
                <artifactId>central-publishing-maven-plugin</artifactId>
                <version>0.11.0</version>
                <extensions>true</extensions>
                <configuration>
                    <publishingServerId>central</publishingServerId>
                    <autoPublish>${modPomInfo.autoPublish}</autoPublish>
                    <deploymentName>${modPomInfo.groupId}:${modPomInfo.artifactId}:${modPomInfo.version}</deploymentName>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
        """.trimIndent()

        outputDir.mkdirs()
        File(outputDir, "pom.xml").writeText(pomContent)
    }

    private fun escape(s: String) = s
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
}
