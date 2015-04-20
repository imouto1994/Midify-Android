package sg.edu.nus.POJOs;

import android.text.Html;
import android.text.Spanned;

import com.google.gson.annotations.SerializedName;

public class ActivityPOJO {

    private static final String ACTIVITY_CREATE_TYPE = "CREATE";
    private static final String ACTIVITY_FORK_TYPE = "FORK";
    private static final String ACTIVITY_PLAY_TYPE = "PLAY";

    @SerializedName("activityType")
    private String activityType;

    @SerializedName("userName")
    private String userName;

    @SerializedName("userId")
    private String userId;

    @SerializedName("targetUserId")
    private String targetUserId;

    @SerializedName("targetUserName")
    private String targetUserName;

    @SerializedName("targetFileName")
    private String targetFileName;

    @SerializedName("createdTime")
    private String createdTime;

    public Spanned getContent() {
        if (isActivityCreateType()) {
            String htmlContent = makeBold(userName) + " created a new midi " + makeBold(targetFileName);
            return Html.fromHtml(htmlContent);
        } else if (isActivityForkType()) {
            String htmlContent = makeBold(userName) + " forked midi " + makeBold(targetFileName);
            if (targetUserName != null) {
                htmlContent += " from " + makeBold(targetUserName);
            }
            return Html.fromHtml(htmlContent);
        } else { // Play Type Activity
            String htmlContent = makeBold(userName) + " played midi " + makeBold(targetFileName);
            if (targetUserName != null) {
                htmlContent += " from " + makeBold(targetUserName);
            }
            return Html.fromHtml(htmlContent);
        }
    }

    private String makeBold(String text) {
        return "<b>" + text + "</b>";
    }

    public boolean isActivityCreateType() {
        return this.activityType.equals(ACTIVITY_CREATE_TYPE);
    }

    public boolean isActivityForkType() {
        return this.activityType.equals(ACTIVITY_FORK_TYPE);
    }

    public boolean isActivityPlayType() {
        return this.activityType.equals(ACTIVITY_PLAY_TYPE);
    }

    public String getActivityType() {
        return this.activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetFileName() {
        return targetFileName;
    }

    public void setTargetFileName(String targetFileName) {
        this.targetFileName = targetFileName;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }
}
