package sg.edu.nus.midify.record;

import android.content.Context;

public interface RecordTaskDelegate {

    public Context getContext();

    public void setRecorder(Recorder recorder);

    public void convertPCMToMidi();
}
