package com.chance.ayden.transcoderdispatchfunction;

import io.quarkus.runtime.Startup;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import software.amazon.awssdk.services.mediaconvert.MediaConvertAsyncClient;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.ListVersionsRequest;

@Startup
@ApplicationScoped
public class MediaConvertClientPriming implements Resource {

    @Inject
    Logger logger;

	@Inject
	MediaConvertAsyncClient client;

    @PostConstruct
    void init() {
        // Important - register the resource
        Core.getGlobalContext().register(this);
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
	  logger.info("before checkout hook");
	  try {
		client.createJob(CreateJobRequest.builder().build());
	  } catch (Exception e) {
		logger.error("Error in MediaConvertClientPriming::beforeCheckpoint", e);
	  }
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) throws Exception {
        logger.info("after checkout hook");
        // if there is anything to do during the restoration, do it here.
    }
}