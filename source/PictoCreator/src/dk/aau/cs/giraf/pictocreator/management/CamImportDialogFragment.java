package dk.aau.cs.giraf.pictocreator.management;

import java.io.File;
import java.util.ArrayList;

import dk.aau.cs.giraf.pictocreator.R;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

public class CamImportDialogFragment extends DialogFragment {
	private final static String TAG = "CamImportDialog";
	
	private View view;
    private ListView listView;
    private FrameLayout previewView;
    private ArrayList<String> fileList, pathList;
    
    private Activity parentActivity;
    
    public CamImportDialogFragment() {
    	//Empty
    }
	
	@Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        
        parentActivity = getActivity();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(parentActivity, R.layout.import_name, fileList);

        view = inflater.inflate(R.layout.import_dialog, container);
        previewView = (FrameLayout) view.findViewById(R.id.save_preview);
        listView = (ListView) view.findViewById(R.id.save_tags_list);
        listView.setAdapter(arrayAdapter);

        return view;
    }
	
	private void imgsToArray() {
		
		File temp = new File(parentActivity.getCacheDir().getPath() + "img" + File.separator);
		
		fileList.clear();
		pathList.clear();
		
		File[] imgFiles = temp.listFiles();
		for (File img : imgFiles) {
			fileList.add(img.getName());
			pathList.add(img.getPath());
		}
		
	}
	
}
