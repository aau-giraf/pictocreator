package dk.aau.cs.giraf.pictocreator.management;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.ArrayList;

import dk.aau.cs.giraf.pictocreator.canvas.Entity;

/**
 * Created by Martin on 24-03-2015.
 */
public final class Helper {

    public static int convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int)px;
    }

    public static void showSoftKeyBoard(EditText et, Context context)
    {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {
            imm.showSoftInput(et,0);
        }
    }

    public static ArrayList<Entity> poppedEntities = new ArrayList<Entity>();
}
