package sg.edu.nus.midify.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.facebook.widget.LoginButton;

import java.util.Arrays;
import java.util.List;

import sg.edu.nus.midify.R;

public class LoginFragment extends Fragment {

    public static final String LOGIN_TAG = "LOGIN";
    private static final List<String> PERMISSIONS = Arrays.asList(
        "email",
        "public_profile",
        "user_friends"
    );
    private LoginButton fbLoginButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        fbLoginButton = (LoginButton) view.findViewById(R.id.login_button);
        fbLoginButton.setReadPermissions(PERMISSIONS);

        return view;
    }
}



