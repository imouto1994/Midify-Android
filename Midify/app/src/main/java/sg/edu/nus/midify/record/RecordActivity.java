package sg.edu.nus.midify.record;

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import sg.edu.nus.helper.Constant;
import sg.edu.nus.midify.R;


public class RecordActivity extends Activity implements InitTaskDelegate, RecordTaskDelegate, ConvertTaskDelegate {

    private static final String RECORD_TAG = "record";
    private static final String STOP_RECORD_TAG = "stop";


    // UI Controls
    private TextView statusTextView;
    private Button recordButton;

    private Recorder recorderProcess;
    private List<Note> midiNotes;
    private PcmToWavConverter pcmToWavConverter;
    private WavToMidiConverter wavToMidiConverter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // UI Control Assignment
        statusTextView = (TextView) findViewById(R.id.status_text_view);
        recordButton = (Button) findViewById(R.id.record_button);

        // Background initializing task
        InitTask initTask = new InitTask(this);
        initTask.execute();
    }

    public void onRecordButtonClick (View view) {
        if (recordButton.getText().toString().toLowerCase().equals(RECORD_TAG)) {
            RecordTask task = new RecordTask(this);
            task.execute();
            recordButton.setText(STOP_RECORD_TAG);
        } else if (recordButton.getText().toString().toLowerCase().equals(STOP_RECORD_TAG)) {
            if (recorderProcess != null) {
                recorderProcess.setRecording(false);
            }
            recordButton.setEnabled(false);
        }
    }


    @Override
    public Context getContext() {
        return getApplicationContext();
    }

    //=============================================================================================
    // INIT TASK DELEGATE
    //=============================================================================================

    /**
     * Load the JNI Native Libs
     */
    @Override
    public void loadNativeLibs() {
        try {
            System.loadLibrary("atm");
        } catch (Exception ex) {
            Log.e(Constant.JNI_TAG, "Failed to load native library: " + ex);
        }
    }

    /**
     * Initialize the two necessary converters
     */
    @Override
    public void initializeConverters() {
        // Initialize the PCM to WAV converter
        pcmToWavConverter = new PcmToWavConverter(Constant.DEFAULT_PCM_FILE_NAME,
                                                   Constant.DEFAULT_WAV_FILE_NAME);

        // Initialize the WAV to MIDI converter
        wavToMidiConverter = new WavToMidiConverter(Constant.DEFAULT_WAV_FILE_NAME);
    }

    //=============================================================================================
    // RECORD TASK DELEGATE
    //=============================================================================================
    @Override
    public void setRecorder(Recorder recorder) {
        recorderProcess = recorder;
    }

    @Override
    public void convertPCMToMidi() {
        ConvertTask task = new ConvertTask(this);
        task.execute();
    }

    //=============================================================================================
    // CONVERT TASK DELEGATE
    //=============================================================================================
    @Override
    public void convertPcmToWav() {
        int bitsPerSampleInt = AudioFormat.ENCODING_PCM_16BIT;
        int bitsPerSample = 16;
        switch (bitsPerSampleInt) {
            case AudioFormat.ENCODING_DEFAULT:
                bitsPerSample = 16;
                break;
            case AudioFormat.ENCODING_PCM_8BIT:
                bitsPerSample = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitsPerSample = 16;
                break;

        }
        pcmToWavConverter.setBitPerSample(bitsPerSample);
        pcmToWavConverter.setChannels(1);
        pcmToWavConverter.setSamplerate(11025);
        pcmToWavConverter.convertPcm2wav();
    }

    @Override
    public void convertWavToMidi() {
        int bitsPerSampleInt = AudioFormat.ENCODING_PCM_16BIT;
        int bitsPerSample = 16;
        switch (bitsPerSampleInt) {
            case AudioFormat.ENCODING_DEFAULT:
                bitsPerSample = 16;
                break;
            case AudioFormat.ENCODING_PCM_8BIT:
                bitsPerSample = 8;
                break;
            case AudioFormat.ENCODING_PCM_16BIT:
                bitsPerSample = 16;
                break;

        }

        // Set Wav Params
        wavToMidiConverter.setBitspersample(bitsPerSample);
        wavToMidiConverter.setChannels(1);
        wavToMidiConverter.setSamplerate(11025);

        //Set Engine Params
        wavToMidiConverter.setBuffer_size(512);
        wavToMidiConverter.setOverlap_size(256);
        wavToMidiConverter.setSilence(-90);
        wavToMidiConverter.setThreshold(0.30f);
        wavToMidiConverter.setType_onset("kl");
        wavToMidiConverter.setType_onset2("complex");
        wavToMidiConverter.setType_pitch("yinfft");
        wavToMidiConverter.setAveraging((float) 1.0);

        // Start converting
        wavToMidiConverter.wav2midiNotes();
    }

    @Override
    public void populateMidiNotes() {
        this.midiNotes = wavToMidiConverter.getNotes();
        for (int i = 0; i < midiNotes.size(); i++) {
            Note note = midiNotes.get(i);
            if (note != null) {
                System.out.println("Note: " + note.note + " Velocity: " + note.vel + " Time: " + note.time);
            } else {
                midiNotes.remove(i--);
            }
        }
    }
}
