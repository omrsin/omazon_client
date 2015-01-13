/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author floriment
 */
public class Product {
    private int id;
    private String name;
    private String description;

    public Product() {
    }

    
    public Product(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Product)
        {
            Product other = (Product)obj;
            if(other.id == this.id)
            {
                return true;
            }else{
                return false;
            }
        }
        
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
}
