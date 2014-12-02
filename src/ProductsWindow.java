/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author floriment
 */
import com.sun.javafx.scene.control.skin.FXVK;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ProductsWindow extends JFrame {

    public OmazonClient client;

    // Dimensions of the window.
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 400;

    public static final String DELETE_STR = "<html><font color=\"red\">Delete</font></html>";
    public static final String EDIT_STR = "<html><font color=\"green\">Edit</font></html>";

    // The tables for shop and cart
    JTable productTable;
    JLabel priceLabel;

    // The shop and cart contents
//    ShoppingCart shoppingCart;
//    Stock stock;
    // The used service
//    ShoppingCartService service;
    // The ID of this client
    long id;

    public ProductsWindow() {
        super("Products - Omazon");
        client = new OmazonClient();
        // Exit VM when closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("Products"));
        // A refresh button
        JButton refreshShopButton = new JButton("Refresh");
        ActionListener refreshSopAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateProductsView();
            }
        };
        refreshShopButton.addActionListener(refreshSopAL);
        mainPanel.add(refreshShopButton);

        // Add tables inside of scrollPanes
        ScrollPane firstScrollPane = new ScrollPane();
        firstScrollPane.setBounds(0, 0, 450, 300);

        productTable = new JTable();
        DefaultTableModel productTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 3 || column == 4 || row == 0) {
                    return false;
                }
                return true;
            }
        };

        // Add Click Listener on productTable
        productTable.setModel(productTableModel);
        // The columns are: ID, name, price (for 1 product), available amount,
        // "add to cart" clickable field
        productTableModel.setColumnCount(5);

        productTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        productTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        productTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        productTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        productTable.getColumnModel().getColumn(4).setPreferredWidth(60);

        productTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int productTableRow = target.getSelectedRow();
                int productTableColumn = target.getSelectedColumn();

                // If we're clicking the "Delete" cell
                if (productTableColumn == 4
                        && target.getModel()
                        .getValueAt(productTableRow, productTableColumn)
                        .equals(DELETE_STR)) {
                    int id = (int) target.getModel().getValueAt(
                            productTableRow, 0);
                    client.deleteProcutById(id);
                    updateProductsView();
                }

                if (productTableColumn == 3
                        && target.getModel()
                        .getValueAt(productTableRow, productTableColumn)
                        .equals(EDIT_STR)) {
                    int id = (int) target.getModel().getValueAt(
                            productTableRow, 0);
                    String name =  (String) target.getModel().getValueAt(productTableRow, 1);
                    String description = (String) target.getModel().getValueAt(productTableRow, 2);
                    Product c = new Product(id,name,description);
                    AddProductsWindow window = new AddProductsWindow(c);
//                    ProductsWindow.this.setVisible(false);
                    window.setLocationRelativeTo(ProductsWindow.this);
                    window.setVisible(true);
                    updateProductsView();
                }
            }
        });

        // The header row
        productTableModel.addRow(new Object[]{"<html><b>ID</b></html>",
            "<html><b>Name</b></html>", "<html><b>Description</b></html>",
            "<html><b>Edit</b></html>", "<html><b>Delete</b></html>"});

        firstScrollPane.add(productTable);
        mainPanel.add(firstScrollPane);

        // Panel to hold clear cart button total price and buy button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JButton addProduct = new JButton("Add new Product!");
        addProduct.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddProductsWindow win = new AddProductsWindow(null);
//                ProductsWindow.this.setVisible(false);
                win.setLocationRelativeTo(ProductsWindow.this);
                win.setVisible(true);
            }
        });
        bottomPanel.add(addProduct);
        // For Space after Button
        bottomPanel.add(new JLabel("            "));
        // For displaying Total Price
        mainPanel.add(bottomPanel);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.add(mainPanel);
        this.pack();

        List<Product> listOfProducts = client.getProducts();
        updateProductsView();
        // Get the contents for cart and shop for the first time
    }

    public void updateProductsView() {
        DefaultTableModel productTableModel = (DefaultTableModel) productTable
                .getModel();
        productTableModel.setRowCount(1);
        List<Product> listOfProducts = client.getProducts();

        for (Product c : listOfProducts) {
            productTableModel.addRow(new Object[]{c.getId(), c.getName(), c.getDescription(), EDIT_STR, DELETE_STR});
        }
    }

    public class AddProductsWindow extends JFrame {

        Product product;
        boolean update = false;

        public AddProductsWindow(Product c) {
            super("Add a new product");

            if (c == null) {
                product = new Product();
            } else {
                product = c;
                update = true;
            }
            JLabel l = new JLabel();
            l.setText("	Name :");
            JTextField name = new JTextField(product.getName(), 30);
            JLabel l2 = new JLabel();
            l2.setText(" Description :");
            JTextField description = new JTextField(product.getDescription(), 30);
            JButton but = new JButton();
            but.setText(update?"Update":"Add new");
            add(l);
            add(name);
            add(l2);
            add(description);
            add(but);
            but.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    product.setName(name.getText());
                    product.setDescription(description.getText());
                    if (update) {
                        client.updateProduct(product);
                    } else {
                        client.addProduct(product);

                    }
                    AddProductsWindow.this.setVisible(false);
                    ProductsWindow.this.setVisible(true);
                    ProductsWindow.this.updateProductsView();
                }
            });
            setLayout(new FlowLayout());
            setSize(400, 400);
            setVisible(true);

        }

    }
}
