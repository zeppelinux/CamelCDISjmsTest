package com.test;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.cdi.CdiRouteBuilder;
import org.apache.camel.cdi.Uri;
import org.apache.deltaspike.core.api.config.ConfigProperty;

/**
 * Configures all our Camel routes, components, endpoints and beans
 */
public class MyRoutes extends CdiRouteBuilder {

    @Inject
    @ConfigProperty(name = "jmsUri", defaultValue = "sjms:topic:${topic.name}?durableSubscriptionId=dmitrys-test")
    private String inputEndpoint;

    @Inject
    @Uri("log:output")
    private Endpoint resultEndpoint;

    @Override
    public void configure() {
        errorHandler(transactionErrorHandler().setTransactionPolicy("PROPAGATION_SUPPORTS")
                .maximumRedeliveries(2)
                .maximumRedeliveryDelay(1000)
                .collisionAvoidancePercent(10)
                .backOffMultiplier(1.5));

        from(inputEndpoint)
                .transacted()
                .process(e -> {throw new RuntimeException("test");})
                .to("bean:counterBean")
                .to(resultEndpoint);
    }

}
