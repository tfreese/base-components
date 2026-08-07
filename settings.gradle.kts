// Can not be configured by Conventions-Plugin.
pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }

    val versionMyJavaConventionPlugin = providers.gradleProperty("version_myJavaConventionPlugin")
    val versionDependencyLicenseReport = providers.gradleProperty("version_dependencyLicenseReport")
    val versionJavafxPlugin = providers.gradleProperty("version_javafxPlugin")
    val versionDependencyCheckGradlePlugin = providers.gradleProperty("version_dependencyCheckGradlePlugin")
    val versionSonarQubeGradlePlugin = providers.gradleProperty("version_sonarQubeGradlePlugin")
    val versionSpringBoot = providers.gradleProperty("version_springBoot")

    plugins {
        id("de.freese.gradle.conventions").version(versionMyJavaConventionPlugin).apply(false)

        // reporting/generateLicenceReport
        id("com.github.jk1.dependency-license-report").version(versionDependencyLicenseReport)

        id("org.openjfx.javafxplugin").version(versionJavafxPlugin).apply(false)

        // owasp dependency-check/dependencyCheckAggregate
        id("org.owasp.dependencycheck").version(versionDependencyCheckGradlePlugin)

        // verification/sonar
        id("org.sonarqube").version(versionSonarQubeGradlePlugin)

        id("org.springframework.boot").version(versionSpringBoot).apply(false)
    }
}

// Without rootProject.name the Name of the Projekt-Directory is used.
rootProject.name = "base-components"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenLocal()
        mavenCentral()
    }
}

include("base-calendar")
include("base-core")
include("base-net")
include("base-persistence")
include("base-reports")
include("base-resourcemap")
include("base-security")
include("base-swing")
include("base-swing-demo")

// buildCache {
//     remote(HttpBuildCache) {
//         enabled = true
//         url = "http://localhost:30090/cache/"
//         push = true
//         allowUntrustedServer = true
//         allowInsecureProtocol = true
//     }
// }

println("")
println("Gradle version: ${GradleVersion.current().version}")
println("Java version: ${JavaVersion.current()}")
println("MaxWorkerCount: ${gradle.startParameter.maxWorkerCount}")
println("")
