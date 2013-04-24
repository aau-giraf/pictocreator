package dk.aau.cs.giraf.audiorecorderv2;

import android.view.View;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;


/**
 * Class for creation of the view reprecenting the decibel meter
 * @author Croc
 *
 */
public class DecibelMeterView extends View {

    private double _level = 0.2;

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
     * @param context The context which the view is created in
     */
    public DecibelMeterView(Context context){
        super(context);
    }

    /**
     * Constructor for the view
     * @param context The context which the view is created in
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
        return _level;
    }

    /**
     * Set function for the decibelmeter level
     * @param level The level to set in the decibelmeter
     */
    public void setLevel(double level){
        // TODO implement set level function
        _level = level;
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
            if(((_level - 0.1) * meterSize) >  (i - 0.5)){
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
