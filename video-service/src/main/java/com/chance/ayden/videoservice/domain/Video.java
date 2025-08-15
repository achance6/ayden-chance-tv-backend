package com.chance.ayden.videoservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbIgnore;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@DynamoDbImmutable(builder = Video.Builder.class)
@RegisterForReflection
public final class Video implements Comparable<Video> {
  private final @NotNull UUID videoId;
  private final @NotBlank String title;
  private final @NotNull String description;
  private final @NotNull List<@NotBlank String> tags;
  private final @NotNull LocalDateTime creationDateTime;
  private final @NotBlank String uploader;
  private final @NotNull Integer viewCount;

  private Video(Builder builder) {
	this.videoId = builder.videoId;
	this.title = builder.title;
	this.description = builder.description;
	this.tags = builder.tags;
	this.creationDateTime = builder.creationDateTime;
	this.uploader = builder.uploader;
	this.viewCount = builder.viewCount;
  }

  @JsonCreator
  public Video(
	  @NotNull @JsonProperty("videoId") UUID videoId,
	  @NotBlank @JsonProperty("title") String title,
	  @NotNull @JsonProperty("description") String description,
	  @NotNull @JsonProperty("tags") List<@NotBlank String> tags,
	  @NotNull @JsonProperty("creationDateTime") LocalDateTime creationDateTime,
	  @NotBlank @JsonProperty("uploader") String uploader,
	  @NotNull @JsonProperty("viewCount") Integer viewCount
  ) {
	this.videoId = videoId;
	this.title = title;
	this.description = description;
	this.tags = tags;
	this.creationDateTime = creationDateTime;
	this.uploader = uploader;
	this.viewCount = viewCount;
  }

  public static Builder builder() {
	return new Builder();
  }

  @JsonProperty("videoId")
  @DynamoDbPartitionKey
  @DynamoDbAttribute("VideoId")
  @NotNull
  public UUID videoId() {
	return videoId;
  }

  @JsonProperty("title")
  @NotBlank
  @DynamoDbAttribute("Title")
  public String title() {
	return title;
  }

  @JsonProperty("description")
  @NotNull
  @DynamoDbAttribute("Description")
  public String description() {
	return description;
  }

  @JsonProperty("tags")
  @NotNull
  @DynamoDbAttribute("Tags")
  public List<@NotBlank String> tags() {
	return tags;
  }

  @JsonProperty("creationDateTime")
  @NotNull
  @DynamoDbAttribute("CreationDateTime")
  public LocalDateTime creationDateTime() {
	return creationDateTime;
  }

  @JsonProperty("uploader")
  @NotBlank
  @DynamoDbAttribute("Uploader")
  public String uploader() {
	return uploader;
  }

  @JsonProperty("viewCount")
  @NotNull
  @DynamoDbAttribute("ViewCount")
  public Integer viewCount() {
	return viewCount;
  }


  @Override
  public boolean equals(Object obj) {
	if (obj == this)
	  return true;
	if (obj == null || obj.getClass() != this.getClass())
	  return false;
	var that = (Video) obj;
	return Objects.equals(this.videoId, that.videoId) &&
		Objects.equals(this.title, that.title) &&
		Objects.equals(this.description, that.description) &&
		Objects.equals(this.tags, that.tags) &&
		Objects.equals(this.creationDateTime, that.creationDateTime) &&
		Objects.equals(this.uploader, that.uploader) &&
		Objects.equals(this.viewCount, that.viewCount);
  }

  @Override
  public int hashCode() {
	return Objects.hash(videoId, title, description, tags, creationDateTime, uploader, viewCount);
  }

  @Override
  public String toString() {
	return "Video[" +
		"videoId=" + videoId + ", " +
		"title=" + title + ", " +
		"description=" + description + ", " +
		"tags=" + tags + ", " +
		"creationDateTime=" + creationDateTime + ", " +
		"uploader=" + uploader + ", " +
		"viewCount=" + viewCount + ']';
  }

  @Override
  @DynamoDbIgnore
  public int compareTo(Video other) {
	if (this.videoId.compareTo(other.videoId) != 0){
	  return this.videoId.compareTo(other.videoId);
	} else {
	  return this.title.compareTo(other.title);
	}
  }

  public static final class Builder {
	private UUID videoId;
	private String title;
	private String description;
	private List<String> tags;
	private LocalDateTime creationDateTime;
	private String uploader;
	private Integer viewCount;

	private Builder() {
	}

	public Builder videoId(@NotNull UUID videoId) {
	  this.videoId = videoId;
	  return this;
	}

	public Builder title(@NotBlank String title) {
	  this.title = title;
	  return this;
	}

	public Builder description(@NotNull String description) {
	  this.description = description;
	  return this;
	}

	public Builder tags(@NotNull List<@NotBlank String> tags) {
	  this.tags = tags;
	  return this;
	}

	public Builder creationDateTime(@NotNull LocalDateTime creationDateTime) {
	  this.creationDateTime = creationDateTime;
	  return this;
	}

	public Builder uploader(@NotBlank String uploader) {
	  this.uploader = uploader;
	  return this;
	}

	public Builder viewCount(@NotNull Integer viewCount) {
	  this.viewCount = viewCount;
	  return this;
	}

	public Video build() {
	  return new Video(this);
	}
  }
}
