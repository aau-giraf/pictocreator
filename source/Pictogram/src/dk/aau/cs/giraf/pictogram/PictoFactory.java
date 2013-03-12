package dk.aau.cs.giraf.pictogram;

import java.util.ArrayList;

public enum PictoFactory {
    INSTANCE;
    private ArrayList<String> temp_image_database;
    private ArrayList<String> temp_audio_database;
    private ArrayList<String> temp_text_database;

    private PictoFactory(){
        String _temp_image_database[] = { "/sdcard/Pictogram/bade.wma",
                                          "/sdcard/Pictogram/børste_tænder.wma",
                                          "/sdcard/Pictogram/drikke.wma",
                                          "/sdcard/Pictogram/du.wma",
                                          "/sdcard/Pictogram/film.wma",
                                          "/sdcard/Pictogram/for_højt.wma",
                                          "/sdcard/Pictogram/gå.wma",
                                          "/sdcard/Pictogram/ja.wma",
                                          "/sdcard/Pictogram/køre.wma",
                                          "/sdcard/Pictogram/lave_mad.wma",
                                          "/sdcard/Pictogram/lege.wma",
                                          "/sdcard/Pictogram/mig.wma",
                                          "/sdcard/Pictogram/morgen_routine.wma",
                                          "/sdcard/Pictogram/nej.wma",
                                          "/sdcard/Pictogram/se.wma",
                                          "/sdcard/Pictogram/side_ned.wma",
                                          "/sdcard/Pictogram/spille_computer.wma",
                                          "/sdcard/Pictogram/stop.wma",
                                          "/sdcard/Pictogram/sulten.wma",
                                          "/sdcard/Pictogram/søvnig.wma",
                                          "/sdcard/Pictogram/tale_sammen.wma",
                                          "/sdcard/Pictogram/tørstig.wma",
                                          "/sdcard/Pictogram/være_stille.wma"};

        String _temp_audio_database[] = {"/sdcard/Pictogram/Bade.png",
                                         "/sdcard/Pictogram/Børste_Tænder.png",
                                         "/sdcard/Pictogram/Drikke.png",
                                         "/sdcard/Pictogram/Du.png",
                                         "/sdcard/Pictogram/Film.png",
                                         "/sdcard/Pictogram/For_Højt.png",
                                         "/sdcard/Pictogram/Gå.png",
                                         "/sdcard/Pictogram/Ja.png",
                                         "/sdcard/Pictogram/Køre.png",
                                         "/sdcard/Pictogram/Lave_Mad.png",
                                         "/sdcard/Pictogram/Lege.png",
                                         "/sdcard/Pictogram/Mig.png",
                                         "/sdcard/Pictogram/Morgen_Routine.png",
                                         "/sdcard/Pictogram/Nej.png",
                                         "/sdcard/Pictogram/Se.png",
                                         "/sdcard/Pictogram/Side_Ned.png",
                                         "/sdcard/Pictogram/Spille_Computer.png",
                                         "/sdcard/Pictogram/Stop.png",
                                         "/sdcard/Pictogram/Sulten.png",
                                         "/sdcard/Pictogram/Søvnig.png",
                                         "/sdcard/Pictogram/Tale_Sammen.png",
                                         "/sdcard/Pictogram/Tørstig.png",
                                         "/sdcard/Pictogram/Være_Stille.png"};

        String _temp_text_database[] = {"Bade",
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

        int length = _temp_image_database.length;
        
        for(int i = 0; i < length; i++){
            temp_image_database.add(_temp_image_database[i]);
        }

        for(int i = 0; i < length; i++){
            temp_text_database.add(_temp_text_database[i]);
        }

        for(int i = 0; i < length; i++){
            temp_audio_database.add(_temp_audio_database[i]);
        }
    }

    /**
     *
     */
    public Pictogram getPictogram(long pictogramID) {

        // Imagine a database of pictograms with tags and
        // beautiful text for plastering on to them here.
        //
        // Imagine also that it was possible to load whole
        // collections of these things just by the switch
        // of a method.



        //TODO replace this when a new snappier version of
        // Pictogram gets implemented.
        Pictogram pictogram = new Pictogram(null, null, pic, aud, text);

        return pictogram;
    }

}
