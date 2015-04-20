package sg.edu.nus.helper.http;

import java.util.List;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import sg.edu.nus.POJOs.ActivityPOJO;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.POJOs.UserPOJO;

/* RetroFit Service */
public interface MidifyService {
    // Authenticate Server
    @POST("/users")
    void authenticate(@Body UserPOJO user, Callback<UserPOJO> callback);

    // Fork MIDI
    @POST("/midi/fork")
    void forkMidi(@Body MidiPOJO requestParams, Callback<MidiPOJO> callback);

    // Convert MIDI
    @Multipart
    @POST("/midi/convert")
    void convertMidi(@Part("wav") TypedFile wavFile, @Part("title") String title,
                    @Part("isPublic") boolean isPublic, @Part("duration") long duration,
                    Callback<MidiPOJO> callback);

    // Download MIDI
    @GET("/midi/download")
    void downloadMidi(@Query("fileId") String fileId, Callback<Response> callback);

    // Retrieve MIDIs for user
    @GET("/midi/user")
    void retrieveMidiForUser(@Query("userId") String userId, Callback<List<MidiPOJO>> callback);

    // Retrieve friends
    @GET("/facebook/friends")
    void retrieveFriends(Callback<List<UserPOJO>> callback);

    // Retrieve activities
    @GET("/activity/user")
    void retrieveActivities(Callback<List<ActivityPOJO>> callback);
}
