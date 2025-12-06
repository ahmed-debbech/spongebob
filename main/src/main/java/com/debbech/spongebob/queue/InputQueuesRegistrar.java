package com.debbech.spongebob.queue;

import com.debbech.spongebob.Config;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class InputQueuesRegistrar {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private static InputQueuesRegistrar instance = null;
    private InputQueuesHandler inputQueuesHandler = null;

    private InputQueuesRegistrar(){
        inputQueuesHandler = new InputQueuesHandler();
    }

    public static InputQueuesRegistrar getInstance(){
        if(instance == null) {
            instance = new InputQueuesRegistrar();
        }
        return instance;
    }
    public void registerAll() throws RuntimeException{
        register_DIRECTORY_READY_TO_PROCESS();
    }

    private void register_DIRECTORY_READY_TO_PROCESS() throws RuntimeException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.getInstance().rabbitMqHost);
        Connection connection = null;

        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(Config.getInstance().in_proc_qu, true, false, false, null);

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                log.info("Received '" + message + "' from queue " + Config.getInstance().in_proc_qu);
                this.inputQueuesHandler.handle_DIRECTORY_READY_TO_PROCESS(message);
            };
            channel.basicConsume(Config.getInstance().in_proc_qu, true, deliverCallback, consumerTag -> { });
            log.info("{} queue is now ready and listening for events", Config.getInstance().in_proc_qu);

        } catch (IOException e) {
            throw new RuntimeException("Error occured while registering queue " + Config.getInstance().in_proc_qu + ": " + e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Error occured while registering queue " + Config.getInstance().in_proc_qu + ": " + e);
        }
    }
}
