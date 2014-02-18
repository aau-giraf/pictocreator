package dk.aau.cs.giraf.pictocreator.canvas;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import android.graphics.Canvas;
import android.os.Parcel;

/**
 * EntityGroup is a collection of Entity objects. Use this to either group
 * objects and their movement, or to ensure a specific drawing order.
 * @author Croc
 */
public class EntityGroup extends Entity {

	/**
	 * List of all the entities kept in the EntityGroup. Consider it a stack
	 * for the purposes of rendering.
	 */
	protected ArrayList<Entity> entities = new ArrayList<Entity>();

	/**
	 * Creates an empty EntityGroup ready for service.
	 */
	public EntityGroup() {};
	
	/**
	 * Creates an EntityGroup based on the contents of a Parcel structure.
	 * @param in The Parcel to open. EntityGroup expects that the next
	 * ArrayList in the parcel is meant for EntityGroup. 
	 */
	public EntityGroup(Parcel in) {
		super(in);
		
		entities = in.readArrayList(ArrayList.class.getClassLoader());
	}
	
	/**
	 * Push a new entity onto the group's list.
	 * @param toAdd The Entity to add.
	 */
	public void addEntity(Entity toAdd) {
		if (entities.contains(toAdd)) 		{
			// Throw something. Possibly a vase.
			throw new InvalidParameterException("Cannot add an Entity twice. Remove it first.");
		}
		else {
			// entities.add(toAdd);
			entities.add(0, toAdd);
		}
	}
	
	/**
	 * Removes an Entity from the stack.
	 * @param toRemove The Entity to remove.
	 * @return The Entity itself. Good for chaining or quick assignment.
	 * Returns null if the Entity is not in the group.
	 */
	public Entity removeEntity(Entity toRemove)	{
		if (entities.contains(toRemove)) {
			entities.remove(toRemove);
			return toRemove;
		}
		else return null;
	}
	
	@Override
	public void draw(Canvas canvas) {
		for (int i = entities.size()-1; i >= 0; i--) {
			entities.get(i).draw(canvas);
		}
	}
	
	/**
	 * Retrieves the number of Entities kept in the group.
	 * @return The number as returned by entities.size();
	 */
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
