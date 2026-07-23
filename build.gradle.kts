import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.isxander.modstitch.base")
    `maven-publish`
    id("me.modmuss50.mod-publish-plugin")
}

// ========== Versions & Project Info ==========
val mcVersion: String by project
val modVersionString = property("mod.version")!!.toString()

val isFabric = modstitch.isLoom
val isNeoforge = modstitch.isModDevGradleRegular
val isForge = modstitch.isModDevGradleLegacy
val loader = when {
    isFabric -> "fabric"
    isNeoforge -> "neoforge"
    isForge -> "forge"
    else -> error("Unknown loader")
}

val supportsUnitTesting = isFabric || (isNeoforge && stonecutter.current.parsed >= "1.20.5")

// ========== ModStitch Setup ==========
modstitch {
    minecraftVersion = mcVersion

    loom {
        prop("deps.fabricLoader") { fabricLoaderVersion = it }
    }

    moddevgradle {
        prop("deps.neoforge") { neoForgeVersion = it }
        prop("deps.forge") { forgeVersion = it }
    }

    metadata {
        modId = "leawind_third_person"
        modName = "Leawind's Third Person"
        modVersion = "$modVersionString+$loader-$mcVersion"
        modGroup = "io.github.leawind.thirdperson"
        modDescription = "A practical, smooth, feature-rich third-person mod."
        modLicense = "MIT"
        modAuthor = "Leawind"

        replacementProperties.put("github", "Leawind/Third-Person")
        replacementProperties.put("mc", "*")
        replacementProperties.put("loaderVersion", "*")
    }

    mixin {
        addMixinsToModManifest = true
        configs.register("leawind_third_person")
        if (isFabric) configs.register("leawind_third_person.fabric")
        if (isForge) configs.register("leawind_third_person.forge")
        if (isNeoforge) configs.register("leawind_third_person.neoforge")
    }

    if (supportsUnitTesting) {
        unitTesting()
    }
}

// ========== Stonecutter ==========
stonecutter {
    constants {
        put("fabric", isFabric)
        put("neoforge", isNeoforge)
        put("forge", isForge)
    }

    // ResourceLocation -> Identifier
    replacements.string(current.parsed >= "1.21.11") {
        replace("net.minecraft.resources.ResourceLocation", "net.minecraft.resources.Identifier")
        replace("ResourceLocation", "Identifier")
    }
    // Input -> ClientInput
    replacements.string(current.parsed > "1.21") {
        replace(
            "net.minecraft.client.player.Input",
            "net.minecraft.client.player.ClientInput"
        )
    }
}

// ========== Dependencies ==========
configurations.all {
    resolutionStrategy {
        force("org.apache.logging.log4j:log4j-api:2.24.3")
        force("org.apache.logging.log4j:log4j-core:2.24.3")
    }
}

dependencies {
    // Perspective API (required)
    modstitchModImplementation("maven.modrinth:LIqveQm1:${property("mod.perspective_api_version")}+${loader}-$mcVersion")

    // Fabric API
    prop("deps.fabricApi") { modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:$it") }

    // ModMenu (optional)
    prop("mod.modmenu_version") { modstitchModImplementation("com.terraformersmc:modmenu:$it") }

    // YACL (optional, recommended)
    prop("mod.yacl_version") { modstitchModImplementation("dev.isxander:yet-another-config-lib:$it") }

    // Compile only
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")

    // Test
    testCompileOnly("org.jspecify:jspecify:1.0.0")
    if (!isFabric) {
        testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    }
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:6.0.3")
    testImplementation("com.google.jimfs:jimfs:1.3.0") {
        exclude(group = "com.google.guava", module = "guava")
    }
}

// ========== Tasks ==========
tasks.test {
    useJUnitPlatform()
    if (!supportsUnitTesting) {
        enabled = false
    }
}

if (!supportsUnitTesting) {
    tasks.compileTestJava {
        enabled = false
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

java {
    withSourcesJar()
}

tasks.withType<Jar>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}
tasks.withType<ProcessResources>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

if (isForge) {
    tasks.named<ProcessResources>("processResources") {
        exclude("leawind_third_person.refmap.json")
    }
}

// ========== Publishing ==========
val buildAndCollect by tasks.registering(Copy::class) {
    group = "build"
    dependsOn(modstitch.finalJarTask, tasks.named("sourcesJar"))
    from(modstitch.finalJarTask.flatMap { it.archiveFile })
    from(tasks.named("sourcesJar").flatMap { (it as org.gradle.jvm.tasks.Jar).archiveFile })
    into(rootProject.layout.buildDirectory.dir("libs"))
}

val changelogFile = rootProject.file("CHANGELOG.md")
val changelogText = if (changelogFile.exists()) changelogFile.readText() else ""

afterEvaluate {
    publishMods {
        dryRun.set(System.getenv("DRY_RUN") != "false")
        displayName.set("$modVersionString for $mcVersion $loader")
        file = modstitch.finalJarTask.flatMap { it.archiveFile }
        additionalFiles.from(tasks.named("sourcesJar"))
        changelog.set(changelogText)

        type = if (modVersionString.contains("beta", true)) {
            BETA
        } else if (modVersionString.contains("alpha", true)) {
            ALPHA
        } else {
            STABLE
        }

        modLoaders.add(loader)
        modrinth {
            accessToken = System.getenv("MODRINTH_TOKEN")
            projectId = System.getenv("MODRINTH_ID")
            minecraftVersions.add(mcVersion)
            if (isFabric) {
                optional { slug.set("modmenu") }
            }
        }
        curseforge {
            accessToken = System.getenv("CURSEFORGE_TOKEN")
            projectId = System.getenv("CURSEFORGE_ID")
            minecraftVersions.add(mcVersion)
            clientRequired = true
            serverRequired = false
            if (isFabric) {
                optional { slug.set("modmenu") }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "leawind_third_person"
            version = "$modVersionString+$loader-$mcVersion"
            from(components["java"])
            pom {
                name.set("Leawind's Third Person")
                description.set("A practical, smooth, feature-rich third-person mod.")
            }
        }
    }
    repositories {
        mavenLocal()
    }
}

fun <T> prop(property: String, block: (String) -> T?): T? {
    return findProperty(property)?.toString()?.takeIf { it.isNotBlank() }?.let(block)
}
