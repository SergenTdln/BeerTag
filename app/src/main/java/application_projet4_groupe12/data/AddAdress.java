package application_projet4_groupe12.data;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class AddAdress extends AppCompatActivity {
    public boolean addAddress(String TableName, Integer identifier, String city, String street, String numbers){
    public static SQLHelper sqLHelper;
    sqLHelper = new SQLHelper(this, "database.sqlite", null, 1);
    String Query = "Select * From TableName where id = '"+identifier+"'";
            if (sqLHelper.getData(query).getCount()>0){
        Toast.makeText(getApplicationContext(),"Already Exists!", Toast.LENGTH_SHORT).show();
    }
            else{
        sqLHelper.insertData{
            identifier,
                    city,
                    street,
                    numbers
                );
            Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
        }
}
}
}
