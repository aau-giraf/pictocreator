package dk.aau.cs.giraf.pictocreator.cam;

import android.app.DialogFragment;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;

import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.pictocreator.R;

public class CamFragment extends DialogFragment {

    private final String TAG = "CamFragment";
    private Preview mPreview;

    private View view;
    private FrameLayout frameLayout;
    private RelativeLayout cameraBar;
    private LinearLayout cameraLayout;

    /* Buttons */
    private GirafButton exitButton;

    //User for swapping layouts
    private ViewSwitcher viewSwitcher;

    //For first layout
    private GirafButton colorSwapButton;
    private GirafButton captureButton;
    private GirafButton switchCamButton;

    //For second layout
    private GirafButton verifyButton;
    private GirafButton retryButton;

    private PhotoHandler photoHandler;

    private Camera mCamera;
    private int mNumberOfCameras;
    private int mCurrentCamera;  // Camera ID currently chosen

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);

        // Create a container that will hold a SurfaceView for camera previews
        mPreview = new Preview(this.getActivity());

        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the rear-facing ("default") camera
        CameraInfo cameraInfo = new CameraInfo();
        if (mNumberOfCameras > 1){
            for (int i = 0; i < mNumberOfCameras; i++){
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK){
                    mCurrentCamera= i;
                }
            }
        }else{
            mCurrentCamera = 0;
        }
        setHasOptionsMenu(mNumberOfCameras > 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.camera_fragment, container, false);

        cameraBar = (RelativeLayout)view.findViewById(R.id.cameraBar);
        cameraBar.setBackgroundColor(getResources().getColor(dk.aau.cs.giraf.gui.R.color.giraf_bar_gradient_start));

        frameLayout = (FrameLayout)view.findViewById(R.id.camera_preview);
        frameLayout.addView(mPreview);

        cameraLayout = (LinearLayout) view.findViewById(R.id.cameraLayout);
        cameraLayout.setBackgroundColor(getResources().getColor(dk.aau.cs.giraf.gui.R.color.giraf_background));

        viewSwitcher = (ViewSwitcher)view.findViewById(R.id.layoutSwitcherCamera);

        /* Assign Buttons*/
        exitButton = (GirafButton)view.findViewById(R.id.quit_buttonCamera);
        exitButton.setOnClickListener(quitButtonClick);

        captureButton = (GirafButton)view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(captureClick);

        colorSwapButton = (GirafButton)view.findViewById(R.id.color_effects);
        colorSwapButton.setOnClickListener(colorSwapClick);

        switchCamButton = (GirafButton)view.findViewById(R.id.switch_cam);
        switchCamButton.setOnClickListener(cameraSwapClick);

        verifyButton = (GirafButton)view.findViewById(R.id.acceptPictureButton);
        verifyButton.setOnClickListener(verifyButtonClick);

        retryButton = (GirafButton)view.findViewById(R.id.retryPictureButton);
        retryButton.setOnClickListener(retryButtonClick);

        photoHandler = new PhotoHandler(this.getActivity());

        return view;
    }

    //Left blank intentionally, is used for the click sound when taking a picture.
    private final Camera.ShutterCallback shutterCall = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    //Click listener for taking a picture. Uses the shutterCall listener for the click sound.
    private final View.OnClickListener captureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPreview.mCamera.takePicture(shutterCall, null, photoHandler);
            viewSwitcher.showNext();
        }
    };

    //Click listener for the retry button.
    private final View.OnClickListener retryButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            viewSwitcher.showPrevious();
            mPreview.mCamera.startPreview();
        }
    };

    private final View.OnClickListener verifyButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            closeDialog();
        }
    };

    private final View.OnClickListener quitButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(photoHandler.pictureFile != null && photoHandler.pictureFile.exists()){
                photoHandler.pictureFile.delete();
            }
            closeDialog();
        }
    };

    /**
     * Picture Taken Interface used to transfer the picture from CamFragment to MainActivity
     */
    public interface PictureTakenListener{
        void onPictureTaken(File picture);
    }

    /**
     * Returns the picture to MainActivity and closes the dialog.
     */
    private void closeDialog(){
        if(photoHandler.pictureFile != null && photoHandler.pictureFile.exists()){
            CamFragment.PictureTakenListener activity = (CamFragment.PictureTakenListener) getActivity();
            activity.onPictureTaken(photoHandler.pictureFile);
        }
        this.dismiss();
    }

    private final View.OnClickListener colorSwapClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            swapColourEffect();
            ((GirafButton)v).toggle();
        }
    };

    /**
     * Swaps the colour effect of the camera.
     */
    private void swapColourEffect(){
        Camera.Parameters tempParams = mPreview.mCamera.getParameters();

        //Initialise if Color effect is null
        if(tempParams.getColorEffect() == null){
            tempParams.setColorEffect(Camera.Parameters.EFFECT_NONE);
        }

        //Swap the colour effect
        if(tempParams.getColorEffect().matches(Camera.Parameters.EFFECT_MONO)){
            tempParams.setColorEffect(Camera.Parameters.EFFECT_NONE);
        }
        else if(tempParams.getColorEffect().matches(Camera.Parameters.EFFECT_NONE)){
            tempParams.setColorEffect(Camera.Parameters.EFFECT_MONO);
        }
        mPreview.mCamera.setParameters(tempParams);
    }

    private final View.OnClickListener cameraSwapClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            /*the colour effect part makes sure we preserve colour effect*/
            String colourEffect = mPreview.mCamera.getParameters().getColorEffect();
            mPreview.switchCamera();
            Camera.Parameters tempParams = mPreview.mCamera.getParameters();
            tempParams.setColorEffect(colourEffect);
            mPreview.mCamera.setParameters(tempParams);
        }
    };

    @Override
    public void onResume() {
        super.onResume();

        // Use mCurrentCamera to select the camera desired to safely restore
        // the fragment after the camera has been changed
        try{
            mCamera = Camera.open(mCurrentCamera);
            mPreview.setCamera(mCamera);
        }
        catch (RuntimeException e){
            Toast.makeText(this.getActivity(), "Kameraet kunne desværre ikke åbnes", Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
    }
}

