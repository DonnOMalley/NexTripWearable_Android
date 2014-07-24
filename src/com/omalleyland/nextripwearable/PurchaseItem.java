package com.omalleyland.nextripwearable;

import java.util.Currency;

/**
 * Created by donn on 7/18/14.
 */
public class PurchaseItem {

    private String Name;
    private String Description;
    private String Price;
    private String ProductID;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getProductID() {
        return ProductID;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    @Override
    public String toString(){return ProductID;}
}
