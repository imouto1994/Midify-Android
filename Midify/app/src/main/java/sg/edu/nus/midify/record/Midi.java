package sg.edu.nus.midify.record;

import com.google.gson.Gson;

/**
 * Created by Youn on 7/4/15.
 */
public class Midi {
    private static final String UNDEFINED = "undefined";

    private String fileName;
    private String filePath;
    private String fileID;
    private String userID;

    public Midi(String fileName, String filePath, String fileID, String userID) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileID = fileID;
        this.userID = userID;
    }

    public Midi(String fileName, String filePath, String userID) {
        this(fileName, filePath, UNDEFINED, userID);
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
