import io.micronaut.gradle.MicronautRuntime
import io.micronaut.gradle.MicronautTestRuntime

plugins {
    id("io.micronaut.application")
    id("io.micronaut.aot")
    id("com.gradleup.shadow")
}

version = "0.1.0"
group = "com.tv.chasers.cloud"

dependencies {
    implementation("io.micronaut:micronaut-http-client-jdk")
    implementation("io.micronaut.aws:micronaut-aws-lambda-events-serde")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.beanvalidation:micronaut-hibernate-validator")
    implementation("software.amazon.awssdk:dynamodb") {
        exclude(group = "software.amazon.awssdk", module = "netty-nio-client")
        exclude(group = "software.amazon.awssdk", module = "apache-client")
    }
    implementation("io.micronaut.aws:micronaut-function-aws-custom-runtime")
    implementation("io.micronaut.aws:micronaut-function-aws-api-proxy")
    implementation("io.micronaut.aws:micronaut-aws-sdk-v2")
    // Use micronaut JDK client (using JDK client) instead of apache or netty
    implementation("software.amazon.awssdk:url-connection-client")
    implementation("io.micronaut.validation:micronaut-validation")
    runtimeOnly("ch.qos.logback:logback-classic")
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")

    developmentOnly("io.micronaut.aws:micronaut-function-aws-api-proxy-test")
    testImplementation("io.micronaut.aws:micronaut-function-aws-api-proxy-test")
    testImplementation("io.micronaut.test:micronaut-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
}

micronaut {
    runtime(MicronautRuntime.LAMBDA_PROVIDED)
    testRuntime(MicronautTestRuntime.JUNIT_5)
    processing {
        incremental(true)
        annotations("com.tv.chasers.cloud.video.service.*")
    }
    nativeLambda {
        lambdaRuntimeClassName = "io.micronaut.function.aws.runtime.APIGatewayV2HTTPEventMicronautLambdaRuntime"
    }
    aot {
        // Please review carefully the optimizations enabled below
        // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading = false
        convertYamlToJava = false
        precomputeOperations = true
        cacheEnvironment = true
        optimizeClassLoading = true
        deduceEnvironment = true
        optimizeNetty = true
        replaceLogbackXml = true
    }
}

application {
    mainClass = "io.micronaut.function.aws.runtime.APIGatewayV2HTTPEventMicronautLambdaRuntime"
}

tasks.dockerfileNative {
    jdkVersion = JavaVersion.VERSION_21.majorVersion
    args(
        "-XX:MaximumHeapSizePercent=80",
        "-Dio.netty.allocator.numDirectArenas=0",
        "-Dio.netty.noPreferDirect=true"
    )
}