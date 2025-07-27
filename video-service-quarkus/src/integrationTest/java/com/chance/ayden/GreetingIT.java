package com.chance.ayden;

import com.chance.ayden.videoservice.resource.GreetingTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import jakarta.inject.Inject;

@QuarkusIntegrationTest
public class GreetingIT extends GreetingTest {
  @Inject
  public GreetingIT(ObjectMapper objectMapper) {
	super(objectMapper);
  }
}
