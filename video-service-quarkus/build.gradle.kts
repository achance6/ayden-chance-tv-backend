plugins {
    id("io.quarkus")
}

val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-amazon-services-bom:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-amazon-lambda-http")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkiverse.amazonservices:quarkus-amazon-dynamodb-enhanced")
    implementation("software.amazon.awssdk:url-connection-client")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.instancio:instancio-junit:5.5.0")
}

group = "com.chance.ayden.videoservice"
version = "0.1.0-SNAPSHOT"