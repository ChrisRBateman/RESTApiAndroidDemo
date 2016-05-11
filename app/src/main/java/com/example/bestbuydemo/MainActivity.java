package com.example.bestbuydemo;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.bestbuydemo.search.Product;
import com.example.bestbuydemo.search.ProductDetailActivity;
import com.example.bestbuydemo.search.SearchAdapter;
import com.example.bestbuydemo.search.SearchResults;
import com.example.bestbuydemo.util.Constants;
import com.example.bestbuydemo.util.DialogUtil;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * MainActivity loads a list of products matching search criteria entered by user. Product list
 * is requested through Best Buy Rest APIs.
 */
public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        AdapterView.OnItemClickListener {

    public static final String TAG = Constants.TAG;

    private OkHttpClient mHttpClient = new OkHttpClient();
    private EditText mSearchEdit;
    private Button mAddButton;
    private ProgressBar mProgressBar;
    private ArrayList<Product> mProducts = new ArrayList<>();
    private SearchAdapter mSearchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "MainActivity:onCreate");

        mAddButton = (Button)findViewById(R.id.search_button);
        if (mAddButton != null) {
            mAddButton.setOnClickListener(this);
        }
        mSearchEdit = (EditText)findViewById(R.id.search_edit);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        mSearchAdapter = new SearchAdapter(this, mProducts);
        ListView productList = (ListView)findViewById(R.id.product_list);
        if (productList != null) {
            productList.setClickable(true);
            productList.setOnItemClickListener(this);
            productList.setAdapter(mSearchAdapter);
        }
    }

    // View.OnClickListener ------------------------------------------------------------------------

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.search_button) {
            mProgressBar.setVisibility(View.VISIBLE);
            mAddButton.setEnabled(false);

            String searchTerm = mSearchEdit.getText().toString().trim();
            String lang = Locale.getDefault().getLanguage();
            String url = String.format(Constants.SEARCH_URL, lang, searchTerm);
            Request request = new Request.Builder().url(url).build();

            Log.d(TAG, "MainActivity:onClick url > " + request.urlString());

            mHttpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e(TAG, "Callback:onFailure stack > " + Log.getStackTraceString(e));

                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            mAddButton.setEnabled(true);
                            showLoadingError();
                        }
                    });
                }

                @Override
                public void onResponse(Response response) throws IOException {

                    // Parse the json from response with gson. Flag any errors.
                    boolean error = false;
                    Gson gson = new Gson();
                    SearchResults searchResults = null;
                    try {
                        searchResults = gson.fromJson(response.body().charStream(), SearchResults.class);
                    } catch (Exception e) {
                        Log.e(TAG, "Callback:onResponse stack > " + Log.getStackTraceString(e));
                        error = true;
                    }

                    if (!error) {
                        final List<Product> products = Arrays.asList(searchResults.products);
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                mProducts.clear();
                                mProducts.addAll(products);
                                mSearchAdapter.notifyDataSetChanged();

                                mProgressBar.setVisibility(View.INVISIBLE);
                                mAddButton.setEnabled(true);
                            }
                        });
                    } else {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                showLoadingError();
                                mProgressBar.setVisibility(View.INVISIBLE);
                                mAddButton.setEnabled(true);
                            }
                        });
                    }
                }
            });
        }
    }

    // AdapterView.OnItemClickListener -------------------------------------------------------------

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
        int parentId = parent.getId();
        if (parentId == R.id.product_list) {
            // A product was tapped in list.
            try {
                Product product = mProducts.get(pos);
                String sku = product.sku;
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra(Constants.SKU, sku);
                startActivity(intent);
            }
            catch (Exception e) {
                Log.e(TAG, "MainActivity:onItemClick stack > " + Log.getStackTraceString(e));
            }
        }
    }

    // Private methods -----------------------------------------------------------------------------

    /**
     * Show the products loading error message.
     */
    private void showLoadingError() {
        Resources res = MainActivity.this.getResources();
        DialogUtil.showOKMessage(MainActivity.this,
                res.getString(R.string.products_load_error_text));
    }
}
