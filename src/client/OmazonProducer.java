package client;

import client.OmazonClient;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author floriment
 */
public class OmazonProducer extends Thread {

    OmazonClient client;
    String topicName;
    String message;

    public OmazonProducer(OmazonClient client, String topic, String message) {
        this.client = client;
        this.topicName = topic;
        this.message = message;
    }

    @Override
    public void run() {
        try {
//            Connection connection = client.getConnectionFactory().createConnection();
            Session session = client.getConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination dest = (Destination) client.lookup(topicName);
            MessageProducer producer = session.createProducer(dest);
            System.out.println("send the message");
            String text = message;
            TextMessage msg = session.createTextMessage(text);
            producer.send(msg);
            session.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
