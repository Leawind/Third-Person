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
        val basePrefix = "io/github/leawind/thirdperson/internal/base/"
        val baseApiPrefix = "io/github/leawind/thirdperson/internal/base/api/"
        val baseCorePrefix = "io/github/leawind/thirdperson/internal/base/core/"
        val schedulerPrefix = "io/github/leawind/thirdperson/internal/scheduler/"
        val bridgePrefix = "io/github/leawind/thirdperson/internal/bridge/"
        val bootstrapPrefix = "io/github/leawind/thirdperson/internal/bootstrap/"
        val basePackage = "io.github.leawind.thirdperson.internal.base."
        val baseApiPackage = "io.github.leawind.thirdperson.internal.base.api."
        val baseCorePackage = "io.github.leawind.thirdperson.internal.base.core."
        val schedulerPackage = "io.github.leawind.thirdperson.internal.scheduler."
        val forbiddenBridgeImports = listOf(
            "io.github.leawind.thirdperson.api.",
            basePackage,
            "io.github.leawind.thirdperson.internal.bootstrap.",
            schedulerPackage,
        )
        val violations = mutableListOf<String>()

        fileTree(sourceRoot).matching { include("**/*.java") }.files.sorted().forEach { source ->
            val relativePath = sourceRoot.asFile.toPath().relativize(source.toPath()).toString()
            val isBase = relativePath.startsWith(basePrefix)
            val isBaseApi = relativePath.startsWith(baseApiPrefix)
            val isBaseCore = relativePath.startsWith(baseCorePrefix)
            val isScheduler = relativePath.startsWith(schedulerPrefix)
            val isBridge = relativePath.startsWith(bridgePrefix)
            val isBootstrap = relativePath.startsWith(bootstrapPrefix)

            source.readLines().forEachIndexed { index, line ->
                if ((isBaseApi || isBaseCore)
                    && (line.contains("/*?") || line.contains("/^?"))
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: base api/core must not use Stonecutter macros"
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

                if (isBaseCore
                    && !imported.startsWith("java.")
                    && !imported.startsWith("org.joml.")
                    && !imported.startsWith(baseApiPackage)
                    && !imported.startsWith(baseCorePackage)
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: base core must not import $imported"
                    )
                }
                if (isBaseApi
                    && !imported.startsWith("java.")
                    && !imported.startsWith("org.joml.")
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: base api must not import $imported"
                    )
                }
                if (isScheduler
                    && imported.startsWith(basePackage)
                    && !imported.startsWith(baseApiPackage)
                ) {
                    violations.add(
                        "$relativePath:${index + 1}: scheduler may only import the base api, not $imported"
                    )
                }
                if (isBridge && forbiddenBridgeImports.any(imported::startsWith)) {
                    violations.add("$relativePath:${index + 1}: bridge must not import $imported")
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
