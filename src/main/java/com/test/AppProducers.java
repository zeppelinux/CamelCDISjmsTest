package com.test;

import org.apache.activemq.artemis.jms.client.ActiveMQJMSConnectionFactory;
import org.apache.activemq.jms.pool.PooledConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.component.sjms.SjmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.SupervisingRouteController;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;

@ApplicationScoped
public class AppProducers {

    private static final Logger logger = LoggerFactory.getLogger(AppProducers.class);

    @Inject
    @ConfigProperty(name = "jms.amq.connectionUrl")
    String connectionUri;

    @Inject
    @ConfigProperty(name = "jms.amq.userName")
    String jmsUser;

    @Inject
    @ConfigProperty(name = "jms.amq.password")
    String jmsPassword;


    DefaultCamelContext context;


    @Produces
    @ApplicationScoped
    CamelContext customize() {
        context = new DefaultCamelContext();
        SupervisingRouteController src = context.getRouteController().supervising();
        src.setBackOffDelay(5000);
        src.setBackOffMaxAttempts(4);
        src.setInitialDelay(1000);
        src.setThreadPoolSize(2);

        SjmsComponent jms = (SjmsComponent) context.getComponent("sjms");
        jms.setConnectionFactory(getConnectionFactory());

        return context;
    }

    private ConnectionFactory getConnectionFactory() {
        ActiveMQJMSConnectionFactory cf = new ActiveMQJMSConnectionFactory(
                connectionUri, jmsUser, jmsPassword);

        cf.setClientID("sjms-test");
        cf.setEnableSharedClientID(true);
        PooledConnectionFactory pcf = new PooledConnectionFactory();
        pcf.setConnectionFactory(cf);
        pcf.setMaxConnections(2);
        pcf.setMaximumActiveSessionPerConnection(100);
        return cf;
    }

    void cleanUp(@Disposes CamelContext context) {
        // do nothing
    }
}
