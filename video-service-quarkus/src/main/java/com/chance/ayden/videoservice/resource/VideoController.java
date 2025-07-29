package com.chance.ayden.videoservice.resource;

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

@Path("/video")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class VideoController {
  private final VideoService videoService;

  public VideoController(VideoService videoService) {
	this.videoService = videoService;
  }

  @Path("/{videoId}")
  @GET
  public RestResponse<Video> getVideo(@PathParam("videoId") UUID videoId) {
	Log.infov("Received /video GET request with videoId {0}", videoId);
	var video = videoService.getVideo(videoId);
	if (video.isEmpty()) {
	  return RestResponse.notFound();
	}
	return RestResponse.ok(video.get());
  }

  @Path("/{videoId}")
  @DELETE
  public RestResponse<String> deleteVideo(@PathParam("videoId") UUID videoId) {
	Log.infov("Received /video DELETE request with videoId {0}", videoId);
	var sdkRestResponse = videoService.deleteVideo(videoId);

	if (sdkRestResponse.statusCode() == RestResponse.Status.NOT_FOUND.getStatusCode()) {
	  return RestResponse.notFound();
	}
	if (!sdkRestResponse.isSuccessful()) {
	  return RestResponse.serverError();
	}

	return RestResponse.ok("Video with ID " + videoId + " deleted");
  }

  @Path("/videos")
  @GET
  public RestResponse<Set<Video>> getVideos(
	  @QueryParam("uploader") String uploader,
	  @QueryParam("search") String search
  ) {
	Log.infov("Received /video/videos GET request with uploader: {0} and search: {1}", uploader, search);
	Set<Video> videos = videoService.getVideos(uploader, search);
	if (videos.isEmpty()) {
	  return RestResponse.notFound();
	}
	return RestResponse.ok(videos);
  }

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
