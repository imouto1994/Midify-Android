package sg.edu.nus.midify.record;

import android.os.AsyncTask;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import sg.edu.nus.midify.R;

public class ConvertTask extends AsyncTask<Void, Void, Void> {

    private ConvertTaskDelegate delegate;
    private MaterialDialog progressDialog;

    public ConvertTask (ConvertTaskDelegate delegate, MaterialDialog dialog) {
        this.delegate = delegate;
        this.progressDialog = dialog;
    }

    @Override
    protected Void doInBackground(Void... arg0) {


        //Converting PCM to WAV
        this.delegate.convertPcmToWav(progressDialog);

        // Converting WAV to MIDI
        this.delegate.convertWavToMidi(progressDialog);

        return null;
    }

    @Override
    protected void onPostExecute(Void test) {
        //Populating Midi Notes
        progressDialog.dismiss();
        this.delegate.populateMidiNotes();
    }
}
