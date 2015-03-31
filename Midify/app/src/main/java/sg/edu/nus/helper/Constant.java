package sg.edu.nus.helper;

import android.os.Environment;

public class Constant {
    // TAGS FOR LOGGER
    public static final String LOGIN_TAG = "LOGIN";

    // Default Directory
    public static String BASE_FILE_DIR = Environment.getExternalStorageDirectory() + "/midify";
    public static String DEFAULT_PCM_FILE_NAME = "temp.pcm";
    public static String DEFAULT_WAV_FILE_NAME = "temp.wav";
    public static String DEFAULT_MIDI_FILE_NAME = "temp.midi";
}
