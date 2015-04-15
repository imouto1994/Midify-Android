package sg.edu.nus.midify.midi;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.melnykov.fab.FloatingActionButton;

import sg.edu.nus.midify.R;

public class MidiViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private String midiId;

    // UI Controls
    private ImageView profilePictureView;
    private TextView midiNameTextView;
    private FloatingActionButton playButton;
    private Context context;

    // Delegate
    private ViewHolderOnClick delegate;

    public MidiViewHolder(View itemView, ViewHolderOnClick delegate, Context context) {
        super(itemView);
        this.delegate = delegate;

        // Assign UI Controls
        profilePictureView = (ImageView) itemView.findViewById(R.id.profile_picture);

        midiNameTextView = (TextView) itemView.findViewById(R.id.midi_name);

        playButton = (FloatingActionButton) itemView.findViewById(R.id.play_button);
        IconDrawable icon = new IconDrawable(context, Iconify.IconValue.fa_play);
        icon.color(Color.WHITE);
        icon.sizeDp(24);
        playButton.setImageDrawable(icon);
        playButton.setShadow(false);
        playButton.setOnClickListener(this);
    }

    public void setMidiId(String id) {
        this.midiId = id;
    }

    public ImageView getProfilePictureView() {
        return this.profilePictureView;
    }

    public TextView getMidiNameTextView() {
        return this.midiNameTextView;
    }

    public FloatingActionButton getPlayButton() {
        return this.playButton;
    }

    @Override
    public void onClick(View v) {
        delegate.onPlayButtonClick(v, this.midiId);
    }

    public static interface ViewHolderOnClick {
        public void onPlayButtonClick(View v, String midiId);
    }
}
