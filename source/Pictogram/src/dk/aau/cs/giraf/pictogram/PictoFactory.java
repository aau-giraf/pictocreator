package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;

import android.content.Context;

public enum PictoFactory {
    INSTANCE;
    private ArrayList<String> tempImageDatabase;
    private ArrayList<String> tempAudioDatabase;
    private ArrayList<String> tempTextDatabase;

    public void repopulateTemporaryDatabase(){
        String _tempAudioDatabase[] = { "/sdcard/Pictogram/bade.wma",
                                        "/sdcard/Pictogram/drikke.wma",
                                        "/sdcard/Pictogram/du.wma",
                                        "/sdcard/Pictogram/film.wma",
                                        "/sdcard/Pictogram/ja.wma",
                                        "/sdcard/Pictogram/lavemad.wma",
                                        "/sdcard/Pictogram/lege.wma",
                                        "/sdcard/Pictogram/mig.wma",
                                        "/sdcard/Pictogram/morgenroutine.wma",
                                        "/sdcard/Pictogram/nej.wma",
                                        "/sdcard/Pictogram/se.wma",
                                        "/sdcard/Pictogram/sidened.wma",
                                        "/sdcard/Pictogram/spillecomputer.wma",
                                        "/sdcard/Pictogram/stop.wma",
                                        "/sdcard/Pictogram/sulten.wma",
                                        "/sdcard/Pictogram/talesammen.wma"};

        String _tempImageDatabase[] = {"/sdcard/Pictogram/Bade.png",
                                       "/sdcard/Pictogram/Drikke.png",
                                       "/sdcard/Pictogram/Du.png",
                                       "/sdcard/Pictogram/Film.png",
                                       "/sdcard/Pictogram/Ja.png",
                                       "/sdcard/Pictogram/LaveMad.png",
                                       "/sdcard/Pictogram/Lege.png",
                                       "/sdcard/Pictogram/Mig.png",
                                       "/sdcard/Pictogram/MorgenRoutine.png",
                                       "/sdcard/Pictogram/Nej.png",
                                       "/sdcard/Pictogram/Se.png",
                                       "/sdcard/Pictogram/SideNed.png",
                                       "/sdcard/Pictogram/SpilleComputer.png",
                                       "/sdcard/Pictogram/Stop.png",
                                       "/sdcard/Pictogram/Sulten.png",
                                       "/sdcard/Pictogram/TaleSammen.png"};

        String _tempTextDatabase[] = {"Bade",
                                      "Børste Tænder",
                                      "Drikke",
                                      "Du",
                                      "Film",
                                      "For Højt",
                                      "Gå",
                                      "Ja",
                                      "Køre",
                                      "Lave Mad",
                                      "Lege",
                                      "Mig",
                                      "Morgen Routine",
                                      "Nej",
                                      "Se",
                                      "Side Ned",
                                      "Spille Computer",
                                      "Stop",
                                      "Sulten",
                                      "Søvnig",
                                      "Tale Sammen",
                                      "Tørstig",
                                      "Være Stille"};

        // honestly I didn't check but I expect that they are exactly the same length.
        int length = _tempImageDatabase.length;

        for(int i = 0; i < length; i++){
            tempImageDatabase.add(_tempImageDatabase[i]);
        }

        for(int i = 0; i < length; i++){
            tempTextDatabase.add(_tempTextDatabase[i]);
        }

        for(int i = 0; i < length; i++){
            tempAudioDatabase.add(_tempAudioDatabase[i]);
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

        String pic = tempImageDatabase.get(0);
        String aud = tempAudioDatabase.get(0);
        String text = tempTextDatabase.get(0);

        tempImageDatabase.remove(0);
        tempAudioDatabase.remove(0);
        tempTextDatabase.remove(0);

        //TODO replace this when a new snappier version of
        // Pictogram gets implemented.
        Pictogram pictogram = new Pictogram(context, pic, text, aud, pictogramID);

        return pictogram;
    }
}
