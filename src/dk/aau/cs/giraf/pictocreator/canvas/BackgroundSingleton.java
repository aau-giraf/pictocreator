package dk.aau.cs.giraf.pictocreator.canvas;

import android.graphics.drawable.Drawable;

/**
 * Created by Praetorian on 24-03-14.
 */
public class BackgroundSingleton {
    static  BackgroundSingleton instance;

    public static BackgroundSingleton  getInstance(){
        if(instance == null)
            instance = new BackgroundSingleton();
        return  instance;
    }

    public Drawable background;
}

