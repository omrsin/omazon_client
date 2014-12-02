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
import java.util.Collection;
import java.util.List;
import javax.ws.rs.core.MediaType;

/**
 * This class enables us to use the REST protocol with JSON for our clients.
 *
 * @author floriment
 */
public class OmazonClient {

    // The URI to use
    public static final String REST_URI = "http://localhost:8080/omazon/";

    // All the resources
//	private WebResource identifyService;
//	private WebResource stockService;
//	private WebResource shoppingCartService;
//	private WebResource addProductToShoppingCartService;
//	private WebResource clearShoppingCartService;
//	private WebResource buyService;
    private WebResource products;
    private WebResource customers;

    // A GSON Builder
    private GsonBuilder gsonBuilder;
    private Gson gson;

    public static void main(String[] args) {
        OmazonClient client = new OmazonClient();
        client.getCustomers();
        //adding a new customer
        JsonObject customer = new JsonObject();
        customer.add("name", new JsonPrimitive("Omar"));
        customer.add("email", new JsonPrimitive("omar@gmail.com"));
        client.addCustomer(customer);
        System.out.println("the updated list");
        client.getCustomers();
        JsonObject customerToUpdate = new JsonObject();
        customerToUpdate.add("id", new JsonPrimitive("1"));
        customerToUpdate.add("name", new JsonPrimitive("Omar"));
        customerToUpdate.add("email", new JsonPrimitive("klinakuf@gmail.com"));
        client.updateCustomer(customerToUpdate);
        client.getCustomers();

    }

    // Connect using REST
    public OmazonClient() {
        ClientConfig config = new DefaultClientConfig();
        Client client = Client.create(config);
        WebResource service = client.resource(REST_URI).path("webresources");
        gson = new Gson();

        products = service.path("com.omazon.entities.products");
        customers = service.path("com.omazon.entities.customers");
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
     * @return List of all Products
     */
    public List<Product> getProducts(){
        String response = getOutputAsJson(products);
        Type collectionType = new TypeToken<List<Product>>(){}.getType();
        List<Product> p = gson.fromJson(response,collectionType);
        return p;
    }
    /**
     * Add a new product
     * @param product 
     */
    public void addProduct(Product product){
        products.type(MediaType.APPLICATION_JSON).post(gson.toJson(product));
    }
    /**
     * Add a new product
     * @param product 
     */
    public void updateProduct(Product product){
        products.path(product.getId()+"").type(MediaType.APPLICATION_JSON).put(gson.toJson(product));
    }
    public void deleteProcutById(int productId){
        products.path(productId+"").delete();
    }
    /**
     * Get all customers
     */
    public List<Customer> getCustomers() {
        String response = getOutputAsJson(customers);
        Type collectionType;
        collectionType = new TypeToken<List<Customer>>() {
        }.getType();
        List<Customer> c = gson.fromJson(response, collectionType);
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
}
