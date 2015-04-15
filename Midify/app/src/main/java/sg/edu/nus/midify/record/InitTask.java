package sg.edu.nus.midify.record;

import android.os.AsyncTask;
import android.widget.Toast;

public class InitTask extends AsyncTask<Void, Void, Void> {

    private InitTaskDelegate delegate;

    public InitTask(InitTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        //Init ATM Converters
        delegate.initializeConverters();

        return null;
    }

    @Override
    protected void onPostExecute(Void test) {

    }
}
