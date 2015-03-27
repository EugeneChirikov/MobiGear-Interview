package ru.mobigear.mobigearinterview.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ru.mobigear.mobigearinterview.R;

/**
 * Created by eugene on 3/25/15.
 */
public class UserPagesAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public UserPagesAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FragmentProfile();
            case 1:
                return new FragmentLogin();
            case 2:
                return new FragmentRegistration();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String[] titles = mContext.getResources().getStringArray(R.array.user_tabs_titles_array);
        return titles[position];
    }

    @Override
    public int getCount() {
        return 3;
    }
}
