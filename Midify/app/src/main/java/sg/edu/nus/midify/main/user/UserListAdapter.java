package sg.edu.nus.midify.main.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.http.ConnectionHelper;
import sg.edu.nus.midify.R;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<UserPOJO> userList = new ArrayList<>();

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserPOJO user = userList.get(position);
        holder.getProfileNameView().setText(user.getName());

        String profilePictureURL = ConnectionHelper.getFacebookProfilePictureURL(user.getUserId());
        ConnectionHelper.downloadImage(holder.getProfilePictureView(), profilePictureURL);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void refreshUserList(List<UserPOJO> newList) {
        this.userList.clear();
        this.userList.addAll(newList);
        notifyDataSetChanged();
    }
}
