plugins {
    id("java-library")
}

description = "Common Patterns and Classes for the Persistence Layer."

configurations.configureEach {
    if (name != "mockitoAgent") {
        exclude(group = "commons-logging", module = "commons-logging")
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")

        resolutionStrategy {
            // Keine SNAPSHOTs.
            failOnDynamicVersions()

            // Keine dynamischen Versionen.
            failOnChangingVersions()
        }
    }
}

dependencies {
    api(project(":base-core"))

    api("org.apache.tomcat:tomcat-jdbc")

    implementation("org.apache.commons:commons-dbcp2")

    implementation("org.springframework.boot:spring-boot-starter-jdbc") {
        // exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
    implementation("org.springframework.boot:spring-boot-starter-data-jpa") {
        // exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }

    compileOnly("jakarta.servlet:jakarta.servlet-api")

    testAnnotationProcessor("org.openjdk.jmh:jmh-generator-annprocess")

    testImplementation("com.h2database:h2")
    // testImplementation("org.assertj:assertj-core")
    testImplementation("org.apache.derby:derby")
    testImplementation("org.hsqldb:hsqldb")
    testImplementation("org.openjdk.jmh:jmh-core")
    testImplementation("org.xerial:sqlite-jdbc")

    testImplementation("org.slf4j:jcl-over-slf4j")
    testImplementation("org.slf4j:jul-to-slf4j")
    testRuntimeOnly("org.apache.logging.log4j:log4j-to-slf4j")

    // testRuntimeOnly("org.slf4j:slf4j-simple")
}
