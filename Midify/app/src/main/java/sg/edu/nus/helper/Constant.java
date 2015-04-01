package sg.edu.nus.helper;

import android.os.Environment;

public class Constant {
    // TAGS FOR LOGGER
    public static final String LOGIN_TAG = "LOGIN";
    public static final String JNI_TAG = "JNI";

    // Default Directory
    public static String BASE_FILE_DIR = Environment.getExternalStorageDirectory().toString() + "/midify";
    public static String DEFAULT_PCM_FILE_NAME = BASE_FILE_DIR + "/temp.pcm";
    public static String DEFAULT_WAV_FILE_NAME = BASE_FILE_DIR + "/temp.wav";
    public static String DEFAULT_MIDI_FILE_NAME = BASE_FILE_DIR + "/temp.midi";
}
