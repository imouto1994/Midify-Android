package sg.edu.nus.midify.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;;import sg.edu.nus.midify.main.activity.ActivityFragment;
import sg.edu.nus.midify.main.user.UserFragment;

public class MainActivityFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int ACTIVITY_FRAGMENT_TAB_INDEX = 0;
    private static final int USER_FRAGMENT_TAB_INDEX = 1;
    private static final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Activity", "Midi"};
    private MainActivity activity;

    public MainActivityFragmentPagerAdapter(FragmentManager fm, MainActivity activity) {
        super(fm);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == ACTIVITY_FRAGMENT_TAB_INDEX) {
            ActivityFragment fragment = ActivityFragment.newInstance();
            activity.setFragment(fragment, MainActivity.ACTIVITY_FRAGMENT_INDEX);
            return fragment;
        } else if (position == USER_FRAGMENT_TAB_INDEX) {
            UserFragment fragment = UserFragment.newInstance();
            activity.setFragment(fragment, MainActivity.USER_FRAGMENT_INDEX);
            return fragment;
        } else {
            System.out.println("No fragment for the required tab index");
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}