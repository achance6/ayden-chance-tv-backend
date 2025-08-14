plugins {
    id("io.quarkus")
}

val quarkusPlatformVersion: String by project
val mockitoAgent: Configuration by configurations.creating

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-amazon-services-bom:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-amazon-lambda-http")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkiverse.amazonservices:quarkus-amazon-dynamodb-enhanced")
    // TODO: Test AWS CRT Client
    implementation("software.amazon.awssdk:url-connection-client")

    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.instancio:instancio-junit:5.5.0")

    // Resolves warning when running tests on Apple Silicon
    testRuntimeOnly("io.netty:netty-resolver-dns-native-macos:4.2.3.Final:osx-aarch_64")

    // Resolves JVM warning concerning dynamically attaching agents
    mockitoAgent("org.mockito:mockito-core:5.18.0") {
        isTransitive = false
    }
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

group = "com.chance.ayden.videoservice"
version = "0.1.0-SNAPSHOT"
