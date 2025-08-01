import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    kotlin("jvm") version "2.2.0"
    id("com.gradleup.shadow") version "9.0.0-rc3"
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

val versionString = "1.0.0"

group = "dev.bypixel"
version = versionString

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.6-R0.1-SNAPSHOT")
    library("dev.jorel:commandapi-bukkit-shade-mojang-mapped:10.1.2")
    library("dev.jorel:commandapi-bukkit-kotlin:10.1.2")
    compileOnly("net.luckperms:api:5.5")
    compileOnly("me.clip:placeholderapi:2.11.6")
    library(kotlin("stdlib"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks {
    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.21.6")

        downloadPlugins {
            url("https://cdn.modrinth.com/data/P1OZGk5p/versions/g0LZEAJl/ViaVersion-5.5.0-SNAPSHOT.jar")
            url("https://cdn.modrinth.com/data/NpvuJQoq/versions/jg29d9Wx/ViaBackwards-5.4.3-SNAPSHOT.jar")
            // papi
            url("https://cdn.bypixel.dev/raw/bhu7zi.jar")
            // luckperms
            url("https://download.luckperms.net/1595/bukkit/loader/LuckPerms-Bukkit-5.5.10.jar")
        }
    }

    compileJava {
        options.encoding = "UTF-8"
        options.release.set(21)
    }

    shadowJar {
        archiveBaseName.set("RedpixUtils")
        archiveVersion.set(versionString)
        archiveClassifier.set("")
    }
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks.jar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

// if you have shadowJar configured
tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
}

paper {
    main = "dev.bypixel.redpixUtils.RedpixUtils"

    loader = "dev.bypixel.redpixUtils.RedpixUtilLoader"
    hasOpenClassloader = false

    generateLibrariesJson = true

    authors = listOf("byPixelTV")

    apiVersion = "1.21.6"

    version = versionString

    foliaSupported = true

    prefix = "RepixUtils"

    serverDependencies {
        register("LuckPerms") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
        register("PlaceholderAPI") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            joinClasspath = true
        }
    }
}

kotlin {
    jvmToolchain(21)
}