package sg.edu.nus.midify.main;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.melnykov.fab.FloatingActionButton;

import retrofit.Callback;
import retrofit.RetrofitError;
import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.MidifyRestClient;
import sg.edu.nus.helper.PersistenceHelper;
import sg.edu.nus.helper.SlidingTabLayout;
import sg.edu.nus.midify.R;
import sg.edu.nus.midify.record.RecordActivity;

public class MainActivity extends ActionBarActivity {

    private static final int LOGIN_FRAGMENT_INDEX = 0;
    private static final int FRAGMENT_COUNT = 1;

    // UI CONTROLS
    private FloatingActionButton midifyButton;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];

    private boolean isResumed = false;

    // FACEBOOK HELPERS
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

        // Initialize toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        // Initialize record button
        midifyButton = (FloatingActionButton) findViewById(R.id.midify_button);
        midifyButton.hide(false);


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
                return getResources().getColor(R.color.White);
            }
        });

        // Initialize Midify REST Client
        MidifyRestClient.initialize();
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
            getSupportActionBar().show();
            hideFragment(LOGIN_FRAGMENT_INDEX, false);
            String currentToken = PersistenceHelper.getFacebookToken(this);
            if (currentToken == null || !currentToken.equals(session.getAccessToken())) {
                retrieveUserId(session);
                PersistenceHelper.saveFacebookToken(this, session.getAccessToken());
            }
            MidifyRestClient.instance().setAccessToken(session.getAccessToken());
            midifyButton.show(true);
        } else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
            getSupportActionBar().hide();
            midifyButton.hide(true);
            showFragment(LOGIN_FRAGMENT_INDEX, false);
        }
    }

    public void onMidifyButtonClicked(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    // Handler when state of session has changed
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (isResumed) {
            FragmentManager manager = getFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                getSupportActionBar().show();
                hideFragment(LOGIN_FRAGMENT_INDEX, false);
                String currentToken = PersistenceHelper.getFacebookToken(this);
                if (currentToken == null || !currentToken.equals(session.getAccessToken())) {
                    retrieveUserId(session);
                    PersistenceHelper.saveFacebookToken(this, session.getAccessToken());
                }
                MidifyRestClient.instance().setAccessToken(session.getAccessToken());
                midifyButton.show(true);
            } else if (state.isClosed()) {
                midifyButton.hide(true);
                getSupportActionBar().hide();
                showFragment(LOGIN_FRAGMENT_INDEX, false);
            }
        }
    }

    private void retrieveUserId(final Session session) {
        final Context context = this;
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
                // If the response is successful
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                        PersistenceHelper.saveFacebookUserId(context, user.getId());
                        MidifyRestClient.instance().authenticate(session.getAccessToken(),
                                user.getId(),
                                new Callback<UserPOJO>() {
                            @Override
                            public void success(UserPOJO userPOJO, retrofit.client.Response response) {
                                Log.i(Constant.LOGIN_TAG, "Authenticate with Midify Server successfully");
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(Constant.LOGIN_TAG, "There is error in authenticating with Midify server");
                            }
                        });
                    }
                }
            }
        });
        Request.executeBatchAsync(request);
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
