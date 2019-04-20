package return_to_sleep;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Vector;

public class trial2 {
	public static ArrayList<String> FromCSV(String name, String table) {
		 BufferedReader br = null;
		 String line = "";
		 String cvsSplitBy = ",";
		 ArrayList<String> s = new ArrayList<String>();
		 try {
		  br = new BufferedReader(new FileReader(name));

		  while ((line = br.readLine()) != null) {
		   if (line.startsWith(table)) {
		    System.out.println(line);
		    s.add(line);
		   }
		  }
		 } catch (IOException e) {
		  System.out.println("Exception");
		 }
		 return s;
		}

public static void main(String[] args) {
	//boolean dir=new File("abc").mkdir();
	  ArrayList<String> array = FromCSV("metadata.csv", "CityShop");
	  for (int i = 0; i < array.size(); i++) {
		System.out.println(array.get(i));
	}
	  Object x=1.0;
Vector<Object>a=new Vector<Object>();
a.add(1.0);
a.add(1.0);
a.add(1.0);
Vector<Object>b=DBApp.removeDuplicates(a);
for (int i = 0; i < b.size(); i++) {
	System.out.println(b.get(i));
}


Page object1;
String pathName = "CityShopindex//gpa//1.class";

File test = new File(pathName);

if (!test.exists())
	System.out.println("The file " + pathName + " Does Not exist.");
else {
	System.out.println("The file " + pathName + " exist.");
	try {
		// Reading the object from a file
		// new FileInputStream("sa");
		
		// File ayesm=new File(System.getProperty("CityShop"));
		FileInputStream file2 = new FileInputStream(new File("CityShopindex//gpa//1.class"));
		ObjectInputStream in = new ObjectInputStream(file2);

		// Method for deserialization of object

		object1 = (Page) in.readObject();
		System.out.println(object1.pagenumber);
		for (int h = 0; h < object1.objbits.size(); h++) {
			ObjBit o=object1.objbits.get(h);
			//System.out.println("Rows Size:" + object1.rows.size());
			System.out.println(object1.pagenumber);
			System.out.println(o.object);
			Vector<Bitmap> bm=o.bmvector;
			for(int j=0;j<bm.size();j++){
				Bitmap bt=bm.get(j);
				System.out.println(bt.index+":"+bt.pagenumber);
			}
			in.close();
			file2.close();

			System.out.println("Object has been deserialized ");
			// System.out.println("a = " + object1.pagenumber);
			// System.out.println("b = " + object1.b);

		}
	}

	catch (IOException ex) {
		System.out.println("IOException is caught here");
	}

	catch (ClassNotFoundException ex) {
		System.out.println("ClassNotFoundException is caught");
	}

}



}
}
