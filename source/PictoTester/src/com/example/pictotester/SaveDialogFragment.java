package com.example.pictotester;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;


import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * @author: croc
 */
public class SaveDialogFragment extends DialogFragment{
    private final static String TAG = "SaveDialog";

    private View view;
    private LinearLayout previewView;
    private Pictogram preview;
    private ArrayList<String> tags;
    private LayoutParams params = new LayoutParams(100 , 100);
    public SaveDialogFragment(){
        // empty constructor required for DialogFragment
    }

    public void setPreview(Pictogram preview){
        this.preview = preview;
    }

    public void setTags(Collection<String> tags){
        this.tags = (ArrayList<String>) tags;
    }

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);

        int style = DialogFragment.STYLE_NO_TITLE;

        setStyle(style, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        ListView listView;
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.save_tag, tags);

        view = inflater.inflate(R.layout.save_dialog_rel, container);
        previewView = (LinearLayout) view.findViewById(R.id.save_preview);
        listView = (ListView) view.findViewById(R.id.save_tags_list);

        // Log.d(TAG, "Created dialog.");

        if(preview != null){
            preview.renderImage();
            previewView.addView(preview, params);

            Log.d(TAG, "Set the image, it's super dope now.");
        }

        return view;
    }
}
