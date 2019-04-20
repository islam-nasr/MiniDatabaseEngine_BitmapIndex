package return_to_sleep;

import java.io.IOException;
import java.util.Hashtable;

import javax.swing.text.html.HTMLDocument.Iterator;

public class trial {
public static void main(String[] args) throws IOException, DBAppException {
	DBApp trial=new DBApp();
	String strTableName = "Student";
	Hashtable htblColNameType = new Hashtable( );
	htblColNameType.put("id", "java.lang.Integer");
	htblColNameType.put("name", "java.lang.String");
	htblColNameType.put("gpa", "java.lang.Double");
	trial.createTable( strTableName, "id", htblColNameType );
	trial.writer.close();

	trial.createBitmapIndex( strTableName, "gpa" );
	Hashtable htblColNameValue = new Hashtable( );
	htblColNameValue.put("id", new Integer( 1 ));
	htblColNameValue.put("name", new String("Ahmed Noor" ) );
	htblColNameValue.put("gpa", new Double( 0.95 ) );
	trial.insertIntoTable( strTableName , htblColNameValue );
	htblColNameValue.clear( );
	htblColNameValue.put("id", new Integer( 2 ));
	htblColNameValue.put("name", new String("Ahmed Noor" ) );
	htblColNameValue.put("gpa", new Double( 0.95 ) );
	trial.insertIntoTable( strTableName , htblColNameValue );
	htblColNameValue.clear( );
	htblColNameValue.put("id", new Integer( 3 ));
	htblColNameValue.put("name", new String("Dalia Noor" ) );
	htblColNameValue.put("gpa", new Double( 1.25 ) );
	trial.insertIntoTable( strTableName , htblColNameValue );
	htblColNameValue.clear( );
	htblColNameValue.put("id", new Integer( 4 ));
	htblColNameValue.put("name", new String("John Noor" ) );
	htblColNameValue.put("gpa", new Double( 1.5 ) );
	trial.insertIntoTable( strTableName , htblColNameValue );
	htblColNameValue.clear( );
	htblColNameValue.put("id", new Integer( 5 ));
	htblColNameValue.put("name", new String("Zaky Noor" ) );
	htblColNameValue.put("gpa", new Double( 1.5 ) );
	trial.insertIntoTable( strTableName , htblColNameValue );
	SQLTerm[] arrSQLTerms;
	long start=System.currentTimeMillis();
	arrSQLTerms = new SQLTerm[1];
	arrSQLTerms[0]=new SQLTerm();
	//arrSQLTerms[1]=new SQLTerm();
	arrSQLTerms[0]._strTableName = "Student";
	arrSQLTerms[0]._strColumnName= "gpa";
	arrSQLTerms[0]._strOperator = "=";
	arrSQLTerms[0]._objValue = "1.5";
//	arrSQLTerms[1]._strTableName = "Student";
//	arrSQLTerms[1]._strColumnName= "gpa";
//	arrSQLTerms[1]._strOperator = "=";
//	arrSQLTerms[1]._objValue = new Double( 1.5 );
	String[]strarrOperators = new String[0];
	//strarrOperators[0] = "OR"; 
	java.util.Iterator q=trial.selectFromTable(arrSQLTerms, strarrOperators);
	System.out.println(System.currentTimeMillis()-start);
//	while(q.hasNext()) {
//		System.out.println(q.next());
//	}
//	Hashtable htblColNameValue6 = new Hashtable();
//	htblColNameValue6.put("id",5);
//	 trial.deleteFromTable("Student", htblColNameValue6);




}
}
