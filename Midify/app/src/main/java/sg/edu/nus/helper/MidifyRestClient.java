package sg.edu.nus.helper;

import com.loopj.android.http.*;

public class MidifyRestClient {
    private static final String IP = "192.168.0.101";
    private static final String PORT = "9000";
    private static final String BASE_URL = "http://" + IP + ":" + PORT;

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, RequestParams params, JsonHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void upload(String filePath) {

    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
