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

    }

}
