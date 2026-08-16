plugins {
    id("dev.kikugie.stonecutter")

    id("dev.isxander.modstitch.base") version "0.8.4" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.0" apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
}

stonecutter active "26.2-fabric"

val checkArchitecture by tasks.registering {
    group = "verification"
    description = "Checks custom source architecture and Mixin compatibility constraints."

    val sourceRoot = layout.projectDirectory.dir("src/main/java")
    val resourceRoot = layout.projectDirectory.dir("src/main/resources")
    inputs.dir(sourceRoot)
    inputs.dir(resourceRoot)

    doLast {
        val apiPrefix = "io/github/leawind/thirdperson/api/"
        val corePrefix = "io/github/leawind/thirdperson/internal/core/"
        val logicPrefix = "io/github/leawind/thirdperson/internal/logic/"
        val extensionPrefix = "io/github/leawind/thirdperson/internal/extension/"
        val minecraftExtensionPrefix = "${extensionPrefix}minecraft/"
        val sableExtensionPrefix = "${extensionPrefix}sable/"
        val internalPackage = "io.github.leawind.thirdperson.internal."
        val interactionOriginIndependentFiles = setOf(
            "io/github/leawind/thirdperson/internal/logic/base/MinecraftCameraRaycasting.java",
            "io/github/leawind/thirdperson/internal/logic/base/MinecraftPlayerRotationTargeting.java",
        )
        val violations = mutableListOf<String>()

        val mixinConfigs =
            fileTree(resourceRoot)
                .matching { include("**/*.mixins.json") }
                .files
                .map { resourceRoot.asFile.toPath().relativize(it.toPath()).toString() }
                .sorted()
        if (mixinConfigs != listOf("leawind_third_person.mixins.json")) {
            violations.add(
                "src/main/resources: expected only leawind_third_person.mixins.json, found $mixinConfigs"
            )
        }

        fileTree(sourceRoot).matching { include("**/*.java") }.files.sorted().forEach { source ->
            val relativePath = sourceRoot.asFile.toPath().relativize(source.toPath()).toString()
            val isApi = relativePath.startsWith(apiPrefix)
            val isCore = relativePath.startsWith(corePrefix)
            val isLogic = relativePath.startsWith(logicPrefix)
            val isExtension = relativePath.startsWith(extensionPrefix)
            val isMinecraftExtension = relativePath.startsWith(minecraftExtensionPrefix)
            val isSableExtension = relativePath.startsWith(sableExtensionPrefix)
            val isExtensionPoint = isExtension && !isMinecraftExtension && !isSableExtension
            val mustIgnoreInteractionOrigin = relativePath in interactionOriginIndependentFiles

            source.readLines().forEachIndexed { index, line ->
                if (line.contains("@Redirect") || line.contains(".injection.Redirect")) {
                    violations.add(
                        "$relativePath:${index + 1}: Mixin @Redirect is forbidden; use a composable injector"
                    )
                }
                if (line.contains("@ModifyArgs")
                    || line.contains(".injection.ModifyArgs")
                    || line.contains(".injection.invoke.arg.Args")
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: Mixin @ModifyArgs is incompatible with Forge 1.20.1; use an injector that does not generate synthetic Args classes"
                    )
                }

                if ((isApi || isCore || isLogic || isExtensionPoint)
                    && (line.contains("/*?") || line.contains("/^?"))
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: api/core/logic/extension points must not use Stonecutter macros"
                    )
                }

                if (mustIgnoreInteractionOrigin && line.contains("raycastOrigin(")) {
                    violations.add(
                        "$relativePath:${index + 1}: camera intent and rotation targeting must not read RaycastOrigin"
                    )
                }

                val importIndex = line.indexOf("import ")
                if (importIndex < 0
                    || !line.substring(0, importIndex).all {
                        it.isWhitespace() || it == '/' || it == '*' || it == '^'
                    }
                    || !line.substring(importIndex).contains(';')
                ) {
                    return@forEachIndexed
                }
                val imported =
                    line.substring(importIndex + "import ".length)
                        .substringBefore(';')
                        .removePrefix("static ")
                        .trim()

                if (isApi && imported.startsWith(internalPackage)) {
                    violations.add(
                        "$relativePath:${index + 1}: public api must not import $imported"
                    )
                }
            }
        }

        if (violations.isNotEmpty()) {
            throw GradleException(
                buildString {
                    appendLine("Architecture boundary violations:")
                    violations.forEach { appendLine("- $it") }
                }.trimEnd()
            )
        }
    }
}

val buildAndCollect by tasks.registering(Sync::class) {
    group = "build"
    description = "Builds and collects all distributable jars."
    dependsOn(checkArchitecture)
    into(layout.buildDirectory.dir("libs"))
}

allprojects {
    repositories {
        mavenCentral()

        exclusiveContent {
            forRepository {
                maven("https://maven.gnomecraft.net/releases") {
                    name = "GnomeCraft (Terraformers Mirror)"
                }
            }
            filter { includeGroup("com.terraformersmc") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.isxander.dev/releases") }
            filter { includeGroup("dev.isxander") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.ryanhcode.dev/releases") }
            filter { includeGroup("dev.ryanhcode.sable-companion") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.quiltmc.org/repository/release") }
            filter { includeGroup("org.quiltmc.parsers") }
        }
        maven("https://maven.neoforged.net/releases/") {
            content { includeGroupByRegex("net\\.neoforged(\\..*)?") }
        }
        maven("https://maven.minecraftforge.net/") {
            content { includeGroupByRegex("net\\.minecraftforge(\\..*)?") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.nucleoid.xyz") }
            filter { includeGroupByRegex("eu\\.pb4(\\..*)?") }
        }
        exclusiveContent {
            forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") }
            filter { includeGroup("thedarkcolour") }
        }
    }
}
