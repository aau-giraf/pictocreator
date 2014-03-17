package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import dk.aau.cs.giraf.pictocreator.MainActivity;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.StoragePictogram;

/**
 * Dialog used for saving a Pictogram
 *
 * @author Croc
 *
 */
public class SaveDialogFragment extends DialogFragment{
    private final static String TAG = "SaveDialog";

    private View view;
    private FrameLayout previewView;
    private Bitmap preview;
    private ArrayList<String> tags;
    private Activity parentActivity;
    private ImageButton acceptButton;

    private ImageView imgView;

    private EditText inputTextLabel;

    private final String defaultTextLabel = "defaultTextLabel";
    private String textLabel;

    private ImageButton cancelButton;

    private StoragePictogram storagePictogram;

    private FileHandler fileHandler;
    private boolean service = false;

    /**
     * Constructor for the Dialog
     */
    public SaveDialogFragment(){
        // empty constructor required for DialogFragment
    }

    /**
     * Method for setting the preview in the dialog
     * @param preview The bitmap to preview
     */
    public void setPreview(Bitmap preview){
        this.preview = preview;
    }

    /**
     * Method for associating tags to the pictogram
     * @param tags The list of tags to associate to the pictogram
     */
    public void setTags(Collection<String> tags){
        //this.tags = (ArrayList<String>) tags;
        this.tags = new ArrayList<String>();
        for(int i = 0; i < 50; i++){
            this.tags.add(Integer.toString(i));
        }
    }

    /**
     * Setter for the StoragePictogram variable
     * @param storageP The StoragePictogram to set
     */
    public void setPictogram(StoragePictogram storageP){
        this.storagePictogram = storageP;
    }

    /**
     * Setter for the service variable
     * @param serv The boolean which describes if CROC is a service.
     */
    public void setService(boolean serv){
        this.service = serv;
    }

    /**
     * Method called when the dialog is first created
     */
    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        // public Pictogram(Context context, final String image,
        //                  final String text, final String audio,
        //                  final long id) {

    }

    /**
     * Method called when the view for the dialog is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        final Dialog tmpDialog = getDialog();

        tmpDialog.setCanceledOnTouchOutside(false);

        parentActivity = getActivity();

        fileHandler = new FileHandler(parentActivity, storagePictogram);

        ListView listView;
        ArrayAdapter<String> arrayAdapter;
        parentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(tags == null){
            setTags(this.tags);
        }

        arrayAdapter  = new ArrayAdapter<String>(parentActivity, R.layout.save_tag, tags);

        view = inflater.inflate(R.layout.save_dialog, container);
        previewView = (FrameLayout) view.findViewById(R.id.save_preview);
        listView = (ListView) view.findViewById(R.id.save_tags_list);
        listView.setAdapter(arrayAdapter);

        inputTextLabel = (EditText) view.findViewById(R.id.save_input_title);

        int length;
        Bitmap bitmap = null;
        File imgFile = new File(parentActivity.getCacheDir(), "cvs");

        imgFile.mkdirs();

        Log.d(TAG, "File path: " + imgFile.getPath());

        File[] images = imgFile.listFiles();

        boolean imageDecoded = false;

        if(images.length > 0){
            length = images.length;
            Log.d(TAG, "Length of images: " + images.length);
            imgFile = images[length - 1];
        }

        try {
            if(imgFile.exists()){
                bitmap = BitmapFactory.decodeStream(new FileInputStream(imgFile), null, null);
                imageDecoded = true;
            }
        }
        catch (FileNotFoundException e){
            Log.e(TAG, "No file was found to decode");
        }

        imgView = new ImageView(parentActivity);

        if(bitmap != null){
            imgView.setImageBitmap(bitmap);
        }

        previewView.addView(imgView);

        acceptButton = (ImageButton) view.findViewById(R.id.save_button_positive);

        cancelButton = (ImageButton) view.findViewById(R.id.save_button_negative);

        cancelButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    textLabel = inputTextLabel.getText().toString();
                    Log.d(TAG, "TextLabel: " + textLabel);

                    if(textLabel.matches("") || textLabel == null){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HHmmss");
                        String date = dateFormat.format(new Date());
                        String label = "GPictogram_" + date;
                        Log.d(TAG, "TextLavel was empty/null, replaced with " + label);
                        textLabel = label;
                    }

                    fileHandler.saveFinalFiles(textLabel);

                    if(storagePictogram.addPictogram()){
                        Toast.makeText(parentActivity, "Saved the pictogram :D", Toast.LENGTH_SHORT).show();
                    }

                    if(service){
                        Intent returnIntent = new Intent(MainActivity.getActionResult());
                        returnIntent.putExtra("pictogramId", storagePictogram.getId());
                        parentActivity.setResult(Activity.RESULT_OK, returnIntent);

                        parentActivity.finish();
                    }

                    tmpDialog.dismiss();
                }
            });

        return view;
    }

    /**
     * Method called when the dialog is resumed
     */
    @Override
	public void onResume() {
        super.onResume();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

}
