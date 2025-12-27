package com.chance.ayden.videoservice.controller;

import com.chance.ayden.videoservice.domain.Video;
import com.chance.ayden.videoservice.service.VideoService;
import io.quarkus.logging.Log;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Set;
import java.util.UUID;

@Path("/videos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VideoController {
  private final VideoService videoService;

  public VideoController(VideoService videoService) {
	this.videoService = videoService;
  }

  @Path("/{videoId}")
  @GET
  public Video getVideo(@PathParam("videoId") UUID videoId) {
	Log.infov("Received /video GET request with videoId {0}", videoId);
	var video = videoService.getVideo(videoId);
	return video.get();
  }

  @Path("/{videoId}")
  @DELETE
  public RestResponse<String> deleteVideo(@PathParam("videoId") UUID videoId) {
	Log.infov("Received /video DELETE request with videoId {0}", videoId);
	var deletedVideo = videoService.deleteVideo(videoId);

	if (deletedVideo.isEmpty()) {
	  return RestResponse.notFound();
	}

	return RestResponse.ok("Video with ID " + deletedVideo.get().videoId() + " deleted");
  }

  @GET
  public RestResponse<Set<Video>> getVideos(
	  @QueryParam("uploader") String uploader,
	  @QueryParam("search") String search
  ) {
	Log.infov("Received /video/videos GET request with uploader: {0} and search: {1}", uploader, search);
	Set<Video> videos;

	if (uploader != null && search != null) {
	  videos = videoService.getVideos(uploader, search);
	  return RestResponse.ok(videos);
	}

	if (uploader != null) {
	  videos = videoService.getVideosByUploader(uploader);
	  return RestResponse.ok(videos);
	}

	if (search != null) {
	  videos = videoService.getVideosByTitle(search);
	  return RestResponse.ok(videos);
	}

	videos = videoService.getVideos();
	return RestResponse.ok(videos);
  }

  @Path("")
  @POST
  public RestResponse<Video> storeVideo(@Valid Video video) {
	Log.infov("Received /video POST request with video: {0}", video);
	try {
	  videoService.storeVideo(video);
	} catch (Exception e) {
	  Log.error("Error in storeVideo :: ", e);
	  return RestResponse.serverError();
	}
	return RestResponse.ok(video);
  }

  @Path("/{videoId}/view")
  @POST
  public RestResponse<Video> incrementVideoView(
	  @PathParam("videoId") UUID videoId
  ) {
	Log.infov("Received /{0}/view POST request", videoId);
	try {
	  var video = videoService.incrementVideoView(videoId);
	  if (video.isEmpty()) {
		return RestResponse.notFound();
	  }
	  return RestResponse.ok(video.get());
	} catch (Exception e) {
	  Log.error("Error in /{videoId}/view POST request :: ", e);
	  return RestResponse.serverError();
	}
  }
}
