package com.example.bestbuydemo;

import android.test.AndroidTestCase;
import android.util.Log;

import com.example.bestbuydemo.util.Constants;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.SocketPolicy;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Some test cases for OkHttpClient
 */
@SuppressWarnings("deprecation")
public class OkHttpClientTest extends AndroidTestCase {

    private static final String TAG = Constants.TAG;

    private MockWebServer mServer;
    private OkHttpClient mHttpClient;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mServer = new MockWebServer();
        mServer.start();

        mHttpClient = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();
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
            public void onFailure(final Call call, final IOException e) {
                Log.w(TAG, "onFailure");
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
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
            public void onFailure(final Call call, final IOException e) {
                String result = "failure";
                Log.w(TAG, result);
                failureRef.set(result);
                responseCountDownLatch.countDown();
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                Log.w(TAG, "onResponse");
            }
        };

        Request request = new Request.Builder().url(url).build();
        mHttpClient.newCall(request).enqueue(cb);
        responseCountDownLatch.await();

        assertEquals("failure", failureRef.get());
    }
}
