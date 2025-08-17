package com.chance.ayden.videoservice;

import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

public class IntegrationProfile implements QuarkusTestProfile {



    /**
     * Allows the default config profile to be overridden. This basically just sets the quarkus.test.profile system
     * property before the test is run.
     */
    @Override
    public String getConfigProfile() {
        return "integration";
    }
}
