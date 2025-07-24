package com.chance.ayden.transcoderdispatchfunction;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.amazonaws.services.lambda.runtime.tests.EventLoader;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import software.amazon.awssdk.regions.Region;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static io.restassured.RestAssured.given;

@QuarkusTest
@Disabled
class LambdaHandlerTest {
    @Test
    void testSimpleLambdaSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too

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

        S3Event eventNotification = EventLoader.loadS3Event("payload.json");

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(eventNotification)
                .when()
                .post()
                .then()
                .statusCode(200);
    }

}
