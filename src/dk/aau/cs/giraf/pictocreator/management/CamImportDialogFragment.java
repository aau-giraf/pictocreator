package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import dk.aau.cs.giraf.gui.GCancelButton;
import dk.aau.cs.giraf.gui.GList;
import dk.aau.cs.giraf.gui.GVerifyButton;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.BackgroundSingleton;

/**
 * Dialog for importing pictures taken by camera to the canvas
 *
 * @author Croc
 *
 */
public class CamImportDialogFragment extends DialogFragment {
    private final static String TAG = "CamImportDialog";

    private View view;
    private Bitmap bitView;
    private ImageView imgView;
    private FrameLayout previewView;
    private ArrayList<String> fileList, pathList;
    private ArrayAdapter<String> arrayAdapter;
    private GList listView;
    private File[] imgFiles;
    private GVerifyButton acceptButton;
    private GCancelButton cancelButton;
    private LinearLayout importDialogLayout;
    ImportResultPath resultPath;
    int currentListPosition;

    private Activity parentActivity;

    /**
     * Constructor for the Dialog
     */
    public CamImportDialogFragment() {
    	//Empty
    }

    /**
     * Method called when the dialog is first created
     */
    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);

        parentActivity = getActivity();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        fileList = new ArrayList<String>();
        pathList = new ArrayList<String>();
        imgView = new ImageView(parentActivity);
    }
    /**
     * Method called when the view for the dialog is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        final Dialog importDialog = getDialog();
        importDialog.setCanceledOnTouchOutside(false);

        view = inflater.inflate(R.layout.import_dialog, container);
        previewView = (FrameLayout) view.findViewById(R.id.image_preview);
        listView = (GList) view.findViewById(R.id.image_names_list);

        acceptButton = (GVerifyButton) view.findViewById(R.id.import_ok_button);
        acceptButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bitView != null) {
                        resultPath.onImport(pathList.get(currentListPosition));
                    }
                    else {
                        Toast.makeText(parentActivity, "No image selected for import", Toast.LENGTH_LONG).show();
                    }
                    importDialog.dismiss();
                }
            });

        cancelButton = (GCancelButton) view.findViewById(R.id.import_cancel_button);
        cancelButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    importDialog.cancel();
                }
            });
        imgsToArray();
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(imageSelectClick);

        importDialogLayout = (LinearLayout)view.findViewById(R.id.importDialogLayout);
        if(BackgroundSingleton.getInstance().background != null)
            importDialogLayout.setBackgroundDrawable(BackgroundSingleton.getInstance().background);
        else
            importDialogLayout.setBackgroundResource(R.drawable.fragment_background);

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

    /**
     * Method for creating list of pictures taken by the camera
     */
    private void imgsToArray() {

        int newImgPosition = -1;
        File temp = new File(parentActivity.getCacheDir() + "/img" + File.separator);

        fileList.clear();
        pathList.clear();

        imgFiles = temp.listFiles();
        if(imgFiles != null) {
            for (File img : imgFiles) {
                fileList.add(img.getName());
                pathList.add(img.getAbsolutePath());
            }
            newImgPosition = (fileList.size() - 1);
            Log.d(TAG, "pathList path: " + pathList.get(newImgPosition));
            changePreview(newImgPosition);
        }
        arrayAdapter = new ArrayAdapter<String>(parentActivity, R.layout.import_listview_text, fileList);
    }

    /**
     * Method for changing the preview picture in the dialog
     * @param position The position of the new preview picture
     */
    private void changePreview(int position) {
        previewView.removeAllViews();
        currentListPosition = position;
        bitView = BitmapFactory.decodeFile(pathList.get(position));
        imgView.setImageBitmap(bitView);
        previewView.addView(imgView);
    }

    /**
     * On item click listener for the listView, calls {@link #changePreview(int position)}
     */
    private final AdapterView.OnItemClickListener imageSelectClick = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Log.d(TAG, String.valueOf(position));
                changePreview(position);
                view.setSelected(true);
            }
	};

    /**
     * Interface used for importing pictures
     * Implemented by {@DrawFragment}
     *
     * @author Croc
     *
     */
    public interface ImportResultPath {
        void onImport(String path);
    }

    /**
     * Method used for setting the importPath for the canvas
     * @param importPath Path for the image to import
     */
    public void setImportPath(ImportResultPath importPath) {
        resultPath = importPath;
    }

}
