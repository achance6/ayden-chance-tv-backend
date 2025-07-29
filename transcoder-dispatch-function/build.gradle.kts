plugins {
    id("io.quarkus")
}

val quarkusPlatformVersion: String by project

dependencies {
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
    implementation(enforcedPlatform("io.quarkus.platform:quarkus-amazon-services-bom:$quarkusPlatformVersion"))
    implementation("io.quarkus:quarkus-amazon-lambda")

    implementation("software.amazon.awssdk:mediaconvert") {
        exclude(group = "software.amazon.awssdk", module = "apache-client")
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
    }
    implementation("software.amazon.awssdk:aws-crt-client")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")

    testImplementation("com.amazonaws:aws-lambda-java-tests:1.1.1")
    testImplementation("com.amazonaws:aws-lambda-java-serialization:1.1.6")

    // Adapter for routing Apache Commons logging to JBoss Log Manager
    // Needed due to AWS SDKs using Apache Commons
    implementation("org.jboss.logging:commons-logging-jboss-logging")
}

group = "com.chance.ayden.transcoderdispatchfunction"
version = "1.0.0-SNAPSHOT"