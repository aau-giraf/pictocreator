package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import dk.aau.cs.giraf.pictocreator.canvas.Entity;

/**
 * The Bitmap entity quite simply displays a Bitmap at a specified location.
 * It does miss caching functionality, so having several entities in play at
 * once wilinLayout very quickly cause Out-Of-Memory errors.
 *
 * @author lindhart
 */
public class TextEntity extends Entity {

    private EditText editText;
    private RelativeLayout mainLayout;
    private Activity mActivity;
    public EditText drawnEditText;
    public int backgroundColor;
    private LinearLayout linLayout;
    private boolean hidden = false;

    public TextEntity(EditText src, RelativeLayout layout, Activity activity, int backgroundColor) {
        editText = src;
        mainLayout = layout;
        mActivity = activity;
        this.backgroundColor = backgroundColor;
        setHeight(editText.getHeight());
        setWidth(editText.getWidth());
        setX(editText.getX() - 155);
        setY(editText.getY() - 18);
    }

    private int calculateWidth(EditText et) {
        Rect bounds = new Rect();
        Paint textPaint = et.getPaint();
        textPaint.getTextBounds(et.getText().toString(), 0, et.getText().length(), bounds);
        int width = bounds.width() + 30;
        return width;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean b)
    {
        hidden = b;
    }

    @Override
    public void doDraw(Canvas canvas) {
        if (editText.getText().length() == 0)
            return;

        linLayout = new LinearLayout(mActivity.getApplicationContext());

        drawnEditText = new EditText(mActivity.getApplicationContext());

        if (hidden) { drawnEditText.setVisibility(View.INVISIBLE); }
        else { drawnEditText.setVisibility(View.VISIBLE); }

        drawnEditText.setTextSize(editText.getTextSize());
        drawnEditText.setTextColor(editText.getCurrentTextColor());
        drawnEditText.setBackgroundColor(this.backgroundColor);
        drawnEditText.setHeight(editText.getHeight());
        drawnEditText.setText(editText.getText());
        drawnEditText.setSingleLine(true);
        drawnEditText.setWidth(calculateWidth(editText));

        linLayout.addView(drawnEditText);

        linLayout.measure(canvas.getWidth(), canvas.getHeight());
        linLayout.layout(0, 0, canvas.getWidth(), canvas.getHeight());

        // Set width and height of Entity for selection handler
        this.setWidth(drawnEditText.getWidth());
        this.setHeight(drawnEditText.getHeight());

        // canvas.drawBitmap(drawnEditText.getDrawingCache(), drawnEditText.getHeight(), drawnEditText.getWidth(), null);
        linLayout.draw(canvas);
    }

    public void setText(String text) {
        drawnEditText.setText(text);
        editText.setText(text);
        linLayout.invalidate();
    }

    /**
     * Sets a new height for the Entity.
     *
     * @param height New height.
     */
    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Sets a new width for the Entity.
     *
     * @param width New width.
     */
    @Override
    public void setWidth(float width) {
        this.width = width;
    }
}
