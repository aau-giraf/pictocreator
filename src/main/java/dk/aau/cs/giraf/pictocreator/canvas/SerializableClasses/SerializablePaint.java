package dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * A serializable paint which can be saved in the database drawStack (ByteArray).
 * @author SW608F14
 */
public class SerializablePaint extends Paint implements Serializable{

    public SerializablePaint(SerializablePaint p){
     super(p);
    }

    public SerializablePaint()
    {
        super();
    }
}
