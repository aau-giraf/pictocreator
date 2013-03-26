package dk.aau.cs.giraf.pictogram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.media.MediaPlayer.OnCompletionListener;

import dk.aau.cs.giraf.oasis.lib.*;
import dk.aau.cs.giraf.oasis.lib.models.*;

//TODO: Make custom ImageView and TextView with predefined "niceness"
public class Pictogram extends FrameLayout implements IPictogram {
    private static final String TAG = "Pictogram";

    private final String imagePath;
    private final String textLabel;
    private final String audioPath;
    private final long pictogramID;

    private Gravity textGravity;

    //Main constructor (no XML)
    public Pictogram(Context context, final String image,
                     final String text, final String audio,
                     final long id) {

        super(context);
        imagePath = image;
        textLabel = text;
        audioPath = audio;
        pictogramID = id;
    }

    @Override
    public void renderAll() {
        renderImage();
        renderText();
    }

    @Override
    public void renderText() {
        TextView text = new TextView(getContext());
        text.setText(textLabel);
        text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        this.addView(text);
    }

    public void renderText(int gravity) {
        TextView text = new TextView(getContext());
        text.setText(textLabel);
        text.setPadding(10, 10, 10, 10);
        text.setGravity(Gravity.CENTER_HORIZONTAL | gravity);
        this.addView(text);
    }

    @Override
    public void renderImage() {
        Bitmap img = BitmapFactory.decodeFile(imagePath);
        ImageView image = new ImageView(getContext());
        String msg = imagePath + " found, making bitmap.";
        Log.d(TAG, msg);
        image.setImageBitmap(img);
        this.addView(image);
    }

    public boolean hasAudio() {
        return audioPath != null;
    }

    @Override
    public void playAudio() {
        playAudio(null);
    }

    public void playAudio(final OnCompletionListener listener){
        if(hasAudio()){
            new Thread(new Runnable(){
                    public void run(){
                        AudioPlayer.INSTANCE.play(audioPath, listener);
                    }
                }).start();

            String msg = "Played audio: " + textLabel;
            Toast.makeText(super.getContext(), msg, Toast.LENGTH_SHORT).show();
            //TODO check that the thread is stopped again at some point. [OLD PARROT TODO]
        } else {
            Log.d(TAG, "No sound attatched: " + pictogramID + "\n\tOn:" + textLabel);
        }
    }

    @Override
    public String[] getTags() {
        return null;
    }

    @Override
    public String getImageData() {
        return null;
    }

    @Override
    public String getAudioData() {
        return null;
    }

    @Override
    public String getTextData() {
        return null;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getTextLabel() {
        return textLabel;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public long getPictogramID() {
        return pictogramID;
    }

}
