package ru.mobigear.mobigearinterview.network;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

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

/**
 * Created by eugene on 3/23/15.
 */
public class VolleyHelper {
    private static RequestQueue requestQueue;
    private static ImageLoader imageLoader;

    public static RequestQueue getRequestQueue(Context context)
    {
        if (requestQueue == null)
        {
            synchronized (VolleyHelper.class)
            {
                if (requestQueue == null)
                    requestQueue = getRequestQueueNewInstance(context);
            }
        }
        return requestQueue;
    }

    public static RequestQueue getRequestQueueNewInstance(Context context) {
        return Volley.newRequestQueue(context);
    }

    public static ImageLoader getImageLoader(Context context)
    {
        if (imageLoader == null) {
            synchronized (VolleyHelper.class) {
                if (imageLoader == null) {
                    imageLoader = getImageLoaderNewInstance(context);
                }
            }
        }
        return imageLoader;
    }

    public static ImageLoader getImageLoaderNewInstance(Context context) {
        return new ImageLoader(getRequestQueue(context), new BitmapLruCache(context));
    }

    public static void destroy() {
        requestQueue = null;
        imageLoader = null;
    }

    public static void setupCookies()
    {
        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
    }

    public static void disableSSLCertificateCheck() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]
                    { makeKindTrustManager() };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier()
            {
                @Override
                public boolean verify(String arg0, SSLSession arg1)
                {
                    return true;
                }
            });
        }
        catch (Exception e) { }
    }

    private static X509TrustManager makeKindTrustManager()
    {
        return new X509TrustManager()
        {
            public X509Certificate[] getAcceptedIssuers()
            {
                return new X509Certificate[0];
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
        };
    }
}
