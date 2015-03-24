package ru.mobigear.mobigearinterview.ui;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.network.LoginRequestBuilder;
import ru.mobigear.mobigearinterview.network.LoginResponseParser;
import ru.mobigear.mobigearinterview.network.ServerResponse;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Constants;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/23/15.
 */
public class FragmentLogin extends FragmentAuth {
    private static final String progressDialogTag = "reg_progress_dialog_tag";
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
                String password = Utils.extractPasswordFrom(account, accountManager);
                requestIfValid(email, password);
            }
        });
        return rootView;
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
        LoginRequestBuilder requestBuilder = new LoginRequestBuilder(email, password);
        Utils.showProgressDialog(getFragmentManager(), progressDialogTag);
        JsonObjectRequest request = new JsonObjectRequest
                (requestBuilder.getURL(), requestBuilder.getPostParameters(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Utils.dismissProgressDialog(getFragmentManager(), progressDialogTag);
                        LoginResponseParser parser = new LoginResponseParser();
                        ServerResponse<String> parsedResponse = parser.parseResponse(response);
                        if (parsedResponse.isError())
                            handleError(parsedResponse.getCode());
                        else {
                            String token = parsedResponse.getData();
                            handleSuccess(email, password, getString(R.string.default_user_name) ,token);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Utils.dismissProgressDialog(getFragmentManager(), progressDialogTag);
                        handleError(error.networkResponse.statusCode);
                    }
                });
        VolleyHelper.getRequestQueue(getActivity()).add(request);
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
