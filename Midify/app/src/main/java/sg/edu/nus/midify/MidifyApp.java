package sg.edu.nus.midify;

import android.app.Application;
import android.content.Context;

/**
 * Created by Youn on 16/4/15.
 */
public class MidifyApp extends Application {
    private static MidifyApp instance;

    public static MidifyApp instance() {
        return instance;
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }
}
