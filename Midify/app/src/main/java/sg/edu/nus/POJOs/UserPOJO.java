package sg.edu.nus.POJOs;

/**
 * Created by Youn on 10/4/15.
 */
public class UserPOJO {
    private String token;
    private String userId;
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

    public void setToken(String token) {
        this.token = token;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
