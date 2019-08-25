package com.bigfinite.iot.server.boundary;

import com.bigfinite.iot.server.utils.SSLUtils;
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

    @ConfigProperty(name = "mqtt.broker.protocol", defaultValue = "tcp")
    String mqttBrokerProtocol;
    @ConfigProperty(name = "mqtt.broker.host", defaultValue = "broker")
    String mqttBrokerHost;
    @ConfigProperty(name = "mqtt.broker.port", defaultValue = "1883")
    String mqttBrokerPort;
    @ConfigProperty(name = "mqtt.broker.topic", defaultValue = "data")
    String mqttBrokerTopic;

    @ConfigProperty(name = "cert.cert-authority")
    String authority;
    @ConfigProperty(name = "cert.public-key")
    String publicKey;
    @ConfigProperty(name = "cert.private-key")
    String privateKey;
    @ConfigProperty(name = "cert.password")
    String password;

    void init(@Observes @Initialized(ApplicationScoped.class) Object event) {
        try {
            this.init();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Something was wrong subscribing the MQTT client", ex);
        }
    }

    void init() throws MqttException, Exception {
        subscriber = createMqttClient();
        if (!subscriber.isConnected()) {
            LOGGER.log(Level.SEVERE, "MQTT client not connected");
            return;
        }

        subscriber.subscribe(mqttBrokerTopic, 0, (topic, message) -> {
            LOGGER.log(Level.INFO, "Handle MQTT message :: {0}", new String(message.getPayload()));
        });
    }

    private IMqttClient createMqttClient() throws MqttException, Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setSocketFactory(
                SSLUtils.getSocketFactory(authority, publicKey, privateKey, password));

        String clientId = "broker-consumer-client";
        IMqttClient client = new MqttClient(mqttBrokerProtocol + "://" + mqttBrokerHost + ":" + mqttBrokerPort, clientId);
        client.connect(options);
        return client;
    }

}
