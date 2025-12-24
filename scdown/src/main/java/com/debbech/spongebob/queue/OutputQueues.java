package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class OutputQueues {

    private static Logger log = LoggerFactory.getLogger(OutputQueues.class);


    public static void publish_out_proc_qu(String message) throws RuntimeException{
        log.info("publishing message {} to queue {}", message, Config.getInstance().out_proc_qu);

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.getInstance().rabbitMqHost);

        try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
            channel.basicQos(1);

            channel.queueDeclare(Config.getInstance().out_proc_qu, true, false, false, null);

            channel.basicPublish("", Config.getInstance().out_proc_qu, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes(StandardCharsets.UTF_8));
            log.info("published message to queue {}", Config.getInstance().out_proc_qu);
        } catch (IOException e) {
            log.error("could not send message to queue {}", Config.getInstance().out_proc_qu, e.getMessage());
            throw new RuntimeException("Error occured while registering output queue: " + e);
        } catch (TimeoutException e) {
            log.error("could not send message to queue {} cause: {}", Config.getInstance().out_proc_qu, e.getMessage());
            throw new RuntimeException("Error occured while registering output queue: " + e);
        }
    }
}
