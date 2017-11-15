package com.example.bestbuydemo.util;

/**
 * Global constants.
 */
@SuppressWarnings("WeakerAccess")
public class Constants {

    public static final String TAG = "BestBuyDemoTag";
    public static final String DOMAIN = "http://www.bestbuy.ca";
    public static final String SKU = "com.example.bestbuydemo.SKU";

    public static final String SEARCH_URL = DOMAIN + "/api/v2/json/search?lang=%s&query=%s";
    public static final String PRODUCT_URL = DOMAIN + "/api/v2/json/product/%s?lang=%s";
}
