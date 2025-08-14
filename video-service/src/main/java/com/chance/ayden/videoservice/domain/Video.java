package com.chance.ayden.videoservice.domain;

import com.chance.ayden.videoservice.MyAttributeConverterProvider;
import com.chance.ayden.videoservice.UUIDAttributeConverter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import software.amazon.awssdk.enhanced.dynamodb.DefaultAttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@DynamoDbImmutable(builder = Video.Builder.class, converterProviders = {
	MyAttributeConverterProvider.class,
	DefaultAttributeConverterProvider.class
})
public final class Video {
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

  public Video(
	  @NotNull UUID videoId,
	  @NotBlank String title,
	  @NotNull String description,
	  @NotNull List<@NotBlank String> tags,
	  @NotNull LocalDateTime creationDateTime,
	  @NotBlank String uploader,
	  @NotNull Integer viewCount
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

  @DynamoDbPartitionKey
  public @NotNull UUID videoId() {
	return videoId;
  }

  public @NotBlank String title() {
	return title;
  }

  public @NotNull String description() {
	return description;
  }

  public @NotNull List<@NotBlank String> tags() {
	return tags;
  }

  public @NotNull LocalDateTime creationDateTime() {
	return creationDateTime;
  }

  public @NotBlank String uploader() {
	return uploader;
  }

  public @NotNull Integer viewCount() {
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
