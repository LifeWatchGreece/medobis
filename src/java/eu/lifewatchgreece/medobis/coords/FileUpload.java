package eu.lifewatchgreece.medobis.coords;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;
import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Handles file uploading functionality
 * 
 * @license MIT
 * @author Nikos Minadakis, Alexandros Gougousis
 */
public class FileUpload extends MyHttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");
        response.setStatus(response.SC_MOVED_TEMPORARILY);
        response.setHeader("Location",baseUrl+"/coords"); 
    }

    /**
     * Handles a file upload
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {        
       
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");
        request.setAttribute("baseUrl",baseUrl);

        // ------------- START YOUR CODE ----------------------

        String UPLOAD_DIR = this.getServletContext().getInitParameter("coordsStoragePath");
        String targetFilePath = null;    
        String fileEncoding = null;

        UserId userId = UserId.getUserId(); 
        int id = userId.getId();

        //create the working directory of the specific user ID
        String UPLOAD_DIRECTORY=UPLOAD_DIR+ File.separator + Integer.toString(id);
        File theDir = new File(UPLOAD_DIRECTORY);
        // if the directory does not exist, create it
        if (!theDir.exists()) {

            boolean result = false;
            try{
                theDir.mkdir();
                result = true;
            } catch(SecurityException se){
                log2db("error","Upload directory could not be created! (Fileupload) - "+se.getMessage()); 
                displayError(response,"Error: Upload failed!");
                return;
            }                           
        }

        if(ServletFileUpload.isMultipartContent(request)){
            try {
                List<FileItem> multiparts = new ServletFileUpload(
                        new DiskFileItemFactory()).parseRequest(request);
                for(FileItem item : multiparts){
                    if(!item.isFormField()){
                        String name = new File(item.getName()).getName();
                        name = name.replace(".csv", id+".csv");
                        targetFilePath = UPLOAD_DIRECTORY + File.separator +name;
                        item.write( new File(targetFilePath));
                    }
                }
                request.setAttribute("message", "File Uploaded Successfully");

            } catch (Exception ex) {
                log2db("error","File uploade failed! (Fileupload) - "+ex.getMessage()); 
                request.setAttribute("message", "File uploade failed!");
            }
        } else{
            log2db("error","File uploade failed! Content was not multipart. (Fileupload)"); 
            request.setAttribute("message", "File uploade failed!");
        }                

        if(targetFilePath != null){
            fileEncoding = getEncoding(targetFilePath);
        }
        request.setAttribute("fileEncoding",fileEncoding);
        request.setAttribute("status","uploaded");
        request.getRequestDispatcher("/coords/main.jsp").forward(request, response);
        // ------------- END YOUR CODE ----------------------        
           
    }
    
    /**
     * Detects file encoding 
     * 
     * @param filePath
     * @return 
     */
    public String getEncoding(String filePath) {
        String encoding;
        try {
            byte[] bytes=new byte[1024];
            InputStream inputStream=new FileInputStream(new File(filePath));
            inputStream.read(bytes);
            CharsetDetector charsetDetector=new CharsetDetector();
            charsetDetector.setText(bytes);
            CharsetMatch charsetMatch=charsetDetector.detect();
            encoding=charsetMatch.getName();
            inputStream.close();
        } catch (Exception e) {
            return "Encoding could not be detected!";
        }
        return encoding;
    }
    
}
