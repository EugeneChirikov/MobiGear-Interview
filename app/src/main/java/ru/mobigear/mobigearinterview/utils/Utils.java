package ru.mobigear.mobigearinterview.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;

import ru.mobigear.mobigearinterview.ui.DialogProgress;

/**
 * Created by eugene on 3/23/15.
 */
public class Utils {
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

//    public static void logout(final Context context) {
//        removePersistedValue(context, Constants.LAST_USER_LOGIN_KEY);
//        final Handler handler = new Handler();
//        Thread deleteDBThread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                DatabaseHelper.getInstance(context).clearDatabase();
//            }
//        });
//        deleteDBThread.start();
//    }

    public static String extractEmailLoginFrom(Account a) {
        return a.name;
    }

    public static String extractUserNameFrom(Account a, AccountManager manager) {
        return manager.getUserData(a, Constants.USER_NAME_KEY);
    }

    public static String extractPasswordFrom(Account a, AccountManager manager) {
        return manager.getPassword(a);
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
}
