package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;

import android.content.Context;

public enum PictoFactory {
    INSTANCE;
    private ArrayList<String> tempImageDatabase;
    private ArrayList<String> tempAudioDatabase;
    private ArrayList<String> tempTextDatabase;

    public void repopulateTemporaryDatabase(){
        String _tempAudioDatabase[] = {"/bade.wma",
                                       "/drikke.wma",
                                       "/du.wma",
                                       "/film.wma",
                                       "/ja.wma",
                                       "/lavemad.wma",
                                       "/lege.wma",
                                       "/mig.wma",
                                       "/morgenroutine.wma",
                                       "/nej.wma",
                                       "/se.wma",
                                       "/sidened.wma",
                                       "/spillecomputer.wma",
                                       "/stop.wma",
                                       "/sulten.wma",
                                       "/talesammen.wma"};

        String _tempImageDatabase[] = {"/Bade.png",
                                       "/Drikke.png",
                                       "/Du.png",
                                       "/Film.png",
                                       "/Ja.png",
                                       "/LaveMad.png",
                                       "/Lege.png",
                                       "/Mig.png",
                                       "/MorgenRoutine.png",
                                       "/Nej.png",
                                       "/Se.png",
                                       "/SideNed.png",
                                       "/SpilleComputer.png",
                                       "/Stop.png",
                                       "/Sulten.png",
                                       "/TaleSammen.png"};

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
        String storagePath = Environment.getExternalStorageDirectory().getPath() + "/Pictogram";

        for(int i = 0; i < length; i++){

            tempImageDatabase.add(storagePath + _tempImageDatabase[i]);
        }

        for(int i = 0; i < length; i++){
            tempTextDatabase.add(_tempTextDatabase[i]);
        }

        for(int i = 0; i < length; i++){
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
