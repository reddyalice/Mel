package com.alice.mel.systems;

import java.util.ArrayList;

import com.alice.mel.components.AffectedLightsComponent;
import com.alice.mel.components.ComponentType;
import com.alice.mel.components.Light;
import com.alice.mel.components.MaterialComponent;
import com.alice.mel.components.Transform;
import com.alice.mel.core.Engine;
import com.alice.mel.core.Family;
import com.alice.mel.entities.Entity;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.SnapshotArray;

public class RenderingSystem extends ComponentSystem{
    private ImmutableArray<Entity> entities;
    SnapshotArray<Entity> lights ;
    
	@Override
	public void addedToEngine (Engine engine) {
		entities = engine.getEntitiesFor(Family.all(Transform.class, MaterialComponent.class).get());
        ImmutableArray<Entity> lightEntities = engine.getEntitiesFor(Family.all(Transform.class, Light.class).get());
        lights = new SnapshotArray<Entity>(lightEntities.toArray());
	}

	@Override
	public void removedFromEngine (Engine engine) {

	}

    @Override
    public void update(float deltaTime) {
        
        
        for(Entity en : entities){
            
            if(en.hasComponent(ComponentType.getFor(AffectedLightsComponent.class))){
                Entity[] lightA = lights.begin();
                for(Entity l : lightA){

                }

            }

        }
        


    }



}
