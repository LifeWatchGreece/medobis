package eu.lifewatchgreece.medobis.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implements a basic HTTP client to communicate with portal's core
 * 
 * @license MIT
 * @author Alexandros Gougousis
 */
public class MyHttpClient {
    
    protected String auth_username = null;
    protected String auth_password = null;
    protected String target_domain = null;
    protected String target_url = null;
    
    public MyHttpClient(String domain, String url, String username, String password){
        this.target_domain = domain;
        this.target_url = url;
        this.auth_username = username;
        this.auth_password = password;
    }
    
    /**
     * Sends a GET request, expecting JSON object as a payload 
     * 
     * @param cookieName  Cookie name to be piggybacked in the request
     * @param cookieValue  Cookie value to be piggybacked in the request
     * @param clientHeaders  Headers to be used in the request
     * @return
     * @throws IOException
     * @throws Exception 
     */
    public JsonObjectResponse getRequestHttpsBasicAuthJson(String cookieName, String cookieValue,HashMap<String,String> clientHeaders) throws IOException, Exception{
        
        CloseableHttpResponse responseObj = null;
        JSONObject jsonObj = null;
        JsonObjectResponse jsonResponse = null;
            
        
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(auth_username,auth_password));

        final HttpHost targetHost = new HttpHost(target_domain, 443, "https");

        final AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setAuthenticationEnabled(true);

        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build());
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setDefaultRequestConfig(requestBuilder.build())
                    .setDefaultCredentialsProvider(credsProvider).build();

            HttpGet httpGet = new HttpGet(target_url);
            if(cookieValue != null){
                httpGet.addHeader("Cookie",cookieName+"="+cookieValue);               
            }          
            
            if(clientHeaders != null){
                if(clientHeaders.get("User-Agent") != null){
                    httpGet.addHeader("User-Agent",clientHeaders.get("User-Agent"));
                }     
                if(clientHeaders.get("Accept-Language") != null){
                    httpGet.addHeader("Accept-Language",clientHeaders.get("Accept-Language"));
                }  
                if(clientHeaders.get("X-Requested-With") != null){
                    httpGet.addHeader("X-Requested-With",clientHeaders.get("X-Requested-With"));
                }  
                if(clientHeaders.get("X-Original-Ip") != null){
                    httpGet.addHeader("X-Original-Ip",clientHeaders.get("X-Original-Ip"));
                }  
            }
            
            responseObj = httpclient.execute(httpGet,context);
            
            // Analyze response
            int statusCode = responseObj.getStatusLine().getStatusCode();
            String statusPhrase = responseObj.getStatusLine().getReasonPhrase();
           
            HttpEntity entity = responseObj.getEntity();
            if (entity != null) {
                String jsonString = EntityUtils.toString(entity);                
                jsonObj = new JSONObject(jsonString);
            } 
            
            EntityUtils.consume(entity);
            
            jsonResponse = new JsonObjectResponse(statusCode,statusPhrase,jsonObj,null);
            return jsonResponse;
            
        } catch(Exception ex){           
            if(responseObj != null)
                responseObj.close();
            jsonResponse = new JsonObjectResponse(-1,null,null,ex.getMessage());
            return jsonResponse;
        } 
    }
    
    /**
     * Sends a GET request, expecting JSON array as a payload 
     * 
     * @param cookieName  Cookie name to be piggybacked in the request
     * @param cookieValue  Cookie value to be piggybacked in the request
     * @param clientHeaders  Headers to be used in the request
     * @return
     * @throws IOException
     * @throws Exception 
     */
    public JsonArrayResponse postRequestHttpsBasicAuthJson(String cookieName, String cookieValue, Map<String,String> postFields) throws IOException, Exception{
        
        CloseableHttpResponse responseObj = null;
        JSONArray jsonArray = null;
        JsonArrayResponse jsonResponse = null;
            
        final CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY,new UsernamePasswordCredentials(auth_username,auth_password));

        final HttpHost targetHost = new HttpHost(target_domain, 443, "https");

        final AuthCache authCache = new BasicAuthCache();
        authCache.put(targetHost, new BasicScheme());

        // Add AuthCache to the execution context
        final HttpClientContext context = HttpClientContext.create();
        context.setCredentialsProvider(credsProvider);
        context.setAuthCache(authCache);

        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setAuthenticationEnabled(true);

        try {
            SSLContextBuilder builder = new SSLContextBuilder();
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                        builder.build());
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .setDefaultRequestConfig(requestBuilder.build())
                    .setDefaultCredentialsProvider(credsProvider).build();

            HttpPost httpPost = new HttpPost(target_url);
            if(cookieValue != null){
                httpPost.addHeader("Cookie",cookieName+"="+cookieValue);
            }            
            
            // Request parameters and other properties.
            int countFields = postFields.size();
            List<NameValuePair> params = new ArrayList<NameValuePair>(countFields);
            for (Map.Entry<String,String> field : postFields.entrySet()) {
                params.add(new BasicNameValuePair(field.getKey(),field.getValue()));
            }
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            
            responseObj = httpclient.execute(httpPost,context);
            
            // Analyze response
            int statusCode = responseObj.getStatusLine().getStatusCode();
            String statusPhrase = responseObj.getStatusLine().getReasonPhrase();
            
            HttpEntity entity = responseObj.getEntity();
            if (entity != null) {
                String jsonString = EntityUtils.toString(entity);                
                jsonArray = new JSONArray(jsonString);
            } 
            
            EntityUtils.consume(entity);
            
            jsonResponse = new JsonArrayResponse(statusCode,statusPhrase,jsonArray,null);
            return jsonResponse;
            
        } catch(Exception ex){            
            responseObj.close();
            jsonResponse = new JsonArrayResponse(-1,null,null,ex.getMessage());
            return jsonResponse;
        } 
    }        
    
}
