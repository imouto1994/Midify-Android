package sg.edu.nus.midify.record;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.midi.MidiFile;
import sg.edu.nus.midi.MidiTrack;
import sg.edu.nus.midi.event.meta.Tempo;
import sg.edu.nus.midi.event.meta.TimeSignature;
import sg.edu.nus.midify.R;


public class RecordActivity extends Activity implements InitTaskDelegate, RecordTaskDelegate, ConvertTaskDelegate {

    private static final String RECORD_TAG = "record";
    private static final String STOP_RECORD_TAG = "stop";
    private static final int DEFAULT_TEMPO = 228;
    private static final int DEFAULT_CHANNEL = 0;

    // UI Controls
    private TextView statusTextView;
    private Button recordButton;

    private Recorder recorderProcess;
    private List<Note> midiNotes;
    private PcmToWavConverter pcmToWavConverter;
    private WavToMidiConverter wavToMidiConverter;

    // Persistence Data
    private SharedPreferences midiPreferences;
    private List<MidiPOJO> midiList;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // UI Control Assignment
        statusTextView = (TextView) findViewById(R.id.status_text_view);
        recordButton = (Button) findViewById(R.id.record_button);

        // Loading Preferences
        midiList = PersistenceHelper.getMidiList(this);

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
        pcmToWavConverter = new PcmToWavConverter(Constant.DEFAULT_PCM_FILE_PATH,
                                                   Constant.DEFAULT_WAV_FILE_PATH);

        // Initialize the WAV to MIDI converter
        wavToMidiConverter = new WavToMidiConverter(Constant.DEFAULT_WAV_FILE_PATH);
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
        // Show progress dialog
        MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_convert_progress_title)
                .content(R.string.dialog_midi_convert_progress_content)
                .progress(true, 0)
                .show();
        ConvertTask task = new ConvertTask(this, progressDialog);
        task.execute();
    }

    //=============================================================================================
    // CONVERT TASK DELEGATE
    //=============================================================================================
    @Override
    public void convertPcmToWav(MaterialDialog progressDialog) {
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
    public void convertWavToMidi(MaterialDialog progressDialog) {
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
            if (note == null) {
                midiNotes.remove(i--);
            }
        }

        MaterialDialog uploadDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_midi_name_input_title)
                .customView(R.layout.dialog_create_midi, true)
                .positiveText(R.string.dialog_upload_action_button)
                .negativeText(R.string.dialog_cancel_action_button)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        dialog.dismiss();
                        EditText midiFileNameInput = (EditText) dialog.getCustomView().findViewById(R.id.dialog_midi_name_input);
                        RadioGroup isPublicRadioGroup = (RadioGroup) dialog.getCustomView().findViewById(R.id.dialog_radio_group);
                        boolean isPublicMidiFile = isPublicRadioGroup.getCheckedRadioButtonId() == R.id.dialog_radio_button_public;
                        createMidiFile(midiFileNameInput.getText().toString(), isPublicMidiFile);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                    }
                }).build();

        final View positiveAction = uploadDialog.getActionButton(DialogAction.POSITIVE);
        EditText midiFileNameInput = (EditText) uploadDialog.getCustomView().findViewById(R.id.dialog_midi_name_input);
        midiFileNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                positiveAction.setEnabled(s.toString().trim().length() > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        uploadDialog.show();
        positiveAction.setEnabled(false);
    }

    private void createMidiFile(String midiFileName, boolean isPublicMidiFile) {

        // Create MIDI tracks
        MidiTrack tempoTrack = new MidiTrack();
        MidiTrack noteTrack = new MidiTrack();

        // First track is for tempo map
        TimeSignature ts = new TimeSignature();
        ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
        Tempo t = new Tempo();
        t.setBpm(DEFAULT_TEMPO);
        tempoTrack.insertEvent(ts);
        tempoTrack.insertEvent(t);

        // Second track is for note map
        for (int i = 0; i < midiNotes.size(); i++) {
            Note note = midiNotes.get(i);
            int channel = DEFAULT_CHANNEL;
            int pitch = note.note;
            int velocity = note.vel;
            long duration = (long) note.time;
            long tick = i * 480;

            noteTrack.insertNote(channel, pitch, velocity, tick, duration);
        }

        // Create MIDI File
        ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(tempoTrack);
        tracks.add(noteTrack);

        MidiFile midiFile = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
        String filePath = Constant.BASE_FILE_DIR + midiFileName
                        + String.valueOf(System.currentTimeMillis() / 1000 + ".mid");
        Log.i(Constant.RECORD_TAG, filePath);
        File output = new File(filePath);
        try {
            midiFile.writeToFile(output);
        } catch(IOException e) {
            Log.e(Constant.RECORD_TAG, "Cannot write the MIDI file output");
        }

        String facebookUserId = PersistenceHelper.getFacebookUserId(this);

        final MidiPOJO newMidi = MidiPOJO.
                createLocalMidiWithoutId(midiFileName, filePath, facebookUserId, isPublicMidiFile);
        midiList.add(newMidi);
        PersistenceHelper.saveMidiList(this, midiList);
        if (ConnectionHelper.checkNetworkConnection(this)) {
            final Context context = this;
            try {
                MidifyRestClient.instance()
                        .uploadMidi(filePath, midiFileName, isPublicMidiFile, new Callback<MidiPOJO>() {
                    @Override
                    public void success(MidiPOJO midiPOJO, Response response) {
                        newMidi.setFileId(midiPOJO.getFileId());
                        newMidi.setEditedTime(midiPOJO.getEditedTime());
                        newMidi.setServerFilePath(midiPOJO.getServerFilePath());
                        PersistenceHelper.saveMidiList(context, midiList);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                    }
                });
            } catch (IOException e) {
                Log.e(Constant.RECORD_TAG, "Cannot upload due to invalid MIDI file path");
            }
        }
    }
}
