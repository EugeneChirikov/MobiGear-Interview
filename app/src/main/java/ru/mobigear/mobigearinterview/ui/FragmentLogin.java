package ru.mobigear.mobigearinterview.ui;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.network.LoginRequest;
import ru.mobigear.mobigearinterview.network.ResponseListener;
import ru.mobigear.mobigearinterview.network.TokenParser;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Constants;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/23/15.
 */
public class FragmentLogin extends FragmentAuth {
    private static final String TAG = FragmentLogin.class.getSimpleName();
    private EditText emailEdit;
    private EditText passwordEdit;
    private AccountsAdapter accountsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountsAdapter = makeAccountsAdapter();
        positiveMessage = "Вход выполнен успешно";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        emailEdit = (EditText) rootView.findViewById(R.id.email);
        passwordEdit = (EditText) rootView.findViewById(R.id.password);
        Button loginButton = (Button) rootView.findViewById(R.id.button_login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                requestIfValid(email, password);
            }
        });
        ListView accountsList = (ListView) rootView.findViewById(R.id.accounts_list);
        accountsList.setAdapter(accountsAdapter);
        accountsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Account account = (Account) parent.getItemAtPosition(position);
                String email = Utils.extractEmailLoginFrom(account);
                handleSuccess(email);
                Utils.refreshEverything(getActivity());
            }
        });
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        VolleyHelper.getRequestQueue(getActivity()).cancelAll(TAG);
        Utils.dismissProgressDialog(getFragmentManager(), TAG);
    }

    private void requestIfValid(String email, String password) {
        if (Utils.isInputValid(new String[]{email, password}))
            request(email, password);
        else
            Toast.makeText(getActivity(), getString(R.string.input_invalid_message), Toast.LENGTH_SHORT).show();
    }

    private AccountsAdapter makeAccountsAdapter()
    {
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        return new AccountsAdapter(getActivity(), accounts);
    }

    private void request(final String email, final String password) {
        LoginRequest loginRequest = new LoginRequest(email, password);
        VolleyHelper.makeJSONObjectRequest(getActivity(), getFragmentManager(), loginRequest, TAG, new ResponseListener() {
            @Override
            public void onError(int errorCode) {
                handleError(errorCode);
            }

            @Override
            public void onSuccess(JSONObject response) {
                TokenParser parser = new TokenParser();
                String token = parser.parseData(response);
                Log.v(TAG, token);
                handleSuccess(email, password, getString(R.string.default_user_name) ,token);
            }
        });
    }

    @Override
    protected Account getAccount(String login, String password, String userName) {
        Account[] accounts = accountManager.getAccountsByType(Constants.ACCOUNT_TYPE);
        for (int i = 0; i < accounts.length; ++i)
            if (accounts[i].name == login)
                return accounts[i];
        return addAccountToSystemStorage(login, password, userName);
    }

    private void handleError(int errorCode) {
        Toast.makeText(getActivity(), "Ошибка входа", Toast.LENGTH_SHORT).show(); // TODO show more precise error
    }
}
