package sg.edu.nus.midify.main.user;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
    private String userName;
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

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ImageView getProfilePictureView() {
        return this.profilePictureView;
    }

    public TextView getProfileNameView() {
        return this.profileNameView;
    }

    @Override
    public void onClick(View v) {
        Bitmap imageBitmap = ((BitmapDrawable) profilePictureView.getDrawable()).getBitmap();
        delegate.onViewHolderClick(v, userId, userName, imageBitmap);
    }

    public static interface ViewHolderOnClick {
        public void onViewHolderClick(View v, String userId, String userName, Bitmap imageBitmap);
    }
}
