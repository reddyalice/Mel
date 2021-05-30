package com.alice.mel.core;

import com.alice.mel.components.Component;
import com.alice.mel.core.ComponentOperationHandler.BooleanInformer;
import com.alice.mel.entities.Entity;
import com.alice.mel.entities.EntityListener;
import com.alice.mel.entities.EntityManager;
import com.alice.mel.systems.ComponentSystem;
import com.alice.mel.systems.SystemManager;
import com.alice.mel.systems.SystemManager.SystemListener;
import com.alice.mel.utils.Listener;
import com.alice.mel.utils.Signal;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.reflections.ClassReflection;
import com.alice.mel.utils.reflections.ReflectionException;

public class Engine {
	private static Family empty = Family.all().get();
	
	private final Listener<Entity> componentAdded = new ComponentListener();
	private final Listener<Entity> componentRemoved = new ComponentListener();
	
	private SystemManager systemManager = new SystemManager(new EngineSystemListener());
	private EntityManager entityManager = new EntityManager(new EngineEntityListener());
	private ComponentOperationHandler componentOperationHandler = new ComponentOperationHandler(new EngineDelayedInformer());
	private FamilyManager familyManager = new FamilyManager(entityManager.getEntities());	
	private boolean updating;

	/**
	 * Creates a new Entity object.
	 * @return @{@link Entity}
	 */

	public Entity createEntity () {
		return new Entity();
	}

	/**
	 * Creates a new {@link Component}. To use that method your components must have a visible no-arg constructor
	 */
	public <T extends Component> T createComponent (Class<T> componentType) {
		try {
			return ClassReflection.newInstance(componentType);
		} catch (ReflectionException e) {
			return null;
		}
	}


	public void addEntity(Entity entity){
		boolean delayed = updating || familyManager.notifying();
		entityManager.addEntity(entity, delayed);
	}

	/**
	 * Removes an entity from this Engine.
	 */
	public void removeEntity(Entity entity){
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeEntity(entity, delayed);
	}
	
	/**
	 * Removes all entities of the given {@link Family}.
	 */
	public void removeAllEntities(Family family) {
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeAllEntities(getEntitiesFor(family), delayed);
	}

	/**
	 * Removes all entities registered with this Engine.
	 */
	public void removeAllEntities() {
		boolean delayed = updating || familyManager.notifying();
		entityManager.removeAllEntities(delayed);
	}


	public ImmutableArray<Entity> getEntities() {
		return entityManager.getEntities();
	}

	/**
	 * Adds the {@link EntitySystem} to this Engine.
	 * If the Engine already had a system of the same class,
	 * the new one will replace the old one.
	 */
	public void addSystem(ComponentSystem system){
		systemManager.addSystem(system);
	}

	/**
	 * Removes the {@link EntitySystem} from this Engine.
	 */
	public void removeSystem(ComponentSystem system){
		systemManager.removeSystem(system);
	}

	/**
	 * Removes all systems from this Engine.
	 */
	public void removeAllSystems(){
		systemManager.removeAllSystems();
	}


	public <T extends ComponentSystem> T getSystem(Class<T> systemType) {
		return systemManager.getSystem(systemType);
	}


	public ImmutableArray<ComponentSystem> getSystems() {
		return systemManager.getSystems();
	}

	public ImmutableArray<Entity> getEntitiesFor(Family family){
		return familyManager.getEntitiesFor(family);
	}


	public void addEntityListener (EntityListener listener) {
		addEntityListener(empty, 0, listener);
	}


	public void addEntityListener (int priority, EntityListener listener) {
		addEntityListener(empty, priority, listener);
	}


	public void addEntityListener(Family family, EntityListener listener) {
		addEntityListener(family, 0, listener);
	}


	public void addEntityListener (Family family, int priority, EntityListener listener) {
		familyManager.addEntityListener(family, priority, listener);
	}


	public void removeEntityListener (EntityListener listener) {
		familyManager.removeEntityListener(listener);
	}

	
	public void update(float deltaTime){
		if (updating) {
			throw new IllegalStateException("Cannot call update() on an Engine that is already updating.");
		}
		
		updating = true;
		ImmutableArray<ComponentSystem> systems = systemManager.getSystems();
		try {
			for (int i = 0; i < systems.size(); ++i) {
				ComponentSystem system = systems.get(i);
				
				if (system.checkProcessing()) {
					system.update(deltaTime);
				}
	
				while(componentOperationHandler.hasOperationsToProcess() || entityManager.hasPendingOperations()) {
					componentOperationHandler.processOperations();
					entityManager.processPendingOperations();
				}
			}
		}
		finally {
			updating = false;
		}	
	}
	
	protected void addEntityInternal(Entity entity) {
		entity.componentAdded.add(componentAdded);
		entity.componentRemoved.add(componentRemoved);
		entity.componentOperationHandler = componentOperationHandler;
		
		familyManager.updateFamilyMembership(entity);
	}
	
	protected void removeEntityInternal(Entity entity) {
		familyManager.updateFamilyMembership(entity);

		entity.componentAdded.remove(componentAdded);
		entity.componentRemoved.remove(componentRemoved);
		entity.componentOperationHandler = null;
	}
	
	private class ComponentListener implements Listener<Entity> {
		@Override
		public void receive(Signal<Entity> signal, Entity object) {
			familyManager.updateFamilyMembership(object);
		}
	}
	
	private class EngineSystemListener implements SystemListener {
		@Override
		public void systemAdded (ComponentSystem system) {
			system.addedToEngineInternal(Engine.this);
		}

		@Override
		public void systemRemoved (ComponentSystem system) {
			system.removedFromEngineInternal(Engine.this);
		}
	}
	
	private class EngineEntityListener implements EntityListener {
		@Override
		public void entityAdded (Entity entity) {
			addEntityInternal(entity);
		}

		@Override
		public void entityRemoved (Entity entity) {
			removeEntityInternal(entity);
		}
	}
	
	private class EngineDelayedInformer implements BooleanInformer {
		@Override
		public boolean value () {
			return updating;
		}
	}
}
