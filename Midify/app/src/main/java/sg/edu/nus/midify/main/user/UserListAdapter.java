package sg.edu.nus.midify.main.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.helper.persistence.PersistenceHelper;
import sg.edu.nus.midify.R;
import sg.edu.nus.midify.midi.MidiActivity;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> implements UserViewHolder.ViewHolderOnClick {

    private List<UserPOJO> userList;
    private Context context;

    public UserListAdapter(Context context) {
        this.context = context;
        this.userList = new ArrayList<>();
    }

    public void addDefaultUser() {
        String userId = PersistenceHelper.getFacebookUserId(context);
        String userName = PersistenceHelper.getFacebookUserName(context);
        this.userList.add(UserPOJO.createUserWithoutToken(userId, userName));
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        if (position >= userList.size()) {
            return;
        }
        UserPOJO user = userList.get(position);
        holder.setUserId(user.getUserId());
        holder.getProfileNameView().setText(user.getName());

        if (ConnectionHelper.checkNetworkConnection(context)) {
            String profilePictureURL = ConnectionHelper.getFacebookProfilePictureURL(user.getUserId());
            ConnectionHelper.downloadImage(holder.getProfilePictureView(), profilePictureURL);
        } else if (position == 0) {
            File localProfilePicture = new File(Constant.DEFAULT_PROFILE_PICTURE_PATH);
            if (localProfilePicture.exists()) {
                holder.getProfilePictureView().setImageURI(Uri.fromFile(localProfilePicture));
            }
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void refreshUserList(List<UserPOJO> newList) {
        this.userList.clear();
        addDefaultUser();
        this.userList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public void onViewHolderClick(View v, String userId) {
        Intent midiIntent = new Intent(context, MidiActivity.class);
        midiIntent.putExtra(Constant.INTENT_PARAM_USER_ID, userId);
        context.startActivity(midiIntent);
    }
}
