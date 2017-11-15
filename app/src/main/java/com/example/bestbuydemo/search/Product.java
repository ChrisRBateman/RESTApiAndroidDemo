package com.example.bestbuydemo.search;

/**
 * Class stores product info of search.
 */
@SuppressWarnings("WeakerAccess")
public class Product {

    public String sku = null;
    public String name = null;
    public String thumbnailImage = null;
    public Double regularPrice = 0.0;

    private Product() {
    }

    @Override
    public String toString() {
        return "sku : " + sku + " name : " + name +
                " thumbnailImage : " + thumbnailImage + " regularPrice : " + regularPrice;
    }
}
