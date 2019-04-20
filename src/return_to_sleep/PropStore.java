package return_to_sleep;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
 
public class PropStore 
{
    public static void main( String[] args )
    {
    	Properties prop = new Properties();
 
    	try {
    		//set the properties value
    		prop.setProperty("MaximumRowsCountinPage", "2");
    		prop.setProperty("BitmapSize", "3");
 
    		//save properties to project root folder
    		prop.store(new FileOutputStream("config/DBApp.properties"), null);
 
    	} catch (IOException ex) {
    		ex.printStackTrace();
        }
    }
}
