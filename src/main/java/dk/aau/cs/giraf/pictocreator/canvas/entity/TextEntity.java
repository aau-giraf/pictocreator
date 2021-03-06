package dk.aau.cs.giraf.pictocreator.canvas.entity;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;

import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;

/**
 * The Bitmap entity quite simply displays a Bitmap at a specified location.
 * It does miss caching functionality, so having several entities in play at
 * once wilinLayout very quickly cause Out-Of-Memory errors.
 *
 * @author lindhart
 */
public class TextEntity extends Entity {

    private EditText editText; // Initial EditText
    private Activity mActivity;
    public EditText drawnEditText; // The EditText which is drawn and can be edited
    public int backgroundColor;
    private LinearLayout linLayout;
    private boolean hidden = false;
    private int layoutXAdjustment = 155;
    private int layoutYAdjustment = 18;
    private int textDefaultBounds = 30;

    public TextEntity(EditText src, Activity activity, int backgroundColor) {
        editText = src;
        mActivity = activity;
        this.backgroundColor = backgroundColor;
        setHeight(editText.getHeight());
        setWidth(editText.getWidth());
        setX(editText.getX() - layoutXAdjustment); // Adjustments to x and y are needed when going from relativelayout to linearlayout
        setY(editText.getY() - layoutYAdjustment);
    }

    private int calculateWidth(EditText et) {
        Rect bounds = new Rect();
        Paint textPaint = et.getPaint();
        textPaint.getTextBounds(et.getText().toString(), 0, et.getText().length(), bounds);
        int width = bounds.width() + textDefaultBounds;
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
        linLayout = new LinearLayout(mActivity.getApplicationContext());

        drawnEditText = new EditText(mActivity.getApplicationContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);

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

        linLayout.draw(canvas);
    }

    public void setText(String text) {
        drawnEditText.setText(text);
        editText.setText(text);
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
