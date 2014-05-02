package dk.aau.cs.giraf.pictocreator.management;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import dk.aau.cs.giraf.gui.GButton;
import dk.aau.cs.giraf.gui.GComponent;
import dk.aau.cs.giraf.pictocreator.R;

public class HelpDialogFragment extends DialogFragment{
	private final static String TAG = "HelpDialog";
	private final static int NUM_PAGES = 4;
	
    private View view;
    private FrameLayout helpBody;
    private ImageView imgView;
    private ArrayList<Integer> helpResourceList;
    private GButton closeButton, prevButton, nextButton;
    private LinearLayout helpDialogLayout;
    private int currentDrawable;
    private int iterator = 0;

    private Activity parentActivity;

    /**
     * Constructor for the Dialog
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
        
        parentActivity = getActivity();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        helpResourceList = new ArrayList<Integer>();
        helpResourceList.add(R.drawable.help_tools_1);
        helpResourceList.add(R.drawable.help_tools_2);
        helpResourceList.add(R.drawable.help_tools_3);
        helpResourceList.add(R.drawable.help_tools_4);
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
        helpBody.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SUBTLEGRADIENT));

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
        		//previous item
        		if(iterator > 0) {
        			iterator--;
        			changeBody(iterator);
        		}
        		else if(iterator == 0) {
        			iterator = (NUM_PAGES -1);
        			changeBody(iterator);
        		}
        	}
        });
        nextButton = (GButton) view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new OnClickListener() {
        	@Override
        	public void onClick(View view) {
        		//next item
        		if(iterator < (NUM_PAGES -1)) {
        			iterator++;
        			changeBody(iterator);
        		}
        		else if(iterator == (NUM_PAGES -1)) {
        			iterator = 0;
        			changeBody(iterator);
        			
        		}
        	}
        });

        helpDialogLayout = (LinearLayout)view.findViewById(R.id.helpDialogLayout);
        helpDialogLayout.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.SUBTLEGRADIENT));

        RelativeLayout topBar = (RelativeLayout)view.findViewById(R.id.topHelpBar);
        topBar.setBackgroundDrawable(GComponent.GetBackground(GComponent.Background.GRADIENT));

        return view;
    }
	
	private void changeBody(int iterator) {
        helpBody.removeAllViews();
        currentDrawable = helpResourceList.get(iterator);
        imgView.setImageResource(currentDrawable);
        helpBody.addView(imgView);
    }
}
