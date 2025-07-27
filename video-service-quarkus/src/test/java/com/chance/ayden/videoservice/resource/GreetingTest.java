package com.chance.ayden.videoservice.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class GreetingTest {
  private final ObjectMapper objectMapper;

  @Inject
  public GreetingTest(ObjectMapper objectMapper) {
	this.objectMapper = objectMapper;
  }

  @Test
  public void testJaxrs() throws JsonProcessingException {
	RestAssured.when().get("/").then()
		.assertThat()
		.contentType(ContentType.JSON)
		.body("message", equalTo("Hello World"))
        .log()
		.headers()
		.log()
        .body(false);
  }
}
