package application_projet4_groupe12.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import application_projet4_groupe12.R;

import java.io.IOException;

import application_projet4_groupe12.activities.MainActivity;
import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;

public class Fragment1 extends Fragment {

    private Button fragment1_sign_in;
    private SQLHelper db;
    private User user;
    private  CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment1_layout, container, false);
        fragment1_sign_in = (Button) view.findViewById(R.id.fragment1_sign_in);

        fragment1_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    db = new SQLHelper(getActivity());

                    EditText username = (EditText)getView().findViewById(R.id.editText1);
                    EditText password = (EditText)getView().findViewById(R.id.editText2);

                    if (db.doesUsernameExist(username.getText().toString())) {
                        Toast.makeText(getActivity(),  R.string.valid_email, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getActivity(),  R.string.invalid_email, Toast.LENGTH_SHORT).show();
                    }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        db.close();
                    }
            }
        });

        /*callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });*/

        return view;
    }
}
