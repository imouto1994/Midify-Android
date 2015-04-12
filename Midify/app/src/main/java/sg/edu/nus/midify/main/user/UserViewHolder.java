package sg.edu.nus.midify.main.user;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pkmmte.view.CircularImageView;

import sg.edu.nus.midify.R;

/**
 * Created by Youn on 12/4/15.
 */
public class UserViewHolder extends RecyclerView.ViewHolder {

    private ImageView profilePictureView;
    private TextView profileNameView;

    public UserViewHolder(View itemView) {
        super(itemView);
        profilePictureView = (CircularImageView) itemView.findViewById(R.id.profile_picture);
        profileNameView = (TextView) itemView.findViewById(R.id.profile_name);
    }

    public ImageView getProfilePictureView() {
        return this.profilePictureView;
    }

    public TextView getProfileNameView() {
        return this.profileNameView;
    }
}
