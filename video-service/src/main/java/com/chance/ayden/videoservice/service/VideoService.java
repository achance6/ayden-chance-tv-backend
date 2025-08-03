package com.chance.ayden.videoservice.service;


import com.chance.ayden.videoservice.domain.Video;
import com.chance.ayden.videoservice.mapper.VideoMapper;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;


@ApplicationScoped
public class VideoService {
  private final VideoMapper videoMapper;
  private final DynamoDbClient dynamoDbClient;
  private final String tableName;

  public VideoService(
	  VideoMapper videoMapper,
	  @SuppressWarnings("CdiInjectionPointsInspection") DynamoDbClient dynamoDbClient,
	  @ConfigProperty(name = "video.table-name") String tableName
  ) {
	this.videoMapper = videoMapper;
	this.dynamoDbClient = dynamoDbClient;
	this.tableName = tableName;
  }

  public void storeVideo(Video video) {
	Map<String, AttributeValue> item = videoMapper.mapVideoToDynamoDbItem(video);

	PutItemRequest request = PutItemRequest.builder()
		.tableName(tableName)
		.item(item)
		.build();

	PutItemResponse response = dynamoDbClient.putItem(request);
	Log.infov("Response from DynamoDB putItem: {0}", response);
  }

  public Optional<Video> getVideo(UUID videoId) {
	Map<String, AttributeValue> item = new HashMap<>();
	item.put("VideoId", AttributeValue.fromS(videoId.toString()));

	GetItemRequest request = GetItemRequest.builder()
		.tableName(tableName)
		.key(item)
		.build();

	GetItemResponse response = dynamoDbClient.getItem(request);
	Log.infov("Response from DynamoDB getItem: {0}", response);
	if (!response.hasItem()) {
	  return Optional.empty();
	}
	var videoItem = response.item();

	return Optional.of(videoMapper.mapDynamoDbItemToVideo(videoItem));
  }

  public Set<Video> getVideos(
	  String uploader,
	  String search
  ) {

	Map<String, AttributeValue> expressionValues = new HashMap<>();

	ScanRequest.Builder scanRequestBuilder = ScanRequest.builder()
		.tableName(tableName);

	boolean filtered = false;
	if (uploader != null) {
	  scanRequestBuilder.filterExpression("Uploader = :uploader");
	  expressionValues.put(":uploader", AttributeValue.fromS(uploader));
	  filtered = true;
	}
	if (search != null) {
	  scanRequestBuilder.filterExpression("contains(Title,:search)");
	  expressionValues.put(":search", AttributeValue.fromS(search));
	  filtered = true;
	}
	if (filtered) {
	  scanRequestBuilder.expressionAttributeValues(expressionValues);
	}

	var scanRequest = scanRequestBuilder.build();
	ScanResponse response = dynamoDbClient.scan(scanRequest);

	List<Map<String, AttributeValue>> items = response.items();

	return items.stream()
		.map(videoMapper::mapDynamoDbItemToVideo)
		.collect(Collectors.toSet());
  }

  public SdkHttpResponse deleteVideo(UUID videoId) {
	Map<String, AttributeValue> item = new HashMap<>();
	item.put("VideoId", AttributeValue.fromS(videoId.toString()));

	DeleteItemRequest deleteItemRequest = DeleteItemRequest.builder()
		.tableName(tableName)
		.key(item)
		.build();

	DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(deleteItemRequest);
	Log.infov("Response from DynamoDB delete: {0}", deleteItemResponse);
	return deleteItemResponse.sdkHttpResponse();
  }

  public Optional<Video> incrementVideoView(UUID videoId) {
	var videoOptional = getVideo(videoId);
	if (videoOptional.isEmpty()) {
	  return Optional.empty();
	}
	var video = videoOptional.get();
	video = new Video(video.videoId(), video.title(), video.description(), video.tags(), video.creationDateTime(), video.uploader(),
		video.viewCount() + 1);
	storeVideo(video);
	return Optional.of(video);
  }
}
