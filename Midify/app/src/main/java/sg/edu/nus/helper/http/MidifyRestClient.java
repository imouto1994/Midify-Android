package sg.edu.nus.helper.http;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.POJOs.UserPOJO;
import sg.edu.nus.helper.Constant;

public class MidifyRestClient {
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String IP = "192.168.0.101";
    private static final String PORT = "9000";
    private static final String BASE_URL = "http://" + IP + ":" + PORT + "/api";

    // Singleton Instance
    private static MidifyRestClient instance;

    // Access Token
    private String accessToken;

    // Retrofit API Interface
    private MidifyService midifyApi;

    // INITIALIZE INSTANCE OF REST CLIENT
    public static void initialize() {
        if (instance == null) {
            instance = new MidifyRestClient();
        }

        // Create the interface
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                instance.checkAccessToken();
                request.addHeader("Authorization", instance.accessToken);
            }
        };

        Gson gson = new GsonBuilder().setDateFormat(DATE_FORMAT).create();
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setConverter(new GsonConverter(gson))
                .setLogLevel(RestAdapter.LogLevel.BASIC)
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        instance.midifyApi = restAdapter.create(MidifyService.class);
    }

    // RETRIVE FRIENDS ACTION
    public void getFriends(Callback<List<UserPOJO>> callback) {
        midifyApi.retrieveFriends(callback);
    }

    // RETRIEVE MIDIS FOR USER
    public void getMidisForUser(String userId, Callback<List<MidiPOJO>> callback) {
        midifyApi.retrieveMidiForUser(userId, callback);
    }

    // FORK ACTION
    public void forkMidi(MidiPOJO requestParams, Callback<MidiPOJO> callback) {
        midifyApi.forkMidi(requestParams, callback);
    }

    // CONVERT ACTION
    public void convertMidi(String filePath, String title, boolean isPublic, long duration,
                           Callback<MidiPOJO> callback) {
        File file = new File(filePath);
        if (!file.exists()) {
            Log.e(Constant.REQUEST_TAG, "File requested for converting does not exist");
        }
        TypedFile uploadFile = new TypedFile("application/octet-stream", file);
        midifyApi.convertMidi(uploadFile, title, isPublic, duration, callback);
    }

    public void downloadMidi(String fileId, Callback<Response> callback) {
        midifyApi.downloadMidi(fileId, callback);
    }

    // AUTHENTICATE ACTION
    public void authenticate(String accessToken, String userId, Callback<UserPOJO> callback) {
        UserPOJO user = UserPOJO.createUserWithoutName(accessToken, userId);
        midifyApi.authenticate(user, callback);
    }

    public static MidifyRestClient instance() {
        return instance;
    }

    /* TOKEN HELPER FUNCTIONS */
    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public void checkAccessToken() {
        if (this.accessToken == null) {
            throw new NullPointerException("The access token is null");
        }
    }
}
