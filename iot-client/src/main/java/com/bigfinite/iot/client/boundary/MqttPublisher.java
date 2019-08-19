package com.bigfinite.iot.client.boundary;

import com.bigfinite.iot.client.utils.SSLUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

@RequestScoped
public class MqttPublisher {

    static final Logger LOGGER = Logger.getLogger(MqttPublisher.class.getName());

    @ConfigProperty(name = "mqtt.broker.protocol")
    String mqttBrokerProtocol;
    @ConfigProperty(name = "mqtt.broker.host")
    String mqttBrokerHost;
    @ConfigProperty(name = "mqtt.broker.port")
    String mqttBrokerPort;
    @ConfigProperty(name = "mqtt.broker.topic")
    String mqttBrokerTopic;

    @ConfigProperty(name = "cert.ca")
    String certAuthority;
    @ConfigProperty(name = "cert.public-key")
    String publicKey;
    @ConfigProperty(name = "cert.private-key")
    String privateKey;

    public void publish(String message) throws Exception {
        IMqttClient publisher = createMqttClient();
        if (!publisher.isConnected()) {
            LOGGER.log(Level.SEVERE, "MQTT client not connected");
            return;
        }
        MqttMessage msg = new MqttMessage(message.getBytes());
        msg.setQos(0);
        msg.setRetained(true);
        publisher.publish(mqttBrokerTopic, msg);
    }

    private IMqttClient createMqttClient() throws MqttException, Exception {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(60);
        options.setKeepAliveInterval(60);
        options.setSocketFactory(SSLUtils.getSocketFactory(certAuthority, publicKey, privateKey, ""));

        String clientId = "broker-producer-client";
        IMqttClient client = new MqttClient(mqttBrokerProtocol + "://" + mqttBrokerHost + ":" + mqttBrokerPort, clientId);
        client.connect(options);
        return client;
    }

}
