/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import windows.CustomersWindow;
import windows.OrdersWindow;
import windows.ProductsWindow;

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

        ProductsWindow window = new ProductsWindow(client);
        window.setBounds(40, 50, 500, 500);
        window.setVisible(true);

        CustomersWindow c_window = new CustomersWindow(client);
        c_window.setBounds(540, 50, 500, 500);
        c_window.setVisible(true);

        OrdersWindow order_window = new OrdersWindow(client);
        order_window.setBounds(1000, 50, 700, 500);
        order_window.setVisible(true);
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
