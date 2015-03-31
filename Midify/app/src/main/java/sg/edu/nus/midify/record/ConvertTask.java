package sg.edu.nus.midify.record;

import android.os.AsyncTask;

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

        //Populating Midi Notes
        this.delegate.populateMidiNotes();

        return null;
    }

    @Override
    protected void onPostExecute(Void test) {

    }
}
