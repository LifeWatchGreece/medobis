package eu.lifewatchgreece.medobis;

import eu.lifewatchgreece.medobis.common.MyHttpServlet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import static org.apache.commons.lang3.StringUtils.stripStart;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Handles the conversion of JSON data (from featuregrid) to CSV or KML file
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class GridServlet extends MyHttpServlet {    
    
    @Override
    public void init(ServletConfig config ) throws ServletException{
        super.init(config);        
    }
    
    /**
     * Sends a CSV or KML file filled with featuregrid data received previously
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {       
     
        try {
            String urlPath = request.getPathInfo();      
            urlPath = stripStart(urlPath,"/");
            String[] urlParts = urlPath.split("/");
            String fileType = urlParts[0];
            String code = urlParts[1];
            log2db(request,"info","File type = "+fileType);

            // Get the filename from database
            String filename = code2gridfilename(code);
            if(filename.equals("")){
                log2db(request,"warning","Grid file code "+code+" was not found."); 
            } else {
                // Send the file
                String filePath = gridFilePath + File.separator + filename;
                response.setHeader("Content-Type", "text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
                PrintWriter out = response.getWriter();            
                FileInputStream fileInputStream = new FileInputStream(filePath);  

                int i;   
                while ((i=fileInputStream.read()) != -1) {  
                    out.write(i);   
                }   
                fileInputStream.close();   
                out.close();   

            }         
        } catch(Exception ex){
            log2db(request,"error",ex.getMessage());
        }                       
        
    }
    
    /**
     * Receives data from featuregrid and stores them in a CSV and a KML formated file
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        String baseUrl = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
        
        StringBuffer jb = new StringBuffer();
        String line = null;
        try {
            // Read the JSON string that has been posted
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);                 
          
            // Extract the CSV lines from JSON
            JSONArray jsonLines = new JSONArray(jb.toString());          
            ArrayList<String> stringLines = new ArrayList<String>();
          
            for (int i = 0; i < jsonLines.length(); i++) {
                stringLines.add(jsonLines.getString(i));
            }
          
            // Save data as CSV
            DateFormat df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");
            Date today = Calendar.getInstance().getTime();        
            String thisDate = df.format(today);
            String filename = "grid_data_"+thisDate+".csv";
            String file_destination = gridFilePath + File.separator + filename;
            FileWriter writer = new FileWriter(file_destination);

            for(String csvLine : stringLines){
                writer.append(csvLine);
                writer.append('\n');
            }

            writer.flush();
            writer.close();
            
            String code1 = randomString(40);
            gridfile2db(code1,filename,"csv");
            
            // Build XML for KML
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("kml");
            root.setAttribute("xmlns","http://earth.google.com/kml/2.2");
            doc.appendChild(root);
            Element docNode = doc.createElement("Document");
            root.appendChild(docNode);
            Element folder = doc.createElement("Folder");
            docNode.appendChild(folder);
            folder.appendChild(getTextNode(doc,"name","grid_data_"+thisDate));
            
            for(int i=1; i<stringLines.size(); i++){
                folder.appendChild(getPlacemark(doc,"layer name",i,stringLines.get(0),stringLines.get(i)));
            }
            
            // Save XML as file
            df = new SimpleDateFormat("MM-dd-yyyy_HH-mm-ss");       
            thisDate = df.format(today);
            filename = "grid_data_"+thisDate+".kml";
            file_destination = gridFilePath + File.separator + filename;
            
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            Result result = new StreamResult(new File(file_destination));
            Source source = new DOMSource(doc);
            transformer.transform(source, result);
            
            String code2 = randomString(40);
            gridfile2db(code2,filename,"kml");
            
            // Return the file links
            PrintWriter out = response.getWriter();

            JSONObject json = new JSONObject();
            json.put("success", true);
            json.put("status", 200);   
            json.put("csv",baseUrl+"/gridData/csv/"+code1);  
            json.put("kml",baseUrl+"/gridData/kml/"+code2);  
            out.print(json.toString());

            out.flush();
            out.close();  
         
        } catch (Exception e) {        
            log2db("error","Grid data could not be stored! (GridServlet) - "+e.getMessage()); 
            PrintWriter out = response.getWriter();

            JSONObject json = new JSONObject();
            json.put("success", false);
            json.put("status", 500);   
            out.print(json.toString());

            out.flush();
            out.close();
        }              
        
        removeOldGridFiles();
        
    }

    /**
     * Converts a CSV line to an XML <placemark> element
     * 
     * @param doc  A Document object needed to create an XML element
     * @param layerName  A layer name to be used in the <Placemark> element
     * @param index  The CSV line number to be processed
     * @param headerLine The CSV header line
     * @param dataLine  The CSV line
     * @return  A Placemark object
     */
    private Element getPlacemark(Document doc, String layerName, int index, String headerLine, String dataLine){
        
        String[] headers = headerLine.split(";");
        String[] data = dataLine.split(";");
        
        // Create <Placemark> element
        Element placemark = doc.createElement("Placemark");
        
        // Add <name>...</name>
        String id = layerName+"."+index;
        placemark.setAttribute("id",id);
        placemark.appendChild(getTextNode(doc,"name",id));
        
        // Add <description>...</description>
        String descString = "<h4>"+layerName+"</h4><ul class=\"textattributes\">";
        int latIndex = 0;
        int longIndex = 0;
        for(int i = 0; i<data.length; i++){
            descString += "  <li><strong><span class=\"atr-name\">"+headers[i]+"</span>:</strong> <span class=\"atr-value\">"+data[i]+"</span></li>";
            if(headers[i].equals("latitude"))
                latIndex = i;
            if(headers[i].equals("longitude"))
                longIndex = i;
                
        }
        descString += descString+"</ul>";
        placemark.appendChild(getTextNode(doc,"description",descString));
        
        // Add <Point>...</Point>
        Element point = doc.createElement("Point");
        point.appendChild(getTextNode(doc,"coordinates",data[latIndex]+","+data[longIndex]));
        placemark.appendChild(point);
        
        return placemark;
    }

    private Element getTextNode(Document doc, String name, String value){
        Element node = doc.createElement(name);
        node.appendChild(doc.createTextNode(value));
        return node;
    }
    
}
