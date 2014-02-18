package dk.aau.cs.giraf.pictocreator.cam;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ToggleButton;
import dk.aau.cs.giraf.pictocreator.R;
/**
 * Fragment class for the camera part of the application
 *
 * @author Croc
 *
 */
public class CamFragment extends Fragment {
	private final static String TAG = "CamFragment";
	private final static int clickDelay = 1000;
	View view;
	CamPreview camFeed;
	public PhotoHandler photoHandler;
	ImageButton captureButton, switchButton;
	ToggleButton colorEffectButton;
	FrameLayout preview;

	private Activity parentActivity;

	/**
	 * Method called when the fragment is fist created
	 */
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		parentActivity = this.getActivity();

		photoHandler = new PhotoHandler(parentActivity);
		camFeed = new CamPreview(parentActivity);

	}

	/**
	 * Method for creating the fragment view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		view = inflater.inflate(R.layout.cam_fragment, container, false);

		preview = (FrameLayout)view.findViewById(R.id.camera_preview);
		preview.addView(camFeed);
		captureButton = (ImageButton)view.findViewById(R.id.button_capture);
		captureButton.setOnClickListener(captureClick);
		colorEffectButton = (ToggleButton)view.findViewById(R.id.color_effects);
		colorEffectButton.setOnClickListener(colorClick);
		switchButton = (ImageButton)view.findViewById(R.id.switch_cam);
		switchButton.setOnClickListener(switchClick);

		if(!camFeed.hasMultiCams) {
			switchButton.setEnabled(false);
		}
		return view;
	}

	/**
	 * On click listener to capture photos
	 * @param view The view which is clicked
	 */
	public void capturePhoto(View view) {
		camFeed.takePicture(shutterCall, null, photoHandler);
	}

	/**
	 * Method called when the fragment is resumed
	 */
	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * Method called when the fragment is paused
	 */
	@Override
	public void onPause() {
		super.onPause();
	}

	/**
	 * On click listener for switching between cameras
	 * @param view The view which is clicked
	 */
	private void switchCam(View view) {
		camFeed.switchCam();
	}

	private final ShutterCallback shutterCall = new ShutterCallback() {
		@Override
		public void onShutter() {
			//This callback is implemented and left blank to
			//provide the shutter sound when snapping a photo
		}
	};

	private final OnClickListener captureClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			captureButton.setClickable(false);
			capturePhoto(view);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					captureButton.setClickable(true);
				}
			}, clickDelay);
		}
	};

	private final OnClickListener colorClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			if(colorEffectButton.isChecked()) {
				camFeed.setColorEffect(Camera.Parameters.EFFECT_MONO);
			}
			else if(!colorEffectButton.isChecked()) {
				camFeed.setColorEffect(Camera.Parameters.EFFECT_NONE);
			}
		}

	};

	private final OnClickListener switchClick = new OnClickListener() {

		@Override
		public void onClick(View view) {
			camFeed.switchCam();
		}
	};

}
