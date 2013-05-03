package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.cam.PhotoHandler;
import android.app.Activity;
import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;

public class CamImportDialogFragment extends DialogFragment {
	private final static String TAG = "CamImportDialog";
	
	private View view;
    private ListView listView;
    private Bitmap bitView;
    private ImageView imgView;
    private FrameLayout previewView;
    private ArrayList<String> fileList, pathList;
    ArrayAdapter<String> arrayAdapter;
    File[] imgFiles;
    
    private Activity parentActivity;
    
    public CamImportDialogFragment() {
    	//Empty
    }
	
	@Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        
        parentActivity = getActivity();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        
        fileList = new ArrayList<String>();
        pathList = new ArrayList<String>();
        imgView = new ImageView(parentActivity);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        view = inflater.inflate(R.layout.import_dialog, container);
        previewView = (FrameLayout) view.findViewById(R.id.image_preview);
        listView = (ListView) view.findViewById(R.id.image_names_list);
        imgsToArray();
        listView.setAdapter(arrayAdapter);
        
        return view;
    }
	
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
		arrayAdapter = new ArrayAdapter<String>(parentActivity, R.layout.import_name, fileList);
	}
	
	private void changePreview(int position) {
		bitView = BitmapFactory.decodeFile(pathList.get(position));
		imgView.setImageBitmap(bitView);
		previewView.addView(imgView);
	}
	
}
