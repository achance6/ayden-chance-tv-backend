pluginManagement {
    val quarkusPluginVersion: String by settings
    plugins {
        id("io.quarkus") version quarkusPluginVersion
    }
    repositories {
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }
}

rootProject.name = "ayden-chance-tv"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(
    "video-service",
    "transcoder-dispatch-function",
    "stack"
)