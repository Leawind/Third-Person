import org.gradle.jvm.tasks.Jar

plugins {
    id("dev.kikugie.stonecutter")
    id("dev.isxander.modstitch.base")
    `maven-publish`
    id("me.modmuss50.mod-publish-plugin")
}

// region Versions & Project Info
val mcVersion: String by project
val modGroupValue = requiredProp("mod.group")
val modIdValue = requiredProp("mod.id")
val modVersionValue = requiredProp("mod.version")
val modNameValue = requiredProp("mod.name")
val modDescriptionValue = requiredProp("mod.description")
val modAuthorValue = requiredProp("mod.author")
val modLicenseValue = requiredProp("mod.license")
val modSourceUrlValue = requiredProp("mod.source_url")
val minecraftDependency = requiredProp("meta.mcDep")

val isFabric = modstitch.isLoom
val isNeoforge = modstitch.isModDevGradleRegular
val isForge = modstitch.isModDevGradleLegacy
val loader = when {
    isFabric -> "fabric"
    isNeoforge -> "neoforge"
    isForge -> "forge"
    else -> error("Unknown loader")
}
// Extra version labels are declared by each version variant for publishing platforms.
val publishedMinecraftVersions = buildList {
    add(mcVersion)
    findProperty("publish.additionalMcVersions")
        ?.toString()
        ?.split(',')
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        ?.forEach(::add)
}.distinct()

val supportsUnitTesting = isFabric || (isNeoforge && stonecutter.current.parsed >= "1.20.5")
// endregion

modstitch {
    minecraftVersion = mcVersion

    loom {
        if (isFabric) {
            fabricLoaderVersion = requiredProp("deps.fabricLoader")
            configureLoom {
                runs.named("client") {
                    runDir = project.relativePath(rootProject.file("run"))
                }
            }
        }
    }

    if (!isFabric) {
        runs {
            register("client") {
                client()
                gameDirectory.set(rootProject.layout.projectDirectory.dir("run"))
            }
        }
    }

    moddevgradle {
        if (isNeoforge) {
            neoForgeVersion = requiredProp("deps.neoforge")
        }
        if (isForge) {
            forgeVersion = requiredProp("deps.forge")
        }
    }


    metadata {
        modId = modIdValue
        modName = modNameValue
        modVersion = "$modVersionValue+$loader-$mcVersion"
        modGroup = modGroupValue
        modDescription = modDescriptionValue
        modLicense = modLicenseValue
        modAuthor = modAuthorValue

        replacementProperties.put("github", modSourceUrlValue.removePrefix("https://github.com/"))
        replacementProperties.put("mc", minecraftDependency)
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

// region Dependencies
// Forge's transitive `net.minecraftforge:unsafe` dependency uses the dynamic version `2.11.+`.
if (isForge || (isNeoforge && stonecutter.current.parsed < "1.21")) {
    configurations.configureEach {
        resolutionStrategy {
            force("org.apache.logging.log4j:log4j-api:2.24.3")
            force("org.apache.logging.log4j:log4j-core:2.24.3")
        }
    }
}

dependencies {
    // Perspective API (required)
    modstitchModImplementation("maven.modrinth:LIqveQm1:${property("mod.perspective_api_version")}+${loader}-$mcVersion")

    // Fabric API
    optionalProp("deps.fabricApi") { modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:$it") }

    // ModMenu (optional)
    optionalProp("mod.modmenu_version") { modstitchModImplementation("com.terraformersmc:modmenu:$it") }

    // YACL (optional, recommended)
    optionalProp("mod.yacl_version") { modstitchModImplementation("dev.isxander:yet-another-config-lib:$it") }

    // Compile only
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("org.jetbrains:annotations:24.0.1")
    // Perspective API already provides MixinExtras at runtime; Forge does not expose it
    // transitively on this mod's compile classpath.
    compileOnly("io.github.llamalad7:mixinextras-common:0.5.4")
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
// endregion

// region Tasks

tasks.withType<JavaExec>().configureEach {
    if (name == "runClient") {
        workingDir(rootProject.layout.projectDirectory.dir("run"))
    }
}

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

tasks.withType<JavaCompile>().configureEach {
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
// endregion

// region Publishing
val sourcesJar = tasks.named<Jar>("sourcesJar")
rootProject.tasks.named<Sync>("buildAndCollect") {
    dependsOn(modstitch.finalJarTask, sourcesJar)
    from(modstitch.finalJarTask.flatMap { it.archiveFile })
    from(sourcesJar.flatMap { it.archiveFile })
}

val changelogFile = rootProject.file("CHANGELOG.md")
val changelogText = if (changelogFile.exists()) changelogFile.readText() else ""

afterEvaluate {
    publishMods {
        dryRun.set(System.getenv("DRY_RUN") != "false")
        displayName.set("$modVersionValue for $mcVersion $loader")
        file = modstitch.finalJarTask.flatMap { it.archiveFile }
        additionalFiles.from(tasks.named("sourcesJar"))
        changelog.set(changelogText)

        type = if (modVersionValue.contains("beta", true)) {
            BETA
        } else if (modVersionValue.contains("alpha", true)) {
            ALPHA
        } else {
            STABLE
        }

        modLoaders.add(loader)
        modrinth {
            accessToken = System.getenv("MODRINTH_TOKEN")
            projectId = System.getenv("MODRINTH_ID")
            minecraftVersions.addAll(publishedMinecraftVersions)
            if (isFabric) {
                optional { slug.set("modmenu") }
            }
        }
        curseforge {
            accessToken = System.getenv("CURSEFORGE_TOKEN")
            projectId = System.getenv("CURSEFORGE_ID")
            minecraftVersions.addAll(publishedMinecraftVersions)
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
            artifactId = modIdValue
            version = "$modVersionValue+$loader-$mcVersion"
            from(components["java"])
            pom {
                name.set(modNameValue)
                description.set(modDescriptionValue)
            }
        }
    }
    repositories {
        mavenLocal()
    }
}
// endregion

// region Helpers
fun requiredProp(property: String): String =
    findProperty(property)?.toString()?.takeIf { it.isNotBlank() }
        ?: error("Required Gradle property '$property' is missing or blank")

fun <T> optionalProp(property: String, block: (String) -> T?): T? {
    return findProperty(property)?.toString()?.takeIf { it.isNotBlank() }?.let(block)
}
// endregion
