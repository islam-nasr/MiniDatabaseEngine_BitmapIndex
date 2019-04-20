package return_to_sleep;
/////NOTE: 7ot

//el code 
//el fe 
//ay 7aga 
//fe el 
//creating el
//table 3lashan
//nsena n7otaha!!!!
///
///
///
//
//
//
//
//
//
//

import java.awt.Window.Type;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.Array;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

public class DBApp {
	File file;
	FileWriter writer;

	public DBApp() throws IOException {
		file = new File("data//metadata.csv");
		writer = new FileWriter(file);
		writer.append("Table Name, Column Name, Column Type, Key, Indexed");
		writer.append('\n');
		writer.flush();
	}

	public void init() throws IOException {

	}

	public void createTable(String strTableName, String strClusteringKeyColumn,
			Hashtable<String, String> htblColNameType) throws DBAppException, IOException {
		Table t1 = new Table(strTableName, strClusteringKeyColumn, htblColNameType);
		File table = new File(strTableName);
		File table2 = new File(strTableName + "index");
		if (table.mkdir()) {
			System.out.println("created successfully");
		} else {
			System.out.println("not created");
		}
		if (table2.mkdir()) {
			System.out.println("index created successfully");
		} else {
			System.out.println("index not created");
		}
		Set<String> x = htblColNameType.keySet();
		Object[] z = x.toArray();
		boolean b = false;
		for (int i = htblColNameType.size() - 1; i >= 0; i--) {
			String q = (String) z[i];
			// System.out.println(htblColNameType.get(q));
			if (q.equalsIgnoreCase(strClusteringKeyColumn)) {
				b = true;
			} else
				b = false;
			System.out.println(strTableName + "," + q + "," + htblColNameType.get(q) + "," + b + "," + "False");
			writer.append(strTableName + "," + q + "," + htblColNameType.get(q) + "," + b + "," + "False");
			writer.append('\n');
		}
		writer.append(strTableName + "," + "TouchDate" + "," + "java.util.Date" + ","+ "False" + "," + "False");
		writer.append('\n');
		writer.flush();
		writer.close();
		/*
		 * Set<String> x= htblColNameType.keySet(); for(String d: x){
		 * System.out.println(htblColNameType.get(d));
		 */
	}

	public void createBitmapIndex(String strTableName, String strColName) throws DBAppException, IOException {
		ArrayList<String> array = FromCSV(this.file.getPath(), strTableName);
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		for (int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
		}
		File dir = new File(strTableName);
		File[] files = dir.listFiles();
		// File csv = new File(this.file.getPath());
		// csv.delete();
		File csvnew = new File(this.file.getPath());
		//		FileWriter writer = new FileWriter(csvnew);

		this.writer = new FileWriter(csvnew);
		writer.append("Table Name, Column Name, Column Type, Key, Indexed");
		writer.append('\n');
		String[] temp;
		for (int j = 0; j < array.size(); j++) {
			temp = array.get(j).split(",");
			if (strColName.equalsIgnoreCase(temp[1])) {
				temp[4] = "True";
			}
			for (int k = 0; k < temp.length; k++) {
				System.out.print(temp[k] + ",");
				if (k != temp.length - 1) {
					writer.append(temp[k] + ",");
				} else {
					writer.append(temp[k]+",");
				}
			}

			System.out.println("");
			writer.append('\n');
			
		}
		writer.flush();
		writer.close();
		//writer.close();
		int position = 0;
		// String type="";
		for (int j = 0; j < array.size(); j++) {
			String[] temp2 = array.get(j).split(",");
			if (strColName.equalsIgnoreCase(temp2[1])) {
				System.out.println("Position" + " " + j);
				position = j;
				// type=temp2[2];
				break;
			}
		}
		String colpath = strTableName + "index" + "//" + strColName;
		File colindex = new File(colpath);
		if (colindex.mkdir()) {
			System.out.println("created successfully");
		} else {
			System.out.println("not created");
		}
		System.out.println(colindex.getPath());
		Page[] pages = new Page[new File(strTableName).listFiles().length];
		for (int i = 0; i < pages.length; i++) {
			Page page = deser(strTableName, i + 1);
			pages[i] = page;
		}
		Vector<RPN> rpn = new Vector<RPN>();
		Vector<Object> objects = new Vector<Object>();
		for (int i = 0; i < pages.length; i++) {
			Vector<Row> rows = pages[i].rows;
			for (int j = 0; j < rows.size(); j++) {
				RPN a = new RPN(rows.get(j), i + 1);
				objects.add(rows.get(j).attributes.get(position));
				rpn.add(a);
			}
		}
		Vector<Object> unique = removeDuplicates(objects);
		Vector<ObjBit> obs = new Vector<ObjBit>();
		for (int i = 0; i < unique.size(); i++) {
			Vector<Bitmap> bitmaps = new Vector<Bitmap>();
			Object un = unique.get(i);

			for (int j = 0; j < rpn.size(); j++) {
				Bitmap bitmap;
				Object o = rpn.get(j).row.attributes.get(position);
				if (o.equals(un)) {
					bitmap = new Bitmap(1, rpn.get(j).number);
				} else {
					bitmap = new Bitmap(0, rpn.get(j).number);
				}
				bitmaps.add(bitmap);

			}
			ObjBit ob = new ObjBit(un, bitmaps);
			obs.add(ob);
		}
		//int MaximumSize = 3;
		// String k="1";
		Vector<Page> ps = new Vector<Page>();
		int k = 1;
		Page p = new Page(k);
		System.out.println("obs size:" + obs.size());
		for (int i = 0; i < obs.size(); i++) {
			if (p.objbits.size() < MaximumBitmapSize || p.objbits == null) {
				System.out.println("hereeeee");
				p.objbits.add(obs.get(i));
			} else {
				System.out.println("nothereee");
				System.out.println(p);
				ps.add(p);
				k++;
				p = new Page(k);
				p.objbits.add(obs.get(i));
			}
		}
		ps.add(p);
		System.out.println("Ps size:" + ps.size());
		for (int i = 0; i < ps.size(); i++) {
			Page px = ps.get(i);
			System.out.println(strTableName + "index" + "//" + strColName + "//" + px.pagenumber+".class");
			px.write(strTableName + "index" + "//" + strColName + "//" + px.pagenumber+".class");
		}
		///////////////////////////////////////////////////////

		// ms2
	}

	public static Vector<Object> removeDuplicates(Vector<Object> v) {
		for (int i = 0; i < v.size(); i++) {
			for (int j = 0; j < v.size(); j++) {
				if (i != j) {
					if (v.elementAt(i).equals(v.elementAt(j)))
						v.removeElementAt(j);
				}
			}
		}
		return v;
	}

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

	public Page deser(String strTableName, int PageNumber) {
		Page object1 = null;
		// Deserialization
		try {
			// Reading the object from a file
			FileInputStream file = new FileInputStream(new File(strTableName + "//" + PageNumber + ".class"));
			ObjectInputStream in = new ObjectInputStream(file);

			// Method for deserialization of object
			object1 = (Page) in.readObject();

			in.close();
			file.close();

			System.out.println("Object has been deserialized ");

		} catch (IOException ex) {
			System.out.println("IOException is caught");
		}

		catch (ClassNotFoundException ex) {
			System.out.println("ClassNotFoundException is caught");
		}
		return object1;
	}
	public Vector<Bitmap> setzero(Vector<Bitmap> bm){
		Vector<Bitmap> bm2=new Vector<Bitmap>();
		for(int i=0;i<bm.size();i++) {
			Bitmap b=new Bitmap(0,bm.get(i).pagenumber);
			bm2.add(b);
		}
		return bm2;
	}
	public boolean shift(String strIndexpath,Page previouspage,ObjBit tobeinserted,int lengthoffiles) throws IOException {
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		System.out.println("in shift");
		if(previouspage.pagenumber!=lengthoffiles) {
		Page nextpage=deser(strIndexpath,previouspage.pagenumber+1);
		nextpage.objbits.add(0,tobeinserted);
		
		nextpage.write(strIndexpath+"//"+nextpage.pagenumber+".class");
		if(nextpage.objbits.size()>MaximumBitmapSize) {
			return true;
		}
		else {
			return false;
		}
		}
		else {
			Page nextpage=new Page(lengthoffiles+1);
			Vector <ObjBit> q=nextpage.objbits;
			q.add(tobeinserted);
			nextpage.write(strIndexpath+"//"+nextpage.pagenumber+".class");
			return false;
		}
	}
	//case if no additional objects
	public void inserttoindex(String strIndexpath,int positiontobeinsertedat,
			int pagetobeinsertedat,Object keyneedtoinsert,int totalelementsbefore,boolean shiftpages,
	int rowspagen) throws IOException {
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		//int maximumSize=3;
		File file=new File(strIndexpath);
		File[] files=file.listFiles();
		
		Page pi=deser(strIndexpath,pagetobeinsertedat);
		Vector<ObjBit> obs=pi.objbits;
		Vector<Bitmap> bm=obs.get(0).bmvector;
		Vector<Bitmap> bm2=setzero(bm);
		System.out.println("ana fl insert to index");
		bm2.add(totalelementsbefore
				,new Bitmap(1,rowspagen));
		System.out.println("3deet 1");
		ObjBit ob=new ObjBit(keyneedtoinsert,bm2 );
		System.out.println("3deet 2");
		if(shiftpages){
			obs.add(positiontobeinsertedat,ob);
		}
		else{
		obs.add(ob);}
		System.out.println("3deet 3");

		pi.write(strIndexpath+"//"+pi.pagenumber+".class");
		System.out.println("3deet 4s");

		System.out.println(bm2.get(totalelementsbefore).index+":"+bm2.get(totalelementsbefore).pagenumber);
		for (int i = 0; i < files.length; i++) {
			//if(i<pagetobeinsertedat) {
				Page pn=deser(strIndexpath,i+1);
				Vector<ObjBit> obq=pn.objbits;
				for (int j = 0; j < obq.size(); j++) {
					Vector<Bitmap> bms=obq.get(j).bmvector;
					if(!obq.get(j).object.equals(keyneedtoinsert)) {
					bms.add(totalelementsbefore,new Bitmap(0,rowspagen));
					System.out.println("ENtered in page:"+(i+1)+"  Inserted vector of object:"+obq.get(j).object.toString());
					}
				//}
					
					
					
					
					
					
					
					
					
			}
				System.out.println("hntba3 deh"+strIndexpath+"//"+pn.pagenumber+".class");
				pn.write(strIndexpath+"//"+pn.pagenumber+".class");
		}
		////////
		if(shiftpages) {
			int shifted=totalelementsbefore+1;
			ArrayList<Integer> shiftedcells=new ArrayList<Integer>();
			//&&shiftpages
		for (int i = pagetobeinsertedat; i <= files.length; i++) {
			//if(i<pagetobeinsertedat) {
				Page pn=deser(strIndexpath,i);
				//MAximumsize
				//comment
				System.out.println("bits size:"+pn.objbits.size()+" maximum size"+MaximumBitmapSize);
				if(pn.objbits.size()>MaximumBitmapSize) {
					System.out.println("must enter here for shifting");
					
					
					ObjBit q=pn.objbits.lastElement();
					System.out.println("Last element must be 4:"+q.object);
					pn.objbits.remove(q);
					System.out.println("AY PRINT LEL ELEMENT"+pn.objbits.lastElement().object);
					shiftedcells.add(new Integer(shifted));
					shifted+=MaximumBitmapSize;
					shiftpages=shift(strIndexpath,pn,q,files.length);
					pn.write(strIndexpath+"//"+pn.pagenumber+".class");

					if(!shiftpages)
						return;
				}
				
		}
		File file2=new File(strIndexpath);
		File[] files2=file2.listFiles();
		for(int i=0;i<files2.length;i++) {
			Page pn=deser(strIndexpath,i+1);
				Vector<ObjBit> obq=pn.objbits;
		for(int k=0;k<obq.size();k++) {		
			ObjBit anobject=obq.get(k);
		for (int j = 0; j < shiftedcells.size(); j++) {
			System.out.println("Shifted cell: "+shiftedcells.get(j));
			System.out.println("BM VECTOR"+anobject.bmvector);
			System.out.println("Size of bm vector:"+anobject.bmvector.size());
			Bitmap bitm=anobject.bmvector.get(shiftedcells.get(j).intValue());
			bitm.pagenumber=bitm.pagenumber+1;
			
		}
		
		}
		pn.write(strIndexpath+"//"+pn.pagenumber+".class");
		}
		}
		
		
		
		
		
		
		
		
	}
	// update index other than clusteringkey
	public void inserttoindex2(String strIndexpath,int positiontobeinsertedat,
			int pagetobeinsertedat,Object newobjectneedtoinsert,int totalelementsbefore,boolean shiftpages,
	int rowspagen) throws IOException {
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
	//	int maximumSize=3;
		boolean alreadyavailable=false;
		int pageavailableatinindex=0;
		int positionavailableindexpage=0;
		// check if this value is not there else i will updated the bitmap of what we have
		File file=new File(strIndexpath);
		File[] files=file.listFiles();
		for(int i=0;i<files.length&&!alreadyavailable;i++) {
			Page pi=deser(strIndexpath,i+1);
			Vector<ObjBit> objbits=pi.objbits;
			for (int j = 0; j < objbits.size(); j++) {
				if(objbits.get(j).object.equals(newobjectneedtoinsert)){
					System.out.println("d5lt fl true condition bta3 el method");
					alreadyavailable=true;
					pageavailableatinindex=i+1;
					positionavailableindexpage=j;
					break;
					
				}
			}
		}
		if(alreadyavailable) {
			Page pageindex=deser(strIndexpath, pageavailableatinindex);
			ObjBit tobeupdate=pageindex.objbits.get(positionavailableindexpage);
			Bitmap one=new Bitmap(1, rowspagen);
			tobeupdate.bmvector.add(totalelementsbefore,one);
			pageindex.write(strIndexpath+"//"+pageindex.pagenumber+".class");
			for(int i=0;i<files.length;i++) {
				Page p1=deser(strIndexpath,i+1);
				Vector<ObjBit> objbits=p1.objbits;
				for (int j = 0; j < objbits.size(); j++) {
					if(!objbits.get(j).equals(tobeupdate)){
						Bitmap zero=new Bitmap(0, rowspagen);
						objbits.get(j).bmvector.add(totalelementsbefore,zero);
						p1.write(strIndexpath+"//"+p1.pagenumber+".class");
						
					}
				}
			}
		}
		else {
			Vector<Bitmap> newvector=new Vector<Bitmap>();
			for(int i=0;i<files.length;i++) {
				Page p1=deser(strIndexpath,i+1);
				Vector<ObjBit> objbits=p1.objbits;
				for (int j = 0; j < objbits.size(); j++) {
					//newvector=setzero(objbits.get(j).bmvector);
					
						Bitmap zero=new Bitmap(0, rowspagen);
						objbits.get(j).bmvector.add(totalelementsbefore,zero);
						p1.write(strIndexpath+"//"+p1.pagenumber+".class");
						
					
				}
				p1.write(strIndexpath+"//"+p1.pagenumber+".class");
			}
			newvector=setzero(deser(strIndexpath,1).objbits.get(0).bmvector);
			newvector.add(totalelementsbefore,new Bitmap(1, rowspagen));
			Page last=deser(strIndexpath,files.length);
			if(last.objbits.size()<MaximumBitmapSize) {
				//Vector <ObjBit> vectortoinsert=last.objbits;
				ObjBit oa=new ObjBit(newobjectneedtoinsert, newvector);
				last.objbits.add(oa);
				last.write(strIndexpath+last.pagenumber+".class");
			}
			else {
				Page newpage=new Page(last.pagenumber+1);
				newpage.objbits.add(new ObjBit(newobjectneedtoinsert, newvector));
				newpage.write(strIndexpath+newpage.pagenumber+".class");
			}
		}
		
		
		
		
		
		
		
		////////
		if(shiftpages) {
			int shifted=totalelementsbefore+1;
			ArrayList<Integer> shiftedcells=new ArrayList<Integer>();
			//&&shiftpages
		for (int i = pagetobeinsertedat; i <= files.length; i++) {
			//if(i<pagetobeinsertedat) {
				Page pn=deser(strIndexpath,i);
				//MAximumsize
				//comment
				System.out.println("bits size:"+pn.objbits.size()+" maximum size");
				if(pn.objbits.size()>MaximumBitmapSize) {
					System.out.println("must enter here for shifting");
					
					
					ObjBit q=pn.objbits.lastElement();
					System.out.println("Last element must be 4:"+q.object);
					pn.objbits.remove(q);
					System.out.println("AY PRINT LEL ELEMENT"+pn.objbits.lastElement().object);
					shiftedcells.add(new Integer(shifted));
					shifted+=MaximumBitmapSize;
					shiftpages=shift(strIndexpath,pn,q,files.length);
					pn.write(strIndexpath+"//"+pn.pagenumber+".class");

					if(!shiftpages)
						break;
				}
				
		}
		File file2=new File(strIndexpath);
		File[] files2=file2.listFiles();
		for(int i=0;i<files2.length;i++) {
			Page pn=deser(strIndexpath,i+1);
				Vector<ObjBit> obq=pn.objbits;
		for(int k=0;k<obq.size();k++) {		
			ObjBit anobject=obq.get(k);
		for (int j = 0; j < shiftedcells.size(); j++) {
			Bitmap bitm=anobject.bmvector.get(shiftedcells.get(j).intValue());
			bitm.pagenumber=bitm.pagenumber+1;
			
		}
		
		}
		pn.write(strIndexpath+"//"+pn.pagenumber+".class");
		}
		}
		
		
		
		
		
		
		
		
	}
	public void insertIntoTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException, IOException {
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		ArrayList<String> array2 = FromCSV(this.file.getPath(), strTableName);
		int indexposition = 0;
		String indexname="";
	//	int maximumSize=3;
		Set<String> hashset = htblColNameValue.keySet();
		Object[] hasharray = hashset.toArray();
		boolean correct = true;
		// htblColNameValue.size() - 1
		for (int i = hasharray.length - 1; i >= 0 && correct; i--) {
			String nameofcolumn = (String) hasharray[i];
			Object dataincolumn = htblColNameValue.get(nameofcolumn);
			ArrayList<String> array = FromCSV(this.file.getPath(), strTableName);

			for (int j = 0; j < array.size(); j++) {
				String[] temp = array.get(j).split(",");
				if (nameofcolumn.equalsIgnoreCase(temp[1])) {
					if (!dataincolumn.getClass().toString().equalsIgnoreCase("class " + temp[2])) {
						System.out.println("Wrong data type inserted in :" + nameofcolumn + " column");
						correct = false;

					}

				}
			}

		}
		if (correct) {
		//btgeeb el name bta3 el clustering key wl index fl metadata
		for (int i = 0; i < array2.size(); i++) {
			String[] temp2 = array2.get(i).split(",");
			System.out.println(temp2[3]);
			if (temp2[3].equalsIgnoreCase("true")) {
				System.out.println("Position" + " " + i);
				indexposition = i;
				indexname=temp2[1];
				System.out.println("This is the index name:"+indexname);
				// type=temp2[2];
				break;
			}
		}
		Object keyneedtoinsert=htblColNameValue.get(indexname);
		File indexdir=new File(strTableName+"index");
		File[] indexdirfiles=indexdir.listFiles();
		System.out.println(indexdirfiles==null);
		boolean keyexists=false;
		//bt3ml check law fe index 3la el clustering key 
ArrayList<String> indices=new ArrayList<String>();
		for (int i = 0; indexdirfiles!=null &&i < indexdirfiles.length; i++) {
			File index1=indexdirfiles[i];
			System.out.println(index1.getName());
			if(index1.getName().equalsIgnoreCase(indexname)) {
				keyexists=true;
			}
			else{
				//System.out.println(index1.getName());
				indices.add(index1.getName());
			}
		}
		System.out.println(keyexists);
		if(keyexists) {
			System.out.println("The key index exists!!");
			File clusindex=new File(strTableName+"index"+"//"+indexname);
			File[] clusindexp=clusindex.listFiles();
			int j;
			int totalelementsbefore=0;
			boolean found = false;
			int pagetobeinsertedat=0;
			int positiontobeinsertedat=0;
			boolean shiftpages=false;
			int rowspagen=0;
			ArrayList<Boolean> nofshifts=new ArrayList<Boolean>();
			for (int i = 0; i < clusindexp.length && !found; i++) {
				System.out.println("indexname:::"+indexname);
				j = i + 1;
				Page pa = deser(strTableName+"index"+"//"+indexname, j);
				Vector <ObjBit>  obs=pa.objbits;
				for (int k = 0; k < obs.size(); k++) {
					ObjBit ob=obs.get(k);
					totalelementsbefore++;
					System.out.println((((Integer)ob.object).intValue()+":COMPARE WITH:"+((Integer)keyneedtoinsert).intValue()));
					if(((Integer)ob.object).intValue()>((Integer)keyneedtoinsert).intValue()) {
						found=true;
						System.out.println("key needed to be inserted");
						//hn3ml save lel page number
						//hn3ml insert fl page number deh 
						//hn3ml insert fl bitindexpagedeh
						pagetobeinsertedat=pa.pagenumber;
						Page insertin=deser(strTableName,ob.bmvector.get(k).pagenumber);
						System.out.println("e7na hena now2");

						if(insertin.rows.size()==MaximumRowSize) {
							System.out.println("e7na hena now3");

							shiftpages=true;
							nofshifts.add(shiftpages);
						}
						insert(strTableName,insertin , htblColNameValue);
						rowspagen=insertin.pagenumber;
						positiontobeinsertedat=k;
						System.out.println("Position:"+positiontobeinsertedat);
						totalelementsbefore--;
						System.out.println("Index reference:"+(strTableName+"index"+"//"+indexname)+'\n'+"Position to be inserted"
								+ "at:"+positiontobeinsertedat+'\n'+"number of page to be inserted at:"+
								pagetobeinsertedat+'\n'+"key need to be inserted:"+keyneedtoinsert.toString()+
								'\n'+"total elements before:"+totalelementsbefore+'\n'+"shift pages:"+
								shiftpages+'\n'+"rowspagenumber"+rowspagen);

						inserttoindex((strTableName+"index"+"//"+indexname),positiontobeinsertedat,
								pagetobeinsertedat,keyneedtoinsert,totalelementsbefore,shiftpages,rowspagen);
						break;
						//ana hena
					}
					/*else{
						if(pa.pagenumber==clusindexp.length && obs.lastElement().equalsIgnoreCase(ob))
						{
							pagetobeinsertedat=pa.pagenumber;
							Page insertin=deser(strTableName,ob.bmvector.get(k).pagenumber);
						}
					}*/
				}
				

			}
			
			
			
			
			
			
			
			if(!found){
				System.out.println("NOT FOUND!!!!!!");
				pagetobeinsertedat=clusindexp.length;
				Page objspage=deser(strTableName+"index"+"//"+indexname, pagetobeinsertedat);
				Page insertin=deser(strTableName,pagetobeinsertedat);
				//Vector <ObjBit>  obs=pa.objbits;
				//ObjBit ob=obs.get(k);
				found=true;
				
				//Page insertin=deser(strTableName,ob.bmvector.get(k).pagenumber);
				//System.out.println("e7na hena now2");

				if(insertin.rows.size()==MaximumRowSize) {
					System.out.println("e7na hena now3");

					shiftpages=true;
					nofshifts.add(shiftpages);
				}
				
				positiontobeinsertedat=insertin.rows.size();
				insert(strTableName,insertin , htblColNameValue);
				rowspagen=insertin.pagenumber;
				
				System.out.println("Position:"+positiontobeinsertedat);
				//totalelementsbefore--;
				System.out.println("Index reference:"+(strTableName+"index"+"//"+indexname)+'\n'+"Position to be inserted"
						+ "at:"+positiontobeinsertedat+'\n'+"number of page to be inserted at:"+
						pagetobeinsertedat+'\n'+"key need to be inserted:"+keyneedtoinsert.toString()+
						'\n'+"total elements before:"+totalelementsbefore+'\n'+"shift pages:"+
						shiftpages+'\n'+"rowspagenumber"+rowspagen);

				inserttoindex((strTableName+"index"+"//"+indexname),positiontobeinsertedat,
						pagetobeinsertedat,keyneedtoinsert,totalelementsbefore,shiftpages,rowspagen);
				//(strTableName+"index"+"//"+indexname)
				//positiontobeinsertedat 25er element fe 25er page
				//pagetobeinsertedat=clusindexp.length
				//totalelementsbefore hdehalo kolaha wala -1
				//ngeeb hn3ml shiftpages wala la2
				//rowspagen heya heya clusindexp.length
				//inserttoindex((strTableName+"index"+"//"+indexname),positiontobeinsertedat,
					//	pagetobeinsertedat,keyneedtoinsert,totalelementsbefore,shiftpages,rowspagen);
				
			
			}
			
		  if(indices.size()!=0){
			  for(int i=0;i<indices.size();i++){
				 String indexTany= indices.get(i);
				 Object newobjectneedtoinsert=htblColNameValue.get(indexTany);
System.out.println("h3ml insert fe el index el tnyeen:"+indexTany);
				 inserttoindex2((strTableName+"index"+"//"+indexTany), positiontobeinsertedat, pagetobeinsertedat, newobjectneedtoinsert, totalelementsbefore, shiftpages, rowspagen);
			  }
		  }
		
		  
		  
		  
		  
		  
		  if(indices.size()!=0){
			  for(int i=0;i<indices.size();i++){
				 String indexTany= indices.get(i);
System.out.println("h3ml insert fe el index el tnyeen:"+indexTany);
try {
	this.shift(strTableName, indexTany);
} catch (IOException e) {
	System.out.println("editing exception in index");
}			  }
		  }	  
		  return;
			
		}
		
		
		
		
		
		
		else{	
		
		
		
	
		
			File dir = new File(strTableName);
			File[] files = dir.listFiles();
			if (files.length == 0) {
				Page p = new Page(1);
				Vector<Row> rows = p.rows;
				Row row = new Row();
				for (int i = hasharray.length - 1; i >= 0; i--) {

					String nameofcolumn = (String) hasharray[i];
					Object dataincolumn = htblColNameValue.get(nameofcolumn);
					System.out.println(dataincolumn);
					row.attributes.add(dataincolumn);
				}
				row.attributes.add(DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDate.now())); 

				rows.add(row);
				p.rows = rows;
				// System.out.println(row.attributes.get(0));
				p.write(strTableName + "//1" + ".class");
				System.out.println("a");
				// System.out.println(p.read(strTableName+"\1").pagenumber);
				System.out.println("b");

			} else {
				int positiontobeinserted=0;
				int j;
				int totalelementsbefore=0;
				boolean inserted = false;
				for (int i = 0; i < files.length && !inserted; i++) {
					
					j = i + 1;
					Page object1 = deser(strTableName, j);

					Vector<Row> rows = object1.rows;
					if (rows.size() != 0) {
						for (int k = 0; k < rows.size() && !inserted; k++) {
							Row rw = rows.get(k);
							Integer a = (Integer) rw.attributes.get(0);

							String nameofcolumn = (String) hasharray[htblColNameValue.size() - 1];
							System.out.println(nameofcolumn);
							Object dataincolumn = (Integer) (htblColNameValue.get(nameofcolumn));
							System.out.println(dataincolumn);
							if (a.intValue() > ((Integer) dataincolumn).intValue()) {
								insert(strTableName, object1, htblColNameValue);
								inserted = true;

							}
							else{
							totalelementsbefore++;	
							}
						}
					}

				}
				if (!inserted) {
					System.out.println("here1.1");

					Page Object1 = deser(strTableName, files.length);
					insert(strTableName, Object1, htblColNameValue);

				}
			}
		}
		
	
	
	
		 if(indices.size()!=0){
			  for(int i=0;i<indices.size();i++){
				 String indexTany= indices.get(i);
System.out.println("h3ml insert fe el index el tnyeen:"+indexTany);
try {
	this.shift(strTableName, indexTany);
} catch (IOException e) {
	System.out.println("editing exception in index");
}			  }
		  }
	

	}}

	private void insert(String strTableName, Page currentpage, Hashtable<String, Object> htblColNameValue) throws IOException {
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		Vector<Row> rows = currentpage.rows;
	//	int MaximumSize = 3;
		Set<String> hashset = htblColNameValue.keySet();
		Object[] hasharray = hashset.toArray();
		Row row = new Row();
		for (int i = htblColNameValue.size() - 1; i >= 0; i--) {
			String nameofcolumn = (String) hasharray[i];
			Object dataincolumn = htblColNameValue.get(nameofcolumn);
			row.attributes.add(dataincolumn);
		}
		row.attributes.add(DateTimeFormatter.ofPattern("dd-MM-yyyy").format(LocalDate.now())); 
		rows.add(row);
		System.out.println("here2");

		rows.sort(new Comparator() {
			public int compare(Object a, Object b) {
				return (new Integer((Integer) ((Row) a).attributes.get(0))
						.compareTo((Integer) ((Row) b).attributes.get(0)));
			}
		});
		if (rows.size() > MaximumRowSize) {
			Row row2 = rows.get(rows.size() - 1);
			rows.remove(row2);
			int number = currentpage.pagenumber + 1;
			File dir = new File(strTableName);
			File[] files = dir.listFiles();

			// method for adding the row in the next page (helper)
			if (files.length >= number) {
				Page NextPage = deser(strTableName, number);
				insertToNext(NextPage, row2, MaximumRowSize, strTableName);
			} else {
				System.out.println("herekariman");
				Page newpage = new Page(number);
				Vector<Row> rowsnewPage = newpage.rows;
				rowsnewPage.add(row2);
				newpage.write(strTableName + "//" + newpage.pagenumber + ".class");
			}

		} else {
			System.out.println("inserted into write place");
		}
		System.out.println("here3");

		currentpage.write(strTableName + "//" + currentpage.pagenumber + ".class");

	}

	public void insertToNext(Page currentpage, Row row, int maximumRowSize, String strTableName) {
		Vector<Row> rows = currentpage.rows;
		rows.add(row);
		int number = currentpage.pagenumber + 1;

		rows.sort(new Comparator() {
			public int compare(Object a, Object b) {
				return (new Integer((Integer) ((Row) a).attributes.get(0))
						.compareTo((Integer) ((Row) b).attributes.get(0)));
			}
		});
		if (rows.size() > maximumRowSize) {
			File dir = new File(strTableName);
			File[] files = dir.listFiles();
			if (files.length >= number) {
				Row row2 = rows.get(rows.size() - 1);
				rows.remove(row2);
				Page NextPage = deser(strTableName, number);
				insertToNext(NextPage, row2, maximumRowSize, strTableName);
			}
			// else if there is no page to add insert in
			else {
				Row rowshifted = rows.get(rows.size() - 1);
				rows.remove(rowshifted);
				Page newpage = new Page(number);
				Vector<Row> rowsnewPage = newpage.rows;
				rowsnewPage.add(rowshifted);
				newpage.write(strTableName + "//" + newpage.pagenumber + ".class");

			}
		} else {
			System.out.println("inserted into write place");

		}
		currentpage.write(strTableName + "//" + currentpage.pagenumber + ".class");

	}
	public void shift(String tobeshifted, String columnshifting) throws DBAppException, IOException {
		ArrayList<String> array = FromCSV(this.file.getPath(), tobeshifted);
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		for (int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
		}
		File dir = new File(tobeshifted);
		File[] files = dir.listFiles();
		// File csv = new File(this.file.getPath());
		// csv.delete();
		File csvnew = new File(this.file.getPath());
		
		//FileWriter writer = new FileWriter(csvnew);
		this.writer = new FileWriter(csvnew);
		writer.append("Table Name, Column Name, Column Type, Key, Indexed");
		writer.append('\n');
		String[] temp;
		for (int j = 0; j < array.size(); j++) {
			temp = array.get(j).split(",");
			if (columnshifting.equalsIgnoreCase(temp[1])) {
				temp[4] = "True";
			}
			for (int k = 0; k < temp.length; k++) {
				System.out.print(temp[k] + ",");
				if (k != temp.length - 1) {
					writer.append(temp[k] + ",");
				} else {
					writer.append(temp[k]);
				}
			}

			System.out.println("");
			writer.append('\n');
			
		}
		writer.flush();
		writer.close();
		//writer.close();
		int position = 0;
		// String type="";
		for (int j = 0; j < array.size(); j++) {
			String[] temp2 = array.get(j).split(",");
			if (columnshifting.equalsIgnoreCase(temp2[1])) {
				System.out.println("Position" + " " + j);
				position = j;
				// type=temp2[2];
				break;
			}
		}
		String colpath = tobeshifted + "index" + "//" + columnshifting;
		File colindex = new File(colpath);
		if (colindex.mkdir()) {
			System.out.println("created successfully");
		} else {
			System.out.println("not created");
		}
		System.out.println(colindex.getPath());
		Page[] pages = new Page[new File(tobeshifted).listFiles().length];
		for (int i = 0; i < pages.length; i++) {
			Page page = deser(tobeshifted, i + 1);
			pages[i] = page;
		}
		Vector<RPN> rpn = new Vector<RPN>();
		Vector<Object> objects = new Vector<Object>();
		for (int i = 0; i < pages.length; i++) {
			Vector<Row> rows = pages[i].rows;
			for (int j = 0; j < rows.size(); j++) {
				RPN a = new RPN(rows.get(j), i + 1);
				objects.add(rows.get(j).attributes.get(position));
				rpn.add(a);
			}
		}
		Vector<Object> unique = removeDuplicates(objects);
		Vector<ObjBit> obs = new Vector<ObjBit>();
		for (int i = 0; i < unique.size(); i++) {
			Vector<Bitmap> bitmaps = new Vector<Bitmap>();
			Object un = unique.get(i);

			for (int j = 0; j < rpn.size(); j++) {
				Bitmap bitmap;
				Object o = rpn.get(j).row.attributes.get(position);
				if (o.equals(un)) {
					bitmap = new Bitmap(1, rpn.get(j).number);
				} else {
					bitmap = new Bitmap(0, rpn.get(j).number);
				}
				bitmaps.add(bitmap);

			}
			ObjBit ob = new ObjBit(un, bitmaps);
			obs.add(ob);
		}
		//int MaximumSize = 3;
		// String k="1";
		Vector<Page> ps = new Vector<Page>();
		int k = 1;
		Page p = new Page(k);
		System.out.println("obs size:" + obs.size());
		for (int i = 0; i < obs.size(); i++) {
			if (p.objbits.size() < MaximumBitmapSize || p.objbits == null) {
				System.out.println("hereeeee");
				p.objbits.add(obs.get(i));
			} else {
				System.out.println("nothereee");
				System.out.println(p);
				ps.add(p);
				k++;
				p = new Page(k);
				p.objbits.add(obs.get(i));
			}
		}
		ps.add(p);
		System.out.println("Ps size:" + ps.size());
		for (int i = 0; i < ps.size(); i++) {
			Page px = ps.get(i);
			System.out.println(tobeshifted + "index" + "//" + columnshifting + "//" + px.pagenumber+".class");
			px.write(tobeshifted + "index" + "//" + columnshifting + "//" + px.pagenumber+".class");
		}
		///////////////////////////////////////////////////////

		// ms2
	}
	
	public void updateTable(String strTableName, String strKey, Hashtable<String, Object> htblColNameValue)
			throws DBAppException {
		// TODO: strkey value of primary key
		// TODO: search for the record with strkey and update the column with
		// the value passed from the hashtable
		Set<String> hashset = htblColNameValue.keySet();
		Object[] hasharray = hashset.toArray();
		ArrayList<String> namesofcolumns= new ArrayList<>();
		ArrayList<Object> dataincolumns= new ArrayList<>();
		for (int i = htblColNameValue.size() - 1; i >= 0; i--) {
			String nameofcolumn = (String) hasharray[i];
			namesofcolumns.add(nameofcolumn);
			Object dataincolumn = htblColNameValue.get(nameofcolumn);
			dataincolumns.add(dataincolumn);

			}

		ArrayList<String> array = FromCSV("metadata.csv", strTableName);
		ArrayList<Integer> positions=new ArrayList<>();
		ArrayList<Boolean> index= new ArrayList<>();
		boolean indexed=false;
		String clusteringKeyName="";
		
		for (int j = 0; j < array.size(); j++) {
			String[] temp = array.get(j).split(",");
			if(temp[3].equalsIgnoreCase("True") && temp[4].equalsIgnoreCase("True")){
				indexed=true;
			    clusteringKeyName= temp[1];
				}
			for(int c=0;c<namesofcolumns.size();c++){
				String nameofcolumn= namesofcolumns.get(c);
				System.out.println("bara awel if");
			if (nameofcolumn.equalsIgnoreCase(temp[1])  ) {
                positions.add(j);
                System.out.println("gowa awel if"+nameofcolumn);
                if(temp[4].equalsIgnoreCase("True")){
                	index.add(true);
                	System.out.println("l2eet index be true fe:"+nameofcolumn);}
                else
                	index.add(false);

                

			}
			}
		}
		int primaryKey = Integer.parseInt(strKey);

		if(indexed){
			updateIndex(strTableName,namesofcolumns,dataincolumns,positions,index,clusteringKeyName,primaryKey);
		}


		File dir = new File(strTableName);
		File[] files = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			Page page = deser(strTableName, i + 1);
			System.out.println(page.pagenumber + "page number");
			Vector<Row> rows = page.rows;
			int k = rows.size();
			for (int j = 0; j < k; j++) {

				Row row = rows.get(j);
				if (row.attributes.get(0).equals(new Integer(primaryKey))) {
					for(int r=0;r<row.attributes.size();r++){
						for(int p=0;p<positions.size();p++){
							if(r==positions.get(p)){
								System.out.println("POSITION BEING UPDATED NOW"+positions.get(p));
    							Object oldValue= row.attributes.get(r);
    							String nameofcolumn= namesofcolumns.get(p);
    							Object dataincolumn= dataincolumns.get(p);
								row.attributes.insertElementAt(dataincolumns.get(p), r);
     							row.attributes.remove(r+1);
     							if(index.get(p) == true){
     								System.out.println("hereeeee100000");
									  updateIndex2(strTableName,nameofcolumn,oldValue,dataincolumn,j);
									  System.out.println("ba3d here 100000");

									}

	
								
							}
						}
					}

					
					page.write(strTableName + "//" + page.pagenumber + ".class");
					break;
				}

			}
		}
	}
	
	
	
	
	public void updateIndex(String strTableName,ArrayList<String> namesofcolumns,ArrayList<Object> dataincolumns,ArrayList<Integer> positions,ArrayList<Boolean>index,String clusteringKeyName,int primaryKey) {
		File dir = new File(strTableName+"index"+"//"+clusteringKeyName);
		File[] files = dir.listFiles();
		
		for(int i=0;i<files.length;i++){
			Page pageIndex = deser(strTableName+"index"+"//"+clusteringKeyName, i + 1);
			System.out.println("Update Index");
			System.out.println(pageIndex.pagenumber + "page number");
			Vector <ObjBit> objbits= pageIndex.objbits;
			for(int j=0;j<objbits.size();j++){
				System.out.println("PRIMARY KEY"+ primaryKey);
				if(primaryKey==((Integer)(objbits.get(j).object)).intValue()){
					Vector<Bitmap> bmvector= objbits.get(j).bmvector;
					for(int k=0;k<bmvector.size();k++){
						Bitmap b= bmvector.get(k);
						if(b.index==1){
							Page page= deser(strTableName, b.pagenumber);
							for(int c=0;c<page.rows.size();c++){
								Row row= page.rows.get(c);
								if(row.attributes.get(0).equals(primaryKey)){
									for(int r=0;r<row.attributes.size();r++){
										for(int p=0;p<positions.size();p++){
											if(r==positions.get(p)){
												Object oldValue= row.attributes.get(r);
												String nameofcolumn= namesofcolumns.get(p);
												Object dataincolumn= dataincolumns.get(p);
												System.out.println("r"+ r + "p" + p + "dataincolumn"+ dataincolumn);
												row.attributes.insertElementAt(dataincolumns.get(p), r);
         										row.attributes.remove(r+1);

                                                  
												if(index.get(p)==true){
													System.out
															.println("el makann ahooo");
												  updateIndex2(strTableName,nameofcolumn,oldValue,dataincolumn,k);
												  System.out
													.println("el makann ahooo222");
												}
												
											}
										}
									}


								}
							}
							page.write(strTableName+ "//" + page.pagenumber + ".class");
						}
					}
				}
			}
			
			
			
		}
		
		
	}
	
	
	public void updateIndex2(String strTableName,String nameofcolumn,Object oldValue, Object dataincolumn, int position){
		File dir = new File(strTableName+"index"+"//"+nameofcolumn);
		File[] files = dir.listFiles();
		boolean flag=false;
		for(int i=0;i<files.length;i++){
			Page pageIndex = deser(strTableName+"index"+"//"+nameofcolumn, i + 1);
			Vector <ObjBit> objbits= pageIndex.objbits;
         for(int j=0;j<objbits.size();j++){
        	if( objbits.get(j).object.equals(oldValue)){
        		Vector<Bitmap> bmvector= objbits.get(j).bmvector;
				for(int k=0;k<bmvector.size();k++){
        		if(position==k){
        			Bitmap b= bmvector.get(k);
        			b.index=0;
        			System.out.println("Here is the old value:"+ oldValue);
        	        pageIndex.write(strTableName+"index"+"//"+nameofcolumn +"//"+ pageIndex.pagenumber + ".class");



        		}
        	}
	        
        }
        	System.out.println("law: "+objbits.get(j).object+" =" +dataincolumn+" mfrood true aw false:"+objbits.get(j).object.toString().equalsIgnoreCase(dataincolumn.toString()));
         if(objbits.get(j).object.toString().equalsIgnoreCase(dataincolumn.toString())){
        	Vector<Bitmap> bmvector= objbits.get(j).bmvector;
			for(int k=0;k<bmvector.size();k++){
    		if(position==k){
    			Bitmap b= bmvector.get(k);
    			 b.index=1;
    			 flag=true;
    			 System.out.println("Here is the new value:"+ dataincolumn);
    		    pageIndex.write(strTableName+"index"+"//"+nameofcolumn +"//"+ pageIndex.pagenumber + ".class");

    		}
        }
		}
	}

         }
		
		if(!flag){
			try {
				createNewBitmap(strTableName,nameofcolumn,dataincolumn,position);
			} catch (IOException e) {
				// TODO Auto-generated catch block
System.out.println("Couldnt create the new bit and insert it in the bitmap");			}
		}
		}
	
	

	private void createNewBitmap(String strTableName, String nameofcolumn,Object dataincolumn, int position) throws IOException {
		File config = new File("config/DBApp.properties");

		FileReader reader = new FileReader(config);
		Properties props = new Properties();
		props.load(reader);
		int MaximumRowSize = Integer.parseInt(props.getProperty("MaximumRowsCountinPage"));

		int MaximumBitmapSize = Integer.parseInt(props.getProperty("BitmapSize"));
		reader.close();
		File dir = new File(strTableName+"index"+"//"+nameofcolumn);
		File[] files = dir.listFiles();
	//	int maximumSize=3;
		Page togetbitmapvector=deser(strTableName+"index"+"//"+nameofcolumn,1);
		Vector<Bitmap> bm=setzero(togetbitmapvector.objbits.get(0).bmvector);
		System.out.println("ana f create new bitmap index");
		boolean created=false;
		for(int i=0;i<files.length;i++){
			Page pageIndex = deser(strTableName+"index"+"//"+nameofcolumn, i + 1);
			Vector <ObjBit> objbits= pageIndex.objbits;
			if(objbits.size()<MaximumBitmapSize){
				ObjBit newObjBit= new ObjBit(dataincolumn,bm);
				objbits.add(newObjBit);
				for(int j=0;j<newObjBit.bmvector.size();j++){
					if(j==position){
						
						Bitmap b= newObjBit.bmvector.get(j);
						b.index=1;
						b.pagenumber=pageIndex.pagenumber;
						pageIndex.write(strTableName+"index"+"//"+nameofcolumn+"//"+pageIndex.pagenumber + ".class");
						created=true;
					}
				}
			}
			
			
		}
		if(!created){
			Page newPage= new Page(files.length);
			ObjBit newObjBit= new ObjBit(dataincolumn,bm);
			newPage.objbits.add(newObjBit);
			for(int j=0;j<newObjBit.bmvector.size();j++){
				if(j==position){
					Bitmap b= newObjBit.bmvector.get(j);
					b.index=1;
					b.pagenumber=newPage.pagenumber;
					newPage.write(strTableName+"index"+"//"+nameofcolumn+"//"+newPage.pagenumber + ".class");

				}
			}
			
		}
		
		
	}



	public void deleteFromTable(String strTableName, Hashtable<String, Object> htblColNameValue) throws DBAppException {
		// TODO: delete all the records that has the VALUE in COLNAME passed in
		// the hashtable.
		// TODO: if the vector size becomes zero, then delete the page
		
		
		Set<String> hashset = htblColNameValue.keySet();
		Object[] hasharray = hashset.toArray();
		ArrayList<String> namesofcolumns= new ArrayList<>();
		ArrayList<Object> dataincolumns= new ArrayList<>();
		for (int i = htblColNameValue.size() - 1; i >= 0; i--) {
			String nameofcolumn = (String) hasharray[i];
			namesofcolumns.add(nameofcolumn);
			Object dataincolumn = htblColNameValue.get(nameofcolumn);
			dataincolumns.add(dataincolumn);

			}
			

		ArrayList<String> array = FromCSV("metadata.csv", strTableName);
		ArrayList<Integer> positions=new ArrayList<>();
		ArrayList<Boolean> index= new ArrayList<>();
//		for (int j = 0; j < array.size(); j++) {
//			String[] temp = array.get(j).split(",");
//			for(int c=0;c<namesofcolumns.size();c++){
//				String nameofcolumn= namesofcolumns.get(c);
//			if (nameofcolumn.equalsIgnoreCase(temp[1])) {
//				
//                  positions.add(j);
//                
//
//			}
//			}
//		}
		boolean indexed=false;
		String clusteringKey="";
		for (int j = 0; j < array.size(); j++) {
			String[] temp = array.get(j).split(",");
			if(temp[3].equalsIgnoreCase("True") && temp[4].equalsIgnoreCase("True")){
				indexed=true;
				clusteringKey=temp[1];
				}
			for(int c=0;c<namesofcolumns.size();c++){
				String nameofcolumn= namesofcolumns.get(c);
			if (nameofcolumn.equalsIgnoreCase(temp[1])  ) {
                positions.add(j);
                if(temp[4].equalsIgnoreCase("True"))
                	index.add(true);
                else
                	index.add(false);

                

			}
			}
		}
      
		
		
		File dir = new File(strTableName);
		File[] files = dir.listFiles();
		for (int k = 0; k < files.length; k++) {
			Page page = deser(strTableName, k + 1);
			Vector<Row> rows = page.rows;
			int h = rows.size();
			int z = 0;
			for (int j = 0; j < h; j++) {

				Row row = rows.get(z);
				boolean delete=true;
				for(int p=0;p<row.attributes.size();p++){
					for(int a=0;a<positions.size();a++){
						if(positions.get(a)==p){
						    Object dataincolumn= dataincolumns.get(a);
						    if(!(row.attributes.get(p).toString().equalsIgnoreCase(dataincolumn.toString()))){
						    	delete=false;
						    	break;
						    }
						}
							
					}
					
					
					
				}
				if(!delete){
					z++;
					}
				else {
					if(indexed){
						deleteFromIndex1(strTableName,clusteringKey,row.attributes.get(0));
					}
					for(int w=0;w<index.size();w++){
						if(index.get(w)==true){
							System.out.println("bnd5ol fe el deletefromindex2");
							deleteFromIndex2(strTableName,namesofcolumns.get(w),z);
						}
					}
					rows.remove(row);
					
					}

			}
			page.write(strTableName + "//" + page.pagenumber + ".class");
			// serialize
			if (rows.isEmpty()) {

				File file = new File(strTableName + "//" + page.pagenumber + ".class");
				file.delete();
			}
			
		}
		rename(strTableName);
	}
	
	
	
	private void deleteFromIndex2(String strTableName, String nameofcolumn,int position) {
		File dir = new File(strTableName+"index"+"//"+nameofcolumn);
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			Page pageIndex= deser(strTableName+"index"+"//"+nameofcolumn,i+1);
			for(int j=0;j<pageIndex.objbits.size();j++){
				
					Vector<Bitmap> bitmaps= pageIndex.objbits.get(j).bmvector;
					for(int k=0;k<bitmaps.size();k++){
						if(k==position){
							System.out.println("bn3ml remove le position"+k);
							bitmaps.remove(k);
							break;
						}
						
					
				}
			}
	        pageIndex.write(strTableName+"index"+"//"+nameofcolumn+"//"+ pageIndex.pagenumber + ".class");

		}
		
	}
	
	
	private void deleteFromIndex1(String strTableName,String clusteringKey,Object dataincolumn) {
		File dir = new File(strTableName+"index"+"//"+clusteringKey);
		File[] files = dir.listFiles();
		for(int i=0;i<files.length;i++){
			Page pageIndex= deser(strTableName+"index"+"//"+clusteringKey,i+1);
			for(int j=0;j<pageIndex.objbits.size();j++){
				if(pageIndex.objbits.get(j).object.equals(dataincolumn)){
					pageIndex.objbits.remove(j);
					break;
				}
			}
	        pageIndex.write(strTableName+"index"+"//"+clusteringKey+"//"+ pageIndex.pagenumber + ".class");

		}
		
	}

	public void rename(String strTableName) {
		File dir = new File(strTableName);
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			name = name.substring(0, name.length() - 6);
			Page page = deser(strTableName, Integer.parseInt(name));
			File oldfile = new File(strTableName + "//" + name + ".class");
			page.pagenumber = i + 1;

			File newfile = new File(strTableName + "//" + page.pagenumber + ".class");

			if (oldfile.renameTo(newfile)) {
				System.out.println("Rename succesful");
			} else {
				System.out.println("Rename failed");
			}

		}
	}

	public Iterator selectFromTable(SQLTerm[] arrSQLTerms, String[] strarrOperators)
			throws DBAppException, IOException {
		Object[][] fortypes=new Object[arrSQLTerms.length][2];
		boolean operatorvalid=true;
		String[] tablename=new String[arrSQLTerms.length];
		Hashtable htblColNum = new Hashtable();

		for (int i = 0; i < arrSQLTerms.length; i++) {
			SQLTerm sql=arrSQLTerms[i];
			tablename[i]=sql._strTableName;
			fortypes[i][0]=sql._strColumnName;
			fortypes[i][1]=sql._objValue;
			String operator=sql._strOperator;
			switch(operator) {
			case "=":
			case ">":
			case "<":
			case ">=":
			case "<=":
			case "!=":break;
			default: operatorvalid=false;break;
			}
			

		}
		boolean strarrOperatorsValid=true;
		
		for (int i = 0; i < strarrOperators.length; i++) {
			String strop= strarrOperators[0];
			switch(strop) {
			case "AND":
			case "OR":
			case "XOR":break;
			default:strarrOperatorsValid=false;break;
			}
			
		}
		String tablenameoffirst=tablename[0];
		boolean sametable=true;
		for (int i = 1; i < tablename.length; i++) {
			if(!tablename[i].equalsIgnoreCase(tablenameoffirst)) {
				sametable=false;
				break;
			}
		}
		boolean correct =true;
		for(int i=0;i<arrSQLTerms.length;i++) {
			String nameofcolumn=(String)fortypes[i][0];
			Object dataincolumn=fortypes[i][1];
			ArrayList<String> array = FromCSV(this.file.getPath(), tablenameoffirst);
			for (int j = 0; j < array.size(); j++) {
				String[] temp = array.get(j).split(",");
				System.out.println("SELECT TRYING!!!!! "+'\n'+tablenameoffirst+":"+temp[0]+":"+
				
						tablenameoffirst.equalsIgnoreCase(temp[0])+'\n'+nameofcolumn+":"+temp[1]+":"+
						nameofcolumn.equalsIgnoreCase(temp[1]));
				if (tablenameoffirst.equalsIgnoreCase(temp[0])&&nameofcolumn.equalsIgnoreCase(temp[1])) {
					if (!dataincolumn.getClass().toString().equalsIgnoreCase("class " + temp[2])) {
						System.out.println("Wrong data type inserted in :" + nameofcolumn + " column");
						correct = false;

					}
					else {
						htblColNum.put(nameofcolumn,j);
					}
		}
			}}
		if(!sametable) {
			System.out.println("can't merge different tables ");
			return null;
		}
		
		if(!correct) {
			System.out.println("Types of data not right");
			return null;
		}
		if(!strarrOperatorsValid) {
			System.out.println("Only AND OR XOR operators are valid between the SQLTerms");
			return null;
		}
		if(!operatorvalid) {
			System.out.println("Only =,!=,>=,<=,>,< operators are valid in the SQLTerm");
			return null;
		}
		File file=new File(tablenameoffirst+"index");
		File[] files=file.listFiles();
		Vector<Row> needed=new Vector<Row>();
		Vector<Vector<Bitmap>> bmvector=new Vector<Vector<Bitmap>>();
		for (int i = 0; i < arrSQLTerms.length; i++) {
			SQLTerm sql=arrSQLTerms[i];
			String colname=sql._strColumnName;
			int colnumber=(int) htblColNum.get(colname);

			Object value=sql._objValue;
			String oper=sql._strOperator;
			boolean indexexists=false;
		for(int j=0;j<files.length;j++) {
			if(files[j].getName().toLowerCase().equalsIgnoreCase(colname)) {
				indexexists=true;
				break;
			}
		}
		
		if(indexexists) {
			File file2=new File(tablenameoffirst+"index"+"//"+colname);
			File[] files2=file2.listFiles();
			boolean found=false;
			for (int j = 0; j < files2.length && !found; j++) {
				Page p=deser(tablenameoffirst+"index"+"//"+colname, j+1);
				Vector<ObjBit> obvec=p.objbits;
				//lesa el operators nfsohom mst3mlthomsh
				for (int k = 0; k < obvec.size(); k++) {
					ObjBit ob=obvec.get(k);
					
					if(oper.equalsIgnoreCase("=")) {
						if(ob.object.toString().equalsIgnoreCase(value.toString())) {
							bmvector.add(ob.bmvector);
							found=true;
							break;
						}
						else {
							bmvector.add(setzero(ob.bmvector));
							break;
						}
					}
					else {

						if(oper.equalsIgnoreCase("<")) {
							if((double)ob.object<(double)value) {
								bmvector.add(ob.bmvector);
								found=true;
								break;
							}
							else {
								bmvector.add(setzero(ob.bmvector));
								break;

							}
						}
						else {
							if(oper.equalsIgnoreCase(">")) {
								if((double)ob.object>(double)value) {
									bmvector.add(ob.bmvector);
									found=true;
									break;
								}
								else {
									bmvector.add(setzero(ob.bmvector));
									break;

								}
							}	
							else {
								if(oper.equalsIgnoreCase("<=")) {
									if((double)ob.object<=(double)value) {
										bmvector.add(ob.bmvector);
										found=true;
										break;
									}
									else {
										bmvector.add(setzero(ob.bmvector));
										break;

									}
								}	
								else {
									if(oper.equalsIgnoreCase(">=")) {
										if((double)ob.object>=(double)value) {
											bmvector.add(ob.bmvector);
											found=true;
											break;
										}
										else {
											bmvector.add(setzero(ob.bmvector));
											break;

										}
									}	
									else {
										
										if(oper.equalsIgnoreCase("!=")) {
											if(!ob.object.toString().equalsIgnoreCase(value.toString())) {
												bmvector.add(ob.bmvector);
												found=true;
												break;
											}
											else {
												bmvector.add(setzero(ob.bmvector));
												break;
											}
										}
										
									}
								}
							}
						}
						
					
					}
				
				}
			
			}
		}
		else {
			
			File file3=new File(tablenameoffirst);
			File[] files3=file3.listFiles();
			Vector <Bitmap> bmv=new Vector<Bitmap>();
			for (int j = 0; j < files3.length; j++) {
				Page p1=deser(tablenameoffirst, j+1);
				Vector<Row> rows=p1.rows;
				for (int k = 0; k < rows.size(); k++) {
				Object insidecolumn=	rows.get(0).attributes.get(colnumber);
				if(oper.equalsIgnoreCase("=")) {
					if(insidecolumn.toString().equalsIgnoreCase(value.toString())) {
						bmv.add(new Bitmap(1,j+1));
					}
					else {
						bmv.add(new Bitmap(0,j+1));

					}
				}
				else {
					if(oper.equalsIgnoreCase("<")) {
						if((double)insidecolumn<(double)value) {
							bmv.add(new Bitmap(1,j+1));

						}
						else {
							bmv.add(new Bitmap(0,j+1));

						}
					}
					else {
						if(oper.equalsIgnoreCase(">")) {
							if((double)insidecolumn>(double)value) {
								bmv.add(new Bitmap(1,j+1));

							}
							else {
								bmv.add(new Bitmap(0,j+1));

							}
						}	
						else {
							if(oper.equalsIgnoreCase("<=")) {
								if((double)insidecolumn<=(double)value) {
									bmv.add(new Bitmap(1,j+1));

								}
								else {
									bmv.add(new Bitmap(0,j+1));

								}
							}	
							else {
								if(oper.equalsIgnoreCase(">=")) {
									if((double)insidecolumn>=(double)value) {
										bmv.add(new Bitmap(1,j+1));

									}
									else {
										bmv.add(new Bitmap(0,j+1));

									}
								}
								else {
									
										if(!insidecolumn.toString().equalsIgnoreCase(value.toString())) {
											bmv.add(new Bitmap(1,j+1));
										}
										else {
											bmv.add(new Bitmap(0,j+1));

										}
									
								}
							}
						}
					}
					
				}
				}
				
				//end of for loop on the rows!
				
			}//end of the loop of the pages!
			bmvector.add(bmv);
		}
		//ArrSQLTerms and Operators <>!==<=>= ended	
		}
		//for the AND
		for (int i = 0; i < strarrOperators.length; i++) {
			if(strarrOperators[i].equalsIgnoreCase("AND")) {
				Vector<Bitmap> bitmap1=bmvector.get(i);
				Vector<Bitmap> bitmap2=bmvector.get(i+1);
				Vector<Bitmap> bitmapnew=new Vector<Bitmap>();

				for (int k = 0; k < bitmap1.size(); k++) {
					if(bitmap1.get(k).index==1&&bitmap2.get(k).index==1) {
						bitmapnew.add(new Bitmap(1,bitmap1.get(k).pagenumber));
						
					}
					else {
						bitmapnew.add(new Bitmap(0,bitmap1.get(k).pagenumber));

					}
				}
				bmvector.add(i,bitmapnew);
				bmvector.remove(bitmap1);
				bmvector.remove(bitmap2);
				String []strarrOperatorsnew=new String[strarrOperators.length-1];
				for (int k = 0; k < strarrOperators.length; k++) {
					if(i!=k) {
						strarrOperatorsnew[k]=strarrOperators[k];
					}
					else {
						k++;
						if(k!=strarrOperators.length) {
							strarrOperatorsnew[k-1]=strarrOperators[k];
						}
					}
				}
				strarrOperators=strarrOperatorsnew;

			}
		}
		//for the XOR
				for (int i = 0; i < strarrOperators.length; i++) {
					if(strarrOperators[i].equalsIgnoreCase("XOR")) {
						Vector<Bitmap> bitmap1=bmvector.get(i);
						Vector<Bitmap> bitmap2=bmvector.get(i+1);
						Vector<Bitmap> bitmapnew=new Vector<Bitmap>();

						for (int k = 0; k < bitmap1.size(); k++) {
							if(bitmap1.get(k).index!=bitmap2.get(k).index) {
								bitmapnew.add(new Bitmap(1,bitmap1.get(k).pagenumber));
								
							}
							else {
								bitmapnew.add(new Bitmap(0,bitmap1.get(k).pagenumber));

							}
						}
						bmvector.add(i,bitmapnew);
						bmvector.remove(bitmap1);
						bmvector.remove(bitmap2);
						String []strarrOperatorsnew=new String[strarrOperators.length-1];
						for (int k = 0; k < strarrOperators.length; k++) {
							if(i!=k) {
								strarrOperatorsnew[k]=strarrOperators[k];
							}
							else {
								k++;
								if(k!=strarrOperators.length) {
									strarrOperatorsnew[k-1]=strarrOperators[k];
								}
							}
						}
						strarrOperators=strarrOperatorsnew;

					}
				}
				//for the OR
				for (int i = 0; i < strarrOperators.length; i++) {
					if(strarrOperators[i].equalsIgnoreCase("XOR")) {
						Vector<Bitmap> bitmap1=bmvector.get(i);
						Vector<Bitmap> bitmap2=bmvector.get(i+1);
						Vector<Bitmap> bitmapnew=new Vector<Bitmap>();

						for (int k = 0; k < bitmap1.size(); k++) {
							if(bitmap1.get(k).index==1 ||bitmap2.get(k).index==1) {
								bitmapnew.add(new Bitmap(1,bitmap1.get(k).pagenumber));
								
							}
							else {
								bitmapnew.add(new Bitmap(0,bitmap1.get(k).pagenumber));

							}
						}
						bmvector.add(i,bitmapnew);
						bmvector.remove(bitmap1);
						bmvector.remove(bitmap2);
						
						String []strarrOperatorsnew=new String[strarrOperators.length-1];
						for (int k = 0; k < strarrOperators.length; k++) {
							if(i!=k) {
								strarrOperatorsnew[k]=strarrOperators[k];
							}
							else {
								k++;
								if(k!=strarrOperators.length) {
									strarrOperatorsnew[k-1]=strarrOperators[k];
								}
							}
						}
						strarrOperators=strarrOperatorsnew;

					}
				}
				//kda 5lst and xor or
				ArrayList<String> result=new ArrayList<String>();
				Vector<Bitmap> resultvector=bmvector.get(0);
				int counter=0;
				for (int i = 0; i < files.length; i++) {
					Page px=deser(tablenameoffirst,i+1);
					Vector <Row> rows=px.rows;
					
					for (int j = 0; j < rows.size(); j++) {
						
						if(resultvector.get(counter).index==1) {
							String r1="";
							for(int q=0;q<rows.get(j).attributes.size();q++) {
								r1=r1+rows.get(j).attributes.get(q).toString()+"| ";
							}
							result.add(r1);
						}
					
					
					counter++;
					}
				}
				
				Iterator iterator=result.iterator();
				return iterator;
		
		
		
		
		
		
		
		
		
	}

	public static void main(String[] args) throws IOException, DBAppException {
		/*
		 * File file = new File("C://Users//lenovo//Desktop//ab.csv"); FileWriter writer
		 * = new FileWriter(file); writer.write("hello,world"); writer.write('\n');
		 * writer.write("sanks,you"); writer.close();
		 */
		DBApp a = new DBApp();
		// a.init();
		Hashtable htblColNameType = new Hashtable();
		htblColNameType.put("id", "java.lang.Integer");
		htblColNameType.put("name", "java.lang.String");
		htblColNameType.put("gpa", "java.lang.Double");
		a.createTable("CityShop", "id", htblColNameType);
		// a.createTable("lala","id" , htblColNameType);
		Hashtable htblColNameValue = new Hashtable();
		htblColNameValue.put("id", 4);
		htblColNameValue.put("name", "Shahd");
		htblColNameValue.put("gpa", 2.0);

		// Hashtable htblColNameType2 = new Hashtable();

		Hashtable htblColNameValue2 = new Hashtable();
		htblColNameValue2.put("id", 3);
		htblColNameValue2.put("name", "Kariman");
		htblColNameValue2.put("gpa", 1.8);
		Hashtable htblColNameValue3 = new Hashtable();
		htblColNameValue3.put("id", 1);
		htblColNameValue3.put("name", "Islam");
		htblColNameValue3.put("gpa", 1.8);
		Hashtable htblColNameValue4 = new Hashtable();
		htblColNameValue4.put("id", 5);
		htblColNameValue4.put("name", "Amr");
		htblColNameValue4.put("gpa", 1.8);
		Hashtable htblColNameValue5 = new Hashtable();
		htblColNameValue5.put("id", 2);
		htblColNameValue5.put("name", "Ya Farah Ya Yasser");
		htblColNameValue5.put("gpa", 2.0);
		a.writer.close();
		// Integer i = new Integer(1);
		// System.out.println(i.getClass().toString()
		// .equalsIgnoreCase("class " + "java.lang.Integer"));
		File dir = new File("CityShop");
		File[] files = dir.listFiles();

/*		for (int q = 0; q < files.length; q++) {
			System.out.println(files[q]);
		}*/
	a.insertIntoTable("CityShop", htblColNameValue);
	a.insertIntoTable("CityShop", htblColNameValue2);
	a.insertIntoTable("CityShop", htblColNameValue3);
	a.insertIntoTable("CityShop", htblColNameValue4);
	a.insertIntoTable("CityShop", htblColNameValue5);
//48
//	Hashtable htblColNameValue6 = new Hashtable();

		//htblColNameValue6.put("name", "Islam");
		/*Hashtable htblColNameValue5 = new Hashtable();
		htblColNameValue5.put("id", 2);*/
		// a.deleteFromTable("CityShop", htblColNameValue6);
		//a.updateTable("CityShop", "1", htblColNameValue6);
	//	a.createBitmapIndex("CityShop", "id");
		a.createBitmapIndex("CityShop", "gpa");
		Hashtable htblColNameValue6 = new Hashtable();
		htblColNameValue6.put("id", 6);
		htblColNameValue6.put("name", "ahmed");
		htblColNameValue6.put("gpa", 2.0);
		//a.insertIntoTable("CityShop", htblColNameValue);
		
		
		//a.insertIntoTable("CityShop", htblColNameValue3);
		Page object1;

		// Deserialization
		String pathName = "CityShop//1.class";

		File test = new File(pathName);

		if (!test.exists())
			System.out.println("The file " + pathName + " Does Not exist.");
		else {
			System.out.println("The file " + pathName + " exist.");
			try {
				// Reading the object from a file
				// new FileInputStream("sa");

				// File ayesm=new File(System.getProperty("CityShop"));
				FileInputStream file2 = new FileInputStream(new File("CityShop//1.class"));
				ObjectInputStream in = new ObjectInputStream(file2);

				// Method for deserialization of object

				object1 = (Page) in.readObject();
				System.out.println(object1.pagenumber);
				for (int h = 0; h < object1.rows.size(); h++) {
					Row row = object1.rows.get(h);
					System.out.println("Rows Size:" + object1.rows.size());
					System.out.println(object1.pagenumber);
					System.out
							.println(row.attributes.get(0) + " " + row.attributes.get(1) + " " + row.attributes.get(2)+ " "+row.attributes.get(3));
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

		// ArrayList<String> x=FromCSV(this.file.getPath(),"CityShop");
		/*
		 * for (int i = 0; i < x.size(); i++) { System.out.println(x.get(i)); }
		 */
		// File f=new File("CityShop");
		// try{
		// if(f.mkdir()){
		// System.out.println("Created");}
		// else{
		// System.out.println("not");
		// }
		//
		// }
		// catch(Exception e){
		// System.out.println("error");
		// }
		// String ab ="ghkj";
		// String c="jkjkj";
		// System.out.println(ab.getClass()==c.getClass());

	}
}
