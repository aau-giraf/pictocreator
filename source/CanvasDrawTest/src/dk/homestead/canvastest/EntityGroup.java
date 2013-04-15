package dk.homestead.canvastest;

import java.util.ArrayList;

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
}
