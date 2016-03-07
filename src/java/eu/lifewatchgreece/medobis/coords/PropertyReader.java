package eu.lifewatchgreece.medobis.coords;

import java.io.IOException;
import java.util.Properties;

/**
 * @license MIT
 * @author Yannis Marketakis (yannismarketakis 'at' gmail 'dot' com)
 */
public class PropertyReader{

    private Properties prop;
    
    public PropertyReader() throws IOException {
                        
       this.prop=new Properties();
       
    }
    
    public String getProperty(String property) {
        String retValue=this.prop.getProperty(property);
            return retValue;

   
    }
}

