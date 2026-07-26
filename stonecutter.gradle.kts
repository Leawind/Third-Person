plugins {
    id("dev.kikugie.stonecutter")

    val modstitchVersion = "0.8.4"
    id("dev.isxander.modstitch.base") version modstitchVersion apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false

    id("me.modmuss50.mod-publish-plugin") version "0.8.4" apply false
}

stonecutter active "26.2-fabric"

val checkArchitecture by tasks.registering {
    group = "verification"
    description = "Checks source package dependency boundaries."

    val sourceRoot = layout.projectDirectory.dir("src/main/java")
    inputs.dir(sourceRoot)

    doLast {
        val corePrefix = "io/github/leawind/thirdperson/internal/core/"
        val applicationPrefix = "io/github/leawind/thirdperson/internal/application/"
        val bridgePrefix = "io/github/leawind/thirdperson/internal/bridge/"
        val integrationPrefix = "io/github/leawind/thirdperson/internal/integration/"
        val bootstrapPrefix = "io/github/leawind/thirdperson/internal/bootstrap/"
        val ownApplicationPrefix = "io.github.leawind.thirdperson.internal.application."
        val ownCorePrefix = "io.github.leawind.thirdperson.internal.core."
        val forbiddenBridgeImports = listOf(
            "io.github.leawind.thirdperson.api.",
            "io.github.leawind.thirdperson.internal.application.",
            "io.github.leawind.thirdperson.internal.bootstrap.",
            "io.github.leawind.thirdperson.internal.core.",
            "io.github.leawind.thirdperson.internal.integration.",
        )
        val violations = mutableListOf<String>()

        fileTree(sourceRoot).matching { include("**/*.java") }.files.sorted().forEach { source ->
            val relativePath = sourceRoot.asFile.toPath().relativize(source.toPath()).toString()
            val isCore = relativePath.startsWith(corePrefix)
            val isApplication = relativePath.startsWith(applicationPrefix)
            val isBridge = relativePath.startsWith(bridgePrefix)
            val isIntegration = relativePath.startsWith(integrationPrefix)
            val isBootstrap = relativePath.startsWith(bootstrapPrefix)

            source.readLines().forEachIndexed { index, line ->
                if ((isCore || isApplication) && (line.contains("/*?") || line.contains("/^?"))) {
                    violations.add(
                        "$relativePath:${index + 1}: core/application must not use Stonecutter macros"
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

                if (isCore
                    && !imported.startsWith("java.")
                    && !imported.startsWith("org.joml.")
                    && !imported.startsWith(ownCorePrefix)
                ) {
                    violations.add("$relativePath:${index + 1}: core must not import $imported")
                }
                if (isApplication
                    && !imported.startsWith("java.")
                    && !imported.startsWith("org.joml.")
                    && !imported.startsWith(ownApplicationPrefix)
                    && !imported.startsWith(ownCorePrefix)
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: application must not import $imported"
                    )
                }
                if (isBridge && forbiddenBridgeImports.any(imported::startsWith)) {
                    violations.add("$relativePath:${index + 1}: bridge must not import $imported")
                }
                if (isIntegration
                    && (imported.startsWith("io.github.leawind.thirdperson.internal.bootstrap.")
                        || imported.startsWith("io.github.leawind.thirdperson.platform."))
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: integration must not import $imported"
                    )
                }
                if (isBootstrap && imported.startsWith("io.github.leawind.thirdperson.platform.")) {
                    violations.add("$relativePath:${index + 1}: bootstrap must not import $imported")
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
