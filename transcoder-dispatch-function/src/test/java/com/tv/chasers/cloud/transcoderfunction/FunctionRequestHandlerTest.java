package com.tv.chasers.cloud.transcoderfunction;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.regions.Region;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@MicronautTest
public class FunctionRequestHandlerTest {

  private static FunctionRequestHandler handler;

  @BeforeAll
  public static void setupServer() {
	handler = new FunctionRequestHandler();
  }

  @AfterAll
  public static void stopServer() {
	if (handler != null) {
	  handler.getApplicationContext().close();
	}
  }

  @Test
  public void testHandler() {

	S3EventNotification.S3EventNotificationRecord s3EventNotificationRecord = new S3EventNotification.S3EventNotificationRecord(
		Region.US_EAST_1.toString(),
		"ObjectCreated:Put",
		"aws:s3",
		ZonedDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
		"2.1",
		new S3EventNotification.RequestParametersEntity("127.0.0.1"),
		new S3EventNotification.ResponseElementsEntity("C3D13FE58DE4C810", "FMyUVURIY8/IgAtTv8xRjskZQpcIZ9KG4V5Wp6S7S/JRWeUWerMUE5JgHvANOjpD"),
		new S3EventNotification.S3Entity("testConfigRule", new S3EventNotification.S3BucketEntity("cctv-video-storage", new S3EventNotification.UserIdentityEntity("A3NL1KOZZKExample"), "arn:aws:s3:::cctv-video-storage"),
			new S3EventNotification.S3ObjectEntity(URLEncoder.encode("Me at the zoo.mp4", StandardCharsets.UTF_8), 1100L, "4e088404aece61e07e7cfc8752927f35", "gKpkHSFzm.3lnBK.vAADCoqwAPiMFsOA", "0055AED6DCD90281E5"),
			"1.0"),
		new S3EventNotification.UserIdentityEntity("AIDAJDPLRKLG7UEXAMPLE")
	);

	S3Event eventNotification = new S3Event(List.of(s3EventNotificationRecord));

	handler.execute(eventNotification);
  }
}
