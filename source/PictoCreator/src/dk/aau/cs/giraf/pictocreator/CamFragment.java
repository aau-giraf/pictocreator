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

public class CamFragment extends Fragment {
private final static String TAG = "CamActivity";

	CamPreview camFeed;
	PhotoHandler photoHandler;
	FrameLayout camView;
	ImageButton captureButton;
	LinearLayout fragmentLayout;
	
	Activity parentActivity;
	LayoutParams matchParent, wrapContent;
	
	@Override
	public void onCreate(Bundle savedInstance) {
		parentActivity = this.getActivity();
		matchParent = new LayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		wrapContent = new LayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		photoHandler = new PhotoHandler(parentActivity);
		camFeed = new CamPreview(parentActivity);
		
		camView = new FrameLayout(parentActivity);
		camView.setLayoutParams(matchParent);
		
		captureButton = new ImageButton(parentActivity);
		captureButton.setLayoutParams(wrapContent);
		captureButton.setBackgroundResource(R.drawable.cam); //Add the proper image
		captureButton.setOnClickListener(captureClick);
		
		fragmentLayout = new LinearLayout(parentActivity);
		fragmentLayout.setOrientation(LinearLayout.HORIZONTAL);
		fragmentLayout.setLayoutParams(matchParent);
		
		camView.addView(camFeed);
		fragmentLayout.addView(camView);
		fragmentLayout.addView(captureButton);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		return fragmentLayout;
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
			capturePhoto(view);
		}
		
	};

}
