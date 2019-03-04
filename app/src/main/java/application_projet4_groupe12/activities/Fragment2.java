package application_projet4_groupe12.activities;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import application_projet4_groupe12.R;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import application_projet4_groupe12.data.SQLHelper;
import application_projet4_groupe12.entities.User;
import application_projet4_groupe12.exceptions.WrongDateFormatException;
import application_projet4_groupe12.exceptions.WrongEmailFormatException;

public class Fragment2 extends Fragment {

    private Button fragment2_sign_up;
    private SQLHelper db;
    private User user;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment2_layout, container, false);
        fragment2_sign_up= (Button) view.findViewById(R.id.fragment2_sign_up);

        fragment2_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    db = new SQLHelper(getActivity());

                    EditText username = (EditText)getView().findViewById(R.id.editText3);
                    EditText password = (EditText)getView().findViewById(R.id.editText4);
                    EditText confirmPassword = (EditText)getView().findViewById(R.id.editText5);

                    if (db.doesUsernameExist(username.getText().toString()))  {
                        Toast.makeText(getActivity(),  R.string.existing_email, Toast.LENGTH_SHORT).show();
                    }
                    else {
                        if (password.getText().toString().equals(confirmPassword.getText().toString())) {

                            int id = db.getFreeIDUser();
                            Date date = Calendar.getInstance().getTime();
                            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String today = formatter.format(date);

                            user = new User(id, id, username.getText().toString(), today,  "albert", "le chat");

                            db.createUser(user);
                            Toast.makeText(getActivity(), R.string.account_done, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), R.string.password_no_match, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (WrongEmailFormatException e) {
                    e.printStackTrace();
                } catch (WrongDateFormatException e) {
                    e.printStackTrace();
                } finally {
                    db.close();
                }
            }
        });

        return view;
    }
}
