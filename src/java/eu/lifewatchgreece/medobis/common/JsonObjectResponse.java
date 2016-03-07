package eu.lifewatchgreece.medobis.common;

import org.json.JSONObject;

/**
 * Defines a structure to carry an HTTP response with JSON object as payload between objects
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class JsonObjectResponse {
    
    private int statusCode ;
    private String statusPhrase = null;
    private JSONObject jsonResponse = null;
    private String errorMessage = null;
    
    public JsonObjectResponse(int param1, String param2, JSONObject param3, String errorMsg){
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
    
    public JSONObject getJsonBody(){
        return this.jsonResponse;
    }
    
}
