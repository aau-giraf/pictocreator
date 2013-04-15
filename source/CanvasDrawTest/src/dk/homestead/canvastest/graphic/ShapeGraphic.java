package dk.homestead.canvastest.graphic;

import android.graphics.Canvas;
import android.graphics.Paint;
import dk.homestead.canvastest.Graphic;
import android.graphics.drawable.shapes.Shape;

public class ShapeGraphic extends Graphic {

	protected Shape shape;
	protected Paint paint;
	
	public Shape getShape() { return shape; }
	public void setShape(Shape shape) { this.shape = shape; }
	
	public Paint getPaint() { return paint; }
	public void setPaint(Paint paint) { this.paint = paint; }
	
	public ShapeGraphic(Shape shape) {
		this.shape = shape;
		paint = new Paint();
		// Default to some black paint if omitted.
		paint.setARGB(255, 0, 0, 0);
	}
	
	public ShapeGraphic(Shape shape, Paint paint) {
		this.shape = shape;
		paint = new Paint();
	}

	@Override
	public void draw(Canvas canvas) {
		shape.draw(canvas, paint);
	}
	@Override
	public float getWidth() {
		return shape.getWidth();
	}
	@Override
	public float getHeight() {
		return shape.getHeight();
	}
	@Override
	public void setWidth(float w) {
		shape.resize(w, shape.getHeight());
	}
	@Override
	public void setHeight(float h) {
		shape.resize(shape.getWidth(), h);
	}

}
