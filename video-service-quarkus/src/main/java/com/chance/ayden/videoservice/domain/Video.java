package com.chance.ayden.videoservice.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RegisterForReflection
public record Video(
	@NotNull UUID videoId,
	@NotBlank String title,
	@NotNull String description,
	@NotNull List<@NotBlank String> tags,
	@NotNull LocalDateTime creationDateTime,
	@NotBlank String uploader,
	@NotNull Integer viewCount
) {

}
