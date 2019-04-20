package return_to_sleep;

import java.io.Serializable;
import java.util.Vector;

public class ObjBit implements java.io.Serializable {
Object object;
Vector<Bitmap> bmvector;
public ObjBit(Object object,Vector<Bitmap> bmvector) {
	this.object=object;
	this.bmvector=bmvector;
}
}
