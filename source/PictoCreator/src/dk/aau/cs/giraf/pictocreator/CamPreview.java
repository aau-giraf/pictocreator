package dk.aau.cs.giraf.pictocreator;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
*
* @author Croc
*
*/
public class CamPreview extends SurfaceView implements SurfaceHolder.Callback{
   private final static String TAG = "CamPreview";

   Camera cam;
   Size previewSize;
   SurfaceHolder holder;
   //Default settings for camera (not all systems report correctly when asked)
   int defaultCameraID = 0;
   int frontCameraID = 1;
   int currentCameraID = 0;
   boolean hasMultiCams = false;

   /**
    *
    * @param context
    * @param attrs
    */
   public CamPreview(Context context, AttributeSet attrs) {
       super(context, attrs);

       configureHolder();
       hasMultiCams = hasMultipleCam();
   }

   /**
    *
    * @param context
    */
   public CamPreview(Context context) {
       super(context);

       configureHolder();
       hasMultiCams = hasMultipleCam();
   }

   /**
    *
    * @param holder
    * @param format
    * @param width
    * @param height
    */
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

       try{
           cam.setPreviewDisplay(holder);
       } catch(Exception e) {
           Log.d(TAG, "Display holder was not set");
       }
       startPreview();
   }

   /**
    *
    * @param holder
    */
   @Override
   public void surfaceCreated(SurfaceHolder holder) {
       if(hasMultiCams) {
           Log.d(TAG, "Multiple camera devices found");
       }
       else if(!hasMultiCams) {
           Log.d(TAG, "Only one device found");
       }
   }

   /**
    *
    * @param holder
    */
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

   /**
    *
    * @param width
    * @param height
    * @param parameters
    * @return
    */
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

   /**
    *
    * @param cameraID
    * @return
    */
   public static Camera getCamera(int cameraID) {
       Camera camera = null;
       try {
           camera = Camera.open(cameraID);
       } catch(Exception e) {
           Log.d(TAG, "Failed to get the requested camera");
       }
       return camera;
   }

   /**
    *
    */
   private void releaseCamera() {
       if (cam != null) {
           cam.release();
           cam = null;
       }
   }
   
   public void setColorEffect(String params) {
	   Camera.Parameters current = cam.getParameters();
	   
	   current.setColorEffect(params);
	   
	   cam.setParameters(current);
   }
   
   public void switchCam() {
       try {
           cam.stopPreview();
       }
       catch (Exception e) {
           Log.d(TAG, "Lol, you tried to stop a non-existent preview");
       }
       releaseCamera();

       if(currentCameraID == defaultCameraID){
           cam = getCamera(frontCameraID);
           currentCameraID = frontCameraID;
           Log.d(TAG, "Cam changed to front");
       }
       else if(currentCameraID == frontCameraID){
           cam = getCamera(defaultCameraID);
           currentCameraID = defaultCameraID;
           Log.d(TAG, "Cam changed to default");
       }
       else {
           Log.e(TAG, "Cam ID unknown");
       }
       try{
           cam.setPreviewDisplay(holder);
       }
       catch(Exception e) {
           Log.d(TAG, "Display holder was not set");
       }
       startPreview();
   }

   /**
    *
    * @return
    */
   
   public boolean hasMultipleCam() {
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

   /**
    *
    */
   private void configureHolder() {
       holder = getHolder();
       holder.addCallback(this);
       holder.setKeepScreenOn(true);
   }

   /**
    *
    * @param shutter
    * @param raw
    * @param jpeg
    */
   public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
       cam.takePicture(shutter, raw, jpeg);
   }

   /**
    *
    */
   public void startPreview() {
       try {
           cam.startPreview();
       } catch (Exception e) {
           Log.d(TAG, "Something went horribly wrong in starting preview");
       }
   }
}

