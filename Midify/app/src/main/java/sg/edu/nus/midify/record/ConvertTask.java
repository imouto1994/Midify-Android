package sg.edu.nus.midify.record;

import android.os.AsyncTask;
import android.widget.Toast;

public class ConvertTask extends AsyncTask<Void, Void, Void> {

    private ConvertTaskDelegate delegate;

    public ConvertTask (ConvertTaskDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        //Converting PCM to WAV
        this.delegate.convertPcmToWav();

        // Converting WAV to MIDI
        this.delegate.convertWavToMidi();

        return null;
    }

    @Override
    protected void onPostExecute(Void test) {
        //Populating Midi Notes
        Toast.makeText(delegate.getContext(),
                "Successfully converting to MIDI notes", Toast.LENGTH_SHORT).show();
        System.out.println("Start populating...");
        this.delegate.populateMidiNotes();
    }
}
