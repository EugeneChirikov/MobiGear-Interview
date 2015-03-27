package ru.mobigear.mobigearinterview.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import ru.mobigear.mobigearinterview.R;

/**
 * Created by eugene on 3/25/15.
 */
public class FragmentUserTabs extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_tabs, container, false);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.user_pager);
        UserPagesAdapter adapter = new UserPagesAdapter(getActivity().getApplicationContext(), getChildFragmentManager());
        viewPager.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments == null)
            return;
        try {
            for (Fragment fragment : fragments)
                fragment.onActivityResult(requestCode, resultCode, data);
        } catch (Exception ignored) {}
    }
}