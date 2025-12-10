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
        register_in_proc_qu();
    }

    private void register_in_proc_qu() throws RuntimeException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Config.getInstance().rabbitMqHost);
        factory.setAutomaticRecoveryEnabled(true);
        factory.setNetworkRecoveryInterval(5000);
        Connection connection = null;

        Channel channel = null;
        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(Config.getInstance().in_proc_qu, true, false, false, null);
            channel.basicQos(1);

            Channel finalChannel = channel;
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                log.info("Received '" + message + "' from queue " + Config.getInstance().in_proc_qu);
                int attempt = Config.getInstance().processAttempts;
                do{
                    if(this.inputQueuesHandler.handle_in_proc_qu(message)) {
                        finalChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        break;
                    }
                    log.info("this is the {} attempt after task failed", attempt);
                    attempt--;
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }while(attempt > 0);

                if(attempt == 0){ //job not succeeded
                    //todo notify admin
                    log.info("task failed completely after {} times of retrying, will requeue it", attempt);
                    finalChannel.basicNack(delivery.getEnvelope().getDeliveryTag(), false, true);
                }
            };
            channel.basicConsume(Config.getInstance().in_proc_qu, false, deliverCallback, consumerTag -> { });
            log.info("{} queue is now ready and listening for events", Config.getInstance().in_proc_qu);

        } catch (IOException e) {
            throw new RuntimeException("Error occured while registering input queue " + Config.getInstance().in_proc_qu + ": " + e);
        } catch (TimeoutException e) {
            throw new RuntimeException("Error occured while registering input queue " + Config.getInstance().in_proc_qu + ": " + e);
        }
    }
}
