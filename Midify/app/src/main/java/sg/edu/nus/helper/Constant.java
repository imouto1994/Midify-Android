package sg.edu.nus.helper;

import android.os.Environment;

public class Constant {

    // TAGS FOR LOGGER
    public static final String REQUEST_TAG = "REQUEST";
    public static final String LOGIN_TAG = "LOGIN";
    public static final String JNI_TAG = "JNI";
    public static final String MEDIA_TAG = "MEDIA";
    public static final String RECORD_TAG = "RECORD";

    // PREFRENCES NAME
    public static final String MIDI_PREFS_NAME = "MIDI_PREFS";
    public static final String MIDI_PREFS_KEY = "MIDI_PREFS_KEY";

    public static final String FACEBOOK_PREFS_NAME = "FACEBOOK_PREFS";
    public static final String FACEBOOK_PREFS_USER_ID = "FACEBOOK_USERID";
    public static final String FACEBOOK_PREFS_USER_NAME = "FACEBOOK_USER_NAME";
    public static final String FACEBOOK_PREFS_TOKEN = "FACEBOOK_TOKEN";

    // Default Directory
    public static String BASE_FILE_DIR = Environment.getExternalStorageDirectory().toString() + "/midify/";
    public static String DEFAULT_PROFILE_PICTURE_NAME = "profile";
    public static String DEFAULT_PROFILE_PICTURE_PATH = BASE_FILE_DIR + DEFAULT_PROFILE_PICTURE_NAME + ".jpg";
    public static String DEFAULT_PCM_FILE_PATH = BASE_FILE_DIR + "temp.pcm";
    public static String DEFAULT_WAV_FILE_PATH = BASE_FILE_DIR + "temp.wav";
    public static String DEFAULT_MIDI_FILE_PATH = BASE_FILE_DIR + "temp.midi";

    // Intent params
    public static String INTENT_PARAM_USER_ID = "userId";

}
