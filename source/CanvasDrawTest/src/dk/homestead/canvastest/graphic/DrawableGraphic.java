package dk.homestead.canvastest.graphic;

import android.graphics.Canvas;
import dk.homestead.canvastest.Graphic;
import android.graphics.drawable.Drawable;

public class DrawableGraphic extends Graphic {

	protected Drawable drawable;
	
	public DrawableGraphic(Drawable drawable) {
		this.drawable = drawable;
	}

	@Override
	public void draw(Canvas canvas) {
		drawable.draw(canvas);
	}

	@Override
	public float getWidth() {
		// TODO Auto-generated method stub
		return drawable.getIntrinsicWidth();
	}

	@Override
	public float getHeight() {
		return drawable.getIntrinsicHeight();
	}

	@Override
	/**
	 * There is no generic way to set width on Drawables!
	 */
	public void setWidth(float w) {
		// TODO Auto-generated method stub

	}

	/**
	 * There is no generic way to set height on Drawables!
	 */
	@Override
	public void setHeight(float h) {
		// TODO Auto-generated method stub
		
	}

}
