package dk.aau.cs.giraf.pictogram;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import dk.aau.cs.giraf.oasis.lib.Helper;
import dk.aau.cs.giraf.oasis.lib.controllers.MediaHelper;
import dk.aau.cs.giraf.oasis.lib.models.Media;


//TODO: Make this a service that applications can hook to
//TODO: If made local, set it to run in seperate thread (DBsync and traversing can be costly)
public enum PictoFactory {
    INSTANCE;
    private final static String TAG = "PictoFactory";

    private Helper databaseHelper;
    private ArrayList<String> tempImageDatabase = new ArrayList<String>();
    private ArrayList<String> tempAudioDatabase = new ArrayList<String>();
    private ArrayList<String> tempTextDatabase = new ArrayList<String>();

    private PictoFactory(){
        tempImageDatabase.clear();
        tempAudioDatabase.clear();
        tempTextDatabase.clear();

        if(databaseHelper == null){
            databaseHelper = new Helper(null);
        }
        Log.d(TAG, "PictoFactory initialized and \"database\" cleared.");
    }

    public void repopulateTemporaryDatabase(){
        String _tempImageDatabase[];
        String _tempTextDatabase[];
        String _tempAudioDatabase[];
        String storagePath = Environment.getExternalStorageDirectory().getPath()
            + "/Pictures/giraf/public";

        File storage = new File(storagePath);

        Log.d(TAG, storage.getPath());

        String[] pictogramNames = storage.list();

        if(pictogramNames != null){
            for(String path : pictogramNames){
                Log.d(TAG, path);
                int lastIndex = path.length();
                String ext = path.substring(lastIndex-4, lastIndex);
                Log.d(TAG, ext);
                try{
                    if(ext.equals(".png")){
                        tempImageDatabase.add(storagePath + "/" + path);
                        tempTextDatabase.add(path.replaceAll("\\p{C}", "_"));
                    } else if(ext.equals(".wma")){
                        tempAudioDatabase.add(storagePath + "/" + path);
                    }
                } catch(IndexOutOfBoundsException exception){
                    Log.i(TAG, "File name too short to consider a media file.");
                }
            }

            Log.d(TAG, "Pictograms added to relevant databases.");

            _tempImageDatabase = new String[tempImageDatabase.size()];
            _tempTextDatabase = new String[tempTextDatabase.size()];
            _tempAudioDatabase = new String[tempAudioDatabase.size()];

            tempImageDatabase.toArray(_tempImageDatabase);
            tempTextDatabase.toArray(_tempTextDatabase);
            tempAudioDatabase.toArray(_tempAudioDatabase);

            tempImageDatabase.clear();
            tempTextDatabase.clear();
            tempAudioDatabase.clear();

            Arrays.sort(_tempImageDatabase);
            Arrays.sort(_tempTextDatabase);
            Arrays.sort(_tempAudioDatabase);

            int i = 0;
            MediaHelper mediaHelper = databaseHelper.mediaHelper;

            for(String img : _tempImageDatabase){
                String sub = img.substring(0,img.length()-4);
                Media imgMedia = new Media(_tempTextDatabase[i], img, true, "pictogram", 0);
                Media audMedia = new Media();

                tempImageDatabase.add(img);
                tempTextDatabase.add(_tempTextDatabase[i]);

                int j = 0;
                for(String aud : tempAudioDatabase){
                    if(aud.startsWith(sub)){
                        String msg = "Found " + sub + " in " + aud;
                        Log.d(TAG, msg);
                        // audMedia = new Media(_tempAudioDatabase[j], aud, true, "audio", 0);
                        tempAudioDatabase.set(i ,_tempAudioDatabase[j]);
                        break;
                    }
                    j++;
                }
                long ida = databaseHelper.mediaHelper.insertMedia(imgMedia);

                if(!audMedia.equals(new Media())){
                    mediaHelper.insertMedia(audMedia);
                    mediaHelper.attachSubMediaToMedia(audMedia, imgMedia);
                }
                i += 1;
            }
        } else {
            String msg = "Could not find anything in " + storagePath;
            Log.e(TAG, msg);
        }
    }

    /**
     *
     */
    public Pictogram getPictogram(Context context, long pictogramID) {

        // Imagine a database of pictograms with tags and
        // beautiful text for plastering on to them here.
        //
        // Imagine also that it was possible to load whole
        // collections of these things just by the switch
        // of a method.

        if(tempImageDatabase.isEmpty()){
            Log.d(TAG, "\"Database\" was empty, filling it up.");
            // completely arbirary, but hey!
            repopulateTemporaryDatabase();
            Log.d(TAG, "Finished filling \"Database\"");
        }

        String pic = tempImageDatabase.get(0);
        tempImageDatabase.remove(0);
        String text = tempTextDatabase.get(0);
        tempTextDatabase.remove(0);
        String aud = null;

        if(!tempAudioDatabase.isEmpty()){
            aud = tempAudioDatabase.get(0);
            aud = tempAudioDatabase.remove(0);
        }
        //TODO replace this when a new snappier version of
        // Pictogram gets implemented.
        Pictogram pictogram = new Pictogram(context, pic, text, aud, pictogramID);

        return pictogram;
    }
}
