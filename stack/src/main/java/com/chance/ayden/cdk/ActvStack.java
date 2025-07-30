package com.chance.ayden.cdk;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.aws_apigatewayv2_integrations.HttpLambdaIntegration;
import software.amazon.awscdk.services.apigateway.LambdaIntegration;
import software.amazon.awscdk.services.apigateway.LambdaRestApi;
import software.amazon.awscdk.services.apigatewayv2.AddRoutesOptions;
import software.amazon.awscdk.services.apigatewayv2.CorsHttpMethod;
import software.amazon.awscdk.services.apigatewayv2.CorsPreflightOptions;
import software.amazon.awscdk.services.apigatewayv2.HttpApi;
import software.amazon.awscdk.services.apigatewayv2.HttpMethod;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.BillingMode;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.iam.AnyPrincipal;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.ManagedPolicy;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.Role;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Alias;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.SnapStartConf;
import software.amazon.awscdk.services.lambda.Version;
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

@SuppressWarnings("unused")
public class ActvStack extends Stack {
  @SuppressWarnings("unused")
  public ActvStack(final Construct scope, final String id) {
	this(scope, id, null);
  }

  public ActvStack(final Construct scope, final String id, final StackProps props) {
	super(scope, id, props);

	final Table actvVideo = Table.Builder.create(this, "ActvVideo")
		.tableName("test-ActvVideo")
		.partitionKey(Attribute.builder()
			.name("VideoId")
			.type(AttributeType.STRING)
			.build()
		)
		.removalPolicy(RemovalPolicy.DESTROY)
		.billingMode(BillingMode.PAY_PER_REQUEST)
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

	final Bucket transcodedVideosBucket = Bucket.Builder.create(this, "transcodedVideosBucket")
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
		.code(Code.fromAsset("../transcoder-dispatch-function/build/function.zip"))
		.handler("io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest")
		.timeout(Duration.seconds(10))
		.memorySize(1024)
		.runtime(Runtime.JAVA_21)
		.architecture(Architecture.ARM_64)
		.role(
			Role.Builder.create(this, "createMediaConvertJobServiceRole")
				.roleName("test-createMediaConvertJob-role")
				.assumedBy(ServicePrincipal.Builder.create("lambda.amazonaws.com").build())
				.managedPolicies(
					List.of(
						ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
						ManagedPolicy.fromAwsManagedPolicyName("AWSElementalMediaConvertFullAccess")
					)
				)
				.description("Role for createMediaConvertJob")
				.build()
		)
		.events(List.of(
			S3EventSource.Builder.create(originalVideosBucket)
				.events(List.of(
					EventType.OBJECT_CREATED
				))
				.build()
		))
		// Version 1 published in stack.
		.snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
		.logGroup(
			LogGroup.Builder.create(this, "createMediaConvertJobLogGroup")
				.removalPolicy(RemovalPolicy.DESTROY)
				.build()
		)
		.build();

	final Version createMediaConvertJobFunctionCurrentVersion = createMediaConvertJobFunction.getCurrentVersion();

	final Alias createMediaConvertJobFunctionAlias = Alias.Builder.create(this, "createMediaConvertJobFunctionAlias")
		.aliasName("prod")
		.description(createMediaConvertJobFunctionCurrentVersion.getVersion())
		.version(createMediaConvertJobFunctionCurrentVersion)
		.build();

	final Function videoApiFunction = Function.Builder.create(this, "videoApiFunction")
		.functionName("test-videoApiFunction")
		.code(Code.fromAsset("../video-service-quarkus/build/function.zip"))
		.handler("io.quarkus.amazon.lambda.runtime.QuarkusStreamHandler::handleRequest")
		.timeout(Duration.seconds(10))
		.memorySize(1024)
		.runtime(Runtime.JAVA_21)
		.architecture(Architecture.ARM_64)
		.role(
			Role.Builder.create(this, "videoApiFunctionServiceRole")
				.roleName("test-videoApiFunction-role")
				.assumedBy(ServicePrincipal.Builder.create("lambda.amazonaws.com").build())
				.managedPolicies(
					List.of(
						ManagedPolicy.fromAwsManagedPolicyName("service-role/AWSLambdaBasicExecutionRole"),
						// TODO: Delete this once this stack replaces legacy stack
						ManagedPolicy.fromAwsManagedPolicyName("AmazonDynamoDBFullAccess_V2")
					)
				)
				.description("Role for createMediaConvertJob")
				.build()
		)
		.currentVersionOptions(
			VersionOptions.builder()
				.removalPolicy(RemovalPolicy.DESTROY)
				.build()
		)
		// Version 1 published in stack.
		.snapStart(SnapStartConf.ON_PUBLISHED_VERSIONS)
		.logGroup(
			LogGroup.Builder.create(this, "videoApiFunctionLogGroup")
				.removalPolicy(RemovalPolicy.DESTROY)
				.build()
		)
		.build();

	final Version videoApiFunctionCurrentVersion = videoApiFunction.getCurrentVersion();

	final Alias videoApiFunctionAlias = Alias.Builder.create(this, "videoApiFunctionAlias")
		.aliasName("prod")
		.description(videoApiFunctionCurrentVersion.getVersion())
		.version(videoApiFunctionCurrentVersion)
		.build();

	actvVideo.grantReadWriteData(videoApiFunction);

	final HttpApi videoApi = HttpApi.Builder.create(this, "test-videoApi")
		.description("Video API")
		.corsPreflight(CorsPreflightOptions.builder()
			.allowOrigins(List.of("*")) // TODO: Revisit
			.allowMethods(List.of(CorsHttpMethod.ANY))
			.allowHeaders(List.of("content-type"))
			.build()
		)
		.build();

	videoApi.addRoutes(AddRoutesOptions.builder()
		.path("/{proxy+}")
		.methods(List.of(HttpMethod.ANY))
		.integration(
			HttpLambdaIntegration.Builder.create("videoApiFunctionIntegration", videoApiFunctionAlias)
				.build()
		)
		.build()
	);
  }
}
