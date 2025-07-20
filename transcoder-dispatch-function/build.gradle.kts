import io.micronaut.gradle.MicronautRuntime
import io.micronaut.gradle.MicronautTestRuntime

plugins {
    id("io.micronaut.application")
    id("com.gradleup.shadow")
}

version = "0.1.0"
group = "com.tv.chasers.cloud"

dependencies {
    implementation("com.amazonaws:aws-lambda-java-events")
    implementation("io.micronaut:micronaut-http-client-jdk")
    implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
    implementation("io.micronaut.aws:micronaut-function-aws")
    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("software.amazon.awssdk:mediaconvert")
    runtimeOnly("ch.qos.logback:logback-classic")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")

    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

micronaut {
    runtime(MicronautRuntime.LAMBDA_PROVIDED)
    testRuntime(MicronautTestRuntime.JUNIT_5)
    processing {
        incremental(true)
        annotations("com.tv.chasers.cloud.transcoderfunction.*")
    }
}

application {
    mainClass = "com.tv.chasers.cloud.transcoderfunction.FunctionLambdaRuntime"
}

tasks.dockerfileNative {
    jdkVersion = JavaVersion.VERSION_21.majorVersion
    args(
        "-XX:MaximumHeapSizePercent=80",
        "-Dio.netty.allocator.numDirectArenas=0",
        "-Dio.netty.noPreferDirect=true"
    )
    nativeImageOptions.map {
        it.quickBuild = true
    }
}