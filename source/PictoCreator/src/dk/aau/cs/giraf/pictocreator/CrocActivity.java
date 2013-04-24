package dk.aau.cs.giraf.pictocreator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import dk.aau.cs.giraf.oasis.lib.*;
import dk.aau.cs.giraf.oasis.lib.controllers.*;
import dk.aau.cs.giraf.oasis.lib.models.*;
import dk.aau.cs.giraf.pictocreator.audiorecorder.*;
import dk.aau.cs.giraf.pictogram.*;


public class CrocActivity extends Activity {

	private final static String TAG = "CrocMain";
    private static Intent girafIntent;

	private FragmentManager fragManager;
	private FragmentTransaction fragTrans;
	private ToggleButton fragSwitch;
	private ImageButton dialogButton;
	private CamFragment camFragment;
	private Dummy dummy;
	private RecordDialogFragment recordDialog;

    // public static final String GUARDIANID = "currentGuardianID";
    // public static final String CHILDID = "currentChildID";
    // public static final String APP_PACKAGENAME = "appPackageName";
    // public static final String APP_ACTIVITYNAME = "appActivityName";
    // public static final String APP_COLOR = "appBackgroundColor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        fragSwitch = (ToggleButton)findViewById(R.id.toggleFragments);

        fragManager = getFragmentManager();
        fragTrans = fragManager.beginTransaction();

        dummy = new Dummy();
        fragTrans.add(R.id.fragmentContainer, dummy);
        fragTrans.commit();

        dialogButton = (ImageButton)findViewById(R.id.start_dialog_button);
        dialogButton.setOnClickListener(showRecorderClick);

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
    		dummy = new Dummy();
    		fragTrans = getFragmentManager().beginTransaction();
    		fragTrans.replace(R.id.fragmentContainer, dummy);
    		fragTrans.commit();
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

}
