plugins {
    id("io.micronaut.application") apply false
    id("com.gradleup.shadow") apply false
    id("io.micronaut.aot") apply false
}

subprojects {
    repositories {
        mavenCentral()
    }

    pluginManager.withPlugin("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_21.majorVersion)
            }
        }
    }
}