package dk.aau.cs.giraf.pictocreator.management;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.giraf.dblib.models.Profile;
import dk.aau.cs.giraf.gui.GRadioButton;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.gui.GirafConfirmDialog;
import dk.aau.cs.giraf.gui.GirafProfileSelectorDialog;
import dk.aau.cs.giraf.pictocreator.MainActivity;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.StoragePictogram;

/**
 * Dialog used for saving a Pictogram
 *
 * @author Croc
 */
public class SaveDialogFragment extends DialogFragment implements GirafProfileSelectorDialog.OnMultipleProfilesSelectedListener, GirafConfirmDialog.Confirmation {
    private final String TAG = "SaveDialog";

    private View view;
    private ListView tagsListView, connectedCitizenList;
    private FrameLayout previewView;
    private LinearLayout saveDialogLayout;
    private Bitmap preview;
    private ArrayList<String> tags, citizenNames;
    private ArrayList<Profile> citizenProfiles;
    private FragmentActivity parentActivity;
    private RelativeLayout saveBar;

    private final int Method_Id_Remove_Tag = 2;
    private final int Method_Id_Remove_Citizen = 3;

    private final int Select_Users_Id = 1;

    private DialogFragment dialog;

    private ImageView imgView;

    private int loadedPictogramId;

    private EditText inputTextLabel;
    private EditText inlineTextLabel;
    private EditText tagInputFind;

    private String pictogramNameText;
    private String inlineText;

    private GirafButton acceptButton, overwriteButton;
    private GirafButton cancelButton, addConnectCitizenButton;

    private GirafProfileSelectorDialog autistSelector;

    private GRadioButton publicityPublic;
    private GRadioButton publicityPrivate;

    private StoragePictogram storagePictogram;

    private FileHandler fileHandler;
    private boolean isService = false;
    private static int tempPos;

    private ArrayAdapter<String> tagArrayAdapter, connectedArrayAdapter;

    /**
     * Constructor for the Dialog, left empty on purpose
     */
    public SaveDialogFragment() {
        // empty constructor required for DialogFragment
    }

    /**
     * Method for setting the preview in the dialog
     *
     * @param preview The bitmap to preview
     */
    public void setPreview(Bitmap preview) {
        this.preview = preview;
    }

    /**
     * Setter for the StoragePictogram variable
     *
     * @param storagePictogram The StoragePictogram to set
     */
    public void setPictogram(StoragePictogram storagePictogram, int loadedPictogramId) {
        this.loadedPictogramId = loadedPictogramId;
        this.storagePictogram = storagePictogram;
    }

    /**
     * Setter for the isService variable
     *
     * @param isService The boolean which describes if CROC is a service.
     */
    public void setService(boolean isService) {
        this.isService = isService;
    }

    /**
     * Method called when the dialog is first created
     */
    @Override
    public void onCreate(Bundle SavedInstanceState) {
        super.onCreate(SavedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    /**
     * Method called when the view for the dialog is created
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.save_dialog, container);

        dialog = this;

        saveBar = (RelativeLayout) view.findViewById(R.id.saveBar);
        saveBar.setBackgroundColor(getResources().getColor(dk.aau.cs.giraf.gui.R.color.giraf_bar_gradient_start));

        this.setCancelable(false);

        parentActivity = getActivity();

        fileHandler = new FileHandler(parentActivity, storagePictogram);

        parentActivity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //general layout for this dialog
        saveDialogLayout = (LinearLayout) view.findViewById(R.id.saveDialogLayout);
        saveDialogLayout.setBackgroundColor(getResources().getColor(dk.aau.cs.giraf.gui.R.color.giraf_background));
        saveDialogLayout.setOnTouchListener(hideKeyboardTouchListener);

        //preview layout configuration
        AssignBitmapPreview();
        previewView = (FrameLayout) view.findViewById(R.id.save_preview);
        previewView.addView(imgView);

        //text for pictogram
        inputTextLabel = (EditText) view.findViewById(R.id.save_input_title);
        inlineTextLabel = (EditText) view.findViewById(R.id.edit_inline_text);

        inputTextLabel.setOnKeyListener(pictogramNameKeyListener);

        //buttons
        acceptButton = (GirafButton) view.findViewById(R.id.save_button_positive);
        acceptButton.setOnClickListener(acceptListener);

        overwriteButton = (GirafButton) view.findViewById(R.id.overwrite_button_positive);

        overwriteButton.setEnabled(false); // TODO: NOT FULLY IMPLEMENTED YET

        overwriteButton.setOnClickListener(overwriteListener);

        cancelButton = (GirafButton) view.findViewById(R.id.save_button_negative);
        cancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        publicityPublic = (GRadioButton) view.findViewById(R.id.radio_public);
        publicityPublic.setOnClickListener(disableConnect);

        publicityPrivate = (GRadioButton) view.findViewById(R.id.radio_private);
        publicityPrivate.setOnClickListener(enableCitizenList);

        //citizen layout configuration
        citizenProfiles = new ArrayList<Profile>();
        citizenNames = new ArrayList<String>();
        connectedArrayAdapter = new ArrayAdapter<String>(parentActivity, R.layout.save_tag, citizenNames);

        connectedCitizenList = (ListView) view.findViewById(R.id.connected_list);
        connectedCitizenList.setAdapter(connectedArrayAdapter);
        connectedCitizenList.setOnItemClickListener(onRemoveCitizen);

        addConnectCitizenButton = (GirafButton) view.findViewById(R.id.connect_autism);
        addConnectCitizenButton.setOnClickListener(addCitizen);

        //tags layout configuration
        tags = new ArrayList<String>();
        tagArrayAdapter = new ArrayAdapter<String>(parentActivity, R.layout.save_tag, tags);

        tagInputFind = (EditText) view.findViewById(R.id.save_input_find);
        tagInputFind.setNextFocusDownId(R.id.save_input_find);

        tagsListView = (ListView) view.findViewById(R.id.save_tags_list);
        tagsListView.setAdapter(tagArrayAdapter);

        tagInputFind.setOnKeyListener(tagKeyListener);
        tagsListView.setOnItemClickListener(tagViewListener);

        return view;
    }

    /**
     * Assign the canvas bitmap from the parentActivity to the preview
     */
    private void AssignBitmapPreview() {
        Bitmap bitmap = null;

        if (preview != null) {
            bitmap = preview;
        }

        imgView = new ImageView(parentActivity);

        if (bitmap != null) {
            imgView.setImageBitmap(bitmap);
        }
    }

    // Is never called but a necessity for having a GirafConfirmDialog
    @Override
    public void confirmDialog(int methodID) {
    }

    // Is never called but a necessity for having a GirafProfileSelectorDialog
    @Override
    public void onProfilesSelected(int dialogIdentifier, java.util.List<android.util.Pair<dk.aau.cs.giraf.dblib.models.Profile, java.lang.Boolean>> list) {
    }

    /**
     * Click listener that enables deletion of tags
     */
    private final AdapterView.OnItemClickListener tagViewListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tempPos = position;
            GirafConfirmDialog removeDialog = GirafConfirmDialog.newInstance(getString(R.string.delete), getString(R.string.remove_tag), Method_Id_Remove_Tag);
            removeDialog.show(getFragmentManager(), "removeDialog");
        }
    };

    public void removeTagConfirm()
    {
        tags.remove(tempPos);
        tagArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Listener for saveDialogLayout which hides the keyboard when the layout is touched
     */
    private final View.OnTouchListener hideKeyboardTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent ev) {
            InputMethodManager in = (InputMethodManager) parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            in.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return false;
        }
    };

    /**
     * Handles the adding of tags to the list.
     * keyCode 66 is a code for the enter and tab key.
     */
    private final View.OnKeyListener tagKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == 66 && !(tagInputFind.getText().toString()).matches("")) {
                if (!tags.contains(tagInputFind.getText().toString())) {
                    tags.add(0, tagInputFind.getText().toString());
                }
                tagInputFind.setText("");
                tagInputFind.requestFocus();
                tagArrayAdapter.notifyDataSetChanged();
            }
            return false;
        }
    };

    /**
     * KeyDown event on pictogramName EditText used to add name as tag
     */
    private final View.OnKeyListener pictogramNameKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View view, int i, KeyEvent keyEvent) {
            if (keyEvent.getKeyCode() == keyEvent.KEYCODE_ENTER) {
                String text = inputTextLabel.getText().toString();
                if (!tags.contains(text) && text.length() > 0) {
                    tags.add(0, text);
                    tagArrayAdapter.notifyDataSetChanged();
                }

                InputMethodManager imm = (InputMethodManager) parentActivity.getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

                if (imm != null) {
                    imm.hideSoftInputFromWindow(inputTextLabel.getWindowToken(), 0);
                }
            }
            return true;
        }
    };

    /**
     * Click listener for the accept button
     * Calls various function from storagePictogram to save the information of the pictogram
     */
    private final OnClickListener acceptListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            savePictogram(false);
        }
    };

    private final OnClickListener overwriteListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            savePictogram(true);
        }
    };

    private void savePictogram(boolean overwrite) {
        pictogramNameText = inputTextLabel.getText().toString();
        inlineText = inlineTextLabel.getText().toString();

        //A pictogram must have a name, if not the user is asked to provide one
        if (pictogramNameText.matches("") || pictogramNameText == null) {
            Toast.makeText(parentActivity, getString(R.string.select_name), Toast.LENGTH_SHORT).show();
            return;
        }

        fileHandler.saveFinalFiles(pictogramNameText, inlineText, preview);

        //Value 1 in database means publicly available
        //Value 0 in database means not publicly available
        if (publicityPublic.isChecked()) {
            storagePictogram.setIsPublic(true);
        } else if (publicityPrivate.isChecked()) {
            storagePictogram.setIsPublic(false);
            if (!citizenProfiles.isEmpty()) {
                for (Profile p : citizenProfiles) {
                    storagePictogram.addCitizen(p);
                }
            } else {
                Toast.makeText(parentActivity, "Angiv venligst borgere dette er privat for", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //adds each tag to the pictogram
        if ((tags != null)) {
            for (String t : tags) {
                storagePictogram.addTag(t);
            }
        }

        //saves the picogram into the database
        if (storagePictogram.addPictogram(loadedPictogramId)) {
            Toast.makeText(parentActivity, getString(R.string.pictogram_saved), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(parentActivity, getString(R.string.pictogram_not_saved), Toast.LENGTH_SHORT).show();
        }

        if (isService) {
            Intent returnIntent = new Intent(MainActivity.getActionResult());
            returnIntent.putExtra("pictogramId", storagePictogram.getId());
            parentActivity.setResult(FragmentActivity.RESULT_OK, returnIntent);

            parentActivity.finish();
        }

        this.dismiss();
    }

    private final AdapterView.OnItemClickListener onRemoveCitizen = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            tempPos = position;
            String description = getString(R.string.remove) + " " + citizenProfiles.get(position).getName() + " " + getString(R.string.from_the_list);
            GirafConfirmDialog removeDialog = GirafConfirmDialog.newInstance(getString(R.string.remove), description, Method_Id_Remove_Citizen);
            removeDialog.show(getChildFragmentManager(), "removeCitizen");
        }
    };

    public void removeCitizenConfirm()
    {
        citizenProfiles.remove(tempPos);
        updateCitizenList();
    }

    private final OnClickListener enableCitizenList = new OnClickListener() {
        @Override
        public void onClick(View v) {
            addConnectCitizenButton.setVisibility(View.VISIBLE);
            connectedCitizenList.setVisibility(View.VISIBLE);
        }
    };

    private final OnClickListener disableConnect = new OnClickListener() {
        @Override
        public void onClick(View v) {
            addConnectCitizenButton.setVisibility(View.INVISIBLE);
            connectedCitizenList.setVisibility(View.INVISIBLE);
        }
    };

    /**
     * Click listener which adds citizen to the list
     * Utilizes the GirafProfileSelectorDialog
     */
    private final OnClickListener addCitizen = new OnClickListener() {
        @Override
        public void onClick(View v) {
            dk.aau.cs.giraf.dblib.Helper helper = new dk.aau.cs.giraf.dblib.Helper(parentActivity);
            try {
                long authorID = storagePictogram.getAuthor();

                if (authorID == 0) {
                    Toast.makeText(getActivity(), getString(R.string.must_be_logged_in), Toast.LENGTH_LONG).show();
                    //return;
                }

              //  ArrayList<Profile> authorChildren = new ArrayList<Profile>();
              //  authorChildren.addAll(helper.profilesHelper.getChildrenByGuardian(helper.profilesHelper.getById(authorID)));

                GirafProfileSelectorDialog autistSelector = GirafProfileSelectorDialog.newInstance(getActivity(), authorID, false, true, getString(R.string.profile_selector_description), Select_Users_Id);
                autistSelector.show(getFragmentManager(), "" + Select_Users_Id);

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    };

    public void addSelectedCitizens(List<Profile> selectedProfiles)
    {
        citizenProfiles.clear();
        citizenProfiles.addAll(selectedProfiles);
        updateCitizenList();
    }


    private void updateCitizenList() {
        citizenNames.clear();
        for (int i = 0; i < citizenProfiles.size(); i++) {
            citizenNames.add(i, citizenProfiles.get(i).getName());
        }
        connectedArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Method called when the dialog is resumed
     */
    @Override
    public void onResume() {
        super.onResume();
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
    }
}