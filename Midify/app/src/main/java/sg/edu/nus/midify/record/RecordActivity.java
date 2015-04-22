package sg.edu.nus.midify.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.melnykov.fab.FloatingActionButton;

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
import sg.edu.nus.helper.AnimationHelper;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.midify.R;


public class RecordActivity extends Activity {

    private static final int INTENT_CODE_PICKING_AUDIO = 1;

    // UI Controls
    private ImageView loadingDisc;
    private FloatingActionButton recordButton;
    private FloatingActionButton pickButton;

    // RECORD
    private WavAudioRecorder audioRecorder;
    private boolean hasRecord;

    // Persistence Data
    private List<MidiPOJO> midiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        // UI Control Assignment
        loadingDisc = (ImageView) findViewById(R.id.loading_disc);

        recordButton = (FloatingActionButton) findViewById(R.id.record_button);
        updateRecordButtonIcon(true);
        recordButton.setShadow(true);

        pickButton = (FloatingActionButton) findViewById(R.id.pick_button);
        updatePickButtonIcon();
        pickButton.setShadow(true);

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

    public void onRecordButtonClick(View view) {
        if (WavAudioRecorder.State.INITIALIZING == audioRecorder.getState()) {
            audioRecorder.prepare();
            audioRecorder.start();
            hasRecord = true;
            updateRecordButtonIcon(false);
            AnimationHelper.rotateInfinitely(this, loadingDisc);
        } else if (WavAudioRecorder.State.ERROR == audioRecorder.getState()) {
            audioRecorder.release();
            audioRecorder = WavAudioRecorder.getInstance();
            audioRecorder.setOutputFile(Constant.DEFAULT_WAV_FILE_PATH);
            updateRecordButtonIcon(true);
            loadingDisc.clearAnimation();
        } else {
            audioRecorder.stop();
            audioRecorder.reset();
            updateRecordButtonIcon(true);
            loadingDisc.clearAnimation();
            fetchUserInput(null);
        }
    }

    public void onPickButtonClick(View view) {
        Intent intentPick = new Intent();
        intentPick.setType("audio/wav");
        intentPick.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentPick, INTENT_CODE_PICKING_AUDIO);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == INTENT_CODE_PICKING_AUDIO){

            if(resultCode == RESULT_OK){
                //the selected audio.
                Uri uri = data.getData();
                File targetFile = new File(uri.getPath());
                audioRecorder.setDuration(15 * 1000);
                fetchUserInput(targetFile.getAbsolutePath());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateRecordButtonIcon(boolean isPaused) {
        IconDrawable icon;
        if (isPaused) {
            icon = new IconDrawable(this, Iconify.IconValue.fa_microphone);
        } else {
            icon = new IconDrawable(this, Iconify.IconValue.fa_stop);
        }
        icon.colorRes(R.color.ColorPrimary);
        icon.sizeDp(24);
        recordButton.setImageDrawable(icon);
    }

    private void updatePickButtonIcon() {
        IconDrawable icon = new IconDrawable(this, Iconify.IconValue.fa_folder_open);
        icon.colorRes(R.color.UnforkedColorNormal);
        icon.sizeDp(24);
        pickButton.setImageDrawable(icon);
    }

    // Fetch user input for the file name and whether this file should be public or private
    public void fetchUserInput(final String filePath) {
        MaterialDialog uploadDialog = new MaterialDialog.Builder(this)
                .title(R.string.dialog_midi_name_input_title)
                .cancelable(false)
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
                        if (filePath == null) {
                            createMidiFile(Constant.DEFAULT_WAV_FILE_PATH,
                                    midiFileNameInput.getText().toString(), isPublicMidiFile);
                        } else {
                            createMidiFile(filePath,
                                    midiFileNameInput.getText().toString(), isPublicMidiFile);
                        }

                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        super.onNegative(dialog);
                        dialog.dismiss();
                        finish();
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

    private void createMidiFile(String inputPath, String fileName, boolean isPublicMidiFile) {

        // Copy WAV File into new file with updated file name
        String filePath = Constant.BASE_FILE_DIR + fileName.replaceAll("\\s+", "")
                        + System.currentTimeMillis() / 1000 + ".wav";
        File output = new File(filePath);
        File input = new File(inputPath);
        try {
            PersistenceHelper.copy(input, output);
        } catch(IOException e) {
            Log.e(Constant.RECORD_TAG, "Cannot copy the wav file");
        }

        // Store temporary MIDI file locally
        String facebookUserId = PersistenceHelper.getFacebookUserId(this);
        final MidiPOJO newMidi = MidiPOJO.
                createLocalMidiWithoutId(fileName, filePath, facebookUserId,
                        audioRecorder.getDuration(), isPublicMidiFile);
        midiList.add(newMidi);
        PersistenceHelper.saveMidiList(this, midiList);

        // Start converting
        if (ConnectionHelper.checkNetworkConnection()) {
            // Show progress dialog
            final MaterialDialog progressDialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_convert_progress_title)
                    .content(R.string.dialog_convert_progress_content_1)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            final Context recordInstance = this;
            MidifyRestClient.instance()
                    .convertMidi(filePath, fileName, isPublicMidiFile,
                            newMidi.getDuration(), new Callback<MidiPOJO>() {
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
                            String localFilePath = PersistenceHelper.saveMidiData(newMidi.getFileName()
                                    + System.currentTimeMillis() / 1000, data);
                            newMidi.setLocalFilePath(localFilePath);
                            PersistenceHelper.saveMidiList(recordInstance, midiList);
                            progressDialog.dismiss();
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                            Toast.makeText(recordInstance, "Download Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    });

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                    Toast.makeText(recordInstance, "Convert Failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
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
