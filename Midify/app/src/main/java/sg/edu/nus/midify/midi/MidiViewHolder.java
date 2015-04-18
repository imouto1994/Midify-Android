package sg.edu.nus.midify.midi;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.melnykov.fab.FloatingActionButton;

import sg.edu.nus.midify.R;

public class MidiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, MidiItemDelegate {

    public static final int FORK_BUTTON_HIDDEN_STATE = 0;
    public static final int FORK_BUTTON_UNFORKED_STATE = 1;
    public static final int FORK_BUTTON_FORKED_STATE = 2;

    private int position;
    private int currentForkState;
    private boolean isPaused;

    // UI Controls
    private ImageView profilePictureView;
    private TextView midiNameTextView;
    private TextView durationTextView;
    private TextView editedTimeTextView;
    private FloatingActionButton playButton;
    private FloatingActionButton forkButton;
    private Context context;

    // Delegate
    private ViewHolderOnClick delegate;

    public MidiViewHolder(View itemView, ViewHolderOnClick delegate, Context context) {
        super(itemView);
        this.delegate = delegate;
        this.context = context;
        this.isPaused = true;
        this.currentForkState = FORK_BUTTON_HIDDEN_STATE;

        // Assign UI Controls
        profilePictureView = (ImageView) itemView.findViewById(R.id.profile_picture);

        midiNameTextView = (TextView) itemView.findViewById(R.id.midi_name);
        durationTextView = (TextView) itemView.findViewById(R.id.midi_duration);
        editedTimeTextView = (TextView) itemView.findViewById(R.id.midi_created_time);

        playButton = (FloatingActionButton) itemView.findViewById(R.id.play_button);
        updatePlayIcon();
        playButton.setShadow(true);
        playButton.setOnClickListener(this);

        forkButton = (FloatingActionButton) itemView.findViewById(R.id.fork_button);
        forkButton.setOnClickListener(this);

    }

    public void updateForkButton(int state) {
        currentForkState = state;
        if (state == FORK_BUTTON_HIDDEN_STATE) {
            forkButton.setVisibility(View.GONE);
        } else if (state == FORK_BUTTON_FORKED_STATE) {
            IconDrawable icon;
            icon = new IconDrawable(context, Iconify.IconValue.fa_check);
            icon.color(Color.WHITE);
            icon.sizeDp(24);
            forkButton.setImageDrawable(icon);
            forkButton.setColorNormalResId(R.color.ForkedColorNormal);
            forkButton.setColorPressedResId(R.color.ForkedColorPressed);
            forkButton.setColorRippleResId(R.color.ForkedColorRipple);
        } else if (state == FORK_BUTTON_UNFORKED_STATE) {
            IconDrawable icon;
            icon = new IconDrawable(context, Iconify.IconValue.fa_code_fork);
            icon.color(Color.WHITE);
            icon.sizeDp(24);
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

    public ImageView getProfilePictureView() {
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.play_button) {
            onPlayClick(v);
        } else if (v.getId() == R.id.fork_button) {
            onForkClick(v);
        }
    }

    private void onPlayClick(View v) {
        delegate.onPlayButtonClick(v, this.position, this);
    }

    private void onForkClick(View v) {
        delegate.onForkButtonClick(v, this.position, this.currentForkState);
    }

    public static interface ViewHolderOnClick {
        // Delegate handle when 'Play' button is tapped
        public void onPlayButtonClick(View v, int position, MidiItemDelegate itemDelegate);
        // Delegate handle when 'Fork' button is tapped
        public int onForkButtonClick(View v, int position, int forkState);
    }
}
