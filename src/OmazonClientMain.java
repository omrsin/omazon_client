
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

    public static void main(String[] args) {
        ProductsWindow window = new ProductsWindow();
        window.setBounds(40, 50, 500, 500);
        window.setVisible(true);

        CustomersWindow c_window = new CustomersWindow();
        c_window.setBounds(540, 50, 500, 500);
        c_window.setVisible(true);

        OrdersWindow order_window = new OrdersWindow();
        order_window.setBounds(1000, 50, 700, 500);
        order_window.setVisible(true);

    }

}
