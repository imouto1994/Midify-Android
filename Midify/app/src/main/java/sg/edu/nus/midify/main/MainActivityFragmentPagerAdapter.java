package sg.edu.nus.midify.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;;import sg.edu.nus.midify.main.activity.ActivityFragment;
import sg.edu.nus.midify.main.user.UserFragment;

public class MainActivityFragmentPagerAdapter extends FragmentPagerAdapter {
    private static final int ACTIVITY_FRAGMENT_INDEX = 0;
    private static final int USER_FRAGMENT_INDEX = 1;
    private static final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[] { "Activity", "Midi"};
    private Context context;

    public MainActivityFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == ACTIVITY_FRAGMENT_INDEX) {
            return ActivityFragment.newInstance();
        } else if (position == USER_FRAGMENT_INDEX) {
            return UserFragment.newInstance();
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