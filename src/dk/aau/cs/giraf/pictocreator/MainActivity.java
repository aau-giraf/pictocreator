package dk.aau.cs.giraf.pictocreator;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GDialogMessage;
import dk.aau.cs.giraf.oasis.lib.controllers.PictogramController;
import dk.aau.cs.giraf.oasis.lib.models.Pictogram;
import dk.aau.cs.giraf.pictocreator.audiorecorder.AudioHandler;
import dk.aau.cs.giraf.pictocreator.audiorecorder.RecordDialogFragment;
import dk.aau.cs.giraf.pictocreator.cam.CamFragment;
import dk.aau.cs.giraf.pictocreator.canvas.DrawFragment;
import dk.aau.cs.giraf.pictocreator.canvas.DrawStackSingleton;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;
import dk.aau.cs.giraf.pictocreator.management.ByteConverter;
import dk.aau.cs.giraf.pictocreator.management.HelpDialogFragment;
import dk.aau.cs.giraf.pictocreator.management.SaveDialogFragment;

/**
 * Main class for the Croc app
 * @author Croc
 *
 */
public class MainActivity extends Activity implements CamFragment.PictureTakenListener{
    private final static String TAG = "MainActivity";
    private final static String actionResult = "dk.aau.cs.giraf.PICTOGRAM";
    private Intent mainIntent;

    private FragmentManager fragManager;
    private FragmentTransaction fragTrans;
    private DrawFragment drawFragment;
    private HelpDialogFragment helpDialog;
    private RelativeLayout topLayout;
    private SaveDialogFragment saveDialog;

    private GDialogMessage clearDialog;
    private GButton clearButton, saveDialogButton, loadDialogButton, helpDialogButton;

    private StoragePictogram storagePictogram;
    private View decor;
    private boolean service;
    private int author;

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
        decor.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SOLID));

        topLayout = (RelativeLayout) findViewById(R.id.topLayer);
        topLayout.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.GRADIENT));

        fragManager = getFragmentManager();
        fragTrans = fragManager.beginTransaction();

        drawFragment = new DrawFragment();

        fragTrans.add(R.id.fragmentContainer, drawFragment);
        fragTrans.commit();

        clearButton = (GButton)findViewById(R.id.clearButton);
        clearButton.setOnClickListener(onClearButtonClick);

        saveDialogButton = (GButton)findViewById(R.id.start_save_dialog_button);
        saveDialogButton.setOnClickListener(showLabelMakerClick);

        loadDialogButton = (GButton)findViewById(R.id.start_load_dialog_button);
        loadDialogButton.setOnClickListener(showPictosearchClick);

        helpDialogButton = (GButton)findViewById(R.id.help_button);
        helpDialogButton.setOnClickListener(showHelpClick);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
	public void onWindowFocusChanged(boolean hasFocus) {
        if(hasFocus) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    /**
     * Method which creates the pictogram used to store the image on screen.
     */
    private void createStoragePictogram (){
        storagePictogram = new StoragePictogram(this);

        if(mainIntent != null){
            String action = mainIntent.getAction();

            if(action != "dk.aau.cs.giraf.CREATEPICTOGRAM"){
                author = mainIntent.getIntExtra("currentGuardianID", 0);
                this.service = false;
            } else {
                author = mainIntent.getIntExtra("author", 0);
                this.service = true;
            }

            storagePictogram.setAuthor(author);
        }
    }

    /**
     * On click listener to show the {@link SaveDialogFragment}
     */
    private final OnClickListener showLabelMakerClick = new OnClickListener() {
        @Override
		public void onClick(View view) {
            drawFragment.DeselectEntity();

            saveDialog = new SaveDialogFragment();
            saveDialog.setService(service);
            saveDialog.setPictogram(storagePictogram);
            saveDialog.setPreview(getBitmap());
            saveDialog.show(getFragmentManager(), TAG);
        }
    };

    private final OnClickListener  onClearButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG,"Clear Button clicked");
            clearDialog = new GDialogMessage(v.getContext(), "Ryd tegnebræt?", onAcceptClearCanvasClick);
            clearDialog.show();
        }
    };

    private final OnClickListener onAcceptClearCanvasClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            AudioHandler.resetSound();

            if(drawFragment.drawView != null && DrawStackSingleton.getInstance().mySavedData != null){
                DrawStackSingleton.getInstance().mySavedData.clear();
                drawFragment.drawView.invalidate();

                //Neeeded, as selectionhandler would have a deleted item selected otherwise
                drawFragment.DeselectEntity();
            }
        }
    };

    /**
     * On click listener that opens Pictosearch
     */
    private final OnClickListener showPictosearchClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            drawFragment.DeselectEntity();
            callPictosearch();
        }
    };
        
    private final OnClickListener showHelpClick = new OnClickListener() {
    	@Override
		public void onClick(View view) {
    		helpDialog = new HelpDialogFragment();
    		helpDialog.show(getFragmentManager(), TAG);
    	}
    };

    /**
     * Opens pictosearch application, so pictograms can be loaded into the application.
     */
    private void callPictosearch(){
        Intent intent = new Intent();

        try{
            intent.setComponent(new ComponentName( "dk.aau.cs.giraf.pictosearch",  "dk.aau.cs.giraf.pictosearch.PictoAdminMain"));
            intent.putExtra("currentGuardianID", author);
            intent.putExtra("purpose", "single");

            startActivityForResult(intent, RESULT_FIRST_USER);
        } catch (Exception e){
            Toast.makeText( this, "Pictosearch er ikke installeret.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Pictosearch is not installed: " + e.getMessage());
        }
    }

    /**
     * This method gets the pictogram that are returned by pictosearch.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            loadPictogram(data);
        }
    }

    /**
     * Loads the pictogram into the canvas by getting it from the database.
     * @param data The data returned from pictosearch.
     */
    private void loadPictogram(Intent data){
        int pictogramID;

        try{
            pictogramID = data.getExtras().getIntArray("checkoutIds")[0];
        }
        catch (ArrayIndexOutOfBoundsException e){
            Log.e(TAG, e.getMessage());
            return;
        }
        catch(NullPointerException e){
            Log.e(TAG, e.getMessage());
            return;
        }

        PictogramController pictogramController = new PictogramController(this);
        Pictogram pictogram = pictogramController.getPictogramById(pictogramID);

        /*if the pictogram has no drawstack, just load the bitmap
          else we load drawstack*/
        if(pictogram.getEditableImage() == null){
            Bitmap bitmap = pictogram.getImage();
            loadPicture(bitmap);
        }
        else{
            try{
                DrawStackSingleton.getInstance().mySavedData = (EntityGroup)ByteConverter.deserialize(pictogram.getEditableImage());
            }
            catch (IOException e){
                Log.e(TAG, e.getMessage());
            }
            catch (ClassNotFoundException e){
                Log.e(TAG, e.getMessage());
            }
        }
        try{
            if(pictogram.getAudioFile(this) != null && pictogram.getAudioFile(this).length() > 0)
                AudioHandler.setFinalPath(pictogram.getAudioFile(this).getPath());
        }
        catch(IOException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onPictureTaken(File picture){
        loadPicture(BitmapFactory.decodeFile(picture.getPath()));
    }

    /**
     * Loads the bitmaps into the canvas in correct size
     * @param bitmap The bitmap to be loaded
     */
    private void loadPicture(Bitmap bitmap){
        int sizeHeightPercentage = (int)(((double)(this.getBitmap().getHeight())/(double)(bitmap).getHeight())*100.0);
        int sizeWidthPercentage = (int)(((double)(this.getBitmap().getWidth())/(double)(bitmap).getWidth())*100.0);

        BitmapEntity tempEntity = new BitmapEntity(bitmap, Math.max(sizeHeightPercentage, sizeWidthPercentage));
        tempEntity.setCenter(this.drawFragment.drawView.getMeasuredWidth()/2, this.drawFragment.drawView.getMeasuredHeight()/2);
        DrawStackSingleton.getInstance().mySavedData.addEntity(tempEntity);
        this.drawFragment.drawView.invalidate();
    }

    private Bitmap getBitmap(){
        return this.drawFragment.drawView.getFlattenedBitmap(Bitmap.Config.ARGB_8888);
    }

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
