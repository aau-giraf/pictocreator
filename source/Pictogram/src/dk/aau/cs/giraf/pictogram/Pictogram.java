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

/**
 * 
 * @author Croc
 *
 */
public class Pictogram extends FrameLayout {
    private static final String TAG = "Pictogram";

    private final String imagePath;
    private final String textLabel;
    private final String audioPath;
    private final long pictogramID;

    private boolean usingColor = true;

    //Main constructor (no XML)

    /**
     * Main constructor, populates most of the fields in the pictogram.
     *
     * <p> The input to this constructor is not verified because it only should
     * be used by a {@link PictoFactory}
     *
     * @param context context in which this pictogram is generated.
     * @param image the path to the image used.
     * @param text the label for the image.
     * @param audio the path to the audio used.
     * @param id usually an id generated by the database.
     * @return A pictogram for use in GIRAF.
     */
    public Pictogram(Context context, final String image,
                     final String text, final String audio,
                     final long id) {

        super(context);
        imagePath = image;
        textLabel = text;
        audioPath = audio;
        pictogramID = id;
    }

    /**
     * Populates the view with both image and text, making it an actual viewable
     * view.
     */
    public void renderAll() {
        renderImage();
        renderText();
    }

    /**
     * Populates the view with text, making it an actual viewable view.
     * The gravity is by default set to {@value} TODO insert value
     * {@link #renderText(int)} can be used if you want to place the text.
     */
    public void renderText() {
        TextView text = new TextView(getContext());
        text.setText(textLabel);
        text.setPadding(15, 15, 15, 15);
        text.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        this.addView(text);
    }

    /**
     * Populates the view with text, making it an actual viewable view.
     * @param gravity is the location of the text in the image.
     */
    public void renderText(int gravity) {
        TextView text = new TextView(getContext());
        text.setText(textLabel);
        text.setPadding(15, 15, 15, 15);
        text.setGravity(Gravity.CENTER_HORIZONTAL | gravity);
        this.addView(text);
    }

    /**
     * Populates the view with an image, making it an actual viewable view.
     */
    public void renderImage() {
        Bitmap img = BitmapFactory.decodeFile(imagePath);
        ImageView image = new ImageView(getContext());
        String msg = imagePath + " found, making bitmap.";
        Log.d(TAG, msg);
        image.setImageBitmap(img);
        this.addView(image);
    }
    //TODO finish writing this up to fully utilize colorfiltering for grayscale
    public void useColor(boolean value) {
    	this.usingColor = value;
    }

    /**
     * A check if the {@link audioPath} is null.
     */
    public boolean hasAudio() {
        return audioPath != null;
    }

    public void playAudio() {
        playAudio(null);
    }
    /**
     * Play audio using the {@link AudioPlayer} class written by digiPECS in 2011
     * @param listener the callback that will be run
     */
    public void playAudio(final OnCompletionListener listener){
        if(hasAudio()){
            new Thread(new Runnable(){
                    @Override
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

    /**
     * <b>NOT YET IMPLEMENTED!</b>
     *
     * <p> Get tags attached to the pictogram.
     * TODO implement properly.
     */
    public String[] getTags() {
        return null;
    }

    /**
     *
     * @return imagePath the path to the image used for the pictogram.
     */
    public String getImagePath() {
        return imagePath;
    }

    /**
     *
     * @return textLabel the label used for the pictogram.
     */
    public String getTextLabel() {
        return textLabel;
    }

    /**
     *
     * @return audioPath the path to the audio used for the pictogram.
     */
    public String getAudioPath() {
        return audioPath;
    }
    /**
     *
     * @return pictogramID the id which the pictogram is found under in the database.
     */
    public long getPictogramID() {
        return pictogramID;
    }

}
