package sg.edu.nus.helper.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;

import sg.edu.nus.helper.Constant;
import sg.edu.nus.helper.persistence.PersistenceHelper;

public class SaveImageTask extends AsyncTask<String, Void, Bitmap> {

    private String fileName;
    private String imageURL;

    public SaveImageTask(String fileName, String imageURL) {
        this.fileName = fileName;
        this.imageURL = imageURL;
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
        PersistenceHelper.saveImage(this.fileName, result);
    }
}
