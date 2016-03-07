package eu.lifewatchgreece.medobis.coords;

/**
 * A simple structure to store coordinates
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class Coordinates {
    
    private String latitude;
    private String longitude;
    
    public String getLatitude(){
        return this.latitude;
    }
    
    public String getLongitude(){
        return this.longitude;
    }
    
    public void setLatitude(String latitude){
        this.latitude = latitude;
    }
    
    public void setLongitude(String longitude){
        this.longitude = longitude;
    }
    
}
