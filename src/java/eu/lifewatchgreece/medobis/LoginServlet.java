package eu.lifewatchgreece.medobis;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Handles login/logout functionality
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class LoginServlet extends MyHttpServlet {
    
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String servletPath = request.getServletPath();
        switch(servletPath){
                case "/logout":
                    HttpSession session=request.getSession();  
                    session.invalidate(); 
                    response.sendRedirect(this.baseUrl+"/login"); 
                    break;                
                case "/login":
                    request.setAttribute("baseUrl",baseUrl); 
                    request.getRequestDispatcher("/auth/login.jsp").forward(request, response);  
                    break;                
            }                    
                
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {         
          
        String username = request.getParameter("username");  
        String password = request.getParameter("password");  
          
        if((username.equals("admin"))&&(password.equals("admin"))){   
            HttpSession session=request.getSession();  
            session.setAttribute("username",username);  
            response.sendRedirect(this.baseUrl);
        } else{  
            request.setAttribute("baseUrl",baseUrl); 
            request.setAttribute("loginError","Login failed! Wrong username or password.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response); 
        }  
        
    }

}
