package com.bigfinite.iot.server.boundary;

import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

@ApplicationScoped
public class MqttSubscriber {

    static final Logger LOGGER = Logger.getLogger(MqttSubscriber.class.getName());

    private IMqttClient subscriber = null;

    @ConfigProperty(name = "mqtt.broker.host")
    String mqttBrokerHost;

    @ConfigProperty(name = "mqtt.broker.port")
    String mqttBrokerPort;

    @ConfigProperty(name = "mqtt.broker.topic")
    String mqttBrokerTopic;

    void init(@Observes @Initialized(ApplicationScoped.class) Object event) {
        try {
            this.init();
        } catch (MqttException ex) {
            LOGGER.log(Level.SEVERE, "Something was wrong initializing the MQTT client", ex);
        }
    }

    void init() throws MqttException {
        String clientId = UUID.randomUUID().toString();
        subscriber = new MqttClient("tcp://" + mqttBrokerHost + ":" + mqttBrokerPort, clientId);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        subscriber.connect(options);
        LOGGER.log(Level.INFO, "MQTT client connected");

        subscriber.subscribe(mqttBrokerTopic, 0, (topic, message) -> {
            LOGGER.log(Level.INFO, "Handle MQTT message :: {0}", new String(message.getPayload()));
        });
    }
}
