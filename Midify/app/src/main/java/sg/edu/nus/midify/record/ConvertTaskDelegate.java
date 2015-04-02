package sg.edu.nus.midify.record;

import android.content.Context;

public interface ConvertTaskDelegate {

    public Context getContext();

    public void convertPcmToWav();

    public void convertWavToMidi();

    public void populateMidiNotes();

}
