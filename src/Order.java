
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author omar
 */
public class Order {
    
    private int id;
    private int status;
    private int shipmentId;
    private Customer customer;
    private List<Product> products;

    public Order(int id, int status, int shipmentId) {
        this.id = id;
        this.status = status;
        this.shipmentId = shipmentId;
    }
    
    public Order(List<Product> products, Customer customer, int status, int shipmentId)
    {
        this.products = products;
        this.customer = customer;
        this.status = status;
        this.shipmentId = shipmentId;
    }
    
    public Order()
    {
        
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getShipmentId() {
        return shipmentId;
    }

    public void setShipmentId(int shipmentId) {
        this.shipmentId = shipmentId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
   
    
    
}
