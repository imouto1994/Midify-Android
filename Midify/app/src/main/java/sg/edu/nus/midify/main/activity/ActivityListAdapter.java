package sg.edu.nus.midify.main.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.POJOs.ActivityPOJO;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.midify.R;
import sg.edu.nus.midify.main.user.UserViewHolder;
import sg.edu.nus.midify.midi.MidiActivity;

/**
 * Created by Youn on 12/4/15.
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityViewHolder> implements ActivityViewHolder.ViewHolderOnClick {

    private List<ActivityPOJO> activityList;
    private Context context;

    public ActivityListAdapter(Context context) {
        this.context = context;
        this.activityList = new ArrayList<>();
    }

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);

        return new ActivityViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        if (position >= activityList.size()) {
            return;
        }
        ActivityPOJO activity = activityList.get(position);
        holder.setUserId(activity.getUserId());
        holder.setUserName(activity.getUserName());
        holder.getActivityContentTextView().setText(activity.getContent());

        if (ConnectionHelper.checkNetworkConnection()) {
            String profilePictureURL = ConnectionHelper.getFacebookProfilePictureURL(activity.getUserId());
            holder.getProfilePictureView().setImageUrl(profilePictureURL);
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public void refreshActivityList(List<ActivityPOJO> newList) {
        this.activityList.clear();
        this.activityList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public void onViewHolderClick(View v, String userId, String userName, Bitmap imageBitmap) {
        Intent midiIntent = new Intent(context, MidiActivity.class);
        midiIntent.putExtra(Constant.INTENT_PARAM_USER_ID, userId);
        midiIntent.putExtra(Constant.INTENT_PARAM_USER_NAME, userName);
        midiIntent.putExtra(Constant.INTENT_PARAM_USER_PROFILE_PICTURE, imageBitmap);
        context.startActivity(midiIntent);
    }
}
