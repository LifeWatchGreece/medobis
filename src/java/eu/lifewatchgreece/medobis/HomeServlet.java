package eu.lifewatchgreece.medobis;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Functionality related to Home Page only
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class HomeServlet extends MyHttpServlet {

   
    /**
     * Displays the MedOBIS vLab Home Page
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        request.setAttribute("baseUrl",this.baseUrl);               
        request.getRequestDispatcher("/home.jsp").forward(request, response);
        
    }

}
