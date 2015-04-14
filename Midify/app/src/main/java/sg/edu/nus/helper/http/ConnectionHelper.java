package sg.edu.nus.helper.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import java.io.InputStream;

import sg.edu.nus.helper.Constant;

public class ConnectionHelper {
    public static boolean checkNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static void downloadImage(ImageView imageView, String imageURL) {
        DownloadImageTask task = new DownloadImageTask(imageView, imageURL);
        task.execute();
    }

    public static void saveImage(String fileName, String imageURL) {
        SaveImageTask task = new SaveImageTask(fileName, imageURL);
        task.execute();
    }

    public static String getFacebookProfilePictureURL(String userId) {
        return "https://graph.facebook.com/" + userId + "/picture?width=9999";
    }

}
