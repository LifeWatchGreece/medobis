package eu.lifewatchgreece.medobis.coords;

import java.util.Random;


/**
 *
 * @license MIT
 * @author Nikos Minadakis
 */
public class UserId {
  
private static boolean sessionid = false;

public static int id = 0;

private static UserId userId = null;
    
public static UserId getUserId(){
    
    if((userId==null)&&(sessionid==false))
            {
            userId = new UserId();
            Random generator = new Random(); 
            id = generator.nextInt(1000) + 1;            
            sessionid = true;
            }
    return userId;
}

public  int getId(){
    return id;
}
   
public static void setSessionid(boolean sessionV){
    
    sessionid = sessionV;
    
}

public static void setUseridFalse(){
    
    userId = null;
    
}

}
