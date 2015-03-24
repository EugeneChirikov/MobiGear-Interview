package ru.mobigear.mobigearinterview.ui;

import android.accounts.Account;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.network.LoginResponseParser;
import ru.mobigear.mobigearinterview.network.RegistrationRequestBuilder;
import ru.mobigear.mobigearinterview.network.ServerResponse;
import ru.mobigear.mobigearinterview.network.VolleyHelper;
import ru.mobigear.mobigearinterview.utils.Utils;

/**
 * Created by eugene on 3/23/15.
 */
public class FragmentRegistration extends FragmentAuth {
    private static final String TAG = FragmentRegistration.class.getSimpleName();
    private static final String progressDialogTag = "reg_progress_dialog_tag";
    private EditText fioEdit;
    private EditText emailEdit;
    private EditText passwordEdit;
    private EditText phoneEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        positiveMessage = "Регистрация прошла успешно";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);
        fioEdit = (EditText) rootView.findViewById(R.id.fio);
        emailEdit = (EditText) rootView.findViewById(R.id.email);
        passwordEdit = (EditText) rootView.findViewById(R.id.password);
        phoneEdit = (EditText) rootView.findViewById(R.id.phone);
        Button registerButton = (Button) rootView.findViewById(R.id.button_register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fio = fioEdit.getText().toString();
                String email = emailEdit.getText().toString();
                String password = passwordEdit.getText().toString();
                String phone = phoneEdit.getText().toString();
                requestIfValid(email, password, fio, phone);
            }
        });
        return rootView;
    }

    private void requestIfValid(String email, String password, String fio, String phone) {
        if (Utils.isInputValid(new String[]{email, password, fio, phone}))
            request(email, password, fio, phone);
        else
            Toast.makeText(getActivity(), getString(R.string.input_invalid_message), Toast.LENGTH_SHORT).show();
    }

    private void request(final String email, final String password, final String fio, String phone) {
        RegistrationRequestBuilder requestBuilder = new RegistrationRequestBuilder(fio, email, phone, password);
        String url = requestBuilder.getURL();
        String params =  requestBuilder.getPostParameters().toString();
        Log.v(TAG, url);
        Log.v(TAG, params);
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
                            handleSuccess(email, password, fio ,token);
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

    private void handleError(int errorCode) {
        Toast.makeText(getActivity(), "Ошибка регистрации", Toast.LENGTH_SHORT).show(); // TODO show more precise error
    }

    @Override
    protected Account getAccount(String login, String password, String userName) {
        return addAccountToSystemStorage(login, password, userName);
    }
}
