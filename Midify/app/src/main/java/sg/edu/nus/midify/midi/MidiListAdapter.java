package sg.edu.nus.midify.midi;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.http.DownloadImageTask;
import sg.edu.nus.helper.http.MidifyRestClient;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.midify.R;

public class MidiListAdapter extends RecyclerView.Adapter<MidiViewHolder>
        implements MidiViewHolder.ViewHolderOnClick, DownloadImageTask.DownloadImageTaskDelegate {
    // List of MIDIs to be displayed
    private List<MidiPOJO> midiList;
    private boolean isLocalUser;
    private List<MidiPOJO> localMidis;
    private Map<String, MidiPOJO> localOwnMidis;
    private Map<String, MidiPOJO> localRefMidis;

    // Delegate for MidiListAdapter
    private MidiListDelegate delegate;

    // Media Player
    private MediaPlayer mediaPlayer;
    private String previousFilePath;
    private String previousFileId;

    public MidiListAdapter(MidiListDelegate delegate, boolean isLocalUser, List<MidiPOJO> localMidis) {
        this.delegate = delegate;
        this.midiList = new ArrayList<>();
        this.isLocalUser = isLocalUser;
        this.localMidis = localMidis;
        updateLocalMidisMap();
    }

    private void updateLocalMidisMap() {
        this.localOwnMidis = new HashMap<>();
        this.localRefMidis = new HashMap<>();
        for (MidiPOJO midi : localMidis) {
            if (midi.isRef()) {
                localRefMidis.put(midi.getRefId(), midi);
            } else {
                localOwnMidis.put(midi.getFileId(), midi);
            }
        }
    }

    @Override
    public MidiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_midi, parent, false);

        return new MidiViewHolder(itemView, this, delegate.getContext());
    }

    @Override
    public void onBindViewHolder(MidiViewHolder holder, int position) {
        if (position >= midiList.size()) {
            return;
        }

        MidiPOJO midi = midiList.get(position);
        holder.setPosition(position);
        holder.getMidiNameTextView().setText(midi.getFileName());
        holder.getDurationTextView().setText(getDurationStringFormat(midi.getDuration()));
        holder.getEditedTimeTextView().setText(getEditedTimeStringFormat(midi.getEditedTime()));
        updateForkButton(position, holder);
        updateSyncButton(position, holder);
        if (ConnectionHelper.checkNetworkConnection()) {
            String profilePictureURL = ConnectionHelper.getFacebookProfilePictureURL(midi.getOwnerId());
            holder.getProfilePictureView().setImageUrl(profilePictureURL);
        }
    }

    private void updateForkButton(int position, MidiViewHolder holder) {
        if (isLocalUser) {
            holder.updateForkButton(MidiViewHolder.ForkState.HIDDEN_STATE);
        } else {
            MidiPOJO currentMidi = midiList.get(position);
            if (localRefMidis.containsKey(currentMidi.getFileId())
                    || localRefMidis.containsKey(currentMidi.getRefId())) {
                holder.updateForkButton(MidiViewHolder.ForkState.FORK_STATE);
            } else if (localOwnMidis.containsKey(currentMidi.getRefId())) {
                holder.updateForkButton(MidiViewHolder.ForkState.HIDDEN_STATE);
            } else {
                holder.updateForkButton(MidiViewHolder.ForkState.UNFORKED_STATE);
            }
        }
    }

    private void updateSyncButton(int position, MidiViewHolder holder) {
        if (!isLocalUser) {
            holder.updateSyncButton(MidiViewHolder.SyncState.HIDDEN_STATE);
        } else {
            MidiPOJO currentMidi = midiList.get(position);
            if (currentMidi.isOnlyRemote()) {
                holder.updateSyncButton(MidiViewHolder.SyncState.DOWNLOAD_STATE);
            } else if (currentMidi.isOnlyLocal()) {
                holder.updateSyncButton(MidiViewHolder.SyncState.UPLOAD_STATE);
            } else {
                holder.updateSyncButton(MidiViewHolder.SyncState.HIDDEN_STATE);
            }
        }
    }

    private String getDurationStringFormat(long seconds) {
        long minute = seconds / 60;
        long remainderSeconds = seconds - minute * 60;

        return String.format("%02d:%02d", minute, remainderSeconds);
    }

    private String getEditedTimeStringFormat(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM, yyyy");
        return "Last edited on " + dateFormat.format(date);
    }

    @Override
    public int getItemCount() {
        return midiList.size();
    }

    public void refreshMidiList(List<MidiPOJO> newList) {
        Collections.sort(newList, new Comparator<MidiPOJO>() {
            @Override
            public int compare(MidiPOJO lhs, MidiPOJO rhs) {
                return -1 * lhs.getEditedTime().compareTo(rhs.getEditedTime());
            }
        });
        this.midiList.clear();
        this.midiList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public void onPlayButtonClick(View v, int position, MidiItemDelegate itemDelegate) {
        MidiPOJO midi = midiList.get(position);

        if (!midi.isOnlyRemote()) {
            playLocalMidi(midi.getLocalFilePath(), itemDelegate);
        } else {
            // Download the midi (This case is mainly for fork feature)
            playRemoteMidi(midi.getFileId(), itemDelegate);
        }
    }

    private void playLocalMidi(String filePath, final MidiItemDelegate itemDelegate) {
        itemDelegate.updatePlayIcon();
        File midiFile = new File(filePath);
        if (!midiFile.exists()) {
            Log.e(Constant.MEDIA_TAG, "MIDI file cannot be found for playback");
            return;
        }
        if (mediaPlayer == null || (previousFilePath != null && !previousFilePath.equals(filePath))) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            mediaPlayer = MediaPlayer.create(delegate.getContext(), Uri.fromFile(midiFile));
            previousFilePath = filePath;
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.pause();
                    mp.seekTo(0);
                }
            });
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    itemDelegate.updatePlayIcon();
                }
            });
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }
    }

    private void playRemoteMidi(final String fileId, final MidiItemDelegate itemDelegate) {
        itemDelegate.updatePlayIcon();
        if (mediaPlayer == null || (previousFileId != null && !previousFileId.equals(fileId))) {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
            previousFileId = fileId;
            final MaterialDialog progressDialog = new MaterialDialog.Builder(delegate.getContext())
                    .title(R.string.dialog_remote_progress_title)
                    .content(R.string.dialog_remote_progress_content)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
            MidifyRestClient.instance().downloadMidi(fileId, new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    byte[] data = ((TypedByteArray) response.getBody()).getBytes();
                    String localFilePath = PersistenceHelper.saveMidiData(Constant.DEFAULT_TEMP_REMOTE_MIDI_NAME, data);
                    if (localFilePath == null) {
                        return;
                    }
                    File midiFile = new File(localFilePath);
                    if (!midiFile.exists()) {
                        Log.e(Constant.MEDIA_TAG, "MIDI file cannot be found for playback");
                        return;
                    }
                    mediaPlayer = MediaPlayer.create(delegate.getContext(), Uri.fromFile(midiFile));
                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mp.pause();
                            mp.seekTo(0);
                        }
                    });
                    mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                        @Override
                        public void onSeekComplete(MediaPlayer mp) {
                            itemDelegate.updatePlayIcon();
                        }
                    });
                    progressDialog.dismiss();
                }

                @Override
                public void failure(RetrofitError error) {
                    progressDialog.dismiss();
                    Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                }
            });
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            } else {
                mediaPlayer.start();
            }
        }

    }

    @Override
    public void onForkButtonClick(View v, final int position, MidiViewHolder.ForkState forkState) {
        MidiPOJO midi = midiList.get(position);
        if (forkState == MidiViewHolder.ForkState.HIDDEN_STATE) {
            Toast.makeText(delegate.getContext(), "Cannot fork a track in hidden state", Toast.LENGTH_SHORT).show();
        } else if (forkState == MidiViewHolder.ForkState.FORK_STATE) {
            Toast.makeText(delegate.getContext(), "Cannot fork an already forked track", Toast.LENGTH_SHORT).show();
        } else {
            if (ConnectionHelper.checkNetworkConnection()) {
                final MaterialDialog progressDialog = new MaterialDialog.Builder(delegate.getContext())
                        .title(R.string.dialog_fork_progress_title)
                        .content(R.string.dialog_fork_progress_content_1)
                        .cancelable(false)
                        .progress(true, 0)
                        .show();
                Map<String, String> params = new HashMap<>();
                params.put(Constant.REQUEST_PARAM_FILE_ID, midi.getFileId());
                MidifyRestClient.instance().forkMidi(MidiPOJO.createBodyRequest(params), new Callback<MidiPOJO>() {
                    @Override
                    public void success(MidiPOJO midiPOJO, Response response) {
                        final MidiPOJO newMidi = midiPOJO;
                        localMidis.add(newMidi);
                        PersistenceHelper.saveMidiList(delegate.getContext(), localMidis);
                        progressDialog.setContent(delegate.getContext().getString(R.string.dialog_fork_progress_content_2));

                        MidifyRestClient.instance().downloadMidi(newMidi.getFileId(), new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {
                                byte[] data = ((TypedByteArray) response.getBody()).getBytes();
                                String localFilePath = PersistenceHelper.saveMidiData(newMidi.getFileName()
                                        + System.currentTimeMillis() / 1000, data);
                                newMidi.setLocalFilePath(localFilePath);
                                PersistenceHelper.saveMidiList(delegate.getContext(), localMidis);
                                updateLocalMidisMap();
                                notifyItemChanged(position);
                                progressDialog.dismiss();
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                                progressDialog.dismiss();
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                        progressDialog.dismiss();
                    }
                });
            } else {
                Toast.makeText(delegate.getContext(), "No network connection", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onSyncButtonClick(View v, final int position, MidiViewHolder.SyncState syncState) {
        final MidiPOJO midi = midiList.get(position);
        if (syncState == MidiViewHolder.SyncState.HIDDEN_STATE) {
            Toast.makeText(delegate.getContext(), "No need to sync a track in hidden state", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!ConnectionHelper.checkNetworkConnection()) {
            Toast.makeText(delegate.getContext(), "No network connection", Toast.LENGTH_SHORT).show();
            return;
        }
        if (syncState == MidiViewHolder.SyncState.UPLOAD_STATE) {
            // Show progress dialog
            final MaterialDialog progressDialog = new MaterialDialog.Builder(delegate.getContext())
                    .title(R.string.dialog_convert_progress_title)
                    .content(R.string.dialog_convert_progress_content_1)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();

            MidifyRestClient.instance().convertMidi(midi.getLocalWavFilePath(), midi.getFileName(),
                    midi.getIsPublic(), midi.getDuration(), new Callback<MidiPOJO>() {
                @Override
                public void success(MidiPOJO midiPOJO, Response response) {
                    midi.setFileId(midiPOJO.getFileId());
                    midi.setEditedTime(midiPOJO.getEditedTime());
                    midi.setServerFilePath(midiPOJO.getServerFilePath());
                    midi.setServerWavFilePath(midiPOJO.getServerWavFilePath());
                    PersistenceHelper.saveMidiList(delegate.getContext(), localMidis);
                    progressDialog.setContent(delegate.getContext().getString(R.string.dialog_convert_progress_content_2));

                    // Start downloading
                    MidifyRestClient.instance().downloadMidi(midi.getFileId(), new Callback<Response>() {
                        @Override
                        public void success(Response response, Response response2) {
                            byte[] data = ((TypedByteArray) response.getBody()).getBytes();
                            String localFilePath = PersistenceHelper.saveMidiData(midi.getFileName()
                                    + System.currentTimeMillis() / 1000, data);
                            midi.setLocalFilePath(localFilePath);
                            PersistenceHelper.saveMidiList(delegate.getContext(), localMidis);
                            progressDialog.dismiss();
                            updateLocalMidisMap();
                            notifyItemChanged(position);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                            progressDialog.dismiss();
                        }
                    });

                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                    progressDialog.dismiss();
                }
            });

        } else if (syncState == MidiViewHolder.SyncState.DOWNLOAD_STATE) {
            final MaterialDialog progressDialog = new MaterialDialog.Builder(delegate.getContext())
                    .title(R.string.dialog_sync_progress_title)
                    .content(R.string.dialog_sync_progress_content)
                    .cancelable(false)
                    .progress(true, 0)
                    .show();
            MidifyRestClient.instance().downloadMidi(midi.getFileId(), new Callback<Response>() {
                @Override
                public void success(Response response, Response response2) {
                    byte[] data = ((TypedByteArray) response.getBody()).getBytes();
                    String localFilePath = PersistenceHelper.saveMidiData(midi.getFileName()
                            + System.currentTimeMillis() / 1000, data);
                    midi.setLocalFilePath(localFilePath);
                    localMidis.add(midi);
                    PersistenceHelper.saveMidiList(delegate.getContext(), localMidis);
                    updateLocalMidisMap();
                    notifyItemChanged(position);
                    progressDialog.dismiss();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(Constant.REQUEST_TAG, "Reuqest Failed for URL: " + error.getUrl());
                    progressDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void handle(ImageView imageView) {
        imageView.setColorFilter(Color.argb(100, 0, 0, 0));
    }

    public static interface MidiListDelegate {
        public Context getContext();
    }


}
