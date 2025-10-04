package com.chance.ayden.videoservice;

import com.chance.ayden.videoservice.domain.Video;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.instancio.Instancio;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
@TestProfile(IntegrationProfile.class)
class VideoServiceTestIT {
  private final ObjectMapper objectMapper;

  @Inject
  VideoServiceTestIT(ObjectMapper objectMapper) {
	this.objectMapper = objectMapper;
  }

  private Video fetchTestVideo(UUID videoId) throws IOException {
	var response = given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.get("/video/{videoId}", videoId)
		.then()
		.statusCode(200);

	return objectMapper.readValue(response.extract().body().asString(), Video.class);
  }

  @Test
  void testVideoPostAndDelete() {
	Video video = Instancio.create(Video.class);

	// Create resource
	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.body(video)
		.when()
		.post("/video")
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.body("videoId", equalTo(video.videoId().toString()));

	// Delete created resource
	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.body(video)
		.when()
		.delete("/video/{videoId}", video.videoId())
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.body(equalTo("Video with ID %s deleted".formatted(video.videoId())));
  }

  @Test
  void testVideosGet() {
	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.get("/video/videos")
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.body("size()", greaterThanOrEqualTo(2));
  }

  @Test
  void testVideosGetWithUploader() {
	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.param("uploader", "ayden.chance@rocketmail.com")
		.when()
		.get("/video/videos")
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.body(
			"size()", greaterThanOrEqualTo(2),
			"uploader", everyItem(equalTo("ayden.chance@rocketmail.com"))
		);
  }

  @Test
  void testVideosGetWithBadUploader() {
	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.param("uploader", "a")
		.when()
		.get("/video/videos")
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.and()
		.body("size()", equalTo(0));
  }

  @Test
  void testVideosGetWithSearch() {
	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.param("search", "test")
		.when()
		.get("/video/videos")
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.and()
		.body("title", everyItem(containsString("test")));
	;
  }

  @Test
  void testVideoGet() {
	UUID videoId = UUID.fromString("ffb25f34-389a-40f2-912f-55cf85c9ea86");

	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.get("/video/{videoId}", videoId)
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.and()
		.body("videoId", equalTo(videoId.toString()))
		.log()
		.body(true);
  }

  @Test
  void testVideoIncrementView() throws IOException {
	UUID videoId = UUID.fromString("ffb25f34-389a-40f2-912f-55cf85c9ea86");

	int viewsBeforeIncrement = fetchTestVideo(videoId).viewCount();

	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post("/video/{videoId}/view", videoId)
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.OK)
		.and()
		.body("viewCount", equalTo(viewsBeforeIncrement + 1));
  }

  @Test
  void testVideoIncrementViewWithNonExistentId() {
	UUID videoId = UUID.fromString("a69da49a-66ff-4275-8df5-be51bee10085");

	given()
		.contentType(ContentType.JSON)
		.accept(ContentType.JSON)
		.when()
		.post("/video/{videoId}/view", videoId)
		.then()
		.assertThat()
		.statusCode(RestResponse.StatusCode.NOT_FOUND);
  }
}
