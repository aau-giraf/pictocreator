package dk.aau.cs.giraf.pictocreator.canvas.handlers;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import org.w3c.dom.Text;

import dk.aau.cs.giraf.pictocreator.R;
import dk.aau.cs.giraf.pictocreator.canvas.ActionHandler;
import dk.aau.cs.giraf.pictocreator.canvas.DrawStackSingleton;
import dk.aau.cs.giraf.pictocreator.canvas.DrawView;
import dk.aau.cs.giraf.pictocreator.canvas.Entity;
import dk.aau.cs.giraf.pictocreator.canvas.EntityGroup;
import dk.aau.cs.giraf.pictocreator.canvas.FloatPoint;
import dk.aau.cs.giraf.pictocreator.canvas.entity.BitmapEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.FreehandEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.LineEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.PrimitiveEntity;
import dk.aau.cs.giraf.pictocreator.canvas.entity.TextEntity;
import dk.aau.cs.giraf.pictocreator.management.Helper;

/**
 * The SelectionHandler is the select/move/rotate/scale tool on the canvas.
 *
 * @author lindhart
 */
public class SelectionHandler extends ActionHandler {

    /**
     * The currently selected Entity. May be null if no Entity is selected.
     */
    private Entity selectedEntity;

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
     * Offset difference between where the pointer was registered, and the
     * top-left corner of the selected Entity's hitbox. Could be used for more natural
     * dragging of Entity objects.
     */
    protected FloatPoint offset;

    /**
     * The location of the Pointer the last place we saw it.
     */
    protected FloatPoint previousPointerLocation;

    /**
     * Paint used for highlighting.
     */
    protected Paint hlPaint = new Paint();

    /**
     * The Entity that displays the icon for resizing and handles collisions
     * for interaction with it.
     */
    protected BitmapEntity resizeIcon;

    /**
     * The Entity that displays the icon for rotating and handles collisions
     * for interaction with it.
     */
    protected BitmapEntity rotateIcon;

    /**
     * The Entity that displays the icon for deletion and handles collisions
     * for interaction with it.
     */
    protected BitmapEntity scrapIcon;

    /**
     * The Entity that displays the icon for flattening and handles collisions
     * for interaction with it.
     */
    protected BitmapEntity flattenIcon;

    /**
     * The Entity that displays the icon for editing a text and handles collisions
     * for interaction with it.
     */
    protected BitmapEntity editTextIcon;

    /**
     * Square size of the icons. The icons will be resized to iconSizeXiconSize.
     */
    protected int iconSize = 124;

    /**
     * The ACTION_MODE enum makes explicit the various modes that
     * SelectionHandler enters during interaction.
     *
     * @author lindhart
     */
    public enum ACTION_MODE {
        NONE, MOVE, RESIZE, ROTATE
    }

    ;

    /**
     * The currently active SelectionHandler mode. Defaults to none.
     */
    protected ACTION_MODE currentMode = ACTION_MODE.NONE;

    /**
     * Used to determine whether icons should be rendered or not. Icons are
     * only rendered when an Entity has been selected.
     */
    protected boolean showIcons = false;

    /**
     * Used to create smooth rotations when using the rotate action.
     */
    protected float previousAngle;

    /**
     * Retrieves the actual Bitmap resource for an icon.
     *
     * @param name      Base name of the icon (for example "resize" or "scrap").
     *                  pixels variants are built into the package.
     * @param resources The source Resource collection to look in.
     * @return The matching Bitmap is returned, ready to use. If not found,
     * behaviour is undefined.
     */
    protected static Bitmap getIconBitmap(String name, Resources resources) {
        return BitmapFactory.decodeResource(resources,
                resources.getIdentifier(name, "drawable", "dk.aau.cs.giraf.pictocreator"));
    }


    private Activity mActivity;

    /**
     * Creates a new SelectionHandler using a specific Resource collection for
     * icons.
     *
     * @param resources Resource collection to retrieve icons from. Must
     *                  contain iconSize-sized Bitmaps with the naming convention
     *                  "<iconName>_icon_<iconSize>x<iconSize>" for resize, rotate, scrap and
     *                  flatten.
     */
    public SelectionHandler(Resources resources, Activity activity) {
        hlPaint.setStyle(Style.STROKE);
        hlPaint.setPathEffect(new DashPathEffect(new float[]{10.0f, 10.0f, 5.0f, 10.0f}, 0));
        hlPaint.setColor(0xFF000000);
        hlPaint.setStrokeWidth(2);
        mActivity = activity;

        initIcons(resources);
    }

    /**
     * Retrieves Bitmaps and initialises the BitmapEntity objects for actions.
     *
     * @param resources Source Resource collection, passed from the constructor.
     */
    private void initIcons(Resources resources) {
        editTextIcon = new BitmapEntity(getIconBitmap("icon_text", resources), iconSize);
        resizeIcon = new BitmapEntity(getIconBitmap("resize", resources), iconSize);
        rotateIcon = new BitmapEntity(getIconBitmap("rotate", resources), iconSize);
        scrapIcon = new BitmapEntity(getIconBitmap("delete", resources), iconSize);
        flattenIcon = new BitmapEntity(getIconBitmap("flatten", resources), iconSize);
    }

    /**
     * Updates the locations of the icon entities based on the selected Entity.
     */
    protected void updateIconLocations() {
        flattenIcon.setX(selectedEntity.getHitboxLeft() - flattenIcon.getWidth());
        flattenIcon.setY(selectedEntity.getHitboxTop() - flattenIcon.getHeight());

        //resize icon is not available for LineEntities and FreehandEntities
        if (selectedEntity instanceof LineEntity || selectedEntity instanceof FreehandEntity || selectedEntity instanceof TextEntity) {
            resizeIcon.setWidth(1);
            resizeIcon.setHeight(1);
        } else {
            resizeIcon.setWidth(rotateIcon.getWidth());
            resizeIcon.setHeight(rotateIcon.getHeight());
        }

        if (selectedEntity instanceof TextEntity) {
            editTextIcon.setWidth(rotateIcon.getWidth());
            editTextIcon.setHeight(rotateIcon.getHeight());
        } else {
            editTextIcon.setWidth(1);
            editTextIcon.setHeight(1);
        }

        editTextIcon.setX(selectedEntity.getHitboxRight());
        editTextIcon.setY(selectedEntity.getHitboxBottom());

        resizeIcon.setX(selectedEntity.getHitboxRight());
        resizeIcon.setY(selectedEntity.getHitboxBottom());

        rotateIcon.setX(selectedEntity.getHitboxLeft() - rotateIcon.getWidth());
        rotateIcon.setY(selectedEntity.getHitboxBottom());

        scrapIcon.setX(selectedEntity.getHitboxRight());
        scrapIcon.setY(selectedEntity.getHitboxTop() - scrapIcon.getHeight());
    }

    /**
     * Deletes the currently selected Entity. It is removed from the
     * draw stack.
     *
     * @param drawStack The draw stack EntityGroup to remove the Entity from.
     */
    protected void deleteEntity(EntityGroup drawStack) {
        selectedEntity.setHasBeenRedone(false);
        selectedEntity.setIsDeleted(true);
        selectedEntity.setIsSelected(false);
    }

    /**
     * Flattens the currently selected Entity onto the stack.
     *
     * @param drawStack
     * @todo Implement. This method remains unimplemented because the basic
     * interaction metaphor remains undefined. Should we flatten to a
     * background Bitmap (and risk moving a top-layer Entity to the back),
     * flatten from that Entity downwards (with potential side-effecting) or
     * simply convert the Entity to a Bitmap (confusing the metaphor)?
     * Note from SW608F14: We decided to move the entity into the back
     * of the canvas (front of the drawStack).
     * We leave the previous note in case another implementation is requested.
     */
    protected void flattenEntity(EntityGroup drawStack) {
        drawStack.moveToBack(this.selectedEntity);
        DrawStackSingleton.getInstance().mySavedData = drawStack;
    }

    protected void editTextEntity(final EntityGroup drawStack) {
        if (selectedEntity instanceof TextEntity) {

            final TextEntity textEntity = (TextEntity)selectedEntity;
            textEntity.setHidden(true);
            final EditText editText = new EditText(mActivity.getApplicationContext());
            final String text = textEntity.drawnEditText.getText().toString().trim();
            editText.setText(text);
            editText.setBackgroundColor(textEntity.backgroundColor);
            editText.setTextColor(textEntity.drawnEditText.getCurrentTextColor());

            final RelativeLayout mainLayout = (RelativeLayout)mActivity.findViewById(R.id.relativeLayout);

            editText.setX(textEntity.getX() + Helper.convertDpToPixel(95, mActivity.getApplicationContext()));
            editText.setY(textEntity.getY());
            editText.setTextSize(textEntity.drawnEditText.getTextSize());
            editText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                    {
                        InputMethodManager imm = (InputMethodManager) mActivity.getApplicationContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);

                        if (imm != null) {
                            textEntity.setText(editText.getText().toString());
                            textEntity.setHidden(false);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            mainLayout.removeView(editText);
                        }
                    }
                    return false;
                }
            });

            mainLayout.addView(editText);
            editText.requestFocus();

            Helper.showSoftKeyBoard(editText, mActivity.getApplicationContext());
        }
    }

    /**
     * Simply deselects the Entity by setting selectedEntity to null and
     * resetting to default action mode.
     */
    public void deselect() {
        if (selectedEntity != null)
            selectedEntity.setIsSelected(false);

        selectedEntity = null;
        currentMode = ACTION_MODE.NONE;
    }

    /**
     * Touch event which handles the different functionality of the selection tools.
     *
     * @param event     The source event, passed uncorrupted from the DrawView parent.
     * @param drawStack The stack of completed drawing operations in the view.
     *                  Probably only used by SelectionHandler.
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event, EntityGroup drawStack) {
        // Determine type of action.
        // Down: find first collision, highlight.
        // Move: reposition by delta.
        // Up: stop. Un-select is a different gesture.
        String touchEventTag = "SelectionHandler.onTouchEvent";

        int index = event.getActionIndex();
        int pointerId = event.getPointerId(index);
        int action = event.getAction();

        if (pointerId != currentPointerId) {
            Log.i(touchEventTag, "Unregistered pointer. Ignoring.");
        }

        if (action == MotionEvent.ACTION_UP) {
            Log.i(touchEventTag, "Pointer raised. Ignoring remainder of logic.");
            isPointerDown = false;
            currentPointerId = -1;
            showIcons = true;
            return true;
        }

        float px = event.getX(index);
        float py = event.getY(index);

        Log.i(touchEventTag, "Entering phased logic. isPointerDown: " + String.valueOf(isPointerDown) + ", action:" + String.valueOf(action));

        // Determine action or new Entity selection.
        if (action == MotionEvent.ACTION_DOWN && !isPointerDown) {
            if (selectedEntity != null) {
                selectedEntity.setIsSelected(false);
                // Attempt to collide with action icon.
                if (resizeIcon.collidesWithPoint(px, py)) {
                    currentMode = ACTION_MODE.RESIZE;
                } else if (editTextIcon.collidesWithPoint(px, py)) {
                    editTextEntity(drawStack);
                } else if (rotateIcon.collidesWithPoint(px, py)) {
                    currentMode = ACTION_MODE.ROTATE;
                    previousAngle = getAngle(selectedEntity.getCenter(), new FloatPoint(px, py));
                } else if (scrapIcon.collidesWithPoint(px, py)) {
                    deleteEntity(drawStack);
                    deselect();
                } else if (flattenIcon.collidesWithPoint(px, py)) {
                    flattenEntity(drawStack);
                    deselect();
                } else if (selectedEntity.collidesWithPoint(px, py)) {
                    currentMode = ACTION_MODE.MOVE;
                } else {
                    Log.i(touchEventTag, "Attempting to select new Entity.");
                    selectedEntity = drawStack.getCollidedWithPoint(px, py);
                }
            } else {
                Log.i(touchEventTag, "No selected Entity. Trying to find one.");
                selectedEntity = drawStack.getCollidedWithPoint(px, py);
            }

            if (selectedEntity == null) {
                deselect();
            } else {
                selectedEntity.setIsSelected(false);
                updateIconLocations(); // Icons.
            }

            // In all cases, register pointer id etc.
            isPointerDown = true;
            currentPointerId = pointerId;

            previousPointerLocation = new FloatPoint(px, py);

            return true; // Handled.
        } else if (isPointerDown && selectedEntity != null) {
            if (action == MotionEvent.ACTION_MOVE) {
                boolean handled = false;

                switch (currentMode) {
                    case MOVE: {
                        FloatPoint diff = new FloatPoint(
                                px - previousPointerLocation.x,
                                py - previousPointerLocation.y);
                        selectedEntity.setX(selectedEntity.getX() + diff.x);
                        selectedEntity.setY(selectedEntity.getY() + diff.y);
                        handled = true;
                        break;
                    }
                    case RESIZE: {
                        //this calculation is done such that the resizing follows the finger
                        //as it did not follow the finger when the entity was rotated
                        float x = selectedEntity.getHitboxRight() - (selectedEntity.getX() + selectedEntity.getWidth());
                        float y = selectedEntity.getHitboxBottom() - (selectedEntity.getY() + selectedEntity.getHeight());

                        float widthNewValue = px - x - selectedEntity.getX();
                        float heightNewValue = py - y - selectedEntity.getY();

                        //Bitmaps are supposed to scale, whereas other entities can be resized.
                        if (selectedEntity instanceof BitmapEntity) {
                            float ratio = selectedEntity.getHeight() / selectedEntity.getWidth();

                            selectedEntity.setWidth(widthNewValue);
                            selectedEntity.setHeight(widthNewValue * ratio);
                        } else {
                            selectedEntity.setWidth(widthNewValue);
                            selectedEntity.setHeight(heightNewValue);
                        }
                        handled = true;
                        break;
                    }
                    case ROTATE: {
                        float currentAngle = getAngle(selectedEntity.getCenter(), new FloatPoint(px, py));

                        selectedEntity.rotateBy(currentAngle - previousAngle);

                        previousAngle = currentAngle;
                        handled = true;
                        break;
                    }
                    default: {
                        handled = false;
                    }
                }

                previousPointerLocation = new FloatPoint(px, py);
                updateIconLocations(); // Icons.

                if (currentMode == ACTION_MODE.NONE) {
                    showIcons = true;
                } else {
                    showIcons = false;
                }

                return handled;
            }
        }
        return false;
    }

    /**
     * Helper function, basic math. Calculates the angle between two points
     * with (0,0) as pivotal point.
     *
     * @param fromPoint The starting point of the angle.
     * @param toPoint   The ending point of the angle.
     * @return The angle, in <b>degrees</b>. Use Math.toRadians to convert.
     */
    public static float getAngle(FloatPoint fromPoint, FloatPoint toPoint) {
        double dx = (toPoint.x - fromPoint.x);
        double dy = (toPoint.y - fromPoint.y);

        double inRads = Math.atan2(dy, dx);

        return (float) Math.toDegrees(inRads);
    }

    @Override
    public void drawBufferPreBounds(Canvas canvas) {
        if (selectedEntity != null) {
            if (selectedEntity.getIsDeleted())
                return;

            drawHighlighted(canvas);
        }
    }

    @Override
    public void drawBufferPostBounds(Canvas canvas) {
        if (selectedEntity != null) {
            if (selectedEntity.getIsDeleted())
                return;

            DrawStackSingleton.getInstance().mySavedData.deselectEntities();

            if (showIcons) {
                editTextIcon.draw(canvas);
                resizeIcon.draw(canvas);
                rotateIcon.draw(canvas);
                scrapIcon.draw(canvas);
                flattenIcon.draw(canvas);
                selectedEntity.setIsSelected(true);
            }
            else
            {
                selectedEntity.setIsSelected(false);
            }
        }
    }

    @Override
    public void draw(Canvas canvas) {
        drawBufferPreBounds(canvas);
    }

    /**
     * Similar to draw
     *
     * @param canvas
     */
    public void drawHighlighted(Canvas canvas) {
        // No need to draw the Entity through the buffer. It is already on the draw stack.
        // selectedEntity.draw(canvas); // Perform normal draw operation.

        canvas.save();

        RectF hitbox = selectedEntity.getHitbox();

        canvas.translate(hitbox.left, hitbox.top);
        canvas.drawRect(0.0f, 0.0f, hitbox.width(), hitbox.height(), hlPaint);
        canvas.restore();
    }

    @Override
    public void setStrokeColor(int color) {
        if (selectedEntity != null && ((Object) selectedEntity).getClass().isAssignableFrom(PrimitiveEntity.class)) {
            PrimitiveEntity tmp = (PrimitiveEntity) selectedEntity;
            tmp.setStrokeColor(color);
        }
        super.setStrokeColor(color);
    }

    @Override
    public void setFillColor(int color) {
        if (selectedEntity != null && ((Object) selectedEntity).getClass().isAssignableFrom(PrimitiveEntity.class)) {
            PrimitiveEntity tmp = (PrimitiveEntity) selectedEntity;
            tmp.setFillColor(color);
        }
        super.setFillColor(color);
    }
}
