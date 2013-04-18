package com.example.pictotester;

import java.util.List;
import dk.aau.cs.giraf.pictogram.*;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;

import java.util.ArrayList;
/**
 *
 * @author Croc
 *
 */
public class MainActivity extends Activity {
    private final static String TAG = "Tester";
    private Pictogram preview;
    private ArrayList<String> tags;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                                               android.view.ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams params2 = new LayoutParams(100 , 100);

        LinearLayout layout = new LinearLayout(this);
        addContentView(layout, params);


        TextView pap = new TextView(this);
        pap.setText("Hey!");

        List<Pictogram> pictograms = PictoFactory.INSTANCE.getPictogramsByTag(this, "Dog");
        if(pictograms != null){
            preview = pictograms.get(0);
            // preview.renderImage();
            // layout.addView(preview, params2);
            showSaveDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void showSaveDialog(){
        SaveDialogFragment dialog = new SaveDialogFragment();
        dialog.setTags(tags);
        dialog.setPreview(preview);
        dialog.show(getFragmentManager(), "SaveDialog");
    }
}
