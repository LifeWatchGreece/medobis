package eu.lifewatchgreece.medobis.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Implements basic functionality that is useful to a number of servlets 
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class MyHttpServlet extends HttpServlet {
    
    private String mysqlUser;
    private String mysqlPwd;
    private String mysqlUrl;
    protected String gridFilePath;
    protected Connection conn;
    protected String baseUrl;
    
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnd = new Random();
    
    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        mysqlUser = config.getServletContext().getInitParameter("mysqlUser"); 
        mysqlPwd = config.getServletContext().getInitParameter("mysqlPwd"); 
        mysqlUrl = config.getServletContext().getInitParameter("mysqlUrl");     
        gridFilePath = config.getServletContext().getInitParameter("gridFilePath"); 
        baseUrl = config.getServletContext().getInitParameter("medobisBaseUrl");
    }    
    
    /**
     * Creates a random alphanumerical string of given length
     * 
     * @param len  The length of the string
     * @return  The random string
     * @throws Exception 
     */
    public String randomString( int len ) throws Exception {

        StringBuilder sb = new StringBuilder( len );
        Statement stmt = null;        

        if(this.conn == null ){
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            this.conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPwd);
        } 

        stmt = this.conn.createStatement();

        int rowCount = 0;
        do {                
            sb.setLength(0);
            for( int i = 0; i < len; i++ ) 
                sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
            String query = "SELECT * FROM gridfiles WHERE code = '"+sb.toString()+"'";
            ResultSet rs = stmt.executeQuery(query);       
            rs.last();
            rowCount = rs.getRow();

        } while(rowCount > 0);
        
        return sb.toString();

    }
    
    /**
     * Stores in the database a log item
     * 
     * @param request
     * @param category  The log type
     * @param message  The log message
     */
    protected void log2db(HttpServletRequest request, String category, String message) {

        Statement stmt = null;
        logText("Entered!");
        try {
            if(this.conn == null ){
                logText("Building new connection...");
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                this.conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPwd);
                logText("Connection established!");
            } 
            
            HttpSession session = request.getSession(false);
            //String user_email = (String) session.getAttribute("user_email");
            String user_email = "dummy value";
            logText("Session Ok!");
            
            //Execute a query
            stmt = this.conn.createStatement();
            stmt.executeUpdate("INSERT INTO logs (user_email,category,message) VALUES ('"+user_email+"','"+category+"','"+message+"')");
            logText("INSERTed!");       
    
        } catch (Exception ex){
            logText(ex.getMessage());
        } finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
                logText("Couldn't close stmt!");
            }// nothing we can do
            
        }
    }
    
    /**
     * Stores in the database a log item without category
     * 
     * @param category
     * @param message 
     */
    protected void log2db(String category, String message) {

        Statement stmt = null;
        try {
            if(this.conn == null ){
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                this.conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPwd);
            } 
                        
            String user_email = "system";
            
            //Execute a query
            stmt = this.conn.createStatement();
            stmt.executeUpdate("INSERT INTO logs (user_email,category,message) VALUES ('"+user_email+"','"+category+"','"+message+"')");      
    
        } catch (Exception ex){
            logText(ex.getMessage());
        } finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
                logText("Couldn't close stmt!");
            }// nothing we can do
            
        }
    }
    
    /**
     * Deletes from database and file system, grid files older than 5 minutes
     */
    protected void removeOldGridFiles(){
        
        Statement stmt = null;        
        
        try {
            if(this.conn == null ){
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                this.conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPwd);
            }             
            
            // Find grid files older than 5 minutes
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, -5);
            Date dateLimit = calendar.getTime();        
            String dateLimitString = df.format(dateLimit);
            
            stmt = this.conn.createStatement();
            String query = "SELECT * FROM gridfiles WHERE created < '"+dateLimitString+"'";
            ResultSet rs = stmt.executeQuery(query);
            
            // Delete old grid files
            while (rs.next()) {   
                // Get file path
                String filename = rs.getString("filename");
                String file_destination = gridFilePath + File.separator + filename;
                
                try {
                    // Delete file
                    File file = new File(file_destination); 
                    if(file.delete()){
                        // Do nothing
                    } else {
                        log2db("error","File "+file_destination+" could not be deleted!");
                    }  

                    // Delete file db entry
                    Statement stmt2 = null;
                    stmt2 = this.conn.createStatement();
                    query = "DELETE FROM gridfiles WHERE id = "+rs.getInt("id");
                    stmt2.executeUpdate(query);
                    stmt2.close();
                } catch(Exception ex) {
                    log2db("error","Old file "+file_destination+" could not be deleted"); 
                }                                
                
            }
            
    
        } catch (Exception ex){
            logText(ex.getMessage());           
        } finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
                logText("Couldn't close stmt! "+se2.getMessage());
            }// nothing we can do           
        }        
        
    }
    
    /**
     * Translates the unique string/code to the name of the respective file with grid data
     * 
     * @param code  The unique string/code
     * @return  The file name
     */
    protected String code2gridfilename(String code) {

        Statement stmt = null;
        String filename = "";
        
        try {
            if(this.conn == null ){
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                this.conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPwd);
            }             
            
            //Execute a query
            stmt = this.conn.createStatement();
            String query = "SELECT * FROM gridfiles WHERE code = '"+code+"'";
            ResultSet rs = stmt.executeQuery(query);       
            rs.last();
            int rowCount = rs.getRow();
            if(rowCount != 1){
                filename = "";
                log2db("warning","Grid file code exists more than once!"); 
            } else {
                rs.first();
                filename = rs.getString("filename");
            }
    
        } catch (Exception ex){
            logText(ex.getMessage());           
            filename = "";
        } finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
                logText("Couldn't close stmt! "+se2.getMessage());
            }// nothing we can do
            return filename;
        }        
    }
    
    /**
     * Logs to database a newly created file with grid data
     * 
     * @param code  A unique string to be used as part of URL in order to access the file
     * @param filename  The name of the file
     * @param filetype  The type of the file (CSV or KML)
     */
    protected void gridfile2db(String code,String filename,String filetype) {

        Statement stmt = null;
        try {
            if(this.conn == null ){
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                this.conn = DriverManager.getConnection(mysqlUrl, mysqlUser, mysqlPwd);
            }             
            
            //Execute a query
            stmt = this.conn.createStatement();
            stmt.executeUpdate("INSERT INTO gridfiles (code,filename,filetype) VALUES ('"+code+"','"+filename+"','"+filetype+"')");      
    
        } catch (Exception ex){
            logText(ex.getMessage());
        } finally{
            //finally block used to close resources
            try{
               if(stmt!=null)
                  stmt.close();
            }catch(SQLException se2){
                logText("Couldn't close stmt! "+se2.getMessage());
            }// nothing we can do
            
        }
                
    }
    
    /**
     * Stores a log item in a log file
     * 
     * @param message  The message to be logged
     */
    protected void logText(String message){
        
        String logFilePath = this.getServletContext().getInitParameter("logFilePath");
        PrintWriter out = null;
        
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date date = new Date();
            
            out = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath, true)));
            out.println(dateFormat.format(date)+message);
            out.close();
        } catch (Exception ex) {
            Logger.getLogger(MyHttpServlet.class.getName()).log(Level.SEVERE, null, ex);
        }                   
    }

    /**
     * Displays an HTML page that includes a specific error message
     * 
     * @param response
     * @param errorMEssage
     * @throws ServletException
     * @throws IOException 
     */
    protected void displayError(HttpServletResponse response, String errorMEssage) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Unexpected Error!</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<strong>Error: </strong>"+errorMEssage);
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }            

}
