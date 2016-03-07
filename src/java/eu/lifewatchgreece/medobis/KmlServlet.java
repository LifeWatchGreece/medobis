package eu.lifewatchgreece.medobis;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import static org.apache.commons.lang3.StringUtils.stripStart;
import org.json.JSONObject;

/**
 * Functionality related to displaying a KML file on the MedOBIS Viewer
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
@MultipartConfig(location="/tmp", fileSizeThreshold=1024*1024*30,
    maxFileSize=1024*1024*30, maxRequestSize=1024*1024*30)
public class KmlServlet extends MyHttpServlet {

    private String kmlPath;
    
    @Override
    public void init(ServletConfig config ) throws ServletException{
        super.init(config);
        kmlPath = config.getServletContext().getInitParameter("kmlPath"); 
    }
    
    /**
     * Serves the (XML) content retrieval of a KML file that has been uploaded
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {       
        
        // The name of the uploaded KML file should be the trailing part of the URL
        String filename = request.getPathInfo();      
        filename = stripStart(filename,"/");
        String file_destination = kmlPath + File.separator + filename;
        
        // Check if such a KML exists
        
        try{                        
            
            PrintWriter out = response.getWriter();
            
            // Read the contents of the KML file (XML)
            Reader fileReader = new FileReader(file_destination); 
            BufferedReader bufReader = new BufferedReader(fileReader); 
            StringBuilder sb = new StringBuilder(); 
            String line = bufReader.readLine(); 
            while( line != null){ 
                sb.append(line.trim()); 
                line = bufReader.readLine(); 
            } 
            String xml2String = sb.toString();                        
            
            // Remove new line and tab characters
            xml2String = xml2String.replaceAll("(\\r|\\n)", "");
            xml2String = xml2String.replaceAll("\t", "");  
                                                 
            // Return the XML
            response.setContentType("text/xml");    
            response.setStatus(HttpServletResponse.SC_OK);      

            out.print(xml2String);              
            out.flush();
            out.close();
            
            // Delete user's KML file
            fileReader.close();
            File file = new File(file_destination);        	
            if(file.delete()){
                // Do nothing
            } else {
                log2db(request,"error","File "+file_destination+" could not be deleted!");
            }           
            
        } catch(Exception e){ 
            log2db("error","KML content could not be retrieved! (KmlServlet) - "+e.getMessage()); 
            response.setContentType("text/html");    
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);     
        } 
    }

    /**
     * Serves the uploading of a KML file by the user (and the display of its contents)
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {                
        
        try {
            
            if (ServletFileUpload.isMultipartContent(request)) {
                List<FileItem> fileCollection = new ServletFileUpload(new DiskFileItemFactory()).parseRequest(request);
                for (FileItem item : fileCollection) {
                    if (!item.isFormField()) {
                        
                        // Get basic information about the uploaded file
                        String fieldName = item.getFieldName();
                        String fileName = item.getName();
                        String contentType = item.getContentType();
                        boolean isInMemory = item.isInMemory();
                        long sizeInBytes = item.getSize();

                        // Save the file
                        String file_destination = kmlPath + File.separator + fileName;
                        item.write(new File(file_destination));                                                
                        
                        // Send an acknowledgement response
                        response.setContentType("text/html");    
                        response.setStatus(HttpServletResponse.SC_OK);
                        PrintWriter out = response.getWriter();
                        JSONObject json = new JSONObject();
                        json.put("success", true);
                        json.put("status", 200);                        
                        out.print(json.toString());
                        out.flush();
                        out.close();
                        
                    } else {
                        response.setContentType("text/html");  
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        PrintWriter out = response.getWriter();
                        JSONObject json = new JSONObject();
                        json.put("success", false);
                        json.put("status", 400);                        
                        out.print(json.toString());
                        out.flush();
                        out.close();
                    }
                }               
            } else {
                response.setContentType("text/html");    
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                JSONObject json = new JSONObject();
                json.put("status", 400);
                json.put("success", false);
                out.print(json.toString());
                out.flush();
                out.close();
            }        
            
        } catch(Exception ex) {
            log2db("error","KML file could not be uploaded! (KmlServlet) - "+ex.getMessage()); 
            response.setContentType("text/html");   
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            PrintWriter out = response.getWriter();
            JSONObject json = new JSONObject();
            json.put("success", false);
            json.put("status", 400);            
            out.print(json.toString());
            out.flush();
            out.close();
        }                           
        
    }


}
