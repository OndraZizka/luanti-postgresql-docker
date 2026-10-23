package ch.zizka.luanti.mavenizer.tests

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.images.PullPolicy
import org.testcontainers.utility.DockerImageName
import java.time.Duration

class ServerIntegrationTest {

    // Must match the docker image built by luanti-mavenizer/sample-server-project
    // (docker-maven-plugin <name>my-luanti-server</name> -> tag defaults to "latest").
    private val imageName = "my-luanti-server:latest"

    @Test
    fun `server starts and loads bakedclay mod`() {

        // 1. Configure Container
        val luantiServer = GenericContainer(DockerImageName.parse(imageName))
            .withImagePullPolicy(PullPolicy.defaultPolicy()) // Use local image if available
            .withExposedPorts(30000) // UDP port
            .withStartupTimeout(Duration.ofSeconds(60))
            // KEY STRATEGY: Wait for the log message that confirms the server loop started
            .waitingFor(
                Wait.forLogMessage(".*Server started.*", 1)
            )

        try {
            // 2. Start Container
            luantiServer.start()

            // 3. Get Logs
            val logs = luantiServer.logs

            println("--- CONTAINER LOGS START ---")
            println(logs)
            println("--- CONTAINER LOGS END ---")

            // 4. Assertions

            // Check if the server is technically running
            assertTrue(luantiServer.isRunning, "Container should be running")

            // Check if our specific Maven-dependency mod was loaded
            // Luanti logs usually look like: "[Main]:    bakedclay [0.1] by TenPlus1"
            val modLoaded = logs.contains("bakedclay", ignoreCase = true)
            assertTrue(modLoaded, "The 'bakedclay' mod should be present in the server logs")

            // Optional: Check for errors
            val hasErrors = logs.contains("ERROR[Main]", ignoreCase = true)
            // Note: Sometimes mods output errors that are warnings, so be careful asserting false here
            if (hasErrors) {
                println("WARNING: Errors detected in server logs!")
            }
        }
        finally {
            luantiServer.stop()
        }
    }
}