package dk.aau.cs.giraf.pictogram;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.models.Media;
import dk.aau.cs.giraf.oasis.lib.models.Profile;
import dk.aau.cs.giraf.oasis.lib.controllers.MediaHelper;



//TODO: Make this a service that applications can hook to
//TODO: If made local, set it to run in seperate thread (DBsync and traversing can be costly)
public enum PictoFactory {
    INSTANCE;
    private final static String TAG = "PictoFactory";

    private Helper databaseHelper;


    /**
     * Somewhat dangerous method that gets every single piece of media
     * from the DB.
     */
    public List<Pictogram> getAllPictograms(Context context){
        databaseHelper = new Helper(context);
        MediaHelper mediaHelper = databaseHelper.mediaHelper;
        List<Media> allMedia = mediaHelper.getMedia();
        List<Pictogram> allPictograms = new ArrayList<Pictogram>();

        for(Media media : allMedia){
            try{
                allPictograms.add(convertMedia(context, media));
            } catch (IllegalArgumentException exc){
                // we ignore this exception because there is no need to do
                // anything about misses in the database.
            }
        }
        return allPictograms;
    }

    private Pictogram convertMedia(Context context, Media media) throws IllegalArgumentException{
        try{
            if(media.getMType().equalsIgnoreCase("IMAGE")){
                List<Media> subs = databaseHelper.mediaHelper.getSubMediaByMedia(media);
                String aud = null;
                Pictogram pictogram;

                if(subs.size() == 1){
                    aud = subs.get(0).getMPath();
                } else if(subs.size() > 1){
                    String msg = "Found several sub medias in media id %d, using first sub.";
                    msg = String.format(msg, media.getId());

                    Log.d(TAG, msg);
                    aud = subs.get(0).getMPath();
                } else {
                    String msg = "Found no sub media in %d, using null.";
                    msg = String.format(msg, media.getId());

                    Log.d(TAG, msg);
                }

                pictogram = new Pictogram(context,
                                          media.getMPath(),
                                          media.getName(),
                                          aud,
                                          media.getId());

                return pictogram;
            } else {
                String msg = "Media id %d not found to be of type IMAGE.";
                msg = String.format(msg, media.getId());

                throw new IllegalArgumentException(msg);
            }
        } catch(NullPointerException e) {
            String msg = "Null object passed to convertMedia.";
            Log.e(TAG, msg);

            return null;
        }
    }

    public List<Pictogram> getPictogramsByProfile(Context context, Profile profile){
        List<Pictogram> pictograms = new ArrayList<Pictogram>();
        List<Media> medias;
        databaseHelper = new Helper(context);
        MediaHelper mediaHelper = databaseHelper.mediaHelper;

        medias = mediaHelper.getMediaByProfile(profile);

        for(Media m : medias){
            try{
                pictograms.add(convertMedia(context, m));
            } catch (IllegalArgumentException exc){
                // we ignore this exception because there is no need to do
                // anything about misses in the database.
            }
        }

        return pictograms;
    }

    /**
     *
     */
    public Pictogram getPictogram(Context context, long pictogramId){
        // Imagine a database of pictograms with tags and
        // beautiful text for plastering on to them here.
        //
        // Imagine also that it was possible to load whole
        // collections of these things just by the switch
        // of a method.
        Pictogram pictogram;

        databaseHelper = new Helper(context);

        MediaHelper mediaHelper = databaseHelper.mediaHelper;

        Media media = mediaHelper.getMediaById(pictogramId);

        pictogram = convertMedia(context, media);

        return pictogram;
    }
}
