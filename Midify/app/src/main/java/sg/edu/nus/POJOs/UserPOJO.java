package sg.edu.nus.POJOs;

import com.google.gson.annotations.SerializedName;

public class UserPOJO {
    @SerializedName("_id")
    private String token;
    @SerializedName("userId")
    private String userId;
    @SerializedName("name")
    private String name;

    public static UserPOJO createUserWithoutName(String token, String userId) {
        UserPOJO user = new UserPOJO();
        user.token = token;
        user.userId = userId;

        return user;
    }

    public static UserPOJO createUserWithoutToken(String userId, String name) {
        UserPOJO user = new UserPOJO();
        user.userId = userId;
        user.name = name;

        return user;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return this.name;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }
}
