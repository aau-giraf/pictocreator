package dk.aau.cs.giraf.pictocreator.canvas.SerializeClasses;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * A serializable paint which can be saved in the database drawStack (ByteArray).
 * @author SW608F14
 */
public class SerializePaint extends Paint implements Serializable{

    public SerializePaint(SerializePaint p){
     super(p);
    }

    public SerializePaint()
    {
        super();
    }
}
