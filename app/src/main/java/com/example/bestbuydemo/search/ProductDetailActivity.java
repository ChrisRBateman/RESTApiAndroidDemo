package com.example.bestbuydemo.search;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bestbuydemo.R;
import com.example.bestbuydemo.util.Constants;
import com.example.bestbuydemo.util.DialogUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Locale;

/**
 * ProductDetailActivity uses sku from intent to load and display a product.
 */
public class ProductDetailActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String TAG = Constants.TAG;

    private OkHttpClient mHttpClient = new OkHttpClient();
    private TextView mNameTextView;
    private ImageView mProductImageView;
    private TextView mPriceTextView;
    private TextView mShortDescTextView;
    private Button mAddCartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mNameTextView = (TextView)findViewById(R.id.name);
        mProductImageView = (ImageView)findViewById(R.id.product_image);
        mPriceTextView = (TextView)findViewById(R.id.price);
        mShortDescTextView = (TextView)findViewById(R.id.description);
        mAddCartButton = (Button)findViewById(R.id.add_cart_button);
        mAddCartButton.setOnClickListener(this);

        Intent intent = getIntent();
        String sku = intent.getStringExtra(Constants.SKU);

        Log.d(TAG, "ProductDetailActivity:onCreate : sku > " + sku);

        if ((sku != null) && (sku.length() > 0)) {
            String lang = Locale.getDefault().getLanguage();
            String url = String.format(Constants.PRODUCT_URL, sku, lang);
            Request request = new Request.Builder().url(url).build();

            Log.d(TAG, "ProductDetailActivity:onClick url > " + request.urlString());

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Callback:onFailure stack > " + Log.getStackTraceString(e));
                    showLoadingError();
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    // Parse the json from response with gson. Flag any errors.
                    boolean error = false;
                    Gson gson = new Gson();
                    ProductDetail productDetail = null;
                    try {
                        productDetail = gson.fromJson(response.body().charStream(), ProductDetail.class);
                    } catch (Exception e) {
                        Log.e(TAG, "Callback:onResponse stack > " + Log.getStackTraceString(e));
                        error = true;
                    }

                    if (!error) {
                        final String name = productDetail.name;
                        final String thumbnailImage = productDetail.thumbnailImage;
                        final Double regularPrice = productDetail.regularPrice;
                        final String shortDescription = productDetail.shortDescription;
                        ProductDetailActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mNameTextView.setText(name);

                                String url = Constants.DOMAIN + thumbnailImage;
                                Glide.with(ProductDetailActivity.this)
                                        .load(url)
                                        .centerCrop()
                                        .placeholder(R.drawable.placeholder_nofilter)
                                        .crossFade()
                                        .into(mProductImageView);

                                mPriceTextView.setText(String.format("$%.2f", regularPrice));
                                mShortDescTextView.setText(shortDescription);
                            }
                        });

                    } else {
                        showLoadingError();
                    }
                }
            });
        }
    }

    // View.OnClickListener ------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.add_cart_button) {
            Resources res = ProductDetailActivity.this.getResources();
            DialogUtil.showOKMessage(ProductDetailActivity.this,
                    res.getString(R.string.add_product_to_cart_text));
        }
    }

    // Private methods -----------------------------------------------------------------------------

    /**
     * Show the product loading error message.
     */
    private void showLoadingError() {
        ProductDetailActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                Resources res = ProductDetailActivity.this.getResources();
                DialogUtil.showOKMessage(ProductDetailActivity.this,
                        res.getString(R.string.product_load_error_text));
            }
        });
    }
}
