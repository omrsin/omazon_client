package client;

import client.listeners.ClNewListener;
import client.listeners.ClientListener;
import model.Product;
import model.Order;
import model.Customer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.ws.rs.core.MediaType;
import model.Shipment;
import windows.Window;

/**
 * This class enables us to use the REST protocol with JSON for our clients.
 *
 * @author floriment
 */
public class OmazonClient extends Thread {

    // The URI to use
    public static final String REST_URI = "http://localhost:8080/omazon/";

    private final WebResource products;
    private final WebResource customers;
    private final WebResource orders;
    private final WebResource shipments;
    // A GSON Builder
    private GsonBuilder gsonBuilder;
    private final Gson gson;
    private Context ctx;
    private ConnectionFactory connectionFactory;
    private Connection connection;
    private SecureRandom random = new SecureRandom();
    private String userName;
    private ClientListener clReady;

    public ClientListener getClReady() {
        return clReady;
    }

    public void setClReady(ClientListener clReady) {
        this.clReady = clReady;
    }

    private boolean lock = false;

    public synchronized String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String nextSessionId() {
        return new BigInteger(130, random).toString(32);
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Context getCtx() {
        return ctx;
    }

    public void setCtx(Context ctx) {
        this.ctx = ctx;
    }

    private boolean online = true;

    private List<Window> windowsToNotify = new ArrayList<>();

    public void setWindowsToNotify(List<Window> windowsToNotify) {
        this.windowsToNotify = windowsToNotify;
    }
    
    public List<Window> getWindowsToNotify(){
        return windowsToNotify;
    }

    public List<Window> getDisableButtons() {
        return windowsToNotify;
    }

    public void subscribeForOnOffNotification(Window window) {
        windowsToNotify.add(window);
    }

    private List<Shipment> shipmentsSnapshot;
    private List<Customer> customersSnapshot;

    public List<Shipment> getShipmentsSnapshot() {
        return shipmentsSnapshot;
    }

    public void setShipmentsSnapshot(List<Shipment> shipmentsSnapshot) {
        this.shipmentsSnapshot = shipmentsSnapshot;
    }

    public List<Customer> getCustomersSnapshot() {
        return customersSnapshot;
    }

    public void setCustomersSnapshot(List<Customer> customersSnapshot) {
        this.customersSnapshot = customersSnapshot;
    }

    public boolean isOnline() {
        return online;
    }

    public void setLock(boolean value) {
        this.lock = value;
        if (!this.lock) {
            for (Window window : windowsToNotify) {
                window.online(true);
            }
        } else {
            for (Window window : windowsToNotify) {
                window.online(false);
            }
        }
    }

    public void setOnline(boolean online) {
        if (lock) {
            return;
        }
        this.online = online;
        if (this.online) {
            new ClNewListener(this).start();
            new OmazonProducer(this, "jms/svNew", userName).start();

        } else {
            new OmazonProducer(this,"jms/svOffline", userName).start();
            clReady.interrupt();
            for (Window window : windowsToNotify) {
                window.online(false);
            }
        }
    }

    // Connect using REST
    public OmazonClient() throws JMSException {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        userName = nextSessionId();
        WebResource service = client.resource(REST_URI).path("webresources");
        Hashtable table = new Hashtable();
//        table.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.appserv.naming.S1ASCtxFactory");
        table.put(Context.PROVIDER_URL, "iiop://127.0.0.1:3700");
        try {
            ctx = new InitialContext(table);
            connectionFactory = (ConnectionFactory) lookup("jms/__defaultConnectionFactory");
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (NamingException ex) {
            ex.printStackTrace();
        }

        gson = new Gson();
        products = service.path("com.omazon.entities.products");
        customers = service.path("com.omazon.entities.customers");
        orders = service.path("com.omazon.entities.orders");
        shipments = service.path("com.omazon.entities.shipments");

    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        new ClNewListener(this).start();
        new OmazonProducer(this, "jms/svNew", userName).start();
        while (true) {

        }
    }

    public Object lookup(String url) {
        try {
            return ctx.lookup(url);
        } catch (NamingException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Used for simple text methods.
     *
     * @param webResource the resource
     * @return retrieve the text content.
     */
    private String getOutputAsText(WebResource webResource) {
        return webResource.accept(MediaType.TEXT_PLAIN).get(String.class);
    }

    /**
     * Used for JSON text methods.
     *
     * @param webResource the resource
     * @return retrieve the JSON text content.
     */
    private String getOutputAsJson(WebResource webResource) {
        return webResource.accept(MediaType.APPLICATION_JSON).get(String.class);
    }

    /**
     * Get all products
     *
     * @return List of all Products
     */
    public List<Product> getProducts() {
        if (!isOnline()) {
            return null;
        }
        String response = getOutputAsJson(products);
        Type collectionType = new TypeToken<List<Product>>() {
        }.getType();
        List<Product> p = gson.fromJson(response, collectionType);
        return p;
    }

    public Product getProductById(int id) {
        String response = getOutputAsJson(products.path(id + ""));
        Product p = gson.fromJson(response, Product.class);
        return p;
    }

    /**
     * Add a new product
     *
     * @param product
     */
    public void addProduct(Product product) {
        products.type(MediaType.APPLICATION_JSON).post(gson.toJson(product));
    }

    /**
     * Add a new product
     *
     * @param product
     */
    public void updateProduct(Product product) {
        products.path(product.getId() + "").type(MediaType.APPLICATION_JSON).put(gson.toJson(product));
    }

    public void deleteProcutById(int productId) {
        products.path(productId + "").delete();
    }

    /**
     * Get all customers
     */
    public List<Customer> getCustomers() {
        if (!isOnline()) {
            return customersSnapshot;
        }
        String response = getOutputAsJson(customers);
        Type collectionType;
        collectionType = new TypeToken<List<Customer>>() {
        }.getType();
        List<Customer> c = gson.fromJson(response, collectionType);
        customersSnapshot = c;
        return c;
    }

    /**
     * Get a specific customer by id
     *
     * @param id
     */
    public void getCustomers(int id) {
        String response = getOutputAsJson(customers.path("" + id));
        System.out.println(response);
    }

    /**
     * Adds a customer
     *
     * @param customer
     */
    public void addCustomer(JsonObject customer) {
        customers.type(MediaType.APPLICATION_JSON).post(customer.toString());
    }

    public void addCustomer(Customer customer) {
        customers.type(MediaType.APPLICATION_JSON).post(gson.toJson(customer));
    }

    /**
     * Updates a customer
     *
     * @param customer
     */
    public void updateCustomer(JsonObject customer) {
        customers.path(customer.get("id").getAsString()).type(MediaType.APPLICATION_JSON).put(customer.toString());
    }

    public void updateCustomer(Customer customer) {
        customers.path(customer.getId() + "").type(MediaType.APPLICATION_JSON).put(gson.toJson(customer));
    }

    /**
     * Use this methot to delete particular customer
     *
     * @param customerId
     */
    public void deleteCustomerById(int customerId) {
        customers.path(customerId + "").delete();
    }

    public List<Order> getOrders() {

        if (!isOnline()) {
            return null;
        }
        String response = getOutputAsJson(orders);
        Type collectionType = new TypeToken<List<Order>>() {
        }.getType();
        List<Order> p = gson.fromJson(response, collectionType);
        return p;
    }

    public void addOrder(Order order) {
        orders.type(MediaType.APPLICATION_JSON).post(gson.toJson(order));
//        System.out.println(gson.toJson(order));

    }

    public List<Shipment> getShipments() {
        if (!isOnline()) {
            return shipmentsSnapshot;
        }
        String response = getOutputAsJson(shipments);
        Type collectionType = new TypeToken<List<Shipment>>() {
        }.getType();
        List<Shipment> p = gson.fromJson(response, collectionType);
        shipmentsSnapshot = p;
        return shipmentsSnapshot;
    }

    public void editShipment(Shipment shipment) {
        shipments.path(shipment.getId() + "").type(MediaType.APPLICATION_JSON).put(gson.toJson(shipment));
    }

    public synchronized void lock() {
        setLock(true);
    }

    public void getLatest() {
        //something to happen
    }

    public synchronized void unlock() {
        setLock(false);
    }

}
