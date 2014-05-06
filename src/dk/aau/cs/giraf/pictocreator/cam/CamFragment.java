package dk.aau.cs.giraf.pictocreator.cam;

import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.io.File;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GCancelButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GToggleButton;
import dk.aau.cs.giraf.gui.GVerifyButton;
import dk.aau.cs.giraf.oasis.lib.models.Tag;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.DrawFragment;
import dk.aau.cs.giraf.pictocreator.canvas.DrawStackSingleton;
import dk.aau.cs.giraf.pictocreator.canvas.DrawView;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;

public class CamFragment extends DialogFragment {

    private String TAG = "CamFragment";
    private Preview mPreview;

    private View view;
    private FrameLayout frameLayout;
    private RelativeLayout kameraBar;

    private ViewSwitcher viewSwitcher;

    /* Buttons */
    private GButton exitButton;

    //For first layout
    private GToggleButton colorSwapButton;
    private GButton captureButton;
    private GButton switchCamButton;

    //for swapped layout
    private GVerifyButton verifyButton;
    private GCancelButton retryButton;

    private PhotoHandler photoHandler;

    Camera mCamera;
    int mNumberOfCameras;
    int mCurrentCamera;  // Camera ID currently chosen
    int mCameraCurrentlyLocked;  // Camera ID that's actually acquired

    // The first rear facing camera
    int mDefaultCameraId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Dialog);
        // Create a container that will hold a SurfaceView for camera previews
        mPreview = new Preview(this.getActivity());

        // Find the total number of cameras available
        mNumberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the rear-facing ("default") camera
        CameraInfo cameraInfo = new CameraInfo();
        for (int i = 0; i < mNumberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                mCurrentCamera = mDefaultCameraId = i;
            }
        }
        setHasOptionsMenu(mNumberOfCameras > 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.camera_fragment, container, false);
        view.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SUBTLEGRADIENT));

        kameraBar = (RelativeLayout)view.findViewById(R.id.kameraBar);
        kameraBar.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.GRADIENT));

        frameLayout = (FrameLayout)view.findViewById(R.id.camera_preview);
        frameLayout.addView(mPreview);

        viewSwitcher = (ViewSwitcher)view.findViewById(R.id.layoutSwitcherCamera);
        /* Assign Buttons*/
        exitButton = (GButton)view.findViewById(R.id.quit_buttonCamera);
        exitButton.setOnClickListener(quitButtonClick);

        captureButton = (GButton)view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(captureClick);

        colorSwapButton = (GToggleButton)view.findViewById(R.id.color_effects);
        colorSwapButton.setOnClickListener(colorSwapClick);

        switchCamButton = (GButton)view.findViewById(R.id.switch_cam);
        switchCamButton.setOnClickListener(cameraSwapClick);

        verifyButton = (GVerifyButton)view.findViewById(R.id.acceptPictureButton);
        verifyButton.setOnClickListener(verifyButtonClick);

        retryButton = (GCancelButton)view.findViewById(R.id.retryPictureButton);
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

    private final View.OnClickListener captureClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mPreview.mCamera.takePicture(shutterCall, null, photoHandler);
            viewSwitcher.showNext();
        }
    };

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
            if(photoHandler.pictureFile != null && photoHandler.pictureFile.exists())
                photoHandler.pictureFile.delete();

            closeDialog();
        }
    };

    public interface PictureTakenListener{
        void onPictureTaken(File picture);
    }

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
            swapColour();
        }
    };
    private void swapColour(){
        Camera.Parameters tempParams = mPreview.mCamera.getParameters();

        //Initialise if Color effect is null
        if(tempParams.getColorEffect() == null)
            tempParams.setColorEffect(Camera.Parameters.EFFECT_NONE);

        //Swap the colour effect
        if(tempParams.getColorEffect().matches(Camera.Parameters.EFFECT_MONO))
            tempParams.setColorEffect(Camera.Parameters.EFFECT_NONE);
        else if(tempParams.getColorEffect().matches(Camera.Parameters.EFFECT_NONE))
            tempParams.setColorEffect(Camera.Parameters.EFFECT_MONO);

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

    public static CamFragment newInstance() {
        CamFragment f = new CamFragment();
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Use mCurrentCamera to select the camera desired to safely restore
        // the fragment after the camera has been changed
        try{
        mCamera = Camera.open(mCurrentCamera);
        mCameraCurrentlyLocked = mCurrentCamera;
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

