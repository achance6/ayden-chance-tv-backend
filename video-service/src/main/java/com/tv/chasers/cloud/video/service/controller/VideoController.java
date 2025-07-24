package com.tv.chasers.cloud.video.service.controller;

import com.tv.chasers.cloud.video.service.constraints.NullOrNotBlank;
import com.tv.chasers.cloud.video.service.domain.Video;
import com.tv.chasers.cloud.video.service.service.VideoService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.UUID;

@Controller("/video")
@Validated
public class VideoController {
  private static final Logger LOGGER = LoggerFactory.getLogger(VideoController.class);

  private final VideoService videoService;

  public VideoController(VideoService videoService) {
	this.videoService = videoService;
  }

  @Get("/{videoId}")
  public HttpResponse<Video> getVideo(
	  @PathVariable UUID videoId
  ) {
	LOGGER.info("Received /video GET request with videoId {}", videoId);
	var video = videoService.getVideo(videoId);
	if (video.isEmpty()) {
	  return HttpResponse.notFound();
	}
	return HttpResponse.ok(video.get());
  }

  @Delete("/{videoId}")
  public HttpResponse<String> deleteVideo(
	  @PathVariable UUID videoId
  ) {
	LOGGER.info("Received /video DELETE request with videoId {}", videoId);
	var sdkHttpResponse = videoService.deleteVideo(videoId);

	if (sdkHttpResponse.statusCode() == HttpStatus.NOT_FOUND.getCode()) {
	  return HttpResponse.notFound();
	}
	if (!sdkHttpResponse.isSuccessful()) {
	  return HttpResponse.serverError(sdkHttpResponse.toString());
	}

	return HttpResponse.ok("Video with ID " + videoId + " deleted");
  }

  @Get("/videos")
  public HttpResponse<Set<Video>> getVideos(
	  @QueryValue @NullOrNotBlank String uploader,
	  @QueryValue @NullOrNotBlank String search
  ) {
	LOGGER.info("Received /video/videos GET request with uploader: {} and search: {}", uploader, search);
	Set<Video> videos = videoService.getVideos(uploader, search);
	if (videos.isEmpty()) {
	  return HttpResponse.notFound();
	}
	return HttpResponse.ok(videos);
  }

  @Post
  public HttpResponse<Video> storeVideo(
	  @Valid @Body Video video
  ) {
	LOGGER.info("Received /video POST request with video: {}", video);
	try {
	  videoService.storeVideo(video);
	} catch (Exception e) {
	  LOGGER.error("Error in storeVideo :: ", e);
	  return HttpResponse.serverError();
	}
	return HttpResponse.created(video);
  }

  @Post("/{videoId}/view")
  public HttpResponse<Video> incrementVideoView(
	  @PathVariable UUID videoId
  ) {
	LOGGER.info("Received /{videoId}/view POST request");
	try {
	  var video = videoService.incrementVideoView(videoId);
	  if (video.isEmpty()) {
		return HttpResponse.notFound();
	  }
	  return HttpResponse.ok(video.get());
	} catch (Exception e) {
	  LOGGER.error("Error in /{videoId}/view POST request :: ", e);
	  return HttpResponse.serverError();
	}
  }
}
