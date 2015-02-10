package windows;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author floriment
 */
import client.OmazonClient;
import model.Product;
import model.Order;
import model.Customer;
import com.sun.javafx.scene.control.skin.FXVK;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import java.awt.BorderLayout;
import java.awt.CardLayout;
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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneUI.BasicHorizontalLayoutManager;
import javax.swing.table.DefaultTableModel;

public class OrdersWindow extends JFrame implements Window {

    public OmazonClient client;
    private List<JButton> buttons = new ArrayList<>();

    // Dimensions of the window.
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 400;

    public static final String DELETE_STR = "<html><font color=\"red\">Delete</font></html>";
    public static final String EDIT_STR = "<html><font color=\"green\">Edit</font></html>";

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

    public OrdersWindow(OmazonClient client) {
        super("Orders - Omazon");
        this.client = client;
        this.client.subscribeForOnOffNotification(this);
        // Exit VM when closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("Orders"));
        // A refresh button
//        JButton refreshShopButton = new JButton("Refresh");
//        buttons.add(refreshShopButton);
//        ActionListener refreshSopAL = new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent ae) {
//                updateOrdersView();
//            }
//        };
//        refreshShopButton.addActionListener(refreshSopAL);
//        mainPanel.add(refreshShopButton);

        // Add tables inside of scrollPanes
        ScrollPane firstScrollPane = new ScrollPane();
        firstScrollPane.setBounds(0, 0, 450, 300);

        orderTable = new JTable();
        DefaultTableModel orderTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
//                if (column == 5 || column == 6 || row == 0) {
//                    return false;
//                }
                return false;
            }
        };

        // Add Click Listener on orderTable
        orderTable.setModel(orderTableModel);
        // The columns are: ID, name, price (for 1 product), available amount,
        // "add to cart" clickable field
        orderTableModel.setColumnCount(5);

        orderTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(40);

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int orderTableRow = target.getSelectedRow();
                int orderTableColumn = target.getSelectedColumn();

                // If we're clicking the "Delete" cell
            }
        });

        // The header row
        orderTableModel.addRow(new Object[]{"<html><b>ID</b></html>",
            "<html><b>ShipmentId</b></html>", "<html><b>Order</b></html>",
            "<html><b>Products</b></html>", "<html><b>Status</b></html>"});

        firstScrollPane.add(orderTable);
        mainPanel.add(firstScrollPane);

        // Panel to hold clear cart button total price and buy button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JButton addOrder = new JButton("Add new Order!");
        buttons.add(addOrder);
        addOrder.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddOrderWindow win = new AddOrderWindow(null);
//                OrdersWindow.this.setVisible(false);
                win.setVisible(true);
            }
        });
        JButton shipment = new JButton("Shipments");
        shipment.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ShipmentWindow window = new ShipmentWindow(client);
                window.setVisible(true);
            }
        });
        bottomPanel.add(addOrder);
        bottomPanel.add(shipment);
        // For Space after Button
        bottomPanel.add(new JLabel("            "));
        // For displaying Total Price
        mainPanel.add(bottomPanel);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.add(mainPanel);
        this.pack();
        updateOrdersView();
        // Get the contents for cart and shop for the first time
    }

    @Override
    public void online(boolean online) {
        if (online) {
            updateOrdersView();
            for (JButton button : buttons) {
                button.setEnabled(true);
            }
        } else {
            for (JButton button : buttons) {
                button.setEnabled(false);
            }

        }

    }

    public void updateOrdersView() {
        DefaultTableModel orderTableModel = (DefaultTableModel) orderTable
                .getModel();
        orderTableModel.setRowCount(1);
        List<Order> listOfOrders = client.getOrders();
        if (listOfOrders == null) {
            return;
        }
        for (Order c : listOfOrders) {

            orderTableModel.addRow(new Object[]{c.getId(), c.getShipment().getId(), c.getCustomer().getName(), c.getProducts().get(0).getName(), c.getShipment().getWrittenStatus()});
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
            l.setBounds(0, 0, 400, l.getHeight());
            l.setText("	Products :");
            add(l);

            DefaultTableModel productListModel = new DefaultTableModel() {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable productList = new JTable(productListModel) {

                @Override
                public Class<?> getColumnClass(int column) {
                    switch (column) {
                        case 0:
                            return String.class;
                        case 1:
                            return String.class;
                        default:
                            return Boolean.class;
                    }
                }

            };
            productListModel.addColumn("<html><b>ID</b></html>");
            productListModel.addColumn("<html><b>Product Name</b></html>");
            productListModel.addColumn("<html><b>Check</b></html>");
            productList.getColumnModel().getColumn(0).setPreferredWidth(30);
            productList.getColumnModel().getColumn(1).setPreferredWidth(100);
            productList.getColumnModel().getColumn(2).setPreferredWidth(100);

            List<Product> listOfProducts = client.getProducts();
            for (Product p : listOfProducts) {
                productListModel.addRow(new Object[]{p.getId(), p.getName(), false});
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
                            .equals(false)) {
                        int id = Integer.parseInt(target.getModel().getValueAt(productTableRow, 0).toString());
                        selectedProducts.add(client.getProductById(id));
                        target.getModel().setValueAt(true, productTableRow, productTableColumn);
                        System.out.println(selectedProducts);
                        return;
                    }
                    if (productTableColumn == 2
                            && target.getModel()
                            .getValueAt(productTableRow, productTableColumn)
                            .equals(true)) {
                        int id = Integer.parseInt(target.getModel().getValueAt(productTableRow, 0).toString());
                        selectedProducts.remove(client.getProductById(id));
                        target.getModel().setValueAt(false, productTableRow, productTableColumn);
                        System.out.println(selectedProducts);
                    }

                }
            });
            add(productList);
            JLabel l2 = new JLabel();
            l2.setText("Customer :");
            add(l2);
            JComboBox<Customer> combo = new JComboBox<>();
            List<Customer> customers = client.getCustomers();
            customers.stream().forEach((customer) -> {
                combo.addItem(customer);
            });
            add(combo);
            JButton but = new JButton();
            buttons.add(but);
            but.setText(update ? "Update" : "Add new");
            add(but);
            but.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Order order = new Order(selectedProducts, (Customer) combo.getSelectedItem());
                    client.addOrder(order);
                    AddOrderWindow.this.setVisible(false);
                    OrdersWindow.this.setVisible(true);
                    OrdersWindow.this.updateOrdersView();
                }
            });
            setLayout(new FlowLayout(FlowLayout.CENTER));
            setSize(300, 400);
            setVisible(true);
        }

    }

}
