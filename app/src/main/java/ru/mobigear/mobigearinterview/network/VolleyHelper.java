package ru.mobigear.mobigearinterview.network;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/23/15.
 */
public class VolleyHelper {
    private static final String TAG = VolleyHelper.class.getSimpleName();
    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;
    private static BitmapLruCache imageCache;

    public static RequestQueue getRequestQueue(Context context)
    {
        if (requestQueue == null)
        {
            synchronized (VolleyHelper.class)
            {
                if (requestQueue == null)
                    requestQueue = getRequestQueueNewInstance(context.getApplicationContext());
            }
        }
        return requestQueue;
    }

    private static RequestQueue getRequestQueueNewInstance(Context context) {
        return Volley.newRequestQueue(context);
    }

    public static ImageLoader getImageLoader(Context context)
    {
        if (imageLoader == null) {
            synchronized (VolleyHelper.class) {
                if (imageLoader == null) {
                    imageLoader = getImageLoaderNewInstance(context.getApplicationContext());
                }
            }
        }
        return imageLoader;
    }

    private static ImageLoader getImageLoaderNewInstance(Context context) {
        imageCache = new BitmapLruCache(context);
        return new ImageLoader(getRequestQueue(context), imageCache);
    }

    public static BitmapLruCache getImageCache() {
        return imageCache;
    }

    public static void makeJSONObjectRequest(final Context context, final FragmentManager fm,
                                             AbstractRequest request, final String tag,
                                             final ResponseListener listener) {
        Log.v(TAG, request.getURL());
        Log.v(TAG, request.getPostParameters().toString());
        Utils.showProgressDialog(fm, tag);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (request.getURL(), request.getPostParameters(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.dismissProgressDialog(fm, tag);
                        ServerResponse parsedResponse = ResponseParser.parseResponse(response);
                        if (parsedResponse.isError())
                            listener.onError(parsedResponse.getCode());
                        else {
                            listener.onSuccess(parsedResponse.getData());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismissProgressDialog(fm, tag);
                        listener.onError(Utils.getVolleyNetworkErrorCode(error));
                    }
                });
        jsonObjectRequest.setTag(tag);
        getRequestQueue(context).add(jsonObjectRequest);
    }
}
