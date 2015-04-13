package sg.edu.nus.midify.record;

import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

public interface ConvertTaskDelegate {

    public Context getContext();

    public void convertPcmToWav(MaterialDialog progressDialog);

    public void convertWavToMidi(MaterialDialog progressDialog);

    public void populateMidiNotes();

}
