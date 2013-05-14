package dk.aau.cs.giraf.pictocreator;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import dk.aau.cs.giraf.pictocreator.audiorecorder.*;
import dk.aau.cs.giraf.pictocreator.cam.CamFragment;
import dk.aau.cs.giraf.pictocreator.canvas.DrawFragment;
import dk.aau.cs.giraf.pictocreator.management.*;


public class CrocActivity extends Activity {

    private final static String TAG = "CrocMain";
    private Intent girafIntent;

    private FragmentManager fragManager;
    private FragmentTransaction fragTrans;
    private ToggleButton fragSwitch;
    private ImageButton dialogButton;
    private CamFragment camFragment;
    private DrawFragment drawFragment;
    private RecordDialogFragment recordDialog;
    private ImageButton recordDialogButton;
    private ImageButton saveDialogButton;
    private SaveDialogFragment saveDialog;
    private StoragePictogram storagePictogram;
    private View decor;

    // public static final String GUARDIANID = "currentGuardianID";
    // public static final String CHILDID = "currentChildID";
    // public static final String APP_PACKAGENAME = "appPackageName";
    // public static final String APP_ACTIVITYNAME = "appActivityName";
    // public static final String APP_COLOR = "appBackgroundColor";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        girafIntent = getIntent();
        createStoragePictogram();

        decor = getWindow().getDecorView();

        fragSwitch = (ToggleButton)findViewById(R.id.toggleFragments);

        fragManager = getFragmentManager();
        fragTrans = fragManager.beginTransaction();

        drawFragment = new DrawFragment();
        fragTrans.add(R.id.fragmentContainer, drawFragment);
        fragTrans.commit();

        recordDialogButton = (ImageButton)findViewById(R.id.start_record_dialog_button);
        recordDialogButton.setOnClickListener(showRecorderClick);

        saveDialogButton = (ImageButton)findViewById(R.id.start_save_dialog_button);
        saveDialogButton.setOnClickListener(showLabelMakerClick);

        //Check for camera last
        //If no cam found, disable the fragment switch button
        if(!checkForCamera(this)) {
            fragSwitch.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    public void switchFragments(View view) {

        if(fragSwitch.isChecked()) {
            camFragment = new CamFragment();
            fragTrans = getFragmentManager().beginTransaction();
            fragTrans.replace(R.id.fragmentContainer, camFragment);
            fragTrans.commit();
        }
        else if(!fragSwitch.isChecked()) {
            drawFragment = new DrawFragment();
            fragTrans = getFragmentManager().beginTransaction();
            fragTrans.replace(R.id.fragmentContainer, drawFragment);
            fragTrans.commit();
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    /**
     * <p> Method which creates the pictogram used to store the image on screen.
     *
     *
     */
    private void createStoragePictogram (){
        long author;
        storagePictogram = new StoragePictogram(this);

        if(girafIntent != null){
            author = girafIntent.getLongExtra("currentGuardianID", 0);
            storagePictogram.setAuthor(author);
        }
    }

    /**
     *
     * @param context
     * @return
     */
    private boolean checkForCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            Log.d(TAG, "No camera found on device");
            return false;
        }
    }

    private final OnClickListener showRecorderClick = new OnClickListener() {
            public void onClick(View view) {
                recordDialog = new RecordDialogFragment();
                recordDialog.show(getFragmentManager(), TAG);
            }
        };

    private final OnClickListener showLabelMakerClick = new OnClickListener() {
            public void onClick(View view) {
                saveDialog = new SaveDialogFragment();

                saveDialog.setPictogram(storagePictogram);

                saveDialog.show(getFragmentManager(), TAG);
            }
        };
}
