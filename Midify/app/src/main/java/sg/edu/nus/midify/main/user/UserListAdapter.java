package sg.edu.nus.midify.main.user;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sg.edu.nus.POJOs.ActivityPOJO;
import sg.edu.nus.midify.R;
import sg.edu.nus.midify.main.activity.ActivityViewHolder;

/**
 * Created by Youn on 12/4/15.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private List<ActivityPOJO> activityList;

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);

        return new UserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        ActivityPOJO activity = activityList.get(position);
        holder.getTitle().setText("Activity");
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}
