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
import model.Customer;
import com.sun.javafx.scene.control.skin.FXVK;
import java.awt.BorderLayout;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class CustomersWindow extends JFrame implements Window{

    public OmazonClient client;
    private List<JButton> buttons = new ArrayList<>();

    // Dimensions of the window.
    public static final int WINDOW_WIDTH = 500;
    public static final int WINDOW_HEIGHT = 400;

    public static final String DELETE_STR = "<html><font color=\"red\">Delete</font></html>";
    public static final String EDIT_STR = "<html><font color=\"green\">Edit</font></html>";

    // The tables for shop and cart
    JTable customerTable;
    JLabel priceLabel;

    // The shop and cart contents
//    ShoppingCart shoppingCart;
//    Stock stock;
    // The used service
//    ShoppingCartService service;
    // The ID of this client
    long id;

    public CustomersWindow(OmazonClient client) {
        super("Customers - Omazon");
        this.client = client;
        this.client.subscribeForOnOffNotification(this);
        // Exit VM when closing
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        this.setResizable(false);
        JPanel mainPanel = new JPanel();
        mainPanel.add(new JLabel("Customers"));
        // A refresh button
        JButton refreshShopButton = new JButton("Refresh");
        ActionListener refreshSopAL = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                updateCustomersView();
            }
        };
        refreshShopButton.addActionListener(refreshSopAL);
        JButton onOffButton = new JButton("Switch to Offline");
        ActionListener onOffListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.setOnline(!client.isOnline());
                if (client.isOnline()) {
                    ((JButton) e.getSource()).setText("Switch to Offline");
                }else{
                    ((JButton)e.getSource()).setText("Switch to Online");
                }
                        
            }
        };
        onOffButton.addActionListener(onOffListener);
        mainPanel.add(refreshShopButton);
        mainPanel.add(onOffButton);

        // Add tables inside of scrollPanes
        ScrollPane firstScrollPane = new ScrollPane();
        firstScrollPane.setBounds(0, 0, 450, 300);

        customerTable = new JTable();
        DefaultTableModel customerTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 3 || column == 4 || row == 0) {
                    return false;
                }
                return true;
            }
        };

        // Add Click Listener on customerTable
        customerTable.setModel(customerTableModel);
        // The columns are: ID, name, price (for 1 product), available amount,
        // "add to cart" clickable field
        customerTableModel.setColumnCount(5);

        customerTable.getColumnModel().getColumn(0).setPreferredWidth(30);
        customerTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        customerTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        customerTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        customerTable.getColumnModel().getColumn(4).setPreferredWidth(60);

        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable target = (JTable) e.getSource();
                int customerTableRow = target.getSelectedRow();
                int customerTableColumn = target.getSelectedColumn();

                // If we're clicking the "Delete" cell
                if (customerTableColumn == 4
                        && target.getModel()
                        .getValueAt(customerTableRow, customerTableColumn)
                        .equals(DELETE_STR)) {
                    if(!client.isOnline())
                    {
                        return;
                    }
                    int id = (int) target.getModel().getValueAt(
                            customerTableRow, 0);
                    client.deleteCustomerById(id);
                    updateCustomersView();
                }

                if (customerTableColumn == 3
                        && target.getModel()
                        .getValueAt(customerTableRow, customerTableColumn)
                        .equals(EDIT_STR)) {
                    int id = (int) target.getModel().getValueAt(
                            customerTableRow, 0);
                    if(!client.isOnline()){
                        return;
                    }
                    String name = (String) target.getModel().getValueAt(customerTableRow, 1);
                    String email = (String) target.getModel().getValueAt(customerTableRow, 2);
                    Customer c = new Customer(id, name, email);
                    AddCustomerWindow window = new AddCustomerWindow(c);
//                    CustomersWindow.this.setVisible(false);
                    window.setVisible(true);
                    updateCustomersView();
                }
            }
        });

        // The header row
        customerTableModel.addRow(new Object[]{"<html><b>ID</b></html>",
            "<html><b>Name</b></html>", "<html><b>Email</b></html>",
            "<html><b>Edit</b></html>", "<html><b>Delete</b></html>"});

        firstScrollPane.add(customerTable);
        mainPanel.add(firstScrollPane);

        // Panel to hold clear cart button total price and buy button
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));

        JButton addCustomer = new JButton("Add new Customer!");
        buttons.add(addCustomer);
        addCustomer.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AddCustomerWindow win = new AddCustomerWindow(null);
//                CustomersWindow.this.setVisible(false);
                win.setVisible(true);
            }
        });
        bottomPanel.add(addCustomer);
        // For Space after Button
        bottomPanel.add(new JLabel("            "));
        // For displaying Total Price
        mainPanel.add(bottomPanel);
        mainPanel.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
        this.add(mainPanel);
        this.pack();

        List<Customer> listOfCustomers = client.getCustomers();
        updateCustomersView();
        // Get the contents for cart and shop for the first time
    }

    public void updateCustomersView() {
        DefaultTableModel customerTableModel = (DefaultTableModel) customerTable
                .getModel();
        customerTableModel.setRowCount(1);
        List<Customer> listOfCustomers = client.getCustomers();

        for (Customer c : listOfCustomers) {
            customerTableModel.addRow(new Object[]{c.getId(), c.getName(), c.getEmail(), EDIT_STR, DELETE_STR});
        }
    }

    @Override
    public void online(boolean online) {
        updateCustomersView();

        if(online){
            for(JButton button: buttons)
            {
                button.setEnabled(true);
            }
        }else{
            for(JButton button : buttons)
            {
                button.setEnabled(false);
            }
        }
        
        
    }

    public class AddCustomerWindow extends JFrame {

        Customer customer;
        boolean update = false;

        public AddCustomerWindow(Customer c) {
            super("Add a new customer");

            if (c == null) {
                customer = new Customer();
            } else {
                customer = c;
                update = true;
            }
            JLabel l = new JLabel();
            l.setText("	Name :");
            JTextField name = new JTextField(customer.getName(), 30);
            JLabel l2 = new JLabel();
            l2.setText("	Email :");
            JTextField email = new JTextField(customer.getEmail(), 30);
            JButton but = new JButton();
            but.setText(update ? "Update" : "Add new");
            add(l);
            add(name);
            add(l2);
            add(email);
            add(but);
            but.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    customer.setName(name.getText());
                    customer.setEmail(email.getText());
                    if (update) {
                        client.updateCustomer(customer);
                    } else {
                        client.addCustomer(customer);

                    }
                    AddCustomerWindow.this.setVisible(false);
                    CustomersWindow.this.setVisible(true);
                    CustomersWindow.this.updateCustomersView();
                }
            });
            setLayout(new FlowLayout());
            setSize(400, 400);
            setVisible(true);
        }

    }
}
