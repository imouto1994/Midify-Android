package sg.edu.nus.POJOs;

/**
 * Created by Youn on 10/4/15.
 */
public class UserPOJO {
    private String token;
    private String userId;

    public UserPOJO(String token, String userId) {
        this.token = token;
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
