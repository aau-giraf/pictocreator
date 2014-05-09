package dk.aau.cs.giraf.pictocreator.audiorecorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.View;

/**
 * Class for creation of the view reprecenting the decibel meter
 * @author Croc
 *
 */
public class DecibelMeterView extends View {

    private double level = 0.0;

    private final int[] meterColors = {0xff5555ff,
                                       0xff5555ff,
                                       0xff00ff00,
                                       0xff00ff00,
                                       0xff00ff00,
                                       0xff00ff00,
                                       0xffffff00,
                                       0xffffff00,
                                       0xffff0000,
                                       0xffff0000};

    private final int offColor = 0xff555555;

    private ShapeDrawable drawable;

    /**
     * Constructor for the view
     * @param context The context in which the view is created
     */
    public DecibelMeterView(Context context){
        super(context);
    }

    /**
     * Constructor for the view
     * @param context The context in which the view is created
     * @param attrs The attributes for the view, defined by XML
     */
    public DecibelMeterView(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    /**
     * Get function for the decibelmeter level
     * @return The current level for the decibelmeter
     */
    public double getLevel(){
        return level;
    }

    /**
     * Set function for the decibelmeter level
     * @param level The level to set in the decibelmeter
     */
    public void setLevel(double level){
        this.level = level;
        invalidate();
    }

    /**
     * Function for drawing the decibelmeter
     * @param canvas The canvas to draw the meter on
     */
    private void drawMeter(Canvas canvas){
        int meterSize = meterColors.length;

        int padding = 3;
        int x = 10;
        int y = 0;
        int width = 50;
        int height = (int) (Math.floor(getHeight() / meterSize)) - (2 * padding);

        drawable = new ShapeDrawable(new RectShape());

        for(int i = meterSize - 1; i >= 0 ; i--){
            y = y + padding;
            if(((getLevel() - 0.1) * meterSize) >  (i - 0.5)){
                drawable.getPaint().setColor(meterColors[i]);
            }
            else {
                drawable.getPaint().setColor(offColor);
            }
            drawable.setBounds(x, y, x + width, y + height);
            drawable.draw(canvas);
            y = y + height + padding;
        }
    }

    /**
     * Draw function, called by android when the view is changed
     */
    @Override
    protected void onDraw(Canvas canvas){
        drawMeter(canvas);
    }
}
