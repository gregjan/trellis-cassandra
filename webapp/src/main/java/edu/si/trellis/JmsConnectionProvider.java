package edu.si.trellis;

import java.net.URI;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.tamaya.inject.api.Config;

public class JmsConnectionProvider {
    
    @Inject
    @Config(key = org.trellisldp.jms.JmsEventService.CONFIG_JMS_URL)
    private Optional<URI> jmsUrl;
    
    @Inject
    @Config(key = org.trellisldp.jms.JmsEventService.CONFIG_JMS_USERNAME)
    private Optional<String> jmsUsername;
    
    @Inject
    @Config(key = org.trellisldp.jms.JmsEventService.CONFIG_JMS_PASSWORD)
    private Optional<String> jmsPassword;
    
    @Produces @ApplicationScoped
    public Connection getJmsConnection() {
	if(!jmsUrl.isPresent()) {
	    throw new Error("No TRELLIS_JMS_URL property configured.");
	}
	try {
            if(jmsUsername.isPresent() && jmsPassword.isPresent()) {
        	ConnectionFactory factory = new ActiveMQConnectionFactory(jmsUsername.get(), jmsPassword.get(), jmsUrl.get());
        	return factory.createConnection();
            } else {
        	ConnectionFactory factory = new ActiveMQConnectionFactory(jmsUrl.get());
        	return factory.createConnection();
            }
	} catch(JMSException e) {
	    throw new Error("JMS connection not available", e);
	}
    }
}
