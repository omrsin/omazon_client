/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.listeners;

import client.OmazonClient;
import client.OmazonProducer;
import client.listeners.ClientListener;
import windows.CustomersWindow;
import windows.OrdersWindow;
import windows.ProductsWindow;
import windows.Window;

/**
 *
 * @author floriment
 */
public class ClNewListener extends ClientListener {

    public ClNewListener(OmazonClient client) {
        super(client, "jms/clNew");
    }

    public ClNewListener(OmazonClient client, String topicName) {
        super(client, topicName);
    }

    @Override
    public void doOperation(String message) {
        System.out.println("Received message in clNew");

        if (client.getWindowsToNotify().isEmpty()) {
            ProductsWindow window = new ProductsWindow(client);
            window.setBounds(40, 50, 500, 500);
            window.setVisible(true);

            CustomersWindow c_window = new CustomersWindow(client);
            c_window.setBounds(540, 50, 500, 500);
            c_window.setVisible(true);

            OrdersWindow order_window = new OrdersWindow(client);
            order_window.setBounds(1000, 50, 700, 500);
            order_window.setVisible(true);
        } else {
            for (Window window : client.getWindowsToNotify()) {
                window.online(true);
            }
        }
        new OmazonProducer(client, "jms/svDone", client.getUserName()).start();

        new ClientListener(client, "jms/clReady", true) {
            @Override
            public void doOperation(String message) {
                System.out.println("Received Get Ready Message");
                System.out.println("I am Ready I locked");
                client.lock();
                new ClientListener(client, "jms/clUpdate", new OmazonProducer(client, "jms/svReady", client.getUserName())) {

                    @Override
                    public void doOperation(String message) {
                        System.out.println("Now I can update!");
//                        client.getLatest();
                        client.unlock();
                        System.out.println("Client Unlocked!");
                        new OmazonProducer(client, "jms/svUpdate", client.getUserName()).start();
                        this.interrupt();
                    }
                }.start();

//                this.interrupt();
            }
        }.start();

        this.interrupt();
    }

}
