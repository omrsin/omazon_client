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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableModel;

public class OrdersWindow extends JFrame {

    public OmazonClient client;

    // Dimensions of the window.
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 400;

    public static final String DELETE_STR = "<html><font color=\"red\">Delete</font></html>";
    public static final String EDIT_STR = "<html><font color=\"green\">Edit</font></html>";
    public static final String SELECT_STR = "<html><font color=\"red\">SELECT</font></html>";
    public static final String DESELECT_STR = "<html><font color=\"red\">SELECTED</font></html>";

    // The tables for shop and cart
    JTable orderTable;
    JLabel priceLabel;

    // The shop and cart contents
//    ShoppingCart shoppingCart;
//    Stock stock;
    // The used service
//    ShoppingCartService service;
    // The ID of this client
    long id;

    public OrdersWindow() {
        super("Orders - Omazon");
        client = new OmazonClient();
        // Exit VM when closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("Orders"));
        // A refresh button
        JButton refreshShopButton = new JButton("Refresh");
        ActionListener refreshSopAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateOrdersView();
            }
        };
        refreshShopButton.addActionListener(refreshSopAL);
        mainPanel.add(refreshShopButton);

        // Add tables inside of scrollPanes
        ScrollPane firstScrollPane = new ScrollPane();
        firstScrollPane.setBounds(0, 0, 450, 300);

        orderTable = new JTable();
        DefaultTableModel orderTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 5 || column == 6 || row == 0) {
                    return false;
                }
                return true;
            }
        };

        // Add Click Listener on orderTable
        orderTable.setModel(orderTableModel);
        // The columns are: ID, name, price (for 1 product), available amount,
        // "add to cart" clickable field
        orderTableModel.setColumnCount(7);

        orderTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(40);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(30);
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(20);

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int orderTableRow = target.getSelectedRow();
                int orderTableColumn = target.getSelectedColumn();

                // If we're clicking the "Delete" cell
                if (orderTableColumn == 4
                        && target.getModel()
                        .getValueAt(orderTableRow, orderTableColumn)
                        .equals(DELETE_STR)) {
                    int id = (int) target.getModel().getValueAt(
                            orderTableRow, 0);
//                    client.deleteOrderById(id);
                    //TODO Delete the order
                    updateOrdersView();
                }

                if (orderTableColumn == 3
                        && target.getModel()
                        .getValueAt(orderTableRow, orderTableColumn)
                        .equals(EDIT_STR)) {
                    int id = (int) target.getModel().getValueAt(
                            orderTableRow, 0);
                    String name = (String) target.getModel().getValueAt(orderTableRow, 1);
                    String email = (String) target.getModel().getValueAt(orderTableRow, 2);
//                    Order c = new Order(id, name, email);
//                    AddOrderWindow window = new AddOrderWindow(c);
//                    OrdersWindow.this.setVisible(false);
//                    window.setVisible(true);
                    //TODO:: Add order window
                    updateOrdersView();
                }
            }
        });

        // The header row
        orderTableModel.addRow(new Object[]{"<html><b>ID</b></html>",
            "<html><b>ShipmentId</b></html>", "<html><b>Order</b></html>",
            "<html><b>Products</b></html>", "<html><b>Status</b></html>",
            "<html><b>Edit</b></html>", "<html><b>Delete</b></html>"});

        firstScrollPane.add(orderTable);
        mainPanel.add(firstScrollPane);

        // Panel to hold clear cart button total price and buy button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JButton addOrder = new JButton("Add new Order!");
        addOrder.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddOrderWindow win = new AddOrderWindow(null);
//                OrdersWindow.this.setVisible(false);
                win.setVisible(true);
            }
        });
        bottomPanel.add(addOrder);
        // For Space after Button
        bottomPanel.add(new JLabel("            "));
        // For displaying Total Price
        mainPanel.add(bottomPanel);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.add(mainPanel);
        this.pack();

        List<Order> listOfOrders = client.getOrders();
        updateOrdersView();
        // Get the contents for cart and shop for the first time
    }

    public void updateOrdersView() {
        DefaultTableModel orderTableModel = (DefaultTableModel) orderTable
                .getModel();
        orderTableModel.setRowCount(1);
        List<Order> listOfOrders = client.getOrders();

        for (Order c : listOfOrders) {

            orderTableModel.addRow(new Object[]{c.getId(), c.getShipmentId(), c.getCustomer().getName(), c.getProducts().get(0).getName(), c.getStatus(), EDIT_STR, DELETE_STR});
        }
    }

    public class AddOrderWindow extends JFrame {

        Order order;
        boolean update = false;

        public List<Product> selectedProducts;

        public AddOrderWindow(Order c) {
            super("Add a new order");

            selectedProducts = new ArrayList<>();
            if (c == null) {
                order = new Order();
            } else {
                order = c;
                update = true;
            }
            JLabel l = new JLabel();
            l.setText("	Products :");
            if (!update) {
//                Object[] data = client.getProducts().toArray();
//                JList list = new JList(data);
//                add(list);
                JTable productList = new JTable();
                DefaultTableModel productListModel = new DefaultTableModel() {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
                productList.setModel(productListModel);
                productListModel.setColumnCount(3);
                productList.getColumnModel().getColumn(0).setPreferredWidth(30);
                productList.getColumnModel().getColumn(1).setPreferredWidth(100);
                productList.getColumnModel().getColumn(2).setPreferredWidth(100);

                productListModel.addRow(new Object[]{"<html><b>ID</b></html>",
                    "<html><b>Product Name</b></html>", "<html><b>Select/Deselect</b></html>"});

                List<Product> listOfProducts = client.getProducts();
                for (Product p : listOfProducts) {
                    productListModel.addRow(new Object[]{p.getId(), p.getName(), SELECT_STR});
                }

                productList.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        JTable target = (JTable) e.getSource();
                        int productTableRow = target.getSelectedRow();
                        int productTableColumn = target.getSelectedColumn();

                        if (productTableColumn == 2
                                && target.getModel()
                                .getValueAt(productTableRow, productTableColumn)
                                .equals(SELECT_STR)) {
                            int id = Integer.parseInt(target.getModel().getValueAt(productTableRow, 0).toString());
                            selectedProducts.add(client.getProductById(id));
                            target.getModel().setValueAt(DESELECT_STR, productTableRow, productTableColumn);
                            System.out.println(selectedProducts);
                        }
                        if (productTableColumn == 2
                                && target.getModel()
                                .getValueAt(productTableRow, productTableColumn)
                                .equals(DESELECT_STR)) {
                            int id = Integer.parseInt(target.getModel().getValueAt(productTableRow, 0).toString());
                            selectedProducts.remove(client.getProductById(id));
                            target.getModel().setValueAt(DESELECT_STR, productTableRow, productTableColumn);
                            System.out.println(selectedProducts);
                        }

                    }
                });
                add(productList);
            }

            JLabel l2 = new JLabel();
            l2.setText("Customer :");
            if (!update) {

            }

            JButton but = new JButton();
            but.setText(update ? "Update" : "Add new");
            add(l);
            add(l2);
            add(but);
            but.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
//                    order.setName(name.getText());
//                    order.setEmail(email.getText());
//                    if (update) {
//                        client.updateOrder(order);
//                    } else {
//                        client.addOrder(order);
//
//                    }
                    AddOrderWindow.this.setVisible(false);
                    OrdersWindow.this.setVisible(true);
                    OrdersWindow.this.updateOrdersView();
                }
            });
            setLayout(new FlowLayout());
            setSize(400, 400);
            setVisible(true);
        }

    }
}
