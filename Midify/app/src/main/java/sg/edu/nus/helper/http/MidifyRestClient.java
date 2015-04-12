package sg.edu.nus.helper.http;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.mime.TypedFile;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.POJOs.UserPOJO;

public class MidifyRestClient {
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

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .build();
        instance.midifyApi = restAdapter.create(MidifyService.class);
    }

    // RETRIVE FRIENDS ACTION
    public void getFriends(Callback<List<UserPOJO>> callback) {
        midifyApi.retrieveFriends(callback);
    }

    // UPLOAD ACTION
    public void uploadMidi(String filePath, String title,
                           Callback<MidiPOJO> callback) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist");
        }
        TypedFile uploadFile = new TypedFile("application/octet-stream", file);
        midifyApi.uploadMidi(uploadFile, title, callback);
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
