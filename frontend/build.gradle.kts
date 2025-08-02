plugins {
    id("io.quarkus")
}

val quarkusPlatformVersion: String by project

dependencies {
    implementation(platform("io.quarkus.platform:quarkus-bom:$quarkusPlatformVersion"))
    implementation(platform("io.quarkus.platform:quarkus-amazon-services-bom:$quarkusPlatformVersion"))

    implementation("io.quarkiverse.qute.web:quarkus-qute-web")
    implementation("io.quarkus:quarkus-rest-qute")
    implementation("io.quarkiverse.renarde:quarkus-renarde:3.1.1")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkiverse.web-bundler:quarkus-web-bundler:1.9.1")

    // Web dependencies
    compileOnly("org.mvnpm:jquery:3.7.0")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.instancio:instancio-junit:5.5.0")
}

group = "com.chance.ayden.actvclient"
version = "0.1.0-SNAPSHOT"