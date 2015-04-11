package sg.edu.nus.POJOs;

/* MIDI POJO */
public class MidiPOJO {
    private static final String UNDEFINED = "undefined";

    private String fileName;
    private String filePath;
    private String fileID;
    private String userID;

    public static MidiPOJO createLocalMidi(String fileName, String filePath, String fileID, String userID) {
        MidiPOJO instance = new MidiPOJO();
        instance.fileName = fileName;
        instance.filePath = filePath;
        instance.fileID = fileID;
        instance.userID = userID;

        return instance;
    }

    public static MidiPOJO createLocalMidiWithoutId(String fileName, String filePath, String userID) {
        return createLocalMidi(fileName, filePath, UNDEFINED, userID);
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getFileID() {
        return this.fileID;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
