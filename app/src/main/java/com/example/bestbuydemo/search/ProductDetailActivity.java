package com.example.bestbuydemo.search;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.bestbuydemo.R;
import com.example.bestbuydemo.util.Constants;
import com.example.bestbuydemo.util.DialogUtil;
import com.google.gson.Gson;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Locale;

/**
 * ProductDetailActivity uses sku from intent to load and display a product.
 */
@SuppressWarnings("ConstantConditions")
public class ProductDetailActivity extends AppCompatActivity implements
        View.OnClickListener {

    public static final String TAG = Constants.TAG;

    private OkHttpClient mHttpClient = new OkHttpClient();
    private TextView mNameTextView;
    private ImageView mProductImageView;
    private TextView mPriceTextView;
    private TextView mShortDescTextView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mNameTextView = findViewById(R.id.name);
        mProductImageView = findViewById(R.id.product_image);
        mPriceTextView = findViewById(R.id.price);
        mShortDescTextView = findViewById(R.id.description);
        Button addCartButton = findViewById(R.id.add_cart_button);
        if (addCartButton != null) {
            addCartButton.setOnClickListener(this);
        }
        mProgressBar = findViewById(R.id.progress_bar);

        Intent intent = getIntent();
        String sku = intent.getStringExtra(Constants.SKU);

        Log.i(TAG, "ProductDetailActivity:onCreate : sku > " + sku);

        if ((sku != null) && (sku.length() > 0)) {
            String lang = Locale.getDefault().getLanguage();
            String url = String.format(Constants.PRODUCT_URL, sku, lang);
            Request request = new Request.Builder().url(url).build();

            Log.i(TAG, "ProductDetailActivity:onClick url > " + request.toString());

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Callback:onFailure stack > " + Log.getStackTraceString(e));
                    showLoadingError();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

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
                                mProgressBar.setVisibility(View.GONE);
                                mNameTextView.setText(name);

                                Glide.with(ProductDetailActivity.this)
                                    .load(thumbnailImage)
                                    .apply(new RequestOptions()
                                        .centerCrop()
                                        .placeholder(R.drawable.placeholder_nofilter))
                                    .transition(DrawableTransitionOptions.withCrossFade())
                                    .into(mProductImageView);

                                mPriceTextView.setText(String.format(Locale.US, "$%.2f", regularPrice));
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
                mProgressBar.setVisibility(View.GONE);
                Resources res = ProductDetailActivity.this.getResources();
                DialogUtil.showOKMessage(ProductDetailActivity.this,
                        res.getString(R.string.product_load_error_text));
            }
        });
    }
}
