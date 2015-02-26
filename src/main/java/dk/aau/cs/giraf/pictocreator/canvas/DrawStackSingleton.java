package dk.aau.cs.giraf.pictocreator.canvas;

/**
 * Singleton to store DrawStack across fragment switches.
 * Created since there is only one DrawStack in the application
 * and makes it easier to access the DrawStack from anywhere.
 * @author SW608F14
 */
public class DrawStackSingleton {
    private static DrawStackSingleton instance;

    public static DrawStackSingleton getInstance() {
        if(instance == null){
            instance = new DrawStackSingleton();
        }
        return instance;
    }

    public EntityGroup mySavedData = null;

    public EntityGroup getSavedData() {
        if(mySavedData == null)
            mySavedData = new EntityGroup();
        return mySavedData;
    }
}
