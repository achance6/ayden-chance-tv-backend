plugins {
    id("io.micronaut.application") apply false
    id("com.gradleup.shadow") apply false
    id("io.micronaut.aot") apply false
    id("io.quarkus") apply false
}

subprojects {
    repositories {
        mavenCentral()
        mavenLocal()
    }

    pluginManager.withPlugin("java") {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(JavaVersion.VERSION_21.majorVersion)
                nativeImageCapable = true
            }
        }
    }
}