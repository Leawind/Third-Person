plugins {
    id("dev.kikugie.stonecutter")

    id("dev.isxander.modstitch.base") version "0.8.4" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.0" apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
}

stonecutter active "26.2-fabric"

val checkArchitecture by tasks.registering {
    group = "verification"
    description = "Checks source architecture and Mixin compatibility constraints."

    val sourceRoot = layout.projectDirectory.dir("src/main/java")
    inputs.dir(sourceRoot)

    doLast {
        val apiPrefix = "io/github/leawind/thirdperson/api/"
        val logicPrefix = "io/github/leawind/thirdperson/internal/logic/"
        val basePrefix = "${logicPrefix}base/"
        val schedulerPrefix = "${logicPrefix}scheduler/"
        val bridgePrefix = "io/github/leawind/thirdperson/internal/bridge/"
        val utilsPrefix = "io/github/leawind/thirdperson/internal/utils/"
        val basePackage = "io.github.leawind.thirdperson.internal.logic.base."
        val schedulerPackage = "io.github.leawind.thirdperson.internal.logic.scheduler."
        val logicPackage = "io.github.leawind.thirdperson.internal.logic."
        val internalPackage = "io.github.leawind.thirdperson.internal."
        val platformPackage = "io.github.leawind.thirdperson.platform."
        val platformApiPackage = "${platformPackage}api."
        val baseCategories = setOf("camera", "math", "rotation")
        val schedulerCategories =
            setOf("aiming", "camera", "config", "hud", "input", "rotation", "sound", "state")
        val allowedBaseImports = setOf(
            "${basePackage}BaseParameters",
            "${basePackage}RaycastOrigin",
            "${basePackage}ThirdPersonBase",
            "${basePackage}camera.CameraProfile",
            "${basePackage}camera.CameraSmoothingParameters",
            "${basePackage}rotation.LookRotation",
            "${basePackage}rotation.PlayerRotationMode",
            "${basePackage}rotation.PlayerRotationParameters",
            "${basePackage}rotation.PlayerRotationSmoothing",
        )
        val legacyLayerPrefixes = listOf(
            "io/github/leawind/thirdperson/internal/base/",
            "io/github/leawind/thirdperson/internal/scheduler/",
            "io/github/leawind/thirdperson/internal/core/",
            "io/github/leawind/thirdperson/internal/application/",
            "io/github/leawind/thirdperson/internal/integration/",
            "io/github/leawind/thirdperson/internal/persistence/",
            "io/github/leawind/thirdperson/internal/configscreen/",
            "io/github/leawind/thirdperson/internal/bootstrap/",
        )
        val forbiddenBridgeImports = listOf(
            "io.github.leawind.thirdperson.api.",
            logicPackage,
        )
        val violations = mutableListOf<String>()

        fileTree(sourceRoot).matching { include("**/*.java") }.files.sorted().forEach { source ->
            val relativePath = sourceRoot.asFile.toPath().relativize(source.toPath()).toString()
            val isApi = relativePath.startsWith(apiPrefix)
            val isLogic = relativePath.startsWith(logicPrefix)
            val isBase = relativePath.startsWith(basePrefix)
            val isScheduler = relativePath.startsWith(schedulerPrefix)
            val isBridge = relativePath.startsWith(bridgePrefix)
            val isUtils = relativePath.startsWith(utilsPrefix)

            if (legacyLayerPrefixes.any(relativePath::startsWith)) {
                violations.add("$relativePath: legacy business-layer package is forbidden")
            }
            if (isBase) {
                val pathWithinLayer = relativePath.removePrefix(basePrefix)
                if (pathWithinLayer.contains('/')) {
                    val category = pathWithinLayer.substringBefore('/')
                    val pathWithinCategory = pathWithinLayer.substringAfter('/')
                    if (category !in baseCategories || pathWithinCategory.contains('/')) {
                        violations.add(
                            "$relativePath: base logic must stay in its root or an approved category"
                        )
                    }
                }
            }
            if (isScheduler) {
                val pathWithinLayer = relativePath.removePrefix(schedulerPrefix)
                if (pathWithinLayer.contains('/')) {
                    val category = pathWithinLayer.substringBefore('/')
                    val pathWithinCategory = pathWithinLayer.substringAfter('/')
                    if (category !in schedulerCategories || pathWithinCategory.contains('/')) {
                        violations.add(
                            "$relativePath: scheduling logic must stay in its root or an approved category"
                        )
                    }
                }
            }
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

                if ((isApi || isLogic)
                    && (line.contains("/*?") || line.contains("/^?"))
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: api/logic must not use Stonecutter macros"
                    )
                }

                if (isBase && line.contains(schedulerPackage)) {
                    violations.add(
                        "$relativePath:${index + 1}: base must not reference the scheduling layer"
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
                if (isScheduler
                    && imported.startsWith(basePackage)
                    && imported !in allowedBaseImports
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: scheduler may not import base implementation $imported"
                    )
                }
                if (isBridge && forbiddenBridgeImports.any(imported::startsWith)) {
                    violations.add("$relativePath:${index + 1}: bridge must not import $imported")
                }
                if (isBridge
                    && imported.startsWith(platformPackage)
                    && !imported.startsWith(platformApiPackage)
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: bridge may only import the platform API, not $imported"
                    )
                }
                if (isUtils && imported.startsWith("io.github.leawind.thirdperson.")) {
                    violations.add("$relativePath:${index + 1}: utils must be business-neutral")
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
