package com.chance.ayden.videoservice.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class GreetingTest {

  @Test
  public void testJaxrs() {
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
