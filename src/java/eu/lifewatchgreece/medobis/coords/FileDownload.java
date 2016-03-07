package eu.lifewatchgreece.medobis.coords;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles file downloading 
 * 
 * @license MIT
 * @author Nikos Minadakis, Alexandros Gougousis
 */
public class FileDownload extends MyHttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
              
        String baseUrl = this.getServletContext().getInitParameter("medobisBaseUrl");
        request.setAttribute("baseUrl",baseUrl);

        // ------------- START YOUR CODE ----------------------
        String DOWNLOAD_DIR = this.getServletContext().getInitParameter("coordsStoragePath");

        UserId userId = UserId.getUserId(); 

        int id = userId.getId(); 
        String DOWNLOAD_DIRECTORY=DOWNLOAD_DIR+ File.separator + Integer.toString(id);
        UserId.setSessionid(false);
        UserId.setUseridFalse();

        File file = new File(DOWNLOAD_DIRECTORY+"/"+id + ".csv");

        int length   = 0;
        ServletOutputStream outStream = response.getOutputStream();
        ServletContext context  = getServletConfig().getServletContext();
        String mimetype = context.getMimeType(DOWNLOAD_DIRECTORY+"/"+id + ".csv");
        // sets response content type
        if (mimetype == null) {
            mimetype = "application/octet-stream";
        }
        response.setContentType(mimetype);
        response.setContentLength((int)file.length());
         String fileName = (new File(DOWNLOAD_DIRECTORY+"/"+id + ".csv")).getName();
        // sets HTTP header
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        byte[] byteBuffer = new byte[4096];
        DataInputStream in = new DataInputStream(new FileInputStream(file));


        while ((in != null) && ((length = in.read(byteBuffer)) != -1))
        {
            outStream.write(byteBuffer,0,length);
        }

        in.close();
        outStream.close();
        // ------------- END YOUR CODE ----------------------                   
              
    }
    
}
