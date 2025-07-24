package com.chance.ayden.transcoderdispatchfunction;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

@QuarkusTest
class LambdaHandlerTest {
    @Test
    void testSimpleLambdaSuccess() throws Exception {
        // you test your lambdas by invoking on http://localhost:8081
        // this works in dev mode too

        InputStream eventStream = this.getClass().getClassLoader().getResourceAsStream("payload.json");
        var outputStream = new ByteArrayOutputStream();
	    Assertions.assertNotNull(eventStream);
	    eventStream.transferTo(outputStream);

        given()
                .contentType("application/json")
                .accept("application/json")
                .body(outputStream.toString(StandardCharsets.UTF_8))
                .when()
                .post()
                .then()
                .statusCode(204);
    }

}
