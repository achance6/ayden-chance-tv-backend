package com.tv.chasers.cloud.video.service.domain;

import com.tv.chasers.cloud.video.service.constraints.NullOrNotBlank;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Serdeable
@Introspected
public record Video(
        @NonNull UUID videoId,
        @NotBlank String title,
        @NullOrNotBlank String description,
        @NonNull List<@NotBlank String> tags,
        @NonNull LocalDateTime creationDateTime,
        @NotBlank String uploader,
        @NonNull Integer viewCount
) {

}
