package com.example.bestbuydemo.search;

/**
 * Class stores results of search.
 */
@SuppressWarnings("WeakerAccess")
public class SearchResults {

    public String Brand = null;
    public Integer pageSize = null;
    public Product[] products = null;

    private SearchResults() {
    }

    @Override
    public String toString() {
        return "Brand : " + Brand + " pageSize : " + pageSize +
                " products : (" + getProductsString() + ")";
    }

    private String getProductsString() {
        StringBuilder sb = new StringBuilder();
        if (products != null) {
            for (Product p : products) {
                sb.append(p.toString()).append("|");
            }
        }

        return sb.toString();
    }
}
