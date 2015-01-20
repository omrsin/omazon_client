/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package windows;

import client.OmazonClient;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import model.Shipment;
import java.util.List;
import java.util.Vector;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.MouseInputListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import model.Customer;

/**
 *
 * @author floriment
 */
public class ShipmentWindow extends JFrame {

    private List<Shipment> shipments;
    private OmazonClient client;

    public static void main(String[] args) {
//        new ShipmentWindow();
    }

    public ShipmentWindow(OmazonClient client) {
        super("Shipments");
        this.client = client;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setResizable(false);
        setSize(500, 300);
        //MainPanel
        JPanel mainPanel = new JPanel();

        //JTable
        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel() {
            @Override
            public String getColumnName(int column) {
                switch (column) {
                    case 0:
                        return "Shipment Id";
                    case 1:
                        return "Status";
                    default:
                        return "Defualt";
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 1) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public Class<?> getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return String.class;
                    case 1:
                        return Boolean.class;
                    default:
                        return String.class;
                }
            }
        };
        table.setModel(tableModel);
        tableModel.setColumnCount(2);
        table.getColumnModel().getColumn(0).setWidth(100);
        table.getColumnModel().getColumn(0).setHeaderValue("ShipmentID");
        table.getColumnModel().getColumn(1).setWidth(100);
        table.getColumnModel().getColumn(1).setHeaderValue("Status (Check delivered)");

        List<Shipment> list = client.getShipments();
        for (Shipment p : list) {
            boolean result;
            result = p.getStatus() != 0;
            tableModel.addRow(new Object[]{p.getId(),result});
        }

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                JTable target = (JTable) e.getSource();
                int TableRow = target.getSelectedRow();
                int TableColumn = target.getSelectedColumn();
                if (TableColumn == 1
                        && target.getModel()
                        .getValueAt(TableRow, TableColumn)
                        .equals(false)) {
                    int id = Integer.parseInt(target.getModel().getValueAt(TableRow, 0).toString());
                    Shipment sh = new Shipment(id, 1);
                    client.editShipment(sh);
                    target.getModel().setValueAt(true, TableRow, TableColumn);
                    return;
                }
                if (TableColumn == 1
                        && target.getModel()
                        .getValueAt(TableRow, TableColumn)
                        .equals(true)) {
                    int id = Integer.parseInt(target.getModel().getValueAt(TableRow, 0).toString());
                    Shipment sh = new Shipment(id, 0);
                    client.editShipment(sh);
                    target.getModel().setValueAt(false, TableRow, TableColumn);
                    return;
                }

            }

        });

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(table.getTableHeader(), BorderLayout.PAGE_START);
        mainPanel.add(table, BorderLayout.CENTER);

        //SecondPanel
        add(mainPanel);
        mainPanel.add(table);
        setVisible(true);
    }
}
