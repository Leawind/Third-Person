import gg.meza.stonecraft.mod
import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("com.gradleup.shadow") version "8.3.10"
    id("gg.meza.stonecraft")
}

val props: Map<String, Any> = project.properties.mapNotNull { (key, value) -> value?.let { key to it } }.toMap()

modSettings {
    // https://stonecraft.meza.gg/docs/configuration

    clientOptions {
        // https://minecraft.wiki/w/Options.txt
        fov = 88
        narrator = false
        musicVolume = 0.0
        guiScale = 3

        additionalLines = mapOf(
            "maxFps" to "60",
            "renderDistance" to "8",
            "simulationDistance" to "5",
            "mouseSensitivity" to "0.22"
        )
    }

    val vars = props
        .filterKeys { it.startsWith("mod.") }
        .mapKeys { it.key.removePrefix("mod.") }
    variableReplacements.putAll(vars)
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    // Mod Menu (Fabric)
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
    // YACL
    maven {
        name = "Xander Maven"
        url = uri("https://maven.isxander.dev/releases")
    }
    // Cloth Config
    maven {
        name = "Shedaniel Maven"
        url = uri("https://maven.shedaniel.me/")
    }
    // KotlinForForge (required by YACL on NeoForge)
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
    // NeoForged
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

val shadowBundle: Configuration by configurations.creating
fun DependencyHandlerScope.shadowBundle(dependencyNotation: String) {
    implementation(dependencyNotation)
    add("shadowBundle", dependencyNotation)
}
dependencies {
    // region mods

    // Fabric API
    if (mod.isFabric) {
        modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("mod.fabric_api_version")}")
    }

    // Architectury API (shared abstraction layer)
    modImplementation("dev.architectury:architectury-${mod.loader}:${project.property("mod.architectury_api_version")}")

    // MixinExtras
    val mixinExtras = "io.github.llamalad7:mixinextras-${mod.loader}:${project.property("mod.mixinextras_version")}"
    include(mixinExtras)
    implementation(mixinExtras)
    annotationProcessor(mixinExtras)

    // Cloth Config API
    modApi("me.shedaniel.cloth:cloth-config-${mod.loader}:${project.property("mod.cloth_config_api_version")}") {
        if (mod.isFabric) {
            exclude(group = "net.fabricmc.fabric-api")
            exclude(module = "modmenu")
        }
    }

    // YACL (Yet Another Config Lib)
    modImplementation("dev.isxander:yet-another-config-lib:${project.property("mod.yacl_mc_version")}-${mod.loader}")

    // ModMenu (Fabric only)
    if (mod.isFabric) {
        modImplementation("com.terraformersmc:modmenu:${project.property("mod.modmenu_version")}")
    }

    // endregion mods

    // region bundled (shadowed)
    shadowBundle("com.github.Leawind:inventory-java:0.2.1")
    // endregion

    // region test
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.google.jimfs:jimfs:1.3.0") {
        // conflict with 1.20.1-forge `guava:32.1.1-jre`
        exclude(group = "com.google.guava", module = "guava")
    }
    // endregion

    // region compile only
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("org.jetbrains:annotations:24.0.1")
    // endregion
}

tasks.shadowJar {
    configurations = listOf(shadowBundle)

    dependsOn(tasks.processResources)
    tasks.findByName("generatePackMCMetaJson")?.let { dependsOn(it) }

    if (tasks.findByName("remapJar") == null) {
        archiveClassifier.set("")
    } else {
        archiveClassifier.set("shadow")
    }

    minimize()

    val dest = "${project.property("mod.group")}.lib"
    // com.github.Leawind:inventory-java
    relocate("io.github.leawind.inventory", "${dest}.inventory")
}

tasks.withType<RemapJarTask>().matching { it.name == "remapJar" }.configureEach {
    dependsOn(tasks.shadowJar)
    inputFile.set(tasks.shadowJar.flatMap { it.archiveFile })
}

val collectJar by tasks.registering(Copy::class) {
    description = "Collect all jars to one directory"
    dependsOn(tasks.assemble)
    from(layout.buildDirectory.dir("libs")) {
        include("*.jar")
        exclude("*-shadow.jar")
    }
    into(rootProject.layout.buildDirectory.dir("all-libs"))
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
    finalizedBy(collectJar)
}

if (mod.isForge) {
    tasks.compileTestJava {
        dependsOn("generatePackMCMetaJson")
    }
}
tasks.test {
    useJUnitPlatform()
}
