package eu.lifewatchgreece.medobis.coords;

import java.util.ArrayList;

/**
 * A structure to store the parsing results of a file with coordinates
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class ParsingResult {
    
    private ArrayList<Coordinates> coordinateList = new ArrayList<Coordinates>();
    private ArrayList<String> errorList = new ArrayList<String>();
    
    public ArrayList<Coordinates> getCoordinateList(){
        return this.coordinateList;
    }
    
    public ArrayList<String> getErrorList(){
        return this.errorList;
    }
    
    public void addCoordinate(Coordinates point){
        this.coordinateList.add(point);
    }
    
    public void addError(String error){
        this.errorList.add(error);
    }
    
}
