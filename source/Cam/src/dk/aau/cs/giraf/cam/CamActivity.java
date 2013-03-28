package dk.aau.cs.giraf.cam;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class CamActivity extends Activity {
	private final static String TAG = "CamActivity";
	
	CamPreview camPreview;
	PhotoHandler photoHandler;
	
	/**
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cam);
		photoHandler = new PhotoHandler(this);
		camPreview = new CamPreview(this);
		
		FrameLayout preview = (FrameLayout)findViewById(R.id.camera_preview);
		preview.addView(camPreview);
		
		Button captureButton = (Button)findViewById(R.id.button_capture);
	}
	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cam, menu);
		return true;
	}
	
	/**
	 * 	
	 * @param v
	 */
	public void capturePhoto(View v) {
		camPreview.takePicture(shutterCall, null, photoHandler);
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
		public void onShutter() {
			//Do nothing
		}
	};

}
