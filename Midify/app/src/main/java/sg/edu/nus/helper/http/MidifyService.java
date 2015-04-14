package sg.edu.nus.helper.http;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import sg.edu.nus.POJOs.MidiPOJO;
import sg.edu.nus.POJOs.UserPOJO;

/* RetroFit Service */
public interface MidifyService {
    // Authenticate Server
    @POST("/users")
    void authenticate(@Body UserPOJO user, Callback<UserPOJO> callback);

    // Upload MIDI
    @Multipart
    @POST("/midi/upload")
    void uploadMidi(@Part("midi") TypedFile midiFile, @Part("title") String title,
                    @Part("isPublic") boolean isPublic, Callback<MidiPOJO> callback);

    // Retrieve MIDIs for user
    @GET("/midi/user")
    void retrieveMidiForUser(@Query("userId") String userId, Callback<List<MidiPOJO>> callback);

    // Retrieve friends
    @GET("/facebook/friends")
    void retrieveFriends(Callback<List<UserPOJO>> callback);


}
