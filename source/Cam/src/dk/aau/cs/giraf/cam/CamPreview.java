package dk.aau.cs.giraf.cam;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamPreview extends SurfaceView implements SurfaceHolder.Callback{
	private final static String TAG = "CamPreview";
	
	Camera cam;
	Size previewSize;
	SurfaceHolder holder;
	int defaultCameraID = 0;
	int frontCameraID = 1;
	int currentCameraID = 0;
	boolean hasMultiCams = false;
	
	
	public CamPreview(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		configureHolder();
		hasMultiCams = checkCameraHardware();
	}
	
	public CamPreview(Context context) {
		super(context);
		
		configureHolder();
		hasMultiCams = checkCameraHardware();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
		if(holder.getSurface() == null) {
			Log.d(TAG, "Surface does not exist");
			return;
		}
		try {
			cam.stopPreview();
		} catch (Exception e) {
			Log.d(TAG, "Lol, you tried to stop a non-existent preview");
		}
		releaseCamera();
		cam = getCamera(defaultCameraID);
		
		Camera.Parameters params = cam.getParameters();
		previewSize = getBestPreviewSize(width, height, params);
		params.setPreviewSize(previewSize.width, previewSize.height);
		cam.setParameters(params);
		
		try {
			cam.setPreviewDisplay(holder);
			cam.startPreview();
		} catch (Exception e) {
			Log.d(TAG, "Something went horribly wrong in starting preview");
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if(hasMultiCams) {
			Log.d(TAG, "Multiple camera devices found");
		}
		else if(!hasMultiCams) {
			Log.d(TAG, "Only one device found");
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		try {
			cam.stopPreview();
			cam.setPreviewDisplay(null);
		} catch (Exception e) {
			Log.d(TAG, "Error stopping preview and setting display to null");
		}
		releaseCamera();
	}
	
	private Camera.Size getBestPreviewSize(int width, int height, Camera.Parameters parameters) {
		Camera.Size result=null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result=size;
				}
				else {
					int resultArea=result.width * result.height;
					int newArea=size.width * size.height;

					if (newArea > resultArea) {
						result=size;
					}
				}
			}
		}

		return(result);
	}
	
	public static Camera getCamera(int cameraID) {
		Camera camera = null;
		try {
			camera = Camera.open(cameraID);
		} catch(Exception e) {
			Log.d(TAG, "Failed to get the requested camera");
		}
		return camera;
	}
	
	private void releaseCamera() {
		if (cam != null) {
			cam.release();
			cam = null;
		}
	}
	
	private boolean checkCameraHardware() {
		/* Dat method, x_x */ 
		Camera.CameraInfo camInfo = new Camera.CameraInfo();
		int numberOfCams = Camera.getNumberOfCameras();
		
		if(numberOfCams == 1) {
			return false;
		}
		
		for(int j = 0; j < numberOfCams; j++) {
			Camera.getCameraInfo(j, camInfo);
			if(camInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
				defaultCameraID = currentCameraID = j;
				Log.d(TAG, "Camera facing back is set, with id: " + String.valueOf(j));
			}
			else if(camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				frontCameraID = j;
				Log.d(TAG, "Camera facing front is set, with id: " + String.valueOf(j));
			}
			else {
				Log.d(TAG, "Default configuration kept, since facing is not set in system");
			}
		}
		return true;
	}
	
	private void configureHolder() {
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setKeepScreenOn(true);
	}
}
