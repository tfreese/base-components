// Execute Tasks in SubModule: gradle MODUL:clean build
plugins {
    id("de.freese.gradle.conventions").apply(false)

    // reporting/generateLicenceReport
    id("com.github.jk1.dependency-license-report")

    // id("io.spring.dependency-management").version("$version_springDependencyManagementPlugin").apply(false)
    id("org.openjfx.javafxplugin").apply(false)

    // owasp dependency-check/dependencyCheckAggregate
    id("org.owasp.dependencycheck")

    // verification/sonar
    id("org.sonarqube")

    id("org.springframework.boot").apply(false)

    // reporting/projectReport
    id("project-report")
}

allprojects {
    plugins.apply("base")
}

subprojects {
    plugins.apply("de.freese.gradle.conventions")
    plugins.apply("io.spring.dependency-management")

    extensions.configure(io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension::class.java) {
        imports {
            mavenBom("org.bouncycastle:bc-jdk18on-bom:" + property("version_bouncycastle"))
//            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
            mavenBom("org.springframework.boot:spring-boot-dependencies:" + property("version_springBoot"))
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:" + property("version_springCloud"))
        }

        dependencies {
            dependency("com.github.lgooddatepicker:LGoodDatePicker:" + property("version_lGoodDatePicker"))
            dependency("com.github.librepdf:openpdf:" + property("version_openpdf"))
            dependency("com.github.spotbugs:spotbugs-annotations:" + property("version_spotbugs"))
            dependency("commons-net:commons-net:" + property("version_commonsNet"))
            dependency("dev.failsafe:failsafe:" + property("version_failsafe"))
            dependency("org.apache.poi:poi-ooxml:" + property("version_poi"))
            dependency("org.apache.sshd:sshd-core:" + property("version_sshd"))
            dependency("org.apiguardian:apiguardian-api:" + property("version_apiGuardian"))
            dependency("org.jfree:jfreechart:" + property("version_jfreechart")) {
                // exclude("com.lowagie:itext")
                // exclude("xml-apis:xml-apis")
            }
            dependencySet("org.openjdk.jmh:" + property("version_jmh")) {
                entry("jmh-core")
                entry("jmh-generator-annprocess")
            }
            dependency("org.swinglabs.swingx:swingx-core:" + property("version_swingx"))
        }
    }

    plugins.withType<JavaPlugin> {
        val mockitoAgent = configurations.create("mockitoAgent")

        dependencies {
            // implementation(platform("org.springframework.boot:spring-boot-dependencies:$version_springBoot"))

            add("testImplementation", "org.awaitility:awaitility")
            add("testImplementation", "org.junit.jupiter:junit-jupiter")

            add("testImplementation", "org.mockito:mockito-junit-jupiter")
            mockitoAgent("org.mockito:mockito-core") {
                isTransitive = false
            }

            // To avoid compiler warnings about @API annotations in Log4j Code.
            // testRuntimeOnly("com.github.spotbugs:spotbugs-annotations")

//            testImplementation("com.google.code.findbugs:annotations:3.0.1") // SuppressFBWarnings

            // To avoid compiler warnings about @API annotations in JUnit Code.
            // testRuntimeOnly("org.apiguardian:apiguardian-api")

            add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
            add("testRuntimeOnly", "org.slf4j:slf4j-simple")
        }

        tasks.withType<Test>().configureEach {
            jvmArgs.add("-javaagent:${mockitoAgent.asPath}")
        }
    }
}

// Stored in build/reports
// Needs Parameter: --no-parallel
tasks.register("createReports") {
    group = "MyTasks"
    description = "generate the reports"

    dependsOn("projectReport")
    dependsOn("generateLicenseReport")
    dependsOn("dependencyCheckAggregate")
}

// project-report
// Needs Parameter: --no-parallel
tasks.named<HtmlDependencyReportTask>("htmlDependencyReport") {
    projects = project.allprojects
}

// project-report
// Needs Parameter: --no-parallel
// No Properties, they contains the Passwords & Token.
tasks.propertyReport {
    onlyIf { false } // Wird niemals ausgeführt
}
// tasks.named<org.gradle.api.plugins.PropertyReportTask>("propertyReport") {
//    enabled = false
//}

// Needs Parameter: --no-parallel
licenseReport {
    configurations = arrayOf("runtimeClasspath")
    excludeBoms = true
    excludeOwnGroup = true
    excludeGroups = arrayOf("de.freese")
}

dependencyCheck {
    skip = false
    autoUpdate = true
    formats.set(listOf("HTML", "JSON"))
    scanConfigurations.set(listOf("runtimeClasspath"))
    // scanProjects = [":core"]
    // skipProjects = [":core"]
    // outputDirectory = "build/security-report"
    failOnError = false

    // < 9.x
    // cve.urlBase = https://HOST:PORT/CONTEXT/nvd-nist-public/json/cve/1.1/nvdcve-1.1-%d.json.gz
    // cve.urlModified = https://HOST:PORT/CONTEXT/nvd-nist-public/json/cve/1.1/nvdcve-1.1-modified.json.gz
    //
    // >= 9.x
    // nvd.datafeedUrl = https://HOST:PORT/CONTEXT/nvd-nist-2.0-public/cache/nvdcve-{0}.json.gz
    nvd.maxRetryCount = 3

    hostedSuppressions.enabled = true
    // hostedSuppressions.url = https://HOST:PORT/CONTEXT/github-dependencycheck/gh-pages/suppressions/publishedSuppressions.xml
    hostedSuppressions.forceupdate = true

    analyzers.assemblyEnabled = false // .NET Analyzer.
    analyzers.centralEnabled = false
    // Requires Internet Access, but all Information is already supported by the GradlePlugin.
    analyzers.nexus.enabled = false // Requires NexusPro.

    // analyzers.artifactory.enabled = true
    // analyzers.artifactory.url = https://HOST:PORT/CONTEXT
    // analyzers.artifactory.parallelAnalysis = true

    // < 12.x
    // analyzers.knownExploitedEnabled = true
    // analyzers.knownExploitedURL = https://HOST:PORT/CONTEXT/cisa-public/sites/default/files/feeds/known_exploited_vulnerabilities.json

    // >= 12.x
    // analyzers.kev.enabled = true
    // analyzers.kev.url = https://HOST:PORT/CONTEXT/cisa-public/sites/default/files/feeds/known_exploited_vulnerabilities.json

    analyzers.nodeAudit.enabled = false // Requires Internet Access.

    analyzers.ossIndex.enabled = true // Requires Internet Access.
    analyzers.ossIndex.warnOnlyOnRemoteErrors = true

    // analyzers.retirejs.enabled = true
    // analyzers.retirejs.retireJsUrl = https://HOST:PORT/CONTEXT/github-retirejs/master/repository/jsrepository.json
}

// --info -Psonar_token=TOKEN
sonar {
    isSkipProject = false

    properties {
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "...")
        // property("sonar.login", sonar_token)
        // property("sonar.password", sonar_password)
        property("sonar.projectName", "base-components")
        property("sonar.projectKey", "base-components")

        // property("sonar.language", "java")
        // property("sonar.exclusions", "src/test/**")

        // Default: ~/.sonar
        // Must be an absolute Path.
        // As Environment Variable: SONAR_USER_HOME=...
        // As VM Variable: -Dsonar.userHome=...
        // property("sonar.userHome", "ABSOLUTE-PATH")

        // property("sonar.log.level", "INFO")
        // property("sonar.verbose", "false")

        property("sonar.log.level", "TRACE")
        property("sonar.verbose", "true")

        // layout.buildDirectory.get().toString(), project.getRootDir().toString()
        property("sonar.dependencyCheck.htmlReportPath", "build/reports/dependency-check-report.html")
        property("sonar.dependencyCheck.jsonReportPath", "build/reports/dependency-check-report.json")
    }
}

