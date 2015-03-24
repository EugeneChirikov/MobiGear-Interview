package ru.mobigear.mobigearinterview.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.network.AbstractRequest;
import ru.mobigear.mobigearinterview.network.RequestsFactory;
import ru.mobigear.mobigearinterview.network.ResponseParser;
import ru.mobigear.mobigearinterview.network.ServerResponse;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Constants;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/24/15.
 */
public class ContentSyncAdapter  extends AbstractThreadedSyncAdapter {
    private static final String TAG = ContentSyncAdapter.class.getSimpleName();
    private Context mContext;
    private RequestQueue requestQueue;

    public ContentSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
        requestQueue = VolleyHelper.getRequestQueue(mContext);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.v(TAG, " onPerformSync");
        AccountManager accountManager = AccountManager.get(mContext);
        try {
            String authToken = accountManager.blockingGetAuthToken(account, Constants.ACCOUNT_TOKEN_TYPE, false);
            if (authToken == null)
            {
                Log.v(TAG, "Token result is null");
                return;
            }
            Log.v(TAG, "Auth token is " + authToken);
            AbstractRequest query = RequestsFactory.instantiateQuery(extras);
            if (query == null)
                return;
            query.setToken(authToken);
            requestAndHandleResult(query, syncResult);
        }
        catch (OperationCanceledException e) {
            printExceptionMessage(e);
        }
        catch (IOException e) {
            syncResult.stats.numIoExceptions++;
            printExceptionMessage(e);
        }
        catch (AuthenticatorException e) {
            syncResult.stats.numAuthExceptions++;
            printExceptionMessage(e);
        }
    }

    private static void printExceptionMessage(Exception e) {
        String message = e.getMessage();
        if (message != null)
            Log.v(TAG, message);
    }

    private RequestFuture<JSONObject> requestBlocking(AbstractRequest query) {
        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(
                query.getURL(), query.getPostParameters(), future, future);
        requestQueue.add(request);
        return future;
    }

    private void requestAndHandleResult(AbstractRequest query, SyncResult syncResult)
    {
        Log.v(TAG, query.getURL());
        Log.v(TAG, query.getPostParameters().toString());
        RequestFuture<JSONObject> future = requestBlocking(query);
        try
        {
            JSONObject response = future.get();
            ServerResponse serverResponse = ResponseParser.parseResponse(response);
            if (serverResponse == null)
                return;
            if (!serverResponse.isError())
                query.handleResponse(mContext, serverResponse.getData());
        }
        catch (ExecutionException | InterruptedException e)
        {
            e.printStackTrace();
            syncResult.stats.numIoExceptions++;
        }
    }

    private static Bundle makeManualSyncBundle() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        return bundle;
    }

    public static void syncImmediately(Context context, Bundle queryBundle) {
        Log.v(TAG, "sync immediately");
        Bundle bundle = makeManualSyncBundle();
        bundle.putAll(queryBundle);
        Account lastAccount = Utils.getLastAccount(context);
        ContentResolver.requestSync(lastAccount, context.getString(R.string.content_authority), bundle);
    }

    // use this if automatic data sync needed
    private static void configurePeriodicSync(Account account, Context context, int syncInterval, int flexTime) {
        Log.v(TAG, "configure periodic sync");
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }
}
