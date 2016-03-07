package eu.lifewatchgreece.medobis.common;

import org.json.JSONArray;

/**
 * Defines a structure to carry an HTTP response with JSON array as payload between objects
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class JsonArrayResponse {
    
    private int statusCode ;
    private String statusPhrase = null;
    private JSONArray jsonResponse = null;
    private String errorMessage = null;
    
    public JsonArrayResponse(int param1, String param2, JSONArray param3,String errorMsg){
        this.statusCode = param1;
        this.statusPhrase = param2;
        this.jsonResponse = param3;
        this.errorMessage = errorMsg;
    }
    
    public int getStatusCode(){
        return this.statusCode;
    }
    
    public String getStatusPhrase(){
        return this.statusPhrase;
    }
    
    public JSONArray getJsonBody(){
        return this.jsonResponse;
    }
    
}
