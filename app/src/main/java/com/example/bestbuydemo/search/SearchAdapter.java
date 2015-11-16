package com.example.bestbuydemo.search;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bestbuydemo.R;
import com.example.bestbuydemo.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchAdapter displays contents of each row in product search results listview.
 */
public class SearchAdapter extends ArrayAdapter<Product> {

    protected static final String TAG = Constants.TAG;

    private Activity mActivity = null;
    private List<Product> mProducts;

    public SearchAdapter(Activity activity, ArrayList<Product> products) {
    super(activity, android.R.layout.simple_list_item_1, products);
        this.mActivity = activity;
        this.mProducts = products;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;

        if (v == null) {
            LayoutInflater vi
                    = (LayoutInflater)mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.search_list_item, parent, false);

            holder = new ViewHolder();
            holder.mName = (TextView)v.findViewById(R.id.name);
            holder.mPrice = (TextView)v.findViewById(R.id.price);
            holder.mProductImage = (ImageView)v.findViewById(R.id.product_image);

            v.setTag(holder);
        }
        else {
            holder = (ViewHolder)v.getTag();
        }

        Product product = mProducts.get(position);
        if (product != null) {
            if (holder.mName != null) {
                holder.mName.setText(product.name);
            }
            if (holder.mPrice != null) {
                holder.mPrice.setText(String.format("$%.2f", product.regularPrice));
            }
            if (holder.mProductImage != null) {
                String url = Constants.DOMAIN + product.thumbnailImage;

                Glide.with(mActivity)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_nofilter)
                        .crossFade()
                        .into(holder.mProductImage);
            }
        }
        return v;
    }

    class ViewHolder {
        TextView mName;
        TextView mPrice;
        ImageView mProductImage;
    }
}
