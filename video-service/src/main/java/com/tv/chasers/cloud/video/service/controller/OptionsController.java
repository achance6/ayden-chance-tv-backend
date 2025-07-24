package com.tv.chasers.cloud.video.service.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Options;
import io.micronaut.http.annotation.PathVariable;

/**
 * Catch-all controller for preflight requests.
 */
@Controller
public class OptionsController {
  @Options("{/path:.*}")
  @SuppressWarnings("unused")
  void handleOptions(@Nullable @PathVariable String path) {
	// let the cors filter do its job
  }
}