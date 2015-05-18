package dk.aau.cs.giraf.pictocreator;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.print.PrintHelper;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.activity.GirafActivity;
import dk.aau.cs.giraf.dblib.models.Pictogram;
import dk.aau.cs.giraf.dblib.models.Profile;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.gui.GirafConfirmDialog;
import dk.aau.cs.giraf.gui.GirafDialog;
import dk.aau.cs.giraf.gui.GirafProfileSelectorDialog;
import dk.aau.cs.giraf.pictocreator.audiorecorder.AudioHandler;
import dk.aau.cs.giraf.pictocreator.cam.CamFragment;
import dk.aau.cs.giraf.pictocreator.canvas.DrawFragment;
import dk.aau.cs.giraf.pictocreator.canvas.DrawStackSingleton;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;
import dk.aau.cs.giraf.pictocreator.management.ByteConverter;
import dk.aau.cs.giraf.pictocreator.management.Helper;
import dk.aau.cs.giraf.pictocreator.management.SaveDialogFragment;

/**
 * Main class for the Croc app
 *
 * @author Croc
 */
public class MainActivity extends GirafActivity implements CamFragment.PictureTakenListener,
        GirafConfirmDialog.Confirmation, GirafProfileSelectorDialog.OnMultipleProfilesSelectedListener {
    private final static String TAG = "MainActivity";
    private final static String actionResult = "dk.aau.cs.giraf.PICTOGRAM";
    private Intent mainIntent;

    private FragmentManager fragManager;
    private FragmentTransaction fragTrans;
    private DrawFragment drawFragment;
    private SaveDialogFragment saveDialog;

    public static final String PICTO_SEARCH_IDS_TAG = "checkoutIds";
    public static final String PICTO_SEARCH_PURPOSE_TAG = "purpose";
    public static final String PICTO_SEARCH_SINGLE_TAG = "single";


    private GirafDialog clearDialog, showcaseDialog;
    public GirafButton clearButton, saveDialoGirafButton, loadDialoGirafButton,
            helpDialoGirafButton, undoButton, redoButton, printButton;

    private final int Method_Id_Clear = 1;
    private final int Method_Id_Remove_Tag = 2;
    private final int Method_Id_Remove_Citizen = 3;
    private final int Method_Id_Showcases = 4;

    private StoragePictogram storagePictogram;
    private View decor;
    private boolean service;
    private long author;

    @Override
    public void onProfilesSelected(int i, java.util.List<android.util.Pair<dk.aau.cs.giraf.dblib.models.Profile, java.lang.Boolean>> list) {
        List<Profile> selectedProfiles = new ArrayList<Profile>();
        for (int index = 0; index < list.size(); index++) {
            if (list.get(index).second == true) // Selected
            {
                selectedProfiles.add(list.get(index).first);
            }
        }
        saveDialog.addSelectedCitizens(selectedProfiles);
    }

    @Override
    public void confirmDialog(int methodID) {
        switch (methodID) {
            case Method_Id_Clear:
                AudioHandler.resetSound();

                if (drawFragment.drawView != null && DrawStackSingleton.getInstance().mySavedData != null) {
                    DrawStackSingleton.getInstance().mySavedData.clear();
                    Helper.poppedEntities.clear();
                    drawFragment.drawView.invalidate();

                    //Neeeded, as selectionhandler would have a deleted item selected otherwise
                    drawFragment.DeselectEntity();
                }
                break;
            case Method_Id_Remove_Citizen:
                saveDialog.removeCitizenConfirm();
                break;
            case Method_Id_Remove_Tag:
                saveDialog.removeTagConfirm();
                break;
            case Method_Id_Showcases:
                DrawStackSingleton.getInstance().mySavedData.clear();
                drawFragment.drawView.invalidate();
                drawFragment.setupShowcases();
                break;
        }
    }

    /**
     * Function called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        mainIntent = getIntent();

        createStoragePictogram();

        decor = getWindow().getDecorView();
        decor.setBackgroundColor(getResources().getColor(R.color.giraf_background));

        drawFragment = new DrawFragment();

        fragManager = getSupportFragmentManager();

        fragTrans = fragManager.beginTransaction();
        fragTrans.add(R.id.fragmentContainer, drawFragment);
        fragTrans.commit();

        printButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_print));
        printButton.setOnClickListener(printClick);
        addGirafButtonToActionBar(printButton, GirafActivity.LEFT);

        undoButton = new GirafButton(this, getResources().getDrawable(R.drawable.undo), getString(R.string.regret));
        undoButton.setOnClickListener(undoClick);
        addGirafButtonToActionBar(undoButton, GirafActivity.RIGHT);

        redoButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_redo), getString(R.string.redo));
        redoButton.setOnClickListener(redoClick);
        addGirafButtonToActionBar(redoButton, GirafActivity.RIGHT);

        helpDialoGirafButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_help));
        helpDialoGirafButton.setOnClickListener(helpClick);
        addGirafButtonToActionBar(helpDialoGirafButton, GirafActivity.RIGHT);

        clearButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_delete));
        clearButton.setOnClickListener(onClearButtonClick);
        addGirafButtonToActionBar(clearButton, GirafActivity.RIGHT);

        saveDialoGirafButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_save));
        saveDialoGirafButton.setOnClickListener(showLabelMakerClick);
        addGirafButtonToActionBar(saveDialoGirafButton, GirafActivity.LEFT);

        loadDialoGirafButton = new GirafButton(this, getResources().getDrawable(R.drawable.icon_open_pictogram), getString(R.string.load_button_text));
        loadDialoGirafButton.setOnClickListener(showPictosearchClick);
        addGirafButtonToActionBar(loadDialoGirafButton, GirafActivity.LEFT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Method which creates the pictogram used to store the image on screen.
     */
    private void createStoragePictogram() {
        storagePictogram = new StoragePictogram(this);

        if (ActivityManager.isUserAMonkey()) {
            dk.aau.cs.giraf.dblib.Helper h = new dk.aau.cs.giraf.dblib.Helper(this);

            author = h.profilesHelper.getGuardians().get(0).getId();
        } else {
            if (mainIntent != null) {
                String action = mainIntent.getAction();

                if (action != "dk.aaau.cs.giraf.CREATEPICTOGRAM") {
                    if (mainIntent.getExtras() != null) {
                        author = ((Long) mainIntent.getExtras().get("currentGuardianID")).longValue();
                        this.service = false;
                    }
                } else {
                    author = mainIntent.getIntExtra("author", 0);
                    this.service = true;
                }
            }
        }

        storagePictogram.setAuthor(author);
    }

    private void overwriteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(getString(R.string.save_pictogram));

        alertDialogBuilder
                .setMessage(getString(R.string.overwrite_existing_question))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.overwrite), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //Create new
                        savePictogram();
                    }
                })
                .setNegativeButton(getString(R.string.create_new), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { // Overwrite
                        savePictogram();
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    /**
     * On click listener to show the {@link SaveDialogFragment}
     */
    private final OnClickListener showLabelMakerClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (author == 0) {
                Toast.makeText(getActivity(), getString(R.string.must_be_logged_in), Toast.LENGTH_LONG).show();

                savePictogram();
            } else {
                savePictogram();
            }
        }
    };

    private void savePictogram() {
        drawFragment.DeselectEntity();

        saveDialog = new SaveDialogFragment();
        saveDialog.setService(service);
        saveDialog.setPictogram(storagePictogram, 1);
        saveDialog.setPreview(getBitmap());
        saveDialog.show(getSupportFragmentManager(), TAG);
    }

    private Activity getActivity() {
        return this;
    }

    private final OnClickListener onClearButtonClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "Clear Button clicked");
            clearDialog = GirafConfirmDialog.newInstance(getString(R.string.clear_button_text), getString(R.string.clear_canvas), Method_Id_Clear);

            clearDialog.show(getSupportFragmentManager(), "clearDialog");
        }
    };

    private final OnClickListener printClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isNetworkConnected()) {
                Toast.makeText(getApplicationContext(), getString(R.string.no_network), Toast.LENGTH_LONG).show();
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                PrintHelper photoPrinter = new PrintHelper(getActivity());
                photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);

                photoPrinter.printBitmap(getString(R.string.print_title), getBitmap());
            } else {
                Intent printIntent = new Intent(getActivity(), PrintDialogActivity.class);
                Uri bitmapToPrint = getImageUri(getApplicationContext(), getBitmap());
                PrintDialogActivity.setBitmapToPrint(bitmapToPrint);
                printIntent.setDataAndType(bitmapToPrint, "image/jpeg");
                printIntent.putExtra("title", getString(R.string.print_title)); // Key value pair
                startActivity(printIntent);
            }
        }
    };

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes); // 100 is quality
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, getString(R.string.print_title), null);
        return Uri.parse(path);
    }

    private final OnClickListener undoClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            EntityGroup savedEntities = DrawStackSingleton.getInstance().mySavedData;

            if (savedEntities != null) {
                Entity toPop = savedEntities.getEntityToPop();

                if (toPop == null) // No entity has been drawn on the canvas
                    return;

                if (!toPop.getIsDeleted() && !toPop.getHasBeenRedone()) { // Basic functionality when entity on top of stack is not a deleted entity
                    undoDrawnEntity(savedEntities);
                } else if (!toPop.getIsDeleted() && toPop.getHasBeenRedone()) {
                    Entity nextEntityToPop = savedEntities.getNextEntityToPop();
                    if (nextEntityToPop == null) {
                        return;
                    }
                    nextEntityToPop.setIsDeleted(false);
                    nextEntityToPop.setHasBeenRedone(true);
                } else {
                    toPop.setHasBeenRedone(true);
                    toPop.setIsDeleted(false);
                }
                drawFragment.drawView.invalidate();

                drawFragment.DeselectEntity(); // Needed, as selectionhandler would have a deleted item selected otherwise
            }
        }
    };

    private void undoDrawnEntity(EntityGroup savedEntities) {
        if (Helper.poppedEntities.size() > 5) {
            Helper.poppedEntities.remove(Helper.poppedEntities.get(0));
        }
        Entity poppedEntity = savedEntities.popEntity();
        poppedEntity.setIsDeleted(false);
        Helper.poppedEntities.add(poppedEntity);
    }

    private final OnClickListener redoClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!Helper.poppedEntities.isEmpty()) {
                EntityGroup savedEntities = DrawStackSingleton.getInstance().mySavedData;
                Entity entityToBeRedone = Helper.poppedEntities.get(Helper.poppedEntities.size() - 1);

                if (entityToBeRedone == null)
                    return;

                Helper.poppedEntities.remove(entityToBeRedone);
                savedEntities.addEntity(entityToBeRedone);

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

    private final OnClickListener helpClick = new OnClickListener() {
        @Override
        public void onClick(View view) {
            showcaseDialog = GirafConfirmDialog.newInstance(getString(R.string.showcase_dialog_title), getString(R.string.showcase_dialog_content), Method_Id_Showcases);

            showcaseDialog.show(getSupportFragmentManager(), "showcasesDialog");
        }
    };

    /**
     * Opens pictosearch as an application, so pictograms can be loaded into the application.
     */
/*    private void callPictosearch() {
        Intent intent = new Intent();

        try {
            // Sets properties on the intent
            intent.setComponent(new ComponentName("dk.aau.cs.giraf.pictosearch", "dk.aau.cs.giraf.PictoAdminMain"));
            intent.putExtra(PICTO_SEARCH_PURPOSE_TAG, PICTO_SEARCH_SINGLE_TAG);
            intent.putExtra("currentGuardianID", author);

            startActivityForResult(intent, RESULT_FIRST_USER);
        } catch (Exception e) {
            Toast.makeText(this, getText(R.string.pictosearch_not_installed), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Pictosearch is not installed: " + e.getMessage());
        }
    }*/

    // Opens the pictosearch to search for a single pictogram which ar public to the current guardian
    private void callPictosearch() {
        Intent intent = new Intent(this, dk.aau.cs.giraf.pictosearch.PictoAdminMain.class);

        intent.putExtra("currentGuardianID", author);
        intent.putExtra("purpose", "single");
        startActivityForResult(intent, RESULT_FIRST_USER);
    }

    /**
     * This method gets the pictogram that are returned by pictosearch.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            loadPictogram(data);
        }
    }

    /**
     * Loads the pictogram into the canvas by getting it from the database.
     *
     * @param data The data returned from pictosearch.
     */
    private void loadPictogram(Intent data) {
        long pictogramID;

        try {
            Bundle extras = data.getExtras(); // Get the data from the intent

            // Check if there was returned any pictogram ids
            if (data.hasExtra(PICTO_SEARCH_IDS_TAG)) {
                pictogramID = extras.getLongArray(PICTO_SEARCH_IDS_TAG)[0];
            } else {
                Toast.makeText(this, getString(R.string.pictosearch_no_pictograms), Toast.LENGTH_LONG).show();
                return;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, e.getMessage());
            return;
        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
            return;
        }

        dk.aau.cs.giraf.dblib.controllers.PictogramController pictogramController = new dk.aau.cs.giraf.dblib.controllers.PictogramController(this);
        Pictogram pictogram = pictogramController.getById(pictogramID);

        /*if the pictogram has no drawstack, just load the bitmap
          else we load drawstack*/
        if (pictogram.getEditableImage() == null) {
            Bitmap bitmap = pictogram.getImage();
            loadPicture(bitmap);
        } else {
            try {
                DrawStackSingleton.getInstance().mySavedData = (EntityGroup) ByteConverter.deserialize(pictogram.getEditableImage());
                drawFragment.drawView.invalidate();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        try {
            if (pictogram.getAudioFile(this) != null && pictogram.getAudioFile(this).length() > 0)
                AudioHandler.setFinalPath(pictogram.getAudioFile(this).getPath());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onPictureTaken(File picture) {
        try {
            loadPicture(BitmapFactory.decodeFile(picture.getPath()));
        } catch (OutOfMemoryError e) {
            Toast.makeText(this, getString(R.string.could_not_take_picture), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Loads the bitmaps into the canvas in correct size
     *
     * @param bitmap The bitmap to be loaded
     */
    private void loadPicture(Bitmap bitmap) {
        int sizeHeightPercentage = (int) (((double) (this.getBitmap().getHeight()) / (double) (bitmap).getHeight()) * 100.0) + 1;
        int sizeWidthPercentage = (int) (((double) (this.getBitmap().getWidth()) / (double) (bitmap).getWidth()) * 100.0);

        BitmapEntity tempEntity = new BitmapEntity(bitmap, Math.max(sizeHeightPercentage, sizeWidthPercentage));
        tempEntity.setCenter(drawFragment.drawView.getMeasuredWidth() / 2, drawFragment.drawView.getMeasuredHeight() / 2 - 4.0f);
        DrawStackSingleton.getInstance().mySavedData.addEntity(tempEntity);
        drawFragment.drawView.invalidate();
    }

    private Bitmap getBitmap() {
        return drawFragment.drawView.getFlattenedBitmap(Bitmap.Config.ARGB_8888);
    }

    public static String getActionResult() {
        return actionResult;
    }

    public void doExit(View v) {
        this.finish();
    }
}
