package com.test;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.CdiRouteBuilder;
import org.apache.camel.cdi.Uri;

/**
 * Configures all our Camel routes, components, endpoints and beans
 */
public class MyRoutes extends CdiRouteBuilder {

    @Inject
    @Uri("sjms:topic:topic:kt.ps.hcm.wfm.accrual.exit?durableSubscriptionId=dmitrys-test")
    private Endpoint inputEndpoint;

    @Inject
    @Uri("log:output")
    private Endpoint resultEndpoint;

    @Override
    public void configure() {
        errorHandler(transactionErrorHandler());

        from(inputEndpoint)
                .transacted()
                .process(e -> {throw new RuntimeException("test");})
                .to("bean:counterBean")
                .to(resultEndpoint);
    }

}
