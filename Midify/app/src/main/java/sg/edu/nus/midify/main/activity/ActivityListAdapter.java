package sg.edu.nus.midify.main.activity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import sg.edu.nus.POJOs.ActivityPOJO;
import sg.edu.nus.midify.R;

/**
 * Created by Youn on 12/4/15.
 */
public class ActivityListAdapter extends RecyclerView.Adapter<ActivityViewHolder> {

    private List<ActivityPOJO> activityList;

    @Override
    public ActivityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);

        return new ActivityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActivityViewHolder holder, int position) {
        ActivityPOJO activity = activityList.get(position);
        holder.getTitle().setText("Activity");
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }
}
