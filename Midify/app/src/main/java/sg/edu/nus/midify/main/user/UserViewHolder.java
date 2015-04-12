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
public class UserViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private String userId;
    private ImageView profilePictureView;
    private TextView profileNameView;
    private ViewHolderOnClick delegate;

    public UserViewHolder(View itemView, ViewHolderOnClick delegate) {
        super(itemView);
        this.delegate = delegate;

        profilePictureView = (CircularImageView) itemView.findViewById(R.id.profile_picture);
        profileNameView = (TextView) itemView.findViewById(R.id.profile_name);
        itemView.setOnClickListener(this);
    }

    public void setUserId(String userId) {
       this.userId = userId;
    }

    public ImageView getProfilePictureView() {
        return this.profilePictureView;
    }

    public TextView getProfileNameView() {
        return this.profileNameView;
    }

    @Override
    public void onClick(View v) {
        delegate.onViewHolderClick(v, userId);
    }

    public static interface ViewHolderOnClick {
        public void onViewHolderClick(View v, String userId);
    }
}
