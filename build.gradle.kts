import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "2.1.0"
    id("com.gradleup.shadow") version "8.3.0"
}

group = "vip.cdms.allayplugin"
description = "Hello Allay from Kotlin!"
version = "0.1.0-alpha"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
    maven("https://repo.opencollab.dev/maven-releases/")
    maven("https://repo.opencollab.dev/maven-snapshots/")
    maven("https://storehouse.okaeri.eu/repository/maven-public/")
}

dependencies {
    @Suppress("VulnerableLibrariesLocal", "RedundantSuppression")
    compileOnly(group = "org.allaymc.allay", name = "api", version = "master-SNAPSHOT")

    implementation(group = "com.github.MineBuilders", name = "allaymc-kotlinx", version = "master-SNAPSHOT")

    // TODO: uncomment to use kotlin shared lib
    // compileOnly(kotlin("stdlib"))
    // compileOnly(kotlin("stdlib-jdk7"))
    // compileOnly(kotlin("stdlib-jdk8"))
    // compileOnly(kotlin("reflect"))
}

kotlin {
    jvmToolchain(21)
}

tasks.register<Copy>("runServer") {
    outputs.upToDateWhen { false }
    dependsOn("shadowJar")
    val launcherRepo = "https://raw.githubusercontent.com/AllayMC/AllayLauncher/refs/heads/main/scripts"
    val cmdWin = "Invoke-Expression (Invoke-WebRequest -Uri \"${launcherRepo}/install_windows.ps1\").Content"
    val cmdLinux = "wget -qO- ${launcherRepo}/install_linux.sh | bash"
    val cwd = layout.buildDirectory.file("run").get().asFile

    val shadowJar = tasks.named("shadowJar", ShadowJar::class).get()
    from(shadowJar.archiveFile.get().asFile)
    into(cwd.resolve("plugins").apply { mkdirs() })

    val isWindows = System.getProperty("os.name").startsWith("Windows")
    fun launch() = exec {
        workingDir = cwd
        if (isWindows) commandLine("powershell", "-Command", cmdWin)
        else commandLine("sh", "-c", cmdLinux)
    }

    // https://github.com/gradle/gradle/issues/18716  // kill it manually by click X...
    doLast { launch() }
}

tasks.named("processResources") {
    doLast {
        val origin = file("src/main/resources/plugin.json")
        val processed = file("${layout.buildDirectory.get()}/resources/main/plugin.json")
        val content = origin.readText()
            .replace("\"entrance\": \".", "\"entrance\": \"" + project.group.toString() + ".")
            .replace("\${description}", project.description.toString())
            .replace("\${version}", version.toString())
        processed.writeText(content)
    }
}
