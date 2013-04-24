package dk.homestead.canvastest.entity;

import android.graphics.Canvas;
import android.graphics.Paint;
import dk.homestead.canvastest.Entity;

public abstract class PrimitiveEntity extends Entity {

	Paint paint;
	
	@Override
	public abstract void draw(Canvas canvas);
}
