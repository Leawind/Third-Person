pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net")
        maven("https://maven.neoforged.net/releases/")
    }
}
plugins {
    id("gg.meza.stonecraft") version "1.10.+"
    id("dev.kikugie.stonecutter") version "0.9.+"
}

stonecutter {
    centralScript = "build.gradle.kts"
    kotlinController = true
    shared {
        fun mc(version: String, vararg loaders: String) {
            for (loader in loaders) {
                version("$version-$loader", version)
            }
        }

        // mc("1.16.5", "fabric", "forge")
        // mc("1.19.4", "fabric", "forge")
        // mc("1.20.1", "fabric", "forge")
        mc("1.21.11", "fabric", "neoforge")
        // mc("26.1.2", "fabric", "neoforge")

        vcsVersion = "1.21.11-fabric"
    }
    create(rootProject)
}

rootProject.name = "leawind_third_person"
