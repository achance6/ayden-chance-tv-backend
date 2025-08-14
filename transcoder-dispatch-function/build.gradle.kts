plugins {
    id("io.quarkus")
}

val quarkusPlatformVersion: String by project
val mockitoAgent: Configuration by configurations.creating

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-amazon-services-bom:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-amazon-lambda")

    // Excluding unneeded clients. Using aws-crt-client.
    implementation("software.amazon.awssdk:mediaconvert") {
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:aws-crt-client")

    // Adapter for routing Apache Commons logging to JBoss Log Manager
    // Needed due to AWS SDKs using Apache Commons
    implementation("org.jboss.logging:commons-logging-jboss-logging")

    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.instancio:instancio-junit:5.5.0")

    testImplementation("com.amazonaws:aws-lambda-java-tests:1.1.1")
    testImplementation("com.amazonaws:aws-lambda-java-serialization:1.1.6")

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

group = "com.chance.ayden.transcoderdispatchfunction"
version = "1.0.0-SNAPSHOT"