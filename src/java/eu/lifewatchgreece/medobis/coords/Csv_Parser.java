package eu.lifewatchgreece.medobis.coords;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Functionality related to parsing a file with coordinates
 * 
 * @license MIT
 * @author Nikos Minadakis, Alexandros Gougousis
 */
public class Csv_Parser {

    /**
     * Parses a CSV file with coordinates
     * 
     * @param fileToRead  The file name of the file to be parsed
     * @param csvSeparator  The value separator used by the file
     * @return  An ArraList with coordinates found and a list of error messages occured in parsing procedure
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public ParsingResult csv_parse(String fileToRead,String csvSeparator) throws FileNotFoundException, IOException {

        BufferedReader fileReader = null;
        String line = "";

        ParsingResult result = new ParsingResult();

        fileReader = new BufferedReader(new FileReader(fileToRead));
        int count = 0;

        while ((line = fileReader.readLine()) != null) {

            if (count == 0) {
                count++;
                continue;
            }

            String[] tokens = line.split(csvSeparator);
            if(tokens.length < 2){
                result.addError("Invalid latitude/longitude values found in line "+(count+1));
            } else {
                Coordinates coords = new Coordinates();
                coords.setLatitude(tokens[0]);
                coords.setLongitude(tokens[1]);
                result.addCoordinate(coords);
            }  
            count++;
        }
        return result;
    }
}

