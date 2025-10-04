package com.chance.ayden.videoservice;

import jakarta.ws.rs.core.Application;
import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

@OpenAPIDefinition(
	info = @Info(title = "Video API", version = "1")
)
public class VideoApiApplication extends Application {
}
