package sg.edu.nus.midify.main.activity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import sg.edu.nus.midify.R;

/**
 * Created by Youn on 12/4/15.
 */
public class ActivityViewHolder extends RecyclerView.ViewHolder {

    private ImageView imageView;
    private TextView textView;

    public ActivityViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.icon);
        textView = (TextView) itemView.findViewById(R.id.title);
    }

    public ImageView getImageView() {
        return this.imageView;
    }

    public TextView getTitle() {
        return this.textView;
    }
}
