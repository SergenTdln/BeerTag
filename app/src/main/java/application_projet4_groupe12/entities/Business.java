package application_projet4_groupe12.entities;

/**
 * Entities of this class can represent any shop, bar or any establishment in which users can earn points.
 */
public class Business {

    private String name;
    private String imagePath; //TODO : Should we store all files in the assets folder or "stream" them from the database ? (In the later case, this variable could directly store the blob object)
}
