package client.listeners;

import client.OmazonClient;
import client.OmazonProducer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import windows.CustomersWindow;
import windows.OrdersWindow;
import windows.ProductsWindow;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author floriment
 */
public abstract class ClientListener extends Thread implements MessageListener {

    OmazonClient client;
    String topicName;
    OmazonProducer producer;
    Session session;
    Topic topic;
    MessageConsumer consumer;
    
    public ClientListener(OmazonClient client, String topicName, boolean clready)
    {
         this.client = client;
        this.topicName = topicName;
        if(clready)
        {
            this.client.setClReady(this);
        }
    }

    public ClientListener(OmazonClient client, String topicName) {
        this.client = client;
        this.topicName = topicName;
    }

    public ClientListener(OmazonClient client, String topicName, OmazonProducer producer) {
        this.client = client;
        this.topicName = topicName;
        this.producer = producer;
    }

    @Override
    public void run() {
        System.out.println("Thread Listener Started on Topic" + topicName);
        try {
            session = client.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = (Topic) client.lookup(topicName);
            consumer = session.createConsumer(topic);
            consumer.setMessageListener(this);
            if (producer != null) {
                producer.start();
            }
            while (true && !this.isInterrupted()) {
            }
            throw new InterruptedException("thread Interrupted");
        } catch (InterruptedException ex) {
            try {
                session.close();
                consumer.close();
            } catch (JMSException ex2) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
//            ex.printStackTrace();
        } catch (JMSException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onMessage(Message msg) {
        try {
            String message = ((TextMessage) msg).getText();
            System.out.println("Message received on topic: " + this.topicName);
            System.out.println("Message: " + message);
            this.doOperation(message);

        } catch (JMSException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println(((TextMessage) msg).getText());
        } catch (JMSException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public abstract void doOperation(String message);

}
