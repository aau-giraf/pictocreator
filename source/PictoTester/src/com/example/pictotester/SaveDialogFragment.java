package com.example.pictotester;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.BitmapFactory;

import dk.aau.cs.giraf.pictogram.Pictogram;

/**
 * @author: croc
 */
public class SaveDialogFragment extends DialogFragment{
    private final static String TAG = "Dialog";

    private View view;
    private ImageView previewView;
    private String preview;
    public SaveDialogFragment(){
        // empty constructor required for DialogFragment
    }

    public void setPreview(String preview){
        this.preview = preview;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        view = inflater.inflate(R.layout.save_dialog, container);
        previewView = (ImageView) view.findViewById(R.id.save_preview);
        Log.d(TAG, "Created dialog.");

        if(preview != null){
            previewView.setImageBitmap(BitmapFactory.decodeFile(preview));
            previewView.invalidate();
            Log.d(TAG, "Set the image, it's super dope now.");
        }

        return view;
    }
}
