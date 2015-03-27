package ru.mobigear.mobigearinterview.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import ru.mobigear.mobigearinterview.sync.ContentSyncAdapter;
import ru.mobigear.mobigearinterview.utils.Constants;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/23/15.
 */
public abstract class FragmentAuth extends Fragment {
    protected AccountManager accountManager;
    protected String positiveMessage = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountManager = AccountManager.get(getActivity());
    }

    protected Account addAccountToSystemStorage(String login, String password, String userName)
    {
        Account newAccount = new Account(login, Constants.ACCOUNT_TYPE);
        Bundle userdata = new Bundle();
        userdata.putString(Constants.USER_NAME_KEY, userName);
        accountManager.addAccountExplicitly(newAccount, password, userdata);
        return newAccount;
    }

    protected abstract Account getAccount(String login, String password, String userName);

    protected void handleSuccess(String email) {
        Toast.makeText(getActivity(), positiveMessage, Toast.LENGTH_SHORT).show();
        Utils.persistValue(getActivity(), Constants.LAST_USER_LOGIN_KEY, email);
    }

    protected void handleSuccess(String email, String password, String userName, String authToken) {
        handleSuccess(email);
        Account account = getAccount(email, password, userName);
        accountManager.setAuthToken(account, Constants.ACCOUNT_TOKEN_TYPE, authToken);
        Utils.refreshEverything(getActivity());
        Activity activity = getActivity();
        if (activity instanceof ActivityAuth) {
            Bundle result = new Bundle();
            ((ActivityAuth) activity).setAccountAuthenticatorResult(result);
            activity.setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }
}
