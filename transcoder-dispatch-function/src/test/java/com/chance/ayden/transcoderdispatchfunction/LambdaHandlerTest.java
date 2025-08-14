package com.chance.ayden.transcoderdispatchfunction;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.mediaconvert.MediaConvertAsyncClient;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class LambdaHandlerTest {

  @InjectMock
  private MediaConvertAsyncClient mediaConvertAsyncClient;

  @Test
  void test_requestHandler_validRequest_204NoContent() throws Exception {

	var outputStream = new ByteArrayOutputStream();
	try (InputStream eventStream = this.getClass().getClassLoader().getResourceAsStream("payload.json")) {
	  assertNotNull(eventStream);
	  eventStream.transferTo(outputStream);
	}

	var mockResponse = Instancio.create(CreateJobResponse.class);

	when(mediaConvertAsyncClient.createJob(any(CreateJobRequest.class)))
		.thenReturn(CompletableFuture.completedFuture(mockResponse));

	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.body(outputStream.toString(StandardCharsets.UTF_8))
		.when()
		.post()
		.then()
		.statusCode(204);
  }

}
