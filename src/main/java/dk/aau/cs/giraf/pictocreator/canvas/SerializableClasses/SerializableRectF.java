package dk.aau.cs.giraf.pictocreator.canvas.SerializableClasses;

import android.graphics.RectF;

import java.io.Serializable;

/**
 * A serializable RectF which can be saved in the database drawStack (ByteArray).
 * @author SW608F14
 */
public class SerializableRectF extends RectF implements Serializable {
    public SerializableRectF(float arg1, float arg2, float arg3, float arg4){
        super(arg1, arg2, arg3, arg4);
    }
}
