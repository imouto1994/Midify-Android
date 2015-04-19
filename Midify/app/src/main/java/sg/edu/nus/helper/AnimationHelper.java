package sg.edu.nus.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;

import sg.edu.nus.midify.R;

/**
 * Created by Youn on 16/4/15
 */
public class AnimationHelper {

    public static void rotateInfinitely(Context context, View v) {
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        rotation.setFillEnabled(true);
        rotation.setFillAfter(true);
        v.startAnimation(rotation);
    }
}
