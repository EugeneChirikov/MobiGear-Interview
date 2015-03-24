package ru.mobigear.mobigearinterview.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.utils.Utils;


public class AccountsAdapter extends BaseAdapter
{
    private LayoutInflater inflater;
    private Account[] accounts;
    private AccountManager accountManager;

    public AccountsAdapter(Context context, Account[] accounts)
    {
        accountManager = AccountManager.get(context);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.accounts = accounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item_account, parent, false);
        TextView userNameText = (TextView)convertView.findViewById(R.id.user_name);
        TextView emailText = (TextView)convertView.findViewById(R.id.email);
        Account a = getItem(position);
        String login = Utils.extractEmailLoginFrom(a);
        String userName = Utils.extractUserNameFrom(a, accountManager);
        userNameText.setText(userName);
        emailText.setText(login);
        return convertView;
    }

    @Override
    public int getCount()
    {
        return accounts.length;
    }

    @Override
    public Account getItem(int position)
    {
        return accounts[position];
    }

    @Override
    public long getItemId(int arg0)
    {
        return 0;
    }

}
