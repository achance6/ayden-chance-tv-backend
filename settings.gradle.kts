pluginManagement {
    val micronautGradlePluginVersion: String by settings
    plugins {
        id("io.micronaut.application") version micronautGradlePluginVersion
        id("io.micronaut.aot") version micronautGradlePluginVersion
        id("com.gradleup.shadow") version "8.3.7"
    }
}

rootProject.name = "ayden-chance-tv"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("video-service", "transcoder-dispatch-function")