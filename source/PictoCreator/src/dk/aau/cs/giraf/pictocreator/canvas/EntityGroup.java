package dk.aau.cs.giraf.pictocreator.canvas;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * EntityGroup is a collection of Entity objects. Use this to either group
 * objects and their movement, or to ensure a specific drawing order.
 * @author lindhart
 */
public class EntityGroup extends Entity {

	protected ArrayList<Entity> entities = new ArrayList<Entity>();

	public EntityGroup() {};
	
	public EntityGroup(Parcel in) {
		super(in);
		
		entities = in.readArrayList(ArrayList.class.getClassLoader());
	}
	
	public void addEntity(Entity toAdd)	{
		if (entities.contains(toAdd)) 		{
			// Throw something. Possibly a vase.
		}
		else {
			// entities.add(toAdd);
			entities.add(0, toAdd);
		}
	}
	
	public Entity removeEntity(Entity toRemove)	{
		if (entities.contains(toRemove))
		{
			entities.remove(toRemove);
			return toRemove;
		}
		else return null;
	}
	
	@Override
	public void draw(Canvas canvas) {
		for (Entity e : entities){
			e.draw(canvas);
		}
	}
	
	public int size() {
		return entities.size();
	}
	
	/**
	 * Returns the first (topmost) Entity that collides with a given set of coordinates, or null.
	 * @param x
	 * @param y
	 * @return The topmost Entity that collides with the point, or null if none.
	 */
	public Entity getCollidedWithPoint(float x, float y) {
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e.collidesWithPoint(x, y)) return e;
		}
		return null;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		super.writeToParcel(dest, flags);
		dest.writeList(this.entities);
	}
}
