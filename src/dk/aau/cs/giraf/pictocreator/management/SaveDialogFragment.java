package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GDialogMessage;
import dk.aau.cs.giraf.pictocreator.MainActivity;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.StoragePictogram;
import dk.aau.cs.giraf.pictocreator.canvas.BackgroundSingleton;

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
    private GButton acceptButton;

    private ImageView imgView;

    private EditText inputTextLabel;
    private EditText inlineTextLabel;
    private EditText tagInputFind;


    private final String defaultTextLabel = "defaultTextLabel";
    private String textLabel;
    private String inlineText;

    private GButton cancelButton;
    private LinearLayout saveDialogLayout;

    private CheckBox publicStatus;
    private StoragePictogram storagePictogram;

    private FileHandler fileHandler;
    private boolean service = false;

    private  ListView tagsListView;
    private ArrayAdapter<String> tagArrayAdapter;
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

        parentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        this.tags = new ArrayList<String>();
        tagArrayAdapter  = new ArrayAdapter<String>(parentActivity, R.layout.save_tag, tags);

        view = inflater.inflate(R.layout.save_dialog, container);
        previewView = (FrameLayout) view.findViewById(R.id.save_preview);


        inputTextLabel = (EditText) view.findViewById(R.id.save_input_title);
        inlineTextLabel = (EditText) view.findViewById(R.id.edit_inline_text);

        tagInputFind = (EditText) view.findViewById(R.id.save_input_find);
        tagInputFind.setNextFocusDownId(R.id.save_input_find);
        tagsListView = (ListView) view.findViewById(R.id.save_tags_list);
        tagsListView.setAdapter(tagArrayAdapter);

        publicStatus = (CheckBox) view.findViewById(R.id.public_status);

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

        saveDialogLayout = (LinearLayout)view.findViewById(R.id.saveDialogLayout);
        if(BackgroundSingleton.getInstance().background != null)
            saveDialogLayout.setBackgroundDrawable(BackgroundSingleton.getInstance().background);
        else
            saveDialogLayout.setBackgroundResource(R.drawable.fragment_background);

        acceptButton = (GButton) view.findViewById(R.id.save_button_positive);

        cancelButton = (GButton) view.findViewById(R.id.save_button_negative);

        cancelButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                textLabel = inputTextLabel.getText().toString();
                inlineText = inlineTextLabel.getText().toString();
                Log.d(TAG, "TextLabel: " + textLabel);

                if (textLabel.matches("") || textLabel == null) {
                    Toast.makeText(parentActivity, "Venligst angiv et navn", Toast.LENGTH_SHORT).show();
                    return;
                        /*SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd-HHmmss");
                        String date = dateFormat.format(new Date());
                        String label = "Pictogram_" + date;
                        Log.d(TAG, "TextLavel was empty/null, replaced with " + label);
                        textLabel = label;*/
                }

                fileHandler.saveFinalFiles(textLabel, inlineText);

                storagePictogram.setpublicPictogram(publicStatus.isChecked() ? 1 : 0);

                if(!(tags != null) && !(tags.isEmpty())){
                    for(String t : tags){
                        storagePictogram.addTag(t);
                    }
                }

                if (storagePictogram.addPictogram()) {
                    Toast.makeText(parentActivity, "Pictogram gemt", Toast.LENGTH_SHORT).show();
                }

                if (service) {
                    Intent returnIntent = new Intent(MainActivity.getActionResult());
                    returnIntent.putExtra("pictogramId", storagePictogram.getId());
                    parentActivity.setResult(Activity.RESULT_OK, returnIntent);

                    parentActivity.finish();
                }

                tmpDialog.dismiss();
            }
        });
        tagInputFind.setOnKeyListener(tagKeyListener);
        tagsListView.setOnItemClickListener(tagViewListener);
        return view;
    }

    private static int tempPos;
    private final AdapterView.OnItemClickListener tagViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tempPos = position;
            GDialogMessage removeDialog = new GDialogMessage(parentActivity,"Slet Tag: " + tags.get(position),
                    new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tags.remove(tempPos);
                            tagArrayAdapter.notifyDataSetChanged();
                        }
                    });
            removeDialog.show();
        }
    };

    private final View.OnKeyListener tagKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            Log.i(TAG, "code: " + keyCode);
            if(keyCode == 66 && !(tagInputFind.getText().toString()).matches("")){
                if(!tags.contains(tagInputFind.getText().toString())){
                    tags.add(0,tagInputFind.getText().toString());
                }
                tagInputFind.setText("");
                tagInputFind.requestFocus();
            }
            return false;
        }
    };
    /**
     * Method called when the dialog is resumed
     */
    @Override
	public void onResume() {
        super.onResume();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

}
