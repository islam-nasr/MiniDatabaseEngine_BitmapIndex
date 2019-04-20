package return_to_sleep;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

public class Page implements java.io.Serializable{
Vector<Row> rows = new Vector<Row>();
int pagenumber;
Vector<ObjBit> objbits=new Vector<ObjBit>();
public Page(int pagenumber){
	this.pagenumber=pagenumber;
}
// TODO: implement read and write method to access page
public Page read(String filename){
	 Page object1 = null; 
	  
	  
     // Deserialization 
     try
     {    
         // Reading the object from a file 
	         FileInputStream file = new FileInputStream(new File(filename)); 
	         ObjectInputStream in = new ObjectInputStream(file); 
	           
         // Method for deserialization of object 
         object1 = (Page)in.readObject(); 
           
         in.close(); 
         file.close(); 
           
         System.out.println("Object has been deserialized "); 
         //System.out.println("a = " + object1.pagenumber); 
         //System.out.println("b = " + object1.b); 
     } 
       
     catch(IOException ex) 
     { 
         System.out.println("IOException is caught"); 
     } 
       
     catch(ClassNotFoundException ex) 
     { 
         System.out.println("ClassNotFoundException is caught"); 
     } 
	return object1;
	
}
public void write(String filename){
	//Page object = new Page(); 
     
      
    // Serialization  
    try
    {    
        //Saving of object in a file 
        FileOutputStream file = new FileOutputStream(new File(filename)); 
        ObjectOutputStream out = new ObjectOutputStream(file); 
          
        // Method for serialization of object 
        out.writeObject(this); 
          
        out.close(); 
        file.close(); 
          
        System.out.println("Object has been serialized"); 

    } 
      
    catch(IOException ex) 
    { 
        System.out.println("IOException is caught"); 
    } 
	
}

}
