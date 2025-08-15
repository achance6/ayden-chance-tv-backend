package com.chance.ayden.videoservice.service;

import com.chance.ayden.videoservice.domain.Video;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@ApplicationScoped
public class VideoService {
  private final DynamoDbTable<Video> videoTable;

  public VideoService(
	  @SuppressWarnings("CdiInjectionPointsInspection") DynamoDbEnhancedClient dynamoDbEnhancedClient,
	  @ConfigProperty(name = "dynamodb.video-table-name") String tableName) {
	this.videoTable = dynamoDbEnhancedClient.table(tableName, TableSchema.fromImmutableClass(Video.class));
  }

  public void storeVideo(Video video) {
	var response = videoTable.putItemWithResponse(putItemRequest -> putItemRequest.item(video));
	Log.infov("Video stored. Response: {0}", response);
  }

  public Optional<Video> getVideo(UUID videoId) {
	var response = videoTable.getItemWithResponse(getItemRequest ->
		getItemRequest.key(Key.builder()
			.partitionValue(videoId.toString())
			.build()
		)
	);
	if (response.attributes() == null) {
	  return Optional.empty();
	}
	var videoItem = response.attributes();
	Log.infov("Response from DynamoDB getItem: {0}", videoItem);

	return Optional.of(videoItem);
  }

  public Set<Video> getVideos(
	  String uploader,
	  String search
  ) {

	var scanEnhancedRequest = ScanEnhancedRequest.builder();

	String filterExpression = "";
	Map<String, AttributeValue> expressionValues = new HashMap<>();

	if (uploader != null) {
	  filterExpression += "Uploader = :uploader";
	  expressionValues.put(":uploader", AttributeValue.fromS(uploader));
	}

	if (search != null) {
	  if (!filterExpression.isEmpty()) {
		filterExpression += " AND ";
	  }
	  filterExpression += "contains(Title, :search)";
	  expressionValues.put(":search", AttributeValue.fromS(search));
	}

	if (!expressionValues.isEmpty()) {
	  scanEnhancedRequest.filterExpression(
		  Expression.builder()
			  .expression(filterExpression)
			  .expressionValues(expressionValues)
			  .build()
	  );
	}

	return videoTable.scan(scanEnhancedRequest.build())
		.items()
		.stream()
		.collect(Collectors.toSet());
  }

  public Optional<Video> deleteVideo(UUID videoId) {
	var response = videoTable.deleteItemWithResponse(deleteItemRequest ->
		deleteItemRequest.key(Key.builder()
			.partitionValue(videoId.toString())
			.build()
		)
	);

	Log.infov("Response from DynamoDB delete: {0}", response);
	return Optional.ofNullable(response.attributes());
  }

  public Optional<Video> incrementVideoView(UUID videoId) {
	var videoOptional = this.getVideo(videoId);
	if (videoOptional.isEmpty()) {
	  return Optional.empty();
	}
	var video = videoOptional.get();

	video = new Video(video.videoId(), video.title(), video.description(), video.tags(), video.creationDateTime(), video.uploader(),
		video.viewCount() + 1);
	this.storeVideo(video);

	return Optional.of(video);
  }
}
