package com.example.bestbuydemo;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.bestbuydemo.util.Constants;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.SocketPolicy;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Some test cases for OkHttpClient
 */
public class OkHttpClientTest extends AndroidTestCase {

    public static final String TAG = Constants.TAG;

    private MockWebServer mServer;
    private OkHttpClient mHttpClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mServer = new MockWebServer();
        mServer.start();
        mHttpClient = new OkHttpClient();
        mHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        mHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mServer.shutdown();
    }

    /**
     * Test the sync response
     * @throws Exception
     */
    public void testSyncResponse() throws Exception {
        mServer.enqueue(new MockResponse().setBody("123"));
        String url = mServer.url("/").toString();

        Request request = new Request.Builder().url(url).build();
        String result = mHttpClient.newCall(request).execute().body().string();
        Log.w(TAG, result);

        assertEquals("123", result);
    }

    /**
     * Test callback onresponse
     * @throws Exception
     */
    public void testCallbackOnResponse() throws Exception {
        final CountDownLatch responseCountDownLatch = new CountDownLatch(1);
        final AtomicReference<String> responseRef = new AtomicReference<>("");

        mServer.enqueue(new MockResponse().setBody("ABC"));
        String url = mServer.url("/").toString();

        final Callback cb = new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                Log.w(TAG, "onFailure");
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                String result = response.body().string();
                Log.w(TAG, result);
                responseRef.set(result);
                responseCountDownLatch.countDown();
            }
        };

        Request request = new Request.Builder().url(url).build();
        mHttpClient.newCall(request).enqueue(cb);
        responseCountDownLatch.await();

        assertEquals("ABC", responseRef.get());
    }

    /**
     * Test callback onfailure
     * @throws Exception
     */
    public void testCallbackOnFailure() throws Exception {
        final CountDownLatch responseCountDownLatch = new CountDownLatch(1);
        final AtomicReference<String> failureRef = new AtomicReference<>("");

        mServer.enqueue(new MockResponse().setSocketPolicy(SocketPolicy.DISCONNECT_AT_START));
        mServer.enqueue(new MockResponse());
        mServer.enqueue(new MockResponse());
        String url = mServer.url("/").toString();

        final Callback cb = new Callback() {
            @Override
            public void onFailure(final Request request, final IOException e) {
                String result = "failure";
                Log.w(TAG, result);
                failureRef.set(result);
                responseCountDownLatch.countDown();
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                Log.w(TAG, "onResponse");
            }
        };

        Request request = new Request.Builder().url(url).build();
        mHttpClient.newCall(request).enqueue(cb);
        responseCountDownLatch.await();

        assertEquals("failure", failureRef.get());
    }
}
