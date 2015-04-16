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

    private float normalTextSize = 28;
    /**
     * All handlers must define a drawBuffer method that draws their current UI
     * output on a passed canvas.
     *
     * @param canvas The canvas to draw onto.
     */
    @Override
    public final void drawBufferPreBounds(Canvas canvas) {

    }

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
        int action = event.getAction();

        if (action != MotionEvent.ACTION_DOWN) {
            return false;
        }
        String touchEventTag = "TextHandler.onTouchEvent";

        Log.i(TAG, "TextEntity instantiated.");
        GirafButton selectButton = (GirafButton) mActivity.findViewById(R.id.select_handler_button);
        selectButton.performClick();

        int x = (int) event.getX() + Helper.convertDpToPixel(mActivity.getResources().getDimension(R.dimen.padding_x), mActivity.getApplicationContext());
        int y = (int) event.getY() - Helper.convertDpToPixel(mActivity.getResources().getDimension(R.dimen.padding_y), mActivity.getApplicationContext());

        final EditText editText = new EditText(mActivity.getApplicationContext());
        editText.setTextColor(getStrokeColor());
        editText.setBackgroundColor(getFillColor());

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
