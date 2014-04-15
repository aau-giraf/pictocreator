package dk.aau.cs.giraf.pictocreator.cam;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GToggleButton;
import dk.aau.cs.giraf.pictocreator.R;

/**
 * Created by Praetorian on 15-04-14.
 */
public class CameraFragment extends Fragment{
    private final static String TAG = "CamFragment";
    private final static int clickDelay = 1000;
    View view;
    CamPreview camFeed;
    public PhotoHandler photoHandler;
    GButton captureButton, switchButton;
    GToggleButton colorEffectButton;
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

        view = inflater.inflate(R.layout.camera_fragment, container, false);

        preview = (FrameLayout)view.findViewById(R.id.camera_preview);
        preview.addView(camFeed);
        captureButton = (GButton)view.findViewById(R.id.button_capture);
        captureButton.setOnClickListener(captureClick);
        colorEffectButton = (GToggleButton)view.findViewById(R.id.color_effects);
        colorEffectButton.setOnClickListener(colorClick);
        switchButton = (GButton)view.findViewById(R.id.switch_cam);
        switchButton.setOnClickListener(switchClick);

        if(!camFeed.hasMultiCams) {
            switchButton.setEnabled(false);
        }
        return view;
    }

    /**
     * Button Click Events
     */
    private final View.OnClickListener captureClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            captureButton.setClickable(false);
            capturePhoto();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    captureButton.setClickable(true);
                }
            }, clickDelay);
        }
    };

    private final View.OnClickListener colorClick = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if(colorEffectButton.isPressed()) {
                camFeed.setColorEffect(Camera.Parameters.EFFECT_MONO);

            }
            else if(!colorEffectButton.isPressed()) {
                camFeed.setColorEffect(Camera.Parameters.EFFECT_NONE);
            }
        }

    };

    private final View.OnClickListener switchClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            camFeed.switchCam();
        }
    };

    private final Camera.ShutterCallback shutterCall = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            //This callback is implemented and left blank to
            //provide the shutter sound when snapping a photo
        }
    };

    /**
     * method used for OnClick listener to capture photos
     */
    public void capturePhoto() {
        camFeed.takePicture(shutterCall, null, photoHandler);
    }

}
