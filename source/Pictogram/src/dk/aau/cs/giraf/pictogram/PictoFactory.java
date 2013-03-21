package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;

import android.content.Context;
import android.os.Environment;

//TODO: Make this a service that applications can hook to
//TODO: If made local, set it to run in seperate thread (DBsync and traversing can be costly)
public enum PictoFactory {
    INSTANCE;
    privat final static TAG = "PictoFactory";

    private ArrayList<String> tempImageDatabase = new ArrayList<String>();
    private ArrayList<String> tempAudioDatabase = new ArrayList<String>();
    private ArrayList<String> tempTextDatabase = new ArrayList<String>();

    public void repopulateTemporaryDatabase(){
        // String _tempAudioDatabase[] = {"/bade.wma",
        //                                "/drikke.wma",
        //                                "/du.wma",
        //                                "/film.wma",
        //                                "/ja.wma",
        //                                "/lege.wma",
        //                                "/mig.wma",
        //                                "/nej.wma",
        //                                "/se.wma",
        //                                "/stop.wma",
        //                                "/sulten.wma"};

        // String _tempImageDatabase[] = {"/Bade.png",
        //                                "/Drikke.png",
        //                                "/Du.png",
        //                                "/Film.png",
        //                                "/Ja.png",
        //                                "/Lege.png",
        //                                "/Mig.png",
        //                                "/Nej.png",
        //                                "/Se.png",
        //                                "/Stop.png",
        //                                "/Sulten.png"};

        // String _tempTextDatabase[] = {"Bade",
        //                               "Drikke",
        //                               "Du",
        //                               "Film",
        //                               "Ja",
        //                               "Lege",
        //                               "Mig",
        //                               "Nej",
        //                               "Se",
        //                               "Stop",
        //                               "Sulten"};

        String _tempImageDatabase[];
        String _tempAudioDatabase[];
        String _tempTextDatabase[];

        String storagePath = Environment.getExternalStorageDirectory().getPath()
            + "/Pictures/giraf/public";
        File storage = new File(storagePath);
        File[] pictogramStorage = storage.listFiles();

        for(File f : pictogramStorage){
            String path = f.getPath();
            int lastIndex = path.length() - 1;
            String ext = path.substring(lastIndex-4, lastIndex);
            try{
                if(ext.equals(".png")){
                    tempImageDatabase.add(path);
                    tempTextDatabase.add(f.getName());
                } else if(ext.equals(".wma")){
                    tempAudioDatabase.add(path);
                }
            } catch(IndexOutOfBoundsException exception){
                Log.i(TAG, "File name too short to consider a media file.");
            }
        }

        _tempImageDatabase = (String[]) tempImageDatabase.toArray();
        _tempTextDatabase = (String[]) tempTextDatabase.toArray();
        _tempAudioDatabase = (String[]) tempAudioDatabase.toArray();

        // honestly I didn't check but I expect that they are exactly the same length.
        int length = _tempImageDatabase.length;

        for(int i = 0; i < length; i++){
            tempImageDatabase.add(storagePath + _tempImageDatabase[i]);
            tempTextDatabase.add(_tempTextDatabase[i]);
            tempAudioDatabase.add(storagePath + _tempAudioDatabase[i]);
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
            // completely arbirary, but hey!
            repopulateTemporaryDatabase();
        }

        String pic = tempImageDatabase.get((int)pictogramID);
        String aud = tempAudioDatabase.get((int)pictogramID);
        String text = tempTextDatabase.get((int)pictogramID);

        //TODO replace this when a new snappier version of
        // Pictogram gets implemented.
        Pictogram pictogram = new Pictogram(context, pic, text, aud, pictogramID);

        return pictogram;
    }
}
