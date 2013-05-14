package dk.aau.cs.giraf.pictocreator.management;

import java.util.ArrayList;
import java.util.Collection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.StoragePictogram;
import dk.aau.cs.giraf.pictogram.*;

public class SaveDialogFragment extends DialogFragment{
    private final static String TAG = "SaveDialog";

    private View view;
    private FrameLayout previewView;
    private Pictogram preview;
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

    public SaveDialogFragment(){
        // empty constructor required for DialogFragment
    }

    public void setPreview(Pictogram preview){
        this.preview = preview;
    }

    public void setTags(Collection<String> tags){
        //this.tags = (ArrayList<String>) tags;
        this.tags = new ArrayList<String>();
        for(int i = 0; i < 50; i++){
            this.tags.add(Integer.toString(i));
        }
    }

    public void setPictogram(StoragePictogram storageP){
        this.storagePictogram = storageP;
    }

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        // public Pictogram(Context context, final String image,
        //                  final String text, final String audio,
        //                  final long id) {

    }

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

        // Log.d(TAG, "Created dialog.");
        int length;
        Bitmap bitmap = null;
        File imgFile = new File(parentActivity.getCacheDir(), "img");

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
        imgView.setImageBitmap(bitmap);

        previewView.addView(imgView);


        // if(preview != null){
        //     preview.renderImage();
        //     previewView.addView(preview);

        //     Log.d(TAG, "Set the image, it's super dope now.");
        // }

        acceptButton = (ImageButton) view.findViewById(R.id.save_button_positive);

        cancelButton = (ImageButton) view.findViewById(R.id.save_button_negative);

        cancelButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    // TODO: Make function to call when dialog is canceled
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    textLabel = inputTextLabel.getText().toString();

                    if(textLabel == null){
                        textLabel = defaultTextLabel;
                    }

                    fileHandler.saveFinalFiles(textLabel);

                    if(storagePictogram.addPictogram()){
                        Toast.makeText(parentActivity, "Saved the pictogram :D", Toast.LENGTH_SHORT).show();
                    }

                    // TODO: Make function to call when dialog is "accepted"
                    tmpDialog.dismiss();
                }
            });

        return view;
    }

    public void onResume() {
        super.onResume();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }

}
