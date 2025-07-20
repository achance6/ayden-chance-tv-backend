package com.tv.chasers.cloud.video.service;

import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.tv.chasers.cloud.video.service.domain.Video;
import io.micronaut.function.aws.proxy.MockLambdaContext;
import io.micronaut.function.aws.proxy.payload2.APIGatewayV2HTTPEventFunction;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.HttpStatus;
import io.micronaut.serde.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@MicronautTest
class VideoServiceTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(VideoServiceTest.class);
    private static final UUID uuid = UUID.randomUUID();
    private static APIGatewayV2HTTPEventFunction handler;

    @BeforeAll
    static void setupSpec() {
        handler = new APIGatewayV2HTTPEventFunction();
    }

    @AfterAll
    static void cleanupSpec() {
        handler.getApplicationContext().close();
    }

    private static Video fetchTestVideo(ObjectMapper objectMapper, UUID videoId) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/%s".formatted(videoId))
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        var response = handler.handleRequest(request, new MockLambdaContext());
        return objectMapper.readValue(response.getBody(), Video.class);
    }

    @Test
    void testHandler() {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals("{\"message\":\"Hello World\"}", response.getBody());
    }

    @Test
    void testVideoPostAndDelete(ObjectMapper objectMapper) throws IOException {
        // Create resource
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video")
                        .withMethod(HttpMethod.POST.toString())
                        .build()
                ).build());

        Video video = new Video(uuid, "Test Video", "A Test Video.", List.of("testTag1", "testTag2"), LocalDateTime.now(), "John Fortnite", 0);
        request.setBody(objectMapper.writeValueAsString(video));
        LOGGER.info("Sending testVideoPost request {}", request);

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.CREATED.getCode(), response.getStatusCode());
        assertTrue(response.getBody().contains(uuid.toString()));

        // Delete created resource
        request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/" + uuid)
                        .withMethod(HttpMethod.DELETE.toString())
                        .build()
                ).build());

        response = handler.handleRequest(request, new MockLambdaContext());
        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertTrue(response.getBody().contains("deleted"));
    }

    @Test
    void testVideosGet(ObjectMapper objectMapper) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/videos/")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        LOGGER.info("Sending testVideosGet request");

        var response = handler.handleRequest(request, new MockLambdaContext());

        var videosArray = objectMapper.readValue(response.getBody(), Video[].class);
        Set<Video> videos = Set.of(videosArray);
        LOGGER.info("Received response from getVideos: {}", videos);

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertFalse(videos.isEmpty());
    }

    @Test
    void testVideosGetWithUploader(ObjectMapper objectMapper) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/videos")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());


        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("uploader", "ayden.chance@rocketmail.com");

        request.setQueryStringParameters(queryParams);

        LOGGER.info("Sending testVideosGetWithUploader request");

        var response = handler.handleRequest(request, new MockLambdaContext());

        var videosArray = objectMapper.readValue(response.getBody(), Video[].class);
        Set<Video> videos = Set.of(videosArray);
        LOGGER.info("Received response from getVideos using uploader: {}", videos);

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertFalse(videos.isEmpty());
    }

    @Test
    void testVideosGetWithBadUploader() {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/videos")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        Map<String, String> queryParams = Map.of("uploader", "a");
        request.setQueryStringParameters(queryParams);

        LOGGER.info("Sending testVideosGetWithBadUploader request");

        var response = handler.handleRequest(request, new MockLambdaContext());
        LOGGER.info("Received response from getVideos using bad uploader: {}", response);

        assertEquals(HttpStatus.NOT_FOUND.getCode(), response.getStatusCode());
    }

    @Test
    void testVideosGetWithSearch(ObjectMapper objectMapper) throws IOException {
        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/videos")
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());

        var queryParams = Map.of("search", "test");
        request.setQueryStringParameters(queryParams);

        LOGGER.info("Sending testVideosGetWithSearch request");

        var response = handler.handleRequest(request, new MockLambdaContext());

        var videosArray = objectMapper.readValue(response.getBody(), Video[].class);
        Set<Video> videos = Set.of(videosArray);
        LOGGER.info("Received response from getVideos sent from testVideosGetWithSearch: {}", videos);

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());

        assertFalse(videos.isEmpty());
    }

    @Test
    void testVideoGet(ObjectMapper objectMapper) throws IOException {
        UUID videoId = UUID.fromString("9537ecd6-ed2b-4feb-8b4d-6fbdab47ebea");

        APIGatewayV2HTTPEvent request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/%s".formatted(videoId))
                        .withMethod(HttpMethod.GET.toString())
                        .build()
                ).build());


        LOGGER.info("Sending testVideoGet request {}", request);
        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertTrue(response.getBody().contains("test"));

        Video video = objectMapper.readValue(response.getBody(), Video.class);
        LOGGER.info("Received testVideoGet response {}", objectMapper.writeValueAsString(video));
    }

    @Test
    void testVideoIncrementView(ObjectMapper objectMapper) throws IOException {
        UUID videoId = UUID.fromString("9537ecd6-ed2b-4feb-8b4d-6fbdab47ebea");

        int viewsBeforeIncrement = fetchTestVideo(objectMapper, videoId).viewCount();

        var request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/%s/view".formatted(videoId))
                        .withMethod(HttpMethod.POST.toString())
                        .build()
                ).build());

        LOGGER.info("Sending testVideosIncrementView request");

        var response = handler.handleRequest(request, new MockLambdaContext());

        var video = objectMapper.readValue(response.getBody(), Video.class);
        int viewsAfterIncrement = video.viewCount();

        assertEquals(HttpStatus.OK.getCode(), response.getStatusCode());
        assertEquals(viewsAfterIncrement, viewsBeforeIncrement + 1);
    }

    @Test
    void testVideoIncrementViewWithNonExistentId() {
        UUID videoId = UUID.fromString("a69da49a-66ff-4275-8df5-be51bee10085");

        var request = new APIGatewayV2HTTPEvent();
        request.setRequestContext(APIGatewayV2HTTPEvent.RequestContext.builder()
                .withHttp(APIGatewayV2HTTPEvent.RequestContext.Http.builder()
                        .withPath("/video/%s/view".formatted(videoId))
                        .withMethod(HttpMethod.POST.toString())
                        .build()
                ).build());

        LOGGER.info("Sending testVideoIncrementViewWithNonExistentId request");

        var response = handler.handleRequest(request, new MockLambdaContext());

        assertEquals(HttpStatus.NOT_FOUND.getCode(), response.getStatusCode());
    }
}
