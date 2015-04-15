package sg.edu.nus.midify.record;

import android.content.Context;

public interface InitTaskDelegate {

    public Context getContext();

    public void initializeConverters();
}
