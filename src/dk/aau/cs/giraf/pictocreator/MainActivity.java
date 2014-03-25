package dk.aau.cs.giraf.pictocreator;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.pictocreator.audiorecorder.RecordDialogFragment;
import dk.aau.cs.giraf.pictocreator.cam.CamFragment;
import dk.aau.cs.giraf.pictocreator.canvas.BackgroundSingleton;
import dk.aau.cs.giraf.pictocreator.canvas.DrawFragment;
import dk.aau.cs.giraf.pictocreator.management.HelpDialogFragment;
import dk.aau.cs.giraf.pictocreator.management.SaveDialogFragment;

/**
 * Main class for the Croc app
 *
 * @author Croc
 *
 */
public class MainActivity extends Activity {

    private final static String TAG = "CrocMain";
    private final static String actionResult = "dk.aau.cs.giraf.PICTOGRAM";
    private Intent mainIntent;

    private FragmentManager fragManager;
    private FragmentTransaction fragTrans;
    private ToggleButton camSwitch, canvasSwitch;
    private ImageButton dialogButton;
    private CamFragment camFragment;
    private DrawFragment drawFragment;
    private RecordDialogFragment recordDialog;
    private GButton recordDialogButton, saveDialogButton, helpDialogButton;

    private HelpDialogFragment helpDialog;
    private RelativeLayout topLayout;
    private SaveDialogFragment saveDialog;

    private StoragePictogram storagePictogram;
    private View decor;
    private boolean service;

    // public static final String GUARDIANID = "currentGuardianID";
    // public static final String CHILDID = "currentChildID";
    // public static final String APP_PACKAGENAME = "appPackageName";
    // public static final String APP_ACTIVITYNAME = "appActivityName";
    // public static final String APP_COLOR = "appBackgroundColor";

    private int colorDarken(int color, float darkening){
        float[] tempHSV = {0.0f,0.0f,0.0f};
        Color.colorToHSV(color, tempHSV);

        tempHSV[2] -= (darkening % tempHSV[2]);
        return Color.HSVToColor(tempHSV);
    }

    private GradientDrawable getGradientColor(int color, float defaultDarken){
        int[] colors = {colorDarken(color, 0.2f + defaultDarken), colorDarken(color, defaultDarken)};

        return  new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
    }

    private Drawable fragmentBackground;

    /**
     * Function called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mainIntent = getIntent();

        createStoragePictogram();

        decor = getWindow().getDecorView();

        topLayout = (RelativeLayout) findViewById(R.id.topLayer);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int color = extras.getInt("appBackgroundColor");
            BackgroundSingleton.getInstance().background = getGradientColor(color,0.0f);
            topLayout.setBackgroundDrawable(getGradientColor(color, 0.3f));

            decor.setBackgroundDrawable(getGradientColor(color, 0.0f));
        }
        else{
            decor.setBackgroundResource(R.drawable.fragment_background);
            topLayout.setBackgroundResource(R.drawable.head_background);
        }

        assignUIObjects();

        //Check for camera last
        //If no cam found, disable the fragment switch button
        if(!checkForCamera(this)) {
            camSwitch.setEnabled(false);
        }
    }

    private void assignUIObjects() {
        canvasSwitch = (ToggleButton)findViewById(R.id.toggleCanvas);
        camSwitch = (ToggleButton)findViewById(R.id.toggleCam);

        fragManager = getFragmentManager();
        fragTrans = fragManager.beginTransaction();

        drawFragment = new DrawFragment();
        fragTrans.add(R.id.fragmentContainer, drawFragment);
        fragTrans.commit();
        canvasSwitch.setChecked(true);
        canvasSwitch.setClickable(false);

        recordDialogButton = (GButton)findViewById(R.id.start_record_dialog_button);
        recordDialogButton.setOnClickListener(showRecorderClick);

        saveDialogButton = (GButton)findViewById(R.id.start_save_dialog_button);
        saveDialogButton.setOnClickListener(showLabelMakerClick);

        helpDialogButton = (GButton)findViewById(R.id.help_button);
        helpDialogButton.setOnClickListener(showHelpClick);
    }

    /**
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * On click function for switching between {@link CamFragment} and {@link DrawFragment}.
     * @param view The view which is clicked.
     */
    public void switchFragments(View view) {

        if(camSwitch.isPressed()) {
        	camSwitch.setClickable(false);
        	canvasSwitch.setChecked(false);
        	canvasSwitch.setClickable(true);
            camFragment = new CamFragment();
            fragTrans = getFragmentManager().beginTransaction();
            fragTrans.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            fragTrans.replace(R.id.fragmentContainer, camFragment);
            fragTrans.commit();
        }
        
        if(canvasSwitch.isPressed()) {
        	canvasSwitch.setClickable(false);
        	camSwitch.setChecked(false);
        	camSwitch.setClickable(true);
            drawFragment = new DrawFragment();
            fragTrans = getFragmentManager().beginTransaction();
            fragTrans.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            fragTrans.replace(R.id.fragmentContainer, drawFragment);
            fragTrans.commit();
        }
    }

    /**
     *
     */
    @Override
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

        if(mainIntent != null){
            String action = mainIntent.getAction();

            if(action != "dk.aau.cs.giraf.CREATEPICTOGRAM"){
                author = mainIntent.getLongExtra("currentGuardianID", 0);
                this.service = false;
            } else {
                author = mainIntent.getLongExtra("author", 0);
                this.service = true;
            }

            storagePictogram.setAuthor(author);
        }

    }

    /**
     * Function for checking whether a camera is available at the device
     * @param context The context in which the function is called
     * @return True if a camera is found on the device, false otherwise
     */
    private boolean checkForCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        } else {
            Log.d(TAG, "No camera found on device");
            return false;
        }
    }

    /**
     * On click listener to show the {@link RecordDialogFragment}
     */
    private final OnClickListener showRecorderClick = new OnClickListener() {
        @Override
		public void onClick(View view) {
            recordDialog = new RecordDialogFragment();
            recordDialog.show(getFragmentManager(), TAG);
        }
    };

    /**
     * On click listener to show the {@link SaveDialogFragment}
     */
    private final OnClickListener showLabelMakerClick = new OnClickListener() {
        @Override
		public void onClick(View view) {
            saveDialog = new SaveDialogFragment();

            drawFragment.saveBitmap();

            saveDialog.setService(service);
            saveDialog.setPictogram(storagePictogram);
            saveDialog.show(getFragmentManager(), TAG);
        }
    };
        
    private final OnClickListener showHelpClick = new OnClickListener() {
    	@Override
		public void onClick(View view) {
    		helpDialog = new HelpDialogFragment();
    		helpDialog.show(getFragmentManager(), TAG);
    	}
    };

    public static String getActionResult(){
        return actionResult;
    }

    public boolean getService(){
        return this.service;
    }
    
    public void doExit(View v){
    	this.finish();
    }
}
