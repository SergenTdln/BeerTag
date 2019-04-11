package application_projet4_groupe12.entities;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;

import application_projet4_groupe12.data.SQLHelper;

public class Address {

    private long id;
    private String country;
    private String city;
    private String street;
    private String numbers; //This is a string in case of a weird mailbox structure is being used

    public Address(long id, String country, String city, String street, String numbers){
        this.id = id;
        this.country = country;
        this.city = city;
        this.street = street;
        this.numbers = numbers;
    }

    public String stringRepresentation(){
        return street+", "+numbers+" - "+city+" "+country;
    }

    //******
    //Getter and setter methods
    //******
    public long getId(){
        return id;
    }

    public String getCountry(){
        return country;
    }

    public String getCity(){
        return city;
    }

    public String getStreet(){
        return street;
    }

    public String getNumbers(){
        return numbers;
    }

    public void setId(Context c, long id){
        this.id = id;
        this.refreshDB(c);
    }

    public void setCountry(Context c, String country){
        this.country = country;
        this.refreshDB(c);
    }

    public void setCity(Context c, String city){
        this.city = city;
        this.refreshDB(c);
    }

    public void setStreet(Context c, String street){
        this.street = street;
        this.refreshDB(c);
    }

    public void setNumbers(Context c, String numbers){
        this.numbers = numbers;
        this.refreshDB(c);
    }

    /**
     * Updates the DB with the newly inserted values
     * @param c the Context used to instantiate the database helper.
     */
    private void refreshDB(Context c){
        SQLHelper db = null;
        try {
            db = new SQLHelper(c);
            if(! db.updateAddressData(this)){
                Toast.makeText(c, "Database update did not work. Please try again", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e){
            e.printStackTrace();
            Toast.makeText(c, "Could not update the database. Please try again", Toast.LENGTH_SHORT).show();
        } finally {
            if(db!=null) {
                db.close();
            }
        }
    }
}
