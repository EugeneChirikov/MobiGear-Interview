package ru.mobigear.mobigearinterview.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.util.Base64;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.data.DataContract;
import ru.mobigear.mobigearinterview.data.DatabaseHelper;
import ru.mobigear.mobigearinterview.demo.Demo;
import ru.mobigear.mobigearinterview.network.ArticlesRequest;
import ru.mobigear.mobigearinterview.network.EventsRequest;
import ru.mobigear.mobigearinterview.network.OnTokenObtainedListener;
import ru.mobigear.mobigearinterview.network.ProfileRequest;
import ru.mobigear.mobigearinterview.network.RunnableWithToken;
import ru.mobigear.mobigearinterview.sync.ContentSyncAdapter;
import ru.mobigear.mobigearinterview.ui.ActivityAuth;
import ru.mobigear.mobigearinterview.ui.DialogProgress;

/**
 * Created by eugene on 3/23/15.
 */
public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void persistValue(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString(key, value);
        prefsEditor.commit();
    }

    public static void removePersistedValue(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

    public static String getPersistedValue(Context context, String key)
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, null);
    }

    public static void logout(final Context context) {
        removePersistedValue(context, Constants.LAST_USER_LOGIN_KEY);
        Thread deleteDBThread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentResolver contentResolver = context.getContentResolver();
                contentResolver.delete(DataContract.getContentUri(DataContract.PATH_PROFILE), null, null);
                contentResolver.delete(DataContract.getContentUri(DataContract.PATH_ARTICLE), null, null);
                contentResolver.delete(DataContract.getContentUri(DataContract.PATH_EVENT), null, null);
            }
        });
        deleteDBThread.start();
    }

    public static Account getLastAccount(Context context) {
        String login = getPersistedValue(context, Constants.LAST_USER_LOGIN_KEY);
        if (login != null && !login.isEmpty())
        {
            AccountManager accountManager = AccountManager.get(context);
            Account[] accounts = accountManager.getAccountsByType(context.getString(R.string.account_type));
            for (int i = 0; i < accounts.length; ++i) {
                Account a = accounts[i];
                String p = accountManager.getPassword(a);
                if (p != null && a.name.equals(login)) {
                    return a;
                }
            }
        }
        return null;
    }

    public static String extractEmailLoginFrom(Account a) {
        return a.name;
    }

    public static String extractUserNameFrom(Account a, AccountManager manager) {
        return manager.getUserData(a, Constants.USER_NAME_KEY);
    }

    public static String extractPasswordFrom(Account a, AccountManager manager) {
        return manager.getPassword(a);
    }

    public static void extractTokenFrom(Account a, Context context, final OnTokenObtainedListener listener) {
        AccountManager manager = AccountManager.get(context);
        manager.getAuthToken(a, Constants.ACCOUNT_TOKEN_TYPE, null, new ActivityAuth(), new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bundle = future.getResult();
                    String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    if (token != null)
                        listener.onTokenObtained(token);
                    else {
                        listener.onTokenNotObtained();
                    }
                } catch (OperationCanceledException | IOException | AuthenticatorException ignored) {}
            }
        }, null);
    }

    public static void doIfTokenObtained(final Context context, final RunnableWithToken runnable) {
        Account lastAccount = Utils.getLastAccount(context);
        if (lastAccount == null) {
            Toast.makeText(context, "Вы не вошли", Toast.LENGTH_SHORT).show();
            return;
        }
        Utils.extractTokenFrom(lastAccount, context, new OnTokenObtainedListener() {
            @Override
            public void onTokenObtained(String token) {
                runnable.run(token);
            }

            @Override
            public void onTokenNotObtained() {
                // this can be fixed in Authenticator, left as is for simplicity
                Toast.makeText(context, "Ошибка входа, перезайдите через форму", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void showProgressDialog(FragmentManager fragmentManager, String tag) {
        DialogProgress dialog = new DialogProgress();
        dialog.setCancelable(false);
        dialog.show(fragmentManager, tag);
    }

    public static void dismissProgressDialog(FragmentManager fm, String tag) {
        DialogProgress progressDialog = (DialogProgress) fm.findFragmentByTag(tag);
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public static boolean isInputValid(String[] entries){
        for (String s: entries)
            if (s == null || s.isEmpty())
                return false;
        return true;
    }

    public static String bitmapToBase64String(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
        byte [] byteArray = outputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void setDateTime(TextView dateTimeView, String dateTime) {
        Date date = Formatter.dateFrom(dateTime);
        if (date == null)
            dateTimeView.setText("");
        else
            dateTimeView.setText(Formatter.formatDateTime(date));
    }

    public static int getVolleyNetworkErrorCode(VolleyError error) {
        if (error == null || error.networkResponse == null)
            return Constants.NetworkResponseCode.ERROR_UNKNOWN;
        return error.networkResponse.statusCode;
    }

    public static void refreshEverything(final Context context) {
        ContentSyncAdapter.syncImmediately(context, new ProfileRequest().getBundle());
        ContentSyncAdapter.syncImmediately(context, new ArticlesRequest().getBundle());
        ContentSyncAdapter.syncImmediately(context, new EventsRequest().getBundle());
        // when all requests fail we want to see at least the demo
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Demo.fillWithData(context);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
        thread.start();
    }
}