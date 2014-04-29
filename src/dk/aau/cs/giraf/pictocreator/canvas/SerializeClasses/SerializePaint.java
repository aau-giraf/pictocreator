package dk.aau.cs.giraf.pictocreator.canvas.SerializeClasses;

import android.graphics.Paint;

import java.io.Serializable;

/**
 * Created by Praetorian on 28-04-14.
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
