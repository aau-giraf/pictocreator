package test;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import android.util.Log;

public class TestRunner {
    private static final String TAG = "TestRunner";

    public static void main(String[] args){
        Result result = JUnitCore.runClasses(StoragePictogramTest.class);
        for(Failure failure : result.getFailures()){
            Log.d(TAG, ""+failure.toString());
        }
    }
}
