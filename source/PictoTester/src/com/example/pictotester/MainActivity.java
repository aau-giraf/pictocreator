package com.example.pictotester;
import java.util.List;

import dk.aau.cs.giraf.pictogram.*;
import android.graphics.BitmapFactory;
import android.os.Bundle;
<<<<<<< HEAD
import android.os.Environment;
import android.widget.Toast;
import android.app.Dialog;
=======
>>>>>>> 81be0b91cb6192252dc11aeddaa55874142c8e9b
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

/**
 *
 * @author Croc
 *
 */
public class MainActivity extends Activity {
    private final static String TAG = "Tester";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT , android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams params2 = new LayoutParams(100 , 100);

        LinearLayout grid = new LinearLayout(this);
        addContentView(grid, params);

        TextView pap = new TextView(this);
        pap.setText("Hey!");

        List<Pictogram> pictograms = PictoFactory.INSTANCE.getPictogramsByTag(this, "Dog");
        if(pictograms != null){
            Pictogram p = pictograms.get(0);
            p.renderImage();
            grid.addView(p, params2);
            String path = p.getImagePath();

            SaveDialogFragment save = new SaveDialogFragment();
            save.setPreview(path);
            save.show(getFragmentManager(), TAG);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
