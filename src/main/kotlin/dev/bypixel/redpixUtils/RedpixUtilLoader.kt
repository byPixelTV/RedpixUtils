package dev.bypixel.redpixUtils

import com.google.gson.Gson
import io.papermc.paper.plugin.loader.PluginClasspathBuilder
import io.papermc.paper.plugin.loader.PluginLoader
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.stream.Stream

class RedpixUtilLoader: PluginLoader {
    override fun classloader(classpathBuilder: PluginClasspathBuilder) {
        val resolver = MavenLibraryResolver()
        val pluginLibraries = load()
        pluginLibraries.asDependencies().forEach { resolver.addDependency(it) }
        pluginLibraries.asRepositories().forEach { resolver.addRepository(it) }
        classpathBuilder.addLibrary(resolver)
    }

    fun load(): PluginLibraries {
        return try {
            val inputStream = javaClass.getResourceAsStream("/paper-libraries.json")
            Gson().fromJson(InputStreamReader(inputStream, StandardCharsets.UTF_8), PluginLibraries::class.java)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    data class PluginLibraries(val repositories: Map<String, String>, val dependencies: List<String>) {
        fun asDependencies(): Stream<Dependency> {
            return dependencies.stream().map { Dependency(DefaultArtifact(it), null) }
        }

        fun asRepositories(): Stream<RemoteRepository> {
            return repositories.entries.stream().map {
                if (it.value.contains("https://repo1.maven.org/maven2") || it.value.contains("http://repo1.maven.org/maven2") || it.value.contains("https://repo.maven.apache.org/maven2") || it.value.contains("http://repo.maven.apache.org/maven2")) {
                    RemoteRepository.Builder("central", "default", MavenLibraryResolver.MAVEN_CENTRAL_DEFAULT_MIRROR).build()
                } else {
                    RemoteRepository.Builder(it.key, "default", it.value).build()
                }
            }
        }
    }
}