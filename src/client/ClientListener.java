package client;

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

    public ClientListener(OmazonClient client, String topicName) {
        this.client = client;
        this.topicName = topicName;
    }

    @Override
    public void run() {
        System.out.println("Thread Listener Started on Topic" + topicName);
        try {
            Session session = client.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
            Topic topic = (Topic) client.lookup(topicName);
            MessageConsumer consumer = session.createConsumer(topic);
            consumer.setMessageListener(this);
            while (true) {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
