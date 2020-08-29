package com.test;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.Uri;
import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * Configures all our Camel routes, components, endpoints and beans
 */
public class MyRoutes extends RouteBuilder {

    @Inject
    @ConfigProperty(name = "jmsUri", defaultValue = "sjms:topic:${topic.name}?durableSubscriptionId=dmitrys-test")
    private String inputEndpoint;

    @Inject
    @Uri("log:output")
    private Endpoint resultEndpoint;

    @Override
    public void configure() {

        from(inputEndpoint)
                .to("bean:counterBean")
                .to(resultEndpoint);
    }

}
