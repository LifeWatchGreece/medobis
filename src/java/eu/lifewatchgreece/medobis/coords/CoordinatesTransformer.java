package eu.lifewatchgreece.medobis.coords;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;

/**
 * Handles the coordinates coversion of a file
 * 
 * @license MIT
 * @author Nikos Minadakis, Alexandros Gougousis
 */
public class CoordinatesTransformer extends MyHttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location",baseUrl+"/coords"); 
        
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");                                

        // ------------- START YOUR CODE ----------------------

        String UPLOAD_DIR = this.getServletContext().getInitParameter("coordsStoragePath");
        String csvSeparator = this.getServletContext().getInitParameter("csvSeparator");                

        UserId userId = UserId.getUserId();
        int id = userId.getId();

        String UPLOAD_DIRECTORY = UPLOAD_DIR + File.separator + Integer.toString(id);

        File filedir = new File(UPLOAD_DIRECTORY);
        String sourcePath = "";
        for (File f : filedir.listFiles()) {
            sourcePath = UPLOAD_DIRECTORY + File.separator + f.getName();
        }

        ArrayList<String> errorList = new ArrayList<String>();
        ParsingResult results = null;

        try {
            // Parse the CSV file
            results = new Csv_Parser().csv_parse(sourcePath,csvSeparator);

            // If errors of known type were found through CSV parsing display the errors
            if(results.getErrorList().size() > 0){
                request.setAttribute("baseUrl",baseUrl);                                       
                request.setAttribute("invalidFileFormat","no");
                request.setAttribute("errorList", results.getErrorList());
                request.getRequestDispatcher("/coords/invalidFile.jsp").forward(request, response);
            } else {
            // otherwise continue with coordinate convertion

                // Open a new CSV file
                FileWriter writer = new FileWriter(UPLOAD_DIRECTORY + File.separator + id + ".csv");

                // Write the CSV column names
                writer.append("latitude");
                writer.append(csvSeparator);
                writer.append("longitude");
                writer.append('\n');                                

                ArrayList<Coordinates> coordinateList = results.getCoordinateList();

                // For each coordinates point found in the input CSV
                for (int i = 0; i < coordinateList.size(); i++) {

                    // Working with latitude
                    String latitude = coordinateList.get(i).getLatitude();

                    try {
                        int posFlag = 1;
                        if (latitude.contains("S")) {
                            posFlag = 0;
                        }
                        latitude = latitude.replaceAll("\\s", "");
                        latitude = latitude.replace("\u00b0", " ").replace("o", " ").replace("\"", " ").replace("''", " ").replace("'", " ")
                                .replace("N", "").replace("S", "").replace("n", "").replace("s", "");
                        String latitude_values[] = latitude.split(" ");
                        double lat_res;

                        if (latitude_values.length == 3) {
                            String lat_degree = latitude_values[0];
                            String lat_mins = latitude_values[1];
                            String lat_secs = latitude_values[2];

                            double lat_degree_float = Float.parseFloat(lat_degree);
                            double lat_mins_float = Float.parseFloat(lat_mins) / 60;
                            double lat_secs_float = Float.parseFloat(lat_secs) / 3600;

                            lat_res = lat_degree_float + lat_mins_float + lat_secs_float;
                            lat_res = (int)Math.round(lat_res * 10000000)/(double)10000000;                        

                        } else if (latitude_values.length == 2){
                            String lat_degree = latitude_values[0];
                            String lat_mins = latitude_values[1];
                            double lat_degree_float = Float.parseFloat(lat_degree);
                            double lat_mins_float = Float.parseFloat(lat_mins) / 60;
                            lat_res = lat_degree_float + lat_mins_float;
                            lat_res = (int)Math.round(lat_res * 10000000)/(double)10000000;                       
                        }
                        else {
                            String lat_degree = latitude_values[0];
                            double lat_degree_float = Float.parseFloat(lat_degree);
                            lat_res = lat_degree_float;
                            lat_res = (int)Math.round(lat_res * 10000000)/(double)10000000;                                               
                        }

                        String lat_result = lat_res+"";
                        if (posFlag == 0) {
                            lat_result = "-" + lat_result;
                        }
                        latitude = lat_result;
                        writer.append(latitude);
                        writer.append(csvSeparator);
                    } catch (Exception ex){
                        errorList.add("Invalid latitude in line "+(i+1)+" ( "+ex.getMessage()+" )<br>");
                    }

                    // Working with longitude
                    String longitude = coordinateList.get(i).getLongitude();

                    try {
                        int posFlag2 = 1;
                        if (longitude.contains("W")) {
                            posFlag2 = 0;
                        }

                        longitude = longitude.replaceAll("\\s", "");
                        longitude = longitude.replace("\u00b0", " ").replace("o", " ").replace("\"", " ").replace("''", " ").replace("'", " ")
                                .replace("E", "").replace("W", "").replace("e", "").replace("w", "");

                        String longitude_values[] = longitude.split(" ");
                        double long_res;

                        if (longitude_values.length == 3) {
                            String long_degree = longitude_values[0];
                            String long_mins = longitude_values[1];
                            String long_secs = longitude_values[2];

                            double long_degree_float = Float.parseFloat(long_degree);
                            double long_mins_float = Float.parseFloat(long_mins) / 60;
                            double long_secs_float = Float.parseFloat(long_secs) / 3600;
                            long_res = long_degree_float + long_mins_float + long_secs_float;

                            long_res = (int)Math.round(long_res * 10000000)/(double)10000000;

                        } else if (longitude_values.length == 2){
                            String long_degree = longitude_values[0];
                            String long_mins = longitude_values[1];

                            double long_degree_float = Float.parseFloat(long_degree);
                            double long_mins_float = Float.parseFloat(long_mins) / 60;
                            long_res = long_degree_float + long_mins_float;
                            long_res = (int)Math.round(long_res * 10000000)/(double)10000000;  
                        }                    
                        else {
                            String long_degree = longitude_values[0];

                            double long_degree_float = Float.parseFloat(long_degree);
                            long_res = long_degree_float;
                            long_res = (int)Math.round(long_res * 10000000)/(double)10000000;                        
                        }

                        String long_result = long_res+"";
                        if (posFlag2 == 0) {
                            long_result = "-" + long_result;
                        }
                        longitude = long_result;
                        writer.append(longitude);
                        writer.append('\n');
                    } catch (Exception ex){
                        errorList.add("Invalid longitude in line "+(i+1)+" ( "+ex.getMessage()+" )<br>");
                    }

                }

                writer.close();

                request.setAttribute("baseUrl",baseUrl);                                      
                request.setAttribute("invalidFileFormat","no");

                // If errors happened during the conversion display the line number
                // of the lines that caused these errors
                if(errorList.size() > 0){
                    request.setAttribute("errorList", errorList);
                    request.getRequestDispatcher("/coords/invalidFile.jsp").forward(request, response);
                } else {
                // If no errors happened, make the new CSV available for download
                    request.setAttribute("status","converted");
                    request.setAttribute("fileConverted","yes");
                    request.getRequestDispatcher("/coords/main.jsp").forward(request, response);
                }
            }


        } catch(Exception ex){
        // If unexpected error happened during the CSV parsing
        // give some direction about building a valid CSV input file
            request.setAttribute("baseUrl",baseUrl);                                    
            request.setAttribute("invalidFileFormat","yes"); 
            request.getRequestDispatcher("/coords/invalidFile.jsp").forward(request, response);                  
        }

        // ------------- END YOUR CODE ----------------------
             
    }                
    
}
