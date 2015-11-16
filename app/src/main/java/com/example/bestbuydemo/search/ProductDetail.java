package com.example.bestbuydemo.search;

/**
 * Class stores product detail info.
 */
public class ProductDetail {

    public String sku = null;
    public String name = null;
    public Double regularPrice = 0.0;
    public String shortDescription = null;
    public String thumbnailImage = null;

    private ProductDetail() {
    }

    @Override
    public String toString() {
        return "sku : " + sku + " name : " + name +
                " thumbnailImage : " + thumbnailImage + " regularPrice : " + regularPrice +
                " shortDescription : " + shortDescription;
    }
}
