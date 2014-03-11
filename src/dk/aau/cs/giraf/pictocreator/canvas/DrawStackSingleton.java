package dk.aau.cs.giraf.pictocreator.canvas;

/**
 * Created by Lars on 11-03-14.
 * Singleton to store DrawStrack across fragment switches
 */
public class DrawStackSingleton {
    static DrawStackSingleton instance;

    public static DrawStackSingleton getInstance() {
        if(instance == null){
            instance = new DrawStackSingleton();
        }
        return instance;
    }

    public EntityGroup mySavedData = null;
    void setSavedData(EntityGroup data){
        mySavedData = data;
    }

    public EntityGroup getSavedData() {
        if(mySavedData == null)
            mySavedData = new EntityGroup();
        return mySavedData;
    }

}
