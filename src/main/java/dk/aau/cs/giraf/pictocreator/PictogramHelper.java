package dk.aau.cs.giraf.pictocreator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public final class PictogramHelper {
    public static final String PICTO_SEARCH_IDS_TAG = "checkoutIds";

    /**
     * Method for getting the ID of a pictogram returned from an Activity
     *
     * @param data data return from another Activity
     * @param activityTag tag used for inserting message to log
     * @param currentContext the context of the running Activity
     * @param currentActivity the running Activity
     * @return returns the ID of the pictogram from data
     */
    public static long getPictogramID(Intent data, String activityTag, Context currentContext, Activity currentActivity) {
        long pictogramID = -1;

        try {
            Bundle extras = data.getExtras(); // Get the data from the intent

            // Check if there was returned any pictogram ids
            if (extras.containsKey(PICTO_SEARCH_IDS_TAG)) {
                pictogramID = extras.getLongArray(PICTO_SEARCH_IDS_TAG)[0];
            } else {
                Toast.makeText(currentActivity, currentContext.getString(R.string.pictosearch_no_pictograms), Toast.LENGTH_LONG).show();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(activityTag, e.getMessage());
        } catch (NullPointerException e) {
            Log.e(activityTag, e.getMessage());
        }

        return pictogramID;
    }
}
