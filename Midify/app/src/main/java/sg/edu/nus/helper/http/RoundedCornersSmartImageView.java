package sg.edu.nus.helper.http;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.loopj.android.image.SmartImageView;

public class RoundedCornersSmartImageView extends SmartImageView {

    private int RADIUS = 0;
    private RectF mRect;
    private Path mClip;

    public RoundedCornersSmartImageView(Context context) {
        super(context);
        init();
    }

    public RoundedCornersSmartImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundedCornersSmartImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    public void onDraw(Canvas canvas) {
        Drawable dr = getDrawable();
        if (dr != null) {
            mRect.set(dr.getBounds());
            getImageMatrix().mapRect(mRect);
            mRect.offset(getPaddingLeft(), getPaddingTop());
            mClip.reset();
            mClip.addRoundRect(mRect, RADIUS, RADIUS, Path.Direction.CCW);

            canvas.clipPath(mClip);
            super.onDraw(canvas);
        }
    }

    public void setRadius(int radius){
        this.RADIUS = radius;
    }

    private void init(){
        mRect = new RectF();
        mClip = new Path();
    }
}
