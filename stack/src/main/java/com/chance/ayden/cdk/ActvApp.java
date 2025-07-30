package com.chance.ayden.cdk;

import software.amazon.awscdk.App;
import software.amazon.awscdk.Environment;
import software.amazon.awscdk.StackProps;

import java.util.Optional;

public class ActvApp {
  @SuppressWarnings("unused")
  public static void main(final String[] args) {
	final App app = new App();

	var awsAccountId = Optional.ofNullable(System.getenv("AWS_ACCOUNT_ID"));
	var awsRegion = Optional.ofNullable(System.getenv("AWS_REGION"));

	final ActvStack actvStack = new ActvStack(app, "ActvStack",
		StackProps.builder()
			.env(Environment.builder()
				.account(awsAccountId.orElse(System.getenv("CDK_DEFAULT_ACCOUNT")))
				.region(awsRegion.orElse(System.getenv("CDK_DEFAULT_REGION")))
				.build()
			)
			.description("Stack defining AWS infrastructure for Ayden Chance Television (ACTV)")
			.build()
	);

	app.synth();
  }
}
