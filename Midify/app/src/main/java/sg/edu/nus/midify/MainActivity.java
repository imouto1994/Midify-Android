package sg.edu.nus.midify;

import android.app.Fragment;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.SlidingTabLayout;

public class MainActivity extends ActionBarActivity {

    private static final int LOGIN_FRAGMENT_INDEX = 0;
    private static final int FRAGMENT_COUNT = 1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Facebook UIHelper
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        FragmentManager fm = getFragmentManager();
        LoginFragment loginFragment = (LoginFragment) fm.findFragmentById(R.id.login_fragment);
        fragments[LOGIN_FRAGMENT_INDEX] = loginFragment;

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new MidifyFragmentPagerAdapter(getSupportFragmentManager(),
                MainActivity.this));

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        // Center the tabs in the layout
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer(){

            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.color_primary);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;
        Session session = Session.getActiveSession();
        if (session != null && (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        } else {
            uiHelper.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open, try to show the selection fragment
        } else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
            showFragment(LOGIN_FRAGMENT_INDEX, false);
        }
    }


    // Handler when state of session has changed
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        Log.d(Constant.LOGIN_TAG, "Session: " + session.getAccessToken());
        if (isResumed) {
            FragmentManager manager = getFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                hideFragment(LOGIN_FRAGMENT_INDEX, false);
            } else if (state.isClosed()) {
                showFragment(LOGIN_FRAGMENT_INDEX, false);
            }
        }
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.show(fragments[fragmentIndex]);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    private void hideFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.hide(fragments[fragmentIndex]);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }
}
