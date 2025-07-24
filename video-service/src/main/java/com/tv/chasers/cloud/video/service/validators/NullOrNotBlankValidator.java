package com.tv.chasers.cloud.video.service.validators;

import com.tv.chasers.cloud.video.service.constraints.NullOrNotBlank;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext;
import jakarta.inject.Singleton;

@Introspected
@Singleton
public class NullOrNotBlankValidator implements ConstraintValidator<NullOrNotBlank, String> {

  @Override
  public boolean isValid(@Nullable String value, @NonNull AnnotationValue<NullOrNotBlank> annotationMetadata, @NonNull ConstraintValidatorContext context) {
	return value == null || !value.trim().isEmpty();
  }

  @Override
  public boolean isValid(String value, jakarta.validation.ConstraintValidatorContext context) {
	return ConstraintValidator.super.isValid(value, context);
  }
}