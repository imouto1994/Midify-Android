package sg.edu.nus.midify.midi;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.loopj.android.image.SmartImageView;
import com.melnykov.fab.FloatingActionButton;

import sg.edu.nus.midify.R;

public class MidiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MidiItemDelegate {

    public enum ForkState {
        HIDDEN_STATE, UNFORKED_STATE, FORK_STATE
    }

    public enum SyncState {
        HIDDEN_STATE, DOWNLOAD_STATE, UPLOAD_STATE
    }

    private int position;
    private ForkState currentForkState;
    private SyncState currentSyncState;
    private boolean isPaused;

    // UI Controls
    private SmartImageView profilePictureView;
    private TextView midiNameTextView;
    private TextView durationTextView;
    private TextView editedTimeTextView;

    private FloatingActionButton playButton;
    private FloatingActionButton forkButton;
    private FloatingActionButton syncButton;

    private Context context;

    // Delegate
    private ViewHolderOnClick delegate;

    public MidiViewHolder(View itemView, ViewHolderOnClick delegate, Context context) {
        super(itemView);
        this.delegate = delegate;
        this.context = context;
        this.isPaused = true;
        this.currentForkState = ForkState.HIDDEN_STATE;
        this.currentSyncState = SyncState.HIDDEN_STATE;

        // Assign UI Controls
        profilePictureView = (SmartImageView) itemView.findViewById(R.id.profile_picture);

        midiNameTextView = (TextView) itemView.findViewById(R.id.midi_name);
        durationTextView = (TextView) itemView.findViewById(R.id.midi_duration);
        editedTimeTextView = (TextView) itemView.findViewById(R.id.midi_created_time);

        playButton = (FloatingActionButton) itemView.findViewById(R.id.play_button);
        updatePlayIcon();
        playButton.setShadow(true);
        playButton.setOnClickListener(this);

        forkButton = (FloatingActionButton) itemView.findViewById(R.id.fork_button);
        forkButton.setShadow(false);
        forkButton.setOnClickListener(this);

        syncButton = (FloatingActionButton) itemView.findViewById(R.id.sync_button);
        syncButton.setShadow(false);
        syncButton.setOnClickListener(this);

    }

    public void updateSyncButton(SyncState state) {
        currentSyncState = state;
        if (state == SyncState.HIDDEN_STATE) {
            syncButton.setVisibility(View.GONE);
        } else if (state == SyncState.UPLOAD_STATE) {
            IconDrawable icon;
            icon = new IconDrawable(context, Iconify.IconValue.fa_upload);
            icon.color(Color.WHITE);
            icon.sizeDp(16);
            syncButton.setImageDrawable(icon);
            syncButton.setColorNormalResId(R.color.ForkedColorNormal);
            syncButton.setColorPressedResId(R.color.ForkedColorPressed);
            syncButton.setColorRippleResId(R.color.ForkedColorRipple);
        } else if (state == SyncState.DOWNLOAD_STATE) {
            IconDrawable icon;
            icon = new IconDrawable(context, Iconify.IconValue.fa_download);
            icon.color(Color.WHITE);
            icon.sizeDp(16);
            syncButton.setImageDrawable(icon);
            syncButton.setColorNormalResId(R.color.UnforkedColorNormal);
            syncButton.setColorPressedResId(R.color.UnforkedColorPressed);
            syncButton.setColorRippleResId(R.color.UnforkedColorRipple);
        }
    }

    public void updateForkButton(ForkState state) {
        currentForkState = state;
        if (state == ForkState.HIDDEN_STATE) {
            forkButton.setVisibility(View.GONE);
        } else if (state == ForkState.FORK_STATE) {
            IconDrawable icon;
            icon = new IconDrawable(context, Iconify.IconValue.fa_check);
            icon.color(Color.WHITE);
            icon.sizeDp(16);
            forkButton.setImageDrawable(icon);
            forkButton.setColorNormalResId(R.color.ForkedColorNormal);
            forkButton.setColorPressedResId(R.color.ForkedColorPressed);
            forkButton.setColorRippleResId(R.color.ForkedColorRipple);
        } else if (state == ForkState.UNFORKED_STATE) {
            IconDrawable icon;
            icon = new IconDrawable(context, Iconify.IconValue.fa_code_fork);
            icon.color(Color.WHITE);
            icon.sizeDp(16);
            forkButton.setImageDrawable(icon);
            forkButton.setColorNormalResId(R.color.UnforkedColorNormal);
            forkButton.setColorPressedResId(R.color.UnforkedColorPressed);
            forkButton.setColorRippleResId(R.color.UnforkedColorRipple);
        }
    }

    @Override
    public void updatePlayIcon() {
        IconDrawable icon;
        if (isPaused) {
            icon = new IconDrawable(context, Iconify.IconValue.fa_play);
        } else {
            icon = new IconDrawable(context, Iconify.IconValue.fa_pause);
        }
        isPaused = !isPaused;
        icon.color(Color.WHITE);
        icon.sizeDp(24);
        playButton.setImageDrawable(icon);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public SmartImageView getProfilePictureView() {
        return this.profilePictureView;
    }

    public TextView getMidiNameTextView() {
        return this.midiNameTextView;
    }

    public TextView getDurationTextView() {
        return this.durationTextView;
    }

    public TextView getEditedTimeTextView() {
        return this.editedTimeTextView;
    }

    public FloatingActionButton getPlayButton() {
        return this.playButton;
    }

    public FloatingActionButton getForkButton() {
        return this.forkButton;
    }

    public FloatingActionButton getSyncButton() {
        return this.syncButton;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_button) {
            onPlayClick(v);
        } else if (v.getId() == R.id.fork_button) {
            onForkClick(v);
        } else if (v.getId() == R.id.sync_button) {
            onSyncClick(v);
        }
    }

    private void onPlayClick(View v) {
        delegate.onPlayButtonClick(v, this.position, this);
    }

    private void onForkClick(View v) {
        delegate.onForkButtonClick(v, this.position, this.currentForkState);
    }

    private void onSyncClick(View v) {
        delegate.onSyncButtonClick(v, this.position, this.currentSyncState);
    }

    public interface ViewHolderOnClick {
        // Delegate handle when 'Play' button is tapped
        void onPlayButtonClick(View v, int position, MidiItemDelegate itemDelegate);
        // Delegate handle when 'Fork' button is tapped
        void onForkButtonClick(View v, int position, ForkState forkState);
        // Delegate handle when 'Sync' button is tapped
        void onSyncButtonClick(View v, int position, SyncState syncState);
    }
}
