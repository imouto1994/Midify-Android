package sg.edu.nus.midify.record;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.midify.R;


public class RecordActivity extends Activity {

    // UI Controls
    private Button recordButton;

    // RECORD
    private WavAudioRecorder audioRecorder;
    private boolean hasRecord;

    // Persistence Data
    private List<MidiPOJO> midiList;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // UI Control Assignment
        recordButton = (Button) findViewById(R.id.record_button);
        recordButton.setText("Start");

        // Loading Preferences
        midiList = PersistenceHelper.getMidiList(this);

        // Recording controls
        audioRecorder = WavAudioRecorder.getInstance();
        audioRecorder.setOutputFile(Constant.DEFAULT_WAV_FILE_PATH);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (audioRecorder != null) {
            audioRecorder.release();
        }
    }

    public void onRecordButtonClick (View view) {
        if (WavAudioRecorder.State.INITIALIZING == audioRecorder.getState()) {
            audioRecorder.prepare();
            audioRecorder.start();
            hasRecord = true;
            recordButton.setText("Stop");
        } else if (WavAudioRecorder.State.ERROR == audioRecorder.getState()) {
            audioRecorder.release();
            audioRecorder = WavAudioRecorder.getInstance();
            audioRecorder.setOutputFile(Constant.DEFAULT_WAV_FILE_PATH);
            recordButton.setText("Start");
        } else {
            audioRecorder.stop();
            audioRecorder.reset();
            fetchUserInput();
            recordButton.setText("Start");
        }
    }

    // Fetch user input for the file name and whether this file should be public or private
    public void fetchUserInput() {
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
                        EditText midiFileNameInput = (EditText) dialog.getCustomView()
                                .findViewById(R.id.dialog_midi_name_input);
                        RadioGroup isPublicRadioGroup = (RadioGroup) dialog.getCustomView()
                                .findViewById(R.id.dialog_radio_group);
                        boolean isPublicMidiFile = isPublicRadioGroup.getCheckedRadioButtonId() == R.id.dialog_radio_button_public;
                        try {
                            createMidiFile(midiFileNameInput.getText().toString(), isPublicMidiFile);
                        } catch (IOException e) {
                            Log.e(Constant.RECORD_TAG, "Cannot convert due to invalid file path");
                        }
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

    private void createMidiFile(String fileName, boolean isPublicMidiFile) throws IOException{

        // Copy WAV File into new file with updated file name
        String filePath = Constant.BASE_FILE_DIR + fileName
                        + System.currentTimeMillis() / 1000 + ".wav";
        File output = new File(filePath);
        File input = new File(Constant.DEFAULT_WAV_FILE_PATH);
        try {
            PersistenceHelper.copy(input, output);
        } catch(IOException e) {
            Log.e(Constant.RECORD_TAG, "Cannot copy the wav file");
        }

        // Store temporary MIDI file locally
        String facebookUserId = PersistenceHelper.getFacebookUserId(this);
        final MidiPOJO newMidi = MidiPOJO.
                createLocalMidiWithoutId(fileName, filePath, facebookUserId, isPublicMidiFile);
        midiList.add(newMidi);
        PersistenceHelper.saveMidiList(this, midiList);

        // Show progress dialog
        final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_convert_progress_title)
                .content(R.string.dialog_convert_progress_content_1)
                .progress(true, 0)
                .show();

        // Start converting
        if (ConnectionHelper.checkNetworkConnection()) {
            final Activity recordInstance = this;
            MidifyRestClient.instance()
                    .convertMidi(filePath, fileName, isPublicMidiFile, new Callback<MidiPOJO>() {
                @Override
                public void success(MidiPOJO midiPOJO, Response response) {
                    newMidi.setFileId(midiPOJO.getFileId());
                    newMidi.setEditedTime(midiPOJO.getEditedTime());
                    newMidi.setServerFilePath(midiPOJO.getServerFilePath());
                    newMidi.setServerWavFilePath(midiPOJO.getServerWavFilePath());
                    PersistenceHelper.saveMidiList(recordInstance, midiList);
                    progressDialog.setContent(getString(R.string.dialog_convert_progress_content_2));

                    // Start downloading
                    MidifyRestClient.instance().downloadMidi(newMidi.getFileId(), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            byte[] data = ((TypedByteArray) response.getBody()).getBytes();
                            String localFilePath = Constant.BASE_FILE_DIR + newMidi.getFileName()
                                    + System.currentTimeMillis() / 1000 + ".mid";
                            File localMidifFile = new File(localFilePath);
                            try {
                                if (!localMidifFile.exists()) {
                                    if (!localMidifFile.createNewFile()) {
                                        throw new IOException();
                                    }
                                }
                                FileOutputStream outputStream = new FileOutputStream(localMidifFile);
                                IOUtils.write(data, outputStream);
                                outputStream.close();
                                newMidi.setLocalFilePath(localFilePath);
                                PersistenceHelper.saveMidiList(recordInstance, midiList);
                            } catch (IOException e) {
                                Log.e(Constant.RECORD_TAG, "Error in storing midi file locally");
                            } finally {
                                progressDialog.dismiss();
                                finish();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                        }
                    });

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                }
            });
        } else {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (!hasRecord) {
            finish();
        } else {
            Toast.makeText(this, "The recording phase is currently in progress", Toast.LENGTH_SHORT).show();
        }
    }
}
