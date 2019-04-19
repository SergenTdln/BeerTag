package application_projet4_groupe12.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Random;

import application_projet4_groupe12.R;

public class VerificationActivity  extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_verification);

        handleVerification();
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleVerification(){
//        ((MyActivity) getActivity()).getResult();
        //on récupére les informations de l'utilisateur apd du fragment


        // on génére le code de vérification avec un random
        int verification_code  = new Random().nextInt(6);



    }
}
