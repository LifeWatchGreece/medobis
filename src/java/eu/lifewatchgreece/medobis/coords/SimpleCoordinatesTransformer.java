package eu.lifewatchgreece.medobis.coords;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles the transformation of a single coordinates pair
 * 
 * @license MIT
 * @author Nikos Minadakis, Alexandros Gougousis
 */
public class SimpleCoordinatesTransformer extends MyHttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {                     
                
        // ------------- START YOUR CODE ----------------------   
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");

        request.setAttribute("baseUrl",baseUrl);       

        try {
            String message = null;
            String latitude = request.getParameter("latitude");
            String latitudeInput = latitude;

            if(latitude.contains("E")||latitude.contains("W")) {
                message = "Wrong Entry! You cannot insert E or W in latitude field!";
            } else {

                int posFlag = 1;
                if(latitude.contains("S"))
                    posFlag = 0;

                latitude=latitude.replaceAll("\\s","");
                latitude=latitude.replace("\u00b0", " ").replace("o"," ").replace("\""," ").replace("''"," ").replace("'"," ")
                        .replace("N","").replace("S","").replace("n","").replace("s","");

                String latitude_values[] = latitude.split(" ");

                if (latitude_values.length == 3) {

                    String lat_degree = latitude_values[0];
                    String lat_mins = latitude_values[1];
                    String lat_secs = latitude_values[2];

                    double lat_degree_float = Float.parseFloat(lat_degree);
                    double lat_mins_float = Float.parseFloat(lat_mins) / 60;
                    double lat_secs_float = Float.parseFloat(lat_secs) / 3600;
                    double lat_res = lat_degree_float + lat_mins_float + lat_secs_float;
                    lat_res = (int)Math.round(lat_res * 10000000)/(double)10000000;

                    String lat_result = lat_res +"";

                    if (posFlag == 0) {
                        lat_result = "-" + lat_result;
                    }
                    latitude = lat_result;

                } else if (latitude_values.length == 2){
                    String lat_degree = latitude_values[0];
                    String lat_mins = latitude_values[1];
                    double lat_degree_float = Float.parseFloat(lat_degree);
                    double lat_mins_float = Float.parseFloat(lat_mins) / 60;
                    double lat_res = lat_degree_float + lat_mins_float;
                    lat_res = (int)Math.round(lat_res * 10000000)/(double)10000000;

                    String lat_result = lat_res +"";

                    if (posFlag == 0) {
                        lat_result = "-" + lat_result;
                    }
                    latitude = lat_result;
                } else {
                    String lat_degree = latitude_values[0];
                    double lat_degree_float = Float.parseFloat(lat_degree);
                    double lat_res = lat_degree_float;
                    lat_res = (int)Math.round(lat_res * 10000000)/(double)10000000;

                    String lat_result = lat_res +"";

                    if (posFlag == 0) {
                        lat_result = "-" + lat_result;
                    }
                    latitude = lat_result;
                }
            }

            String longitude = request.getParameter("longitude");
            String longitudeInput = longitude;

            if(longitude.contains("N")||longitude.contains("S")) {
                message = "Wrong Entry! You cannot insert N or S in longitude field!";
            } else {
                int posFlag = 1;
                if(longitude.contains("W"))
                    posFlag = 0;

                longitude=longitude.replaceAll("\\s","");                        
                longitude=longitude.replace("\u00b0", " ").replace("o"," ").replace("\""," ").replace("''"," ").replace("'"," ")
                        .replace("E","").replace("W","").replace("e","").replace("w","");

                String longitude_values[] = longitude.split(" ");

                if (longitude_values.length == 3) {
                    String long_degree = longitude_values[0];
                    String long_mins = longitude_values[1];
                    String long_secs = longitude_values[2];

                    double long_degree_float = Float.parseFloat(long_degree);
                    double long_mins_float = Float.parseFloat(long_mins) / 60;
                    double long_secs_float = Float.parseFloat(long_secs) / 3600;

                    double long_res = long_degree_float + long_mins_float + long_secs_float;
                    long_res = (int)Math.round(long_res * 10000000)/(double)10000000;

                    String long_result = long_res +"";
                    if (posFlag == 0) {
                        long_result = "-" + long_result;
                    }

                    longitude = long_result;
                } else  if (longitude_values.length == 2){

                    String long_degree = longitude_values[0];
                    String long_mins = longitude_values[1];

                    double long_degree_float = Float.parseFloat(long_degree);
                    double long_mins_float = Float.parseFloat(long_mins) / 60;
                    double long_res = long_degree_float + long_mins_float;
                    long_res = (int)Math.round(long_res * 10000000)/(double)10000000;

                    String long_result = long_res +"";
                    if (posFlag == 0) {
                        long_result = "-" + long_result;
                    }

                    longitude = long_result;

                } else {

                    String long_degree = longitude_values[0];

                    double long_degree_float = Float.parseFloat(long_degree);
                    double long_res = long_degree_float;
                    long_res = (int)Math.round(long_res * 10000000)/(double)10000000;

                    String long_result = long_res +"";
                    if (posFlag == 0) {
                        long_result = "-" + long_result;
                    }

                    longitude = long_result;

                }

                request.setAttribute("latitude", latitude);
                request.setAttribute("longitude", longitude);
                request.setAttribute("latitudeInput", latitudeInput);
                request.setAttribute("longitudeInput", longitudeInput);                                                                     

            }

            request.setAttribute("invalidCoordinates",message);   
            request.getRequestDispatcher("/coords/main.jsp").forward(request, response);

        } catch(Exception ex){
            request.setAttribute("invalidCoordinates","Coordinates could not be converted! Please check the validity of the coordinates format.");
            request.getRequestDispatcher("/coords/main.jsp").forward(request, response);                 
        }                                
           // ------------- END YOUR CODE ----------------------                     
        
    }
    
}
