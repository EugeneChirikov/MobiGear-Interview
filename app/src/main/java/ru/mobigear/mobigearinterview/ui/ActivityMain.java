package ru.mobigear.mobigearinterview.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import ru.mobigear.mobigearinterview.R;
import ru.mobigear.mobigearinterview.demo.Demo;
import ru.mobigear.mobigearinterview.utils.Constants;
import ru.mobigear.mobigearinterview.utils.Utils;


public class ActivityMain extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        OnFragmentsIntaractionListener {
    private static final String TAG = ActivityMain.class.getSimpleName();
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Fragment fragment = null;
        switch (position) {
            case Constants.Navigation.USER: {
                fragment = new FragmentUserTabs();
                break;
            }
            case Constants.Navigation.LOGOUT: {
                Utils.logout(this);
                Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
                return;
            }
            case Constants.Navigation.ARTICLES: {
                fragment = new FragmentArticlesList();
                break;
            }
            case Constants.Navigation.EVENTS: {
                fragment = new FragmentEventsList();
                break;
            }
            default:
                return;
        }
        setFragment(fragment, null);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.activity_main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Utils.refreshEverything(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager
                .beginTransaction().replace(R.id.container, fragment);
        if (tag != null)
            transaction.addToBackStack(tag);
        transaction.commit();
    }

    @Override
    public void articleChosen(long articleId) {
        Fragment fragment = FragmentArticle.newInstance(articleId);
        setFragment(fragment, "article_tag");
    }

    @Override
    public void eventChosen(long eventId) {
        Fragment fragment = FragmentEvent.newInstance(eventId);
        setFragment(fragment, "event_tag");
    }
}
