package sg.edu.nus.midify.record;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import sg.edu.nus.midify.R;


public class RecordActivity extends Activity implements InitTaskDelegate, RecordTaskDelegate, ConvertTaskDelegate {

    private Recorder recorderProcess;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        InitTask initTask = new InitTask(this);
        initTask.execute();
    }

    public void onRecordButtonClick (View view) {

    }


    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    //=============================================================================================
    // INIT TASK DELEGATE
    //=============================================================================================
    @Override
    public void loadNativeLibs() {

    }

    @Override
    public void initializeConverters() {

    }

    //=============================================================================================
    // RECORD TASK DELEGATE
    //=============================================================================================
    @Override
    public void setRecorder(Recorder recorder) {

    }

    @Override
    public void convertPCMToMidi() {

    }

    //=============================================================================================
    // CONVERT TASK DELEGATE
    //=============================================================================================
    @Override
    public void convertPcmToWav() {

    }

    @Override
    public void convertWavToMidi() {

    }

    @Override
    public void populateMidiNotes() {

    }
}
