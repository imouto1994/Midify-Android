package sg.edu.nus.POJOs;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.Map;

import sg.edu.nus.helper.Constant;

/* MIDI POJO */
public class MidiPOJO {
    public static final String UNDEFINED = "undefined";

    @SerializedName("_id")
    private String fileId;

    @SerializedName("refId")
    private String refId;

    @SerializedName("ownerId")
    private String ownerId;

    @SerializedName("userId")
    private String userId;

    @SerializedName("title")
    private String fileName;

    @SerializedName("filePath")
    private String serverFilePath;

    private String localFilePath;

    @SerializedName("duration")
    private long duration;

    @SerializedName("wavFilePath")
    private String serverWavFilePath;

    private String localWavFilePath;

    @SerializedName("isPublic")
    private boolean isPublic;

    @SerializedName("editedTime")
    private Date editedTime;

    public static MidiPOJO createLocalMidi(String fileName, String filePath, String fileId,
                                           String userId, long duration, boolean isPublic) {
        MidiPOJO instance = new MidiPOJO();
        instance.fileName = fileName;
        instance.localWavFilePath = filePath;
        instance.fileId = fileId;
        instance.ownerId = userId;
        instance.userId = userId;
        instance.duration = duration;
        instance.isPublic = isPublic;
        instance.editedTime = new Date();

        return instance;
    }

    public static MidiPOJO createLocalMidiWithoutId(String fileName, String filePath,
                                                    String userId, long duration, boolean isPublic) {
        return createLocalMidi(fileName, filePath, UNDEFINED + System.currentTimeMillis() / 1000,
                userId, duration, isPublic);
    }

    public static MidiPOJO createBodyRequest(Map<String, String> params) {
        MidiPOJO instanceRequest = new MidiPOJO();
        for (Map.Entry<String, String> entry : params.entrySet())
        {
            if (entry.getKey().equals(Constant.REQUEST_PARAM_REF_ID)) {
                instanceRequest.setRefId(entry.getValue());
            } else if (entry.getKey().equals(Constant.REQUEST_PARAM_FILE_ID)) {
                instanceRequest.setFileId(entry.getValue());
            }
        }
        return instanceRequest;
    }


    public boolean isOnlyLocal() {
        return this.getFileId().startsWith(UNDEFINED);
    }

    public boolean isOnlyRemote() {
        return !this.getFileId().startsWith(UNDEFINED) && this.getLocalFilePath() == null;
    }

    public boolean isRef() {
        return this.getRefId() != null;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getLocalFilePath() {
        return this.localFilePath;
    }

    public String getLocalWavFilePath() {
        return this.localWavFilePath;
    }

    public String getServerFilePath() {
        return this.serverFilePath;
    }

    public String getServerWavFilePath() {
        return this.serverWavFilePath;
    }

    public String getFileId() {
        return this.fileId;
    }

    public String getRefId() {
        return this.refId;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public String getUserId() {
        return this.userId;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean getIsPublic() {
        return this.isPublic;
    }

    public Date getEditedTime() {
        return this.editedTime;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLocalFilePath(String filePath) {
        this.localFilePath = filePath;
    }

    public void setLocalWavFilePath(String filePath) {
        this.localWavFilePath = filePath;
    }

    public void setServerFilePath(String filePath) {
        this.serverFilePath = filePath;
    }

    public void setServerWavFilePath(String filePath) {
        this.serverWavFilePath = filePath;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public void setOwnerId(String userId) {
        this.ownerId = userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setEditedTime(Date time) {
        this.editedTime = time;
    }
}
