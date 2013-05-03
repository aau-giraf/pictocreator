package dk.aau.cs.giraf.pictocreator.management;

import java.util.ArrayList;
import java.util.Collection;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictogram.Pictogram;

public class SaveDialogFragment extends DialogFragment{
    private final static String TAG = "SaveDialog";

    private View view;
    private FrameLayout previewView;
    private Pictogram preview;
    private ArrayList<String> tags;
    private Activity parentActivity;
    private ImageButton acceptButton;

    private ImageButton cancelButton;

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

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        final Dialog tmpDialog = getDialog();

        tmpDialog.setCanceledOnTouchOutside(false);

        parentActivity = getActivity();

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
        // Log.d(TAG, "Created dialog.");

        if(preview != null){
            preview.renderImage();
            previewView.addView(preview);

            Log.d(TAG, "Set the image, it's super dope now.");
        }

        acceptButton = (ImageButton) view.findViewById(R.id.save_button_positive);

        cancelButton = (ImageButton) view.findViewById(R.id.save_button_negative);

        cancelButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    // TODO: Make function to call when dialog is "accepted"
                    tmpDialog.cancel();
                }
            });

        acceptButton.setOnClickListener(new OnClickListener(){

                @Override
                public void onClick(View view){
                    // TODO: Make function to call when dialog is canceled
                    tmpDialog.dismiss();
                }
            });

        return view;
    }

}
