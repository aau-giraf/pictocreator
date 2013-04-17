package dk.homestead.canvastest;

import java.util.ArrayList;

import android.graphics.Canvas;

/**
 * EntityGroup is a collection of Entity objects. Use this to either group
 * objects and their movement, or to ensure a specific drawing order.
 * @author lindhart
 */
public class EntityGroup extends Entity {

	ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public EntityGroup() {
		// TODO Auto-generated constructor stub
	}

	public void addEntity(Entity toAdd)
	{
		if (entities.contains(toAdd))
		{
			// Throw something. Possibly a vase.
		}
		else
		{
			entities.add(toAdd);
		}
	}
	
	public Entity removeEntity(Entity toRemove)
	{
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
	
	public int size(){
		return entities.size();
	}
}
