package application_projet4_groupe12.entities;

public class Address {

    private int id;
    private String city;
    private String street;
    private String numbers; //This is a string in case of a weird mailbox structure is being used

    public Address(int id, String city, String street, String numbers){
        this.id = id;
        this.city = city;
        this.street = street;
        this.numbers = numbers;
    }

    public String stringRepresentation(){
        //TODO Representation of this Address as a unique String
        return null;
    }

    //******
    //Getter and setter methods
    //******
    public int getId(){
        return id;
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

    //TODO : Setter methods should update the DB ?

    public void setId(int id){
        this.id = id;
    }

    public void setCity(String city){
        this.city = city;
    }

    public void setStreet(String street){
        this.street = street;
    }

    public void setNumbers(String numbers){
        this.numbers = numbers;
    }
}
