package eu.lifewatchgreece.medobis.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Implements access control functionality
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
@WebFilter(
	filterName="AuthFilter",
	servletNames={"HomeServlet","ViewerServlet","KmlServlet","GridServlet","CoordsHomeServlet","SimpleCoordinatesTransformer","CoordinatesTransformer","FileDownload","FileUpload"}
)
public class AuthFilter implements Filter {
    
    private String baseUrl;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain){
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;        
        HttpSession session = httpRequest.getSession();
        String loginURI = this.baseUrl+"/login";

        // Check if the user is logged in
        boolean loggedIn = ((session != null)&&(session.getAttribute("username") != null));         
        boolean loginRequest = httpRequest.getRequestURI().equals(loginURI);

        try {
            if (loggedIn) {
                if(loginRequest){                                        
                    httpResponse.sendRedirect(this.baseUrl);                    
                } else {                    
                    chain.doFilter(httpRequest, httpResponse);
                }                
            } else { 
                if(loginRequest) {                
                    chain.doFilter(httpRequest, httpResponse);
                } else {                
                    httpResponse.sendRedirect(loginURI);                
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AuthFilter.class.getName()).log(Level.SEVERE, null, ex);
            displayMessage(httpResponse,ex.getMessage());
        } catch (ServletException ex) {
            Logger.getLogger(AuthFilter.class.getName()).log(Level.SEVERE, null, ex);
            displayMessage(httpResponse,ex.getMessage());
        }
                
    }

    @Override
    public void init(FilterConfig filterConfig){
        this.baseUrl = "";
    }

    @Override
    public void destroy(){

    }
    
    private void displayMessage(HttpServletResponse response, String errorMEssage) {
        PrintWriter out = null;      
        try {
            out = response.getWriter();
            /* TODO output your page here. You may use following sample code. */
            out.println("<strong>Error: </strong>"+errorMEssage);
        } catch (IOException ex) {
            Logger.getLogger(AuthFilter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if(out != null)
                out.close();
        }
    }

}