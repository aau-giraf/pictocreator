package dk.aau.cs.giraf.pictocreator.management;

import java.util.ArrayList;
import java.util.TooManyListenersException;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.gui.GTextView;
import dk.aau.cs.giraf.gui.GToast;
import dk.aau.cs.giraf.pictocreator.R;

public class HelpDialogFragment extends DialogFragment{
	private final String TAG = "HelpDialog";
	
    private View view;
    private FrameLayout helpBody;
    private ImageView imgView;
    private ArrayList<Integer> helpResourceList;
    private GButton closeButton, prevButton, nextButton;
    private GTextView statusText;
    private LinearLayout helpDialogLayout;
    private int currentDrawable;
    private int iterator = 0;

    private Activity parentActivity;

    /**
     * Constructor for the Dialog, empty on purpose
     */
    public HelpDialogFragment() {
    	//Empty
    }

    /**
     * Method called when the dialog is first created
     */
    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        this.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        
        parentActivity = getActivity();
        helpResourceList = new ArrayList<Integer>();
        helpResourceList.add(R.drawable.help_selection);

        helpResourceList.add(R.drawable.help_camera_1);
        helpResourceList.add(R.drawable.help_camera_2);
        helpResourceList.add(R.drawable.help_camera_3);
        helpResourceList.add(R.drawable.help_camera_4);

        helpResourceList.add(R.drawable.help_record_1);
        helpResourceList.add(R.drawable.help_record_2);
        helpResourceList.add(R.drawable.help_record_3);

        helpResourceList.add(R.drawable.help_save_1);
        helpResourceList.add(R.drawable.help_save_2);
        helpResourceList.add(R.drawable.help_save_3);
        helpResourceList.add(R.drawable.help_save_4);
        helpResourceList.add(R.drawable.help_save_5);
        helpResourceList.add(R.drawable.help_save_6);
        helpResourceList.add(R.drawable.help_save_7);
        helpResourceList.add(R.drawable.help_save_8);
        helpResourceList.add(R.drawable.help_save_9);

        helpResourceList.add(R.drawable.help_load_1);
        helpResourceList.add(R.drawable.help_load_2);
        helpResourceList.add(R.drawable.help_load_3);

        helpResourceList.add(R.drawable.help_preview);

        helpResourceList.add(R.drawable.help_color_1);
        helpResourceList.add(R.drawable.help_color_2);

        helpResourceList.add(R.drawable.help_clear);

        imgView = new ImageView(parentActivity);
    }
    /**
     * Method called when the view for the dialog is created
     */
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final Dialog helpDialog = getDialog();
        helpDialog.setCanceledOnTouchOutside(false);

        view = inflater.inflate(R.layout.help_dialog, container);
        
        helpBody = (FrameLayout) view.findViewById(R.id.help_body);
        helpBody.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SOLID));

        statusText = (GTextView)view.findViewById(R.id.help_status_text);

        changeBody(iterator);
        
        closeButton = (GButton) view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    helpDialog.dismiss();
                }
            });

        prevButton = (GButton) view.findViewById(R.id.previous_button);
        prevButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
                iterator = (iterator - 1 + helpResourceList.size()) % helpResourceList.size();
                changeBody(iterator);
        	}
        });

        nextButton = (GButton) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
                iterator = (iterator + 1) % helpResourceList.size();
        		changeBody(iterator);
        	}
        });

        helpDialogLayout = (LinearLayout)view.findViewById(R.id.helpDialogLayout);
        helpDialogLayout.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SOLID));

        RelativeLayout topBar = (RelativeLayout)view.findViewById(R.id.topHelpBar);
        topBar.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.GRADIENT));

        return view;
    }

    /**
     * Changes the help pictures  depending on the iterated number.
     * @param iterator
     */
	private void changeBody(int iterator) {
        try{
        helpBody.removeAllViews();
        currentDrawable = helpResourceList.get(iterator);
        imgView.setImageResource(currentDrawable);
        helpBody.addView(imgView);
        statusText.setText(iterator + 1 +" / " + helpResourceList.size());
        }
        catch (OutOfMemoryError e){
            GToast.makeText(parentActivity, "Der er desværre ikke plads til at vise billedet", Toast.LENGTH_LONG).show();
        }
    }

}
