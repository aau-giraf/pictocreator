package dk.homestead.canvastest.shape;

import android.graphics.Canvas;

public class Rect extends Shape {

	public Rect() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawRect(x, y, x+width, y+height, paint);
	}
}
