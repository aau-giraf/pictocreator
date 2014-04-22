package dk.aau.cs.giraf.pictocreator.cam;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Class used for the preview of the camera
 *
 * @author Croc
 *
 */
public class CamPreview extends SurfaceView implements SurfaceHolder.Callback{
   private final static String TAG = "CamPreview";

   Camera cam;
   Size previewSize;
   SurfaceHolder holder;
   //Default settings for camera (not all systems report correctly when queried)
   int defaultCameraID = 0;
   int frontCameraID = 1;
   int currentCameraID = 0;
   boolean hasMultiCams = false;
   private String camEffect = Camera.Parameters.EFFECT_NONE;



   /**
    * Constructor for the class
    * @param context The context in which the preview is created
    * @param attrs The attributes for the preview
    */
   public CamPreview(Context context, AttributeSet attrs) {
       super(context, attrs);

       configureHolder();
       hasMultiCams = hasMultipleCam();
   }

   /**
    * Constructor for the class
    * @param context The context in which the preview is created
    */
   public CamPreview(Context context) {
       super(context);

       configureHolder();
       hasMultiCams = hasMultipleCam();
   }

   /**
    * Method called when the surface changes
    * @param holder The holder for the surface
    * @param format The format of the surface
    * @param width The width of the surface
    * @param height The height for the surface
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
           Log.d(TAG, "You tried to stop a non-existent preview");
       }
       releaseCamera();
       cam = getCamera(defaultCameraID);


       Camera.Parameters params = cam.getParameters();
       /*previewSize = getBestPreviewSize(width, height, params);
       params.setPreviewSize(previewSize.width, previewSize.height);
       requestLayout();
       cam.setParameters(params);
       */
       camEffect = params.getColorEffect();

       try{
           cam.setPreviewDisplay(holder);
       } catch(Exception e) {
           Log.d(TAG, "Display holder was not set");
       }
       startPreview();
   }

   /**
    * Method called when the surface is first created
    * @param holder The holder for the surface
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
    * Method called when the surface is destroyed
    * @param holder The holder for the surface
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
    * Method for calculating the best size of the preview
    * Called by {@link #surfaceChanged}
    * @param width The width of the surface
    * @param height The height of the surface
    * @param parameters The parameters of the camera
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
    * Method for accessing a camera by id
    * @param cameraID Id of the camera to get
    * @return The camera associated with the id
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
    * Method for releasing the camera
    */
   private void releaseCamera() {
       if (cam != null) {
           cam.release();
           cam = null;
       }
   }

   /**
    * Method for setting the color effect of the camera
    * @param params The color effect to set for the camera
    */
   public void setColorEffect(String params) {
	   Camera.Parameters current = cam.getParameters();

	   current.setColorEffect(params);

	   cam.setParameters(current);
       camEffect = params;
       Log.i(TAG,camEffect);
   }

   /**
    * Method for switching between cameras
    */
   public void switchCam() {
       try {
           cam.stopPreview();
       }
       catch (Exception e) {
           Log.d(TAG, "You tried to stop a non-existent preview");
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
    * Method for checking whether the device has more than one camera
    * @return True if the device have two cameras, false if not
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
    * Method for configuration of the surface holder
    */
   private void configureHolder() {
       holder = getHolder();
       holder.addCallback(this);
       holder.setKeepScreenOn(true);
   }

   /**
    * Method called when a picture is taken
    * @param shutter Callback for the image capture moment
    * @param raw Callback for raw image data
    * @param jpeg Callback for JPEG image data, see {@link PhotoHandler}
    */
   public void takePicture(Camera.ShutterCallback shutter, Camera.PictureCallback raw, Camera.PictureCallback jpeg) {
       cam.takePicture(shutter, raw, jpeg);
   }

   /**
    * Method for starting the camera preview
    */
   public void startPreview() {
       try {
           cam.startPreview();
           setColorEffect(camEffect);
       } catch (Exception e) {
           Log.d(TAG, "Something went horribly wrong in starting preview");
       }
   }
}
