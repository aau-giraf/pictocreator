package dk.aau.cs.giraf.pictocreator;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import dk.aau.cs.giraf.oasis.lib.*;
import dk.aau.cs.giraf.oasis.lib.controllers.*;
import dk.aau.cs.giraf.oasis.lib.models.*;
import dk.aau.cs.giraf.pictogram.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CrocActivity extends Activity {

    private final static String TAG = "CrocMain";

    private static Intent girafIntent;

    // public static final String GUARDIANID = "currentGuardianID";
    // public static final String CHILDID = "currentChildID";
    // public static final String APP_PACKAGENAME = "appPackageName";
    // public static final String APP_ACTIVITYNAME = "appActivityName";
    // public static final String APP_COLOR = "appBackgroundColor";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

}
