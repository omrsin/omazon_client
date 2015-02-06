
import client.OmazonClient;
import javax.jms.JMSException;
import windows.ProductsWindow;
import windows.OrdersWindow;
import windows.CustomersWindow;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author floriment
 */
public class OmazonClientMain {

    public static void main(String[] args) throws JMSException {
        OmazonClient client = new OmazonClient();
        client.start();
    }

}
