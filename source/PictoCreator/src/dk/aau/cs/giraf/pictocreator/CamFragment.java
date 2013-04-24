package dk.aau.cs.giraf.pictocreator;

import dk.aau.cs.giraf.pictocreator.CamPreview;
import dk.aau.cs.giraf.pictocreator.PhotoHandler;
import dk.aau.cs.giraf.pictocreator.R;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class CamFragment extends Fragment {
	private final static String TAG = "CamFragment";
	
	View view;
	CamPreview camFeed;
	PhotoHandler photoHandler;
	ImageButton captureButton;
	FrameLayout preview;
	
	Activity parentActivity;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		parentActivity = this.getActivity();

		photoHandler = new PhotoHandler(parentActivity);
		camFeed = new CamPreview(parentActivity);

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = inflater.inflate(R.layout.cam_fragment, container, false);
		
		preview = (FrameLayout)view.findViewById(R.id.camera_preview);
		preview.addView(camFeed);
		captureButton = (ImageButton)view.findViewById(R.id.button_capture);
		captureButton.setOnClickListener(captureClick);
		
		return view;
	}
	
	/**
	 * 	
	 * @param v
	 */
	public void capturePhoto(View v) {
		camFeed.takePicture(shutterCall, null, photoHandler);
	}
	
	/**
	 * 
	 */
	@Override
	public void onResume() {
		super.onResume();
	}
	
	/**
	 * 
	 */
	@Override
	public void onPause() {
		super.onPause();
	}
	
	/**
	 * 
	 * @param context
	 * @return
	 */
	private boolean checkCameraHardware(Context context) {
	    if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
	        return true;
	    } else {
	        Log.d(TAG, "No camera found on device");
	        return false;
	    }
	}
	
	/**
	 * 
	 */
	private final ShutterCallback shutterCall = new ShutterCallback() {
		@Override
		public void onShutter() {
			//Do nothing
		}
	};
	
	private final OnClickListener captureClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			captureButton.setClickable(false);
			capturePhoto(view);
		}
		
	};

}
