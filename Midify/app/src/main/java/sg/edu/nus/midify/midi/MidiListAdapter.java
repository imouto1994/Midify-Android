package sg.edu.nus.midify.midi;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.midify.R;

public class MidiListAdapter extends RecyclerView.Adapter<MidiViewHolder> implements MidiViewHolder.ViewHolderOnClick {
    private List<MidiPOJO> midiList;
    private MidiListDelegate delegate;

    public MidiListAdapter(MidiListDelegate delegate) {
        this.delegate = delegate;
        this.midiList = new ArrayList<>();
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
        holder.setMidiId(midi.getFileId());
        holder.getMidiNameTextView().setText(midi.getFileName());
        if (ConnectionHelper.checkNetworkConnection(delegate.getContext())) {
            String profilePictureURL = ConnectionHelper.getFacebookProfilePictureURL(midi.getOwnerId());
            ConnectionHelper.downloadImage(holder.getProfilePictureView(), profilePictureURL);
        }
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
    public void onPlayButtonClick(View v, String midiId) {
        for (MidiPOJO midi : midiList) {
            if (midi.getFileId().equals(midiId)) {
                System.out.println(midi.getLocalFilePath());
                if (!midi.isOnlyRemote()) {
                    delegate.play(midi.getLocalFilePath());
                } else {
                    // Download the midi (This case is mainly for fork feature)
                }

                break;
            }
        }
    }

    public static interface MidiListDelegate {

        public Context getContext();

        public void play(String filePath);
    }
}
