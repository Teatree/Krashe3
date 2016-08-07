package com.uwsoft.editor.renderer.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.SnapshotArray;

import java.util.HashMap;
import java.util.Map;

public class NodeComponent implements Component {
	public SnapshotArray<Entity> children = new SnapshotArray<Entity>(true, 1, Entity.class);
	public Map<String, Entity> childrenWithNames = new HashMap<String, Entity>();

	public void removeChild(Entity entity) {
		children.removeValue(entity, false);
		childrenWithNames.remove(entity);
	}

	public void addChild(Entity entity) {
		children.add(entity);
		childrenWithNames.put(entity.getComponent(MainItemComponent.class).itemIdentifier, entity);
	}

	public Entity getChild(String name){
		return childrenWithNames.get(name);
	}
}
