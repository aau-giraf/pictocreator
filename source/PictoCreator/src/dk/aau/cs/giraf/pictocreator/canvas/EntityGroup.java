package dk.aau.cs.giraf.pictocreator.canvas;

import java.util.ArrayList;

import android.graphics.Canvas;

/**
 * EntityGroup is a collection of Entity objects. Use this to either group
 * objects and their movement, or to ensure a specific drawing order.
 * @author lindhart
 */
public class EntityGroup extends Entity {

	protected ArrayList<Entity> entities = new ArrayList<Entity>();

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
		else
		{
			return null;
		}
	}
	
	/**
	 * EntityGroup's draw method diverges from regular Entity objects because
	 * it has no graphic itself.
	 */
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
		/*
		for (Entity e : entities) {
			if (e.collidesWithPoint(x, y)) return e;
		}*/
		return null;
	}
}
