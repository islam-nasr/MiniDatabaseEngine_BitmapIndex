package return_to_sleep;

import java.io.Serializable;

public class Bitmap implements java.io.Serializable{
int index;
int pagenumber;

public Bitmap(int index,int pagenumber) {
	this.index=index;
	this.pagenumber=pagenumber;
}
}
