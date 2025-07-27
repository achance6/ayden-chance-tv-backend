package com.chance.ayden.cdk;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SnapStartConf;
import software.amazon.awscdk.services.lambda.VersionOptions;
import software.amazon.awscdk.services.lambda.eventsources.S3EventSource;
import software.amazon.awscdk.services.logs.LogGroup;
import software.amazon.awscdk.services.s3.BlockPublicAccess;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.CorsRule;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.HttpMethods;
import software.constructs.Construct;

import java.util.List;

public class ActvStack extends Stack {
  @SuppressWarnings("unused")
  public ActvStack(final Construct scope, final String id) {
	this(scope, id, null);
  }

  public ActvStack(final Construct scope, final String id, final StackProps props) {
	super(scope, id, props);

	final Role createMediaConvertJobServiceRole = Role.Builder.create(this, "createMediaConvertJobServiceRole")
		.roleName("test-createMediaConvertJob-role")
		.assumedBy(ServicePrincipal.Builder.create("lambda.amazonaws.com").build())
		.managedPolicies(
			List.of(
				ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
				ManagedPolicy.fromAwsManagedPolicyName("AWSElementalMediaConvertFullAccess")
			)
		)
		.description("Role for createMediaConvertJob")
		.build();

	final Bucket originalVideosBucket = Bucket.Builder.create(this, "originalVideosBucket")
		.bucketName("test-actv-original-videos")
		.cors(List.of(
			CorsRule.builder()
				.allowedHeaders(List.of("*"))
				.allowedMethods(List.of(
					HttpMethods.GET,
					HttpMethods.POST,
					HttpMethods.PUT,
					HttpMethods.DELETE,
					HttpMethods.HEAD
				))
				.allowedOrigins(List.of("*"))
				.exposedHeaders(List.of("Etag"))
				.build()
		))
		.removalPolicy(RemovalPolicy.DESTROY)
		.build();

	Bucket.Builder.create(this, "transcodedVideosBucket")
		.bucketName("test-actv-transcoded-videos")
		.cors(List.of(
			CorsRule.builder()
				.allowedHeaders(List.of("*"))
				.allowedMethods(List.of(
					HttpMethods.GET,
					HttpMethods.POST,
					HttpMethods.PUT,
					HttpMethods.DELETE,
					HttpMethods.HEAD
				))
				.allowedOrigins(List.of("*"))
				.exposedHeaders(List.of("Etag"))
				.build()
		))
		.removalPolicy(RemovalPolicy.DESTROY)
		.build();

	final String websiteDomain = "test-aydenchancetv.com";
	final Bucket websiteBucket = Bucket.Builder.create(this, "websiteBucket")
		.bucketName(websiteDomain)
		.blockPublicAccess(BlockPublicAccess.BLOCK_ACLS_ONLY)
		.publicReadAccess(true)
		.websiteIndexDocument("index.html")
		.cors(List.of(
			CorsRule.builder()
				.allowedHeaders(List.of("*"))
				.allowedMethods(List.of(
					HttpMethods.GET,
					HttpMethods.POST,
					HttpMethods.PUT,
					HttpMethods.DELETE,
					HttpMethods.HEAD
				))
				.allowedOrigins(List.of("*"))
				.exposedHeaders(List.of("Etag"))
				.build()
		))
		.removalPolicy(RemovalPolicy.DESTROY)
		.build();

	websiteBucket.addToResourcePolicy(PolicyStatement.Builder.create()
		.sid("PublicReadForWebsiteBucket")
		.effect(Effect.ALLOW)
		.principals(List.of(new AnyPrincipal()))
		.actions(List.of("s3:GetObject"))
		.resources(List.of(websiteBucket.arnForObjects("*")))
		.build()
	);

	final Function createMediaConvertJobFunction = Function.Builder.create(this, "createMediaConvertJobFunction")
		.functionName("test-createMediaConvertJob")
		.code(Code.fromAsset("../transcoder-dispatch-function-quarkus/build/function.zip"))
		.handler("io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest")
		.timeout(Duration.seconds(10))
		.memorySize(1024)
		.runtime(Runtime.JAVA_21)
		.architecture(Architecture.ARM_64)
		.role(createMediaConvertJobServiceRole)
		.events(List.of(
			S3EventSource.Builder.create(originalVideosBucket)
				.events(List.of(
					EventType.OBJECT_CREATED
				))
				.build()
		))
		.currentVersionOptions(
			VersionOptions.builder()
				.removalPolicy(RemovalPolicy.DESTROY)
				.build()
		)
		// Version 1 published in stack.
		.snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
		.logGroup(
			LogGroup.Builder.create(this, "createMediaConvertJobLogGroup")
				.removalPolicy(RemovalPolicy.DESTROY)
				.build()
		)
		.build();

	// Create a version every time CDK synth is run
	createMediaConvertJobFunction.getCurrentVersion();
  }
}
