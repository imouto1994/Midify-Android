package sg.edu.nus.midify.main.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sg.edu.nus.helper.http.RoundedCornersSmartImageView;
import sg.edu.nus.midify.R;

/**
 * Created by Youn on 12/4/15.
 */
public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private String userId;
    private String userName;
    private RoundedCornersSmartImageView profilePictureView;
    private TextView activityContentTextView;
    private ViewHolderOnClick delegate;

    public ActivityViewHolder(View itemView, ViewHolderOnClick delegate) {
        super(itemView);
        this.delegate = delegate;

        profilePictureView = (RoundedCornersSmartImageView) itemView.findViewById(R.id.profile_picture);
        profilePictureView.setRadius(112);
        activityContentTextView = (TextView) itemView.findViewById(R.id.activity_content);
        itemView.setOnClickListener(this);
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RoundedCornersSmartImageView getProfilePictureView() {
        return this.profilePictureView;
    }

    public TextView getActivityContentTextView() {
        return this.activityContentTextView;
    }

    @Override
    public void onClick(View v) {
        delegate.onViewHolderClick(v, userId, userName);
    }

    public interface ViewHolderOnClick {
        void onViewHolderClick(View v, String userId, String userName);
    }
}
