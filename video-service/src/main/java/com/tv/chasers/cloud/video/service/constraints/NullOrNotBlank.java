package com.tv.chasers.cloud.video.service.constraints;

import com.tv.chasers.cloud.video.service.validators.NullOrNotBlankValidator;
import io.micronaut.core.annotation.Nullable;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE_PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Nullable // Composing this annotation so that this annotation can be used to mark micronaut params as optional
public @interface NullOrNotBlank {
  String message() default "{jakarta.validation.constraints.NullOrNotBlank.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}