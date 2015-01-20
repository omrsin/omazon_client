/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author floriment
 */
public class Shipment {

    private int id;
    private int status;
    private String writtenStatus;

    public String getWrittenStatus() {
        return writtenStatus;
    }

    public void setWrittenStatus(String writtenStatus) {
        this.writtenStatus = writtenStatus;
    }

    public static final int INITIAL = 0;
    public static final int DELIVERED = 1;
    public static final int IN_PROGRESS = 2;

    public Shipment() {

    }

    public Shipment(int id, int status) {
        this.id = id;
        this.status = status;
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

}
