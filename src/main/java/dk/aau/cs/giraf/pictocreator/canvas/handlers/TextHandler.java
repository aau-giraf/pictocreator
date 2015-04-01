package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import dk.aau.cs.giraf.gui.GToggleButton;
import dk.aau.cs.giraf.gui.GirafButton;
import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.ActionHandler;
import dk.aau.cs.giraf.pictocreator.canvas.DrawView;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.entity.TextEntity;
import dk.aau.cs.giraf.pictocreator.management.Helper;


public class TextHandler extends ActionHandler {

    /**
     * The currently selected Entity. May be null if no Entity is selected.
     */
    private Entity selectedEntity;

    /**
     * All handlers must define a drawBuffer method that draws their current UI
     * output on a passed canvas.
     *
     * @param canvas The canvas to draw onto.
     */
    @Override
    public final void drawBufferPreBounds(Canvas canvas) {

    }

    /**
     * The ID of the pointer currently being tracked. Check isPointerDown to see
     * if one is *actually* being tracked.
     */
    private int currentPointerId;

    /**
     * Tracks whether a pointer is currently being tracked.
     */
    private boolean isPointerDown = false;
    /**
     * The location of the Pointer the last place we saw it.
     */
    protected FloatPoint previousPointerLocation;

    private Activity mActivity;
    private RelativeLayout mainLayout;
    private DrawView mDrawView;

    public TextHandler(Activity activity, DrawView drawView) {
        mActivity = activity;
        mainLayout = (RelativeLayout) mActivity.findViewById(R.id.relativeLayout);
        mDrawView = drawView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event, final EntityGroup drawStack) {
        // Determine type of action.
        // Down: find first collision, highlight.
        // Move: reposition by delta.
        // Up: stop. Un-select is a different gesture.
        String touchEventTag = "TextHandler.onTouchEvent";
        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        int action = event.getAction();

        if (pointerId != currentPointerId) {
            Log.i(touchEventTag, "Unregistered pointer. Ignoring.");
        }

        if (action != MotionEvent.ACTION_DOWN) {
            return false;
        }

        GirafButton selectButton = (GirafButton) mActivity.findViewById(R.id.select_handler_button);
        selectButton.performClick();

        int x = (int) event.getX() + Helper.convertDpToPixel(mActivity.getResources().getDimension(R.dimen.padding_x), mActivity.getApplicationContext());
        int y = (int) event.getY() - Helper.convertDpToPixel(mActivity.getResources().getDimension(R.dimen.padding_y), mActivity.getApplicationContext());

        final EditText editText = new EditText(mActivity.getApplicationContext());
        editText.setTextColor(getStrokeColor());
        editText.setBackgroundColor(getFillColor());

        float normalTextSize = 28;
        editText.setTextSize(normalTextSize + getStrokeWidth());
        editText.setX(x);
        editText.setY(y);

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    InputMethodManager imm = (InputMethodManager) mActivity.getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm != null) {
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        if (editText.getText().length() != 0) {
                            mainLayout.removeView(editText);
                            drawStack.addEntity(new TextEntity(editText, mActivity, getFillColor()));
                        }
                    }
                }
                return false;
            }
        });

        mainLayout.addView(editText);
        editText.requestFocus();

        Helper.showSoftKeyBoard(editText, mActivity.getApplicationContext());

        return true;
    }

    private FloatPoint getCenter(EditText ed) {
        return new FloatPoint(ed.getX() + ed.getWidth() / 2, ed.getY() + ed.getHeight() / 2);
    }
}
