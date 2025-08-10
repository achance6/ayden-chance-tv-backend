package com.chance.ayden.transcoderdispatchfunction;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertAsyncClient;

@ApplicationScoped
public class MediaConvertClientConfiguration {

  @Produces
  @ApplicationScoped
  public MediaConvertAsyncClient mediaConvertAsyncClient() {
	// Logic to create and configure MyCustomBean
	return MediaConvertAsyncClient.builder()
		.region(Region.US_EAST_1) // Improves performance
		.httpClient(AwsCrtAsyncHttpClient.create())
		.build();
  }
}