package return_to_sleep;

import java.util.Hashtable;
import java.util.Vector;

public class Table {
Vector<Page> pages= new Vector<Page>();
 String ClusteringKey;
 String TableName;
// String samplePath = "\" + TableName + "\" + "1";
 Hashtable<String,String> htblColNameType ;
 String metadata="C://Users//lenovo//Desktop//metadata.csv";
 public Table(String strTableName,String strClusteringKeyColumn,Hashtable<String,String> htblColNameType){
	 this.ClusteringKey=strClusteringKeyColumn;
	 this.TableName=strTableName;
	 this.htblColNameType=htblColNameType;
	
 }
 
}
