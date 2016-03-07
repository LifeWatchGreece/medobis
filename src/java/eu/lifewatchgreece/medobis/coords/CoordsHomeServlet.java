package eu.lifewatchgreece.medobis.coords;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Functionality related to Home Page of "Coordinates Convertor tool"
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class CoordsHomeServlet extends MyHttpServlet {
   
    /**
     * Displays the Home Page
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
        
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");
        request.setAttribute("baseUrl",baseUrl);                               

        // ------------- START YOUR CODE ----------------------
        request.getRequestDispatcher("/coords/main.jsp").forward(request, response);

        // ------------- END YOUR CODE ----------------------
               
    }
    
}
