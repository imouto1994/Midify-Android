package sg.edu.nus.helper.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import sg.edu.nus.helper.Constant;

public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

    private ImageView imageView;
    private String imageURL;
    private DownloadImageTaskDelegate delegate;

    public DownloadImageTask(ImageView imageView, String imageURL) {
        this.imageView = imageView;
        this.imageURL = imageURL;
    }

    public DownloadImageTask(ImageView imageView, String imageURL, DownloadImageTaskDelegate delegate) {
        this.imageView = imageView;
        this.imageURL = imageURL;
        this.delegate = delegate;
    }

    protected Bitmap doInBackground(String... params) {
        Bitmap bitmapImage = null;
        try {
            InputStream in = new java.net.URL(this.imageURL).openStream();
            bitmapImage = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e(Constant.REQUEST_TAG, e.getMessage());
            e.printStackTrace();
        }
        return bitmapImage;
    }

    protected void onPostExecute(Bitmap result) {
        Log.i(Constant.REQUEST_TAG, "Finish fetching the required image from URL: " + imageURL);
        this.imageView.setImageBitmap(result);
        if (delegate != null) {
            this.delegate.handle(this.imageView);
        }
    }

    public static interface DownloadImageTaskDelegate {

        public void handle(ImageView imageView);
    }
}
