package com.alice.mel.core;

import java.util.HashMap;

import com.alice.mel.components.Component;
import com.alice.mel.entities.Entity;
import com.alice.mel.utils.Pool;
import com.alice.mel.utils.ReflectionPool;
import com.alice.mel.utils.Pool.Poolable;
import com.alice.mel.utils.collections.Array;

public class PooledEngine extends Engine {

	private EntityPool entityPool;
	private ComponentPools componentPools;

	
	public PooledEngine () {
		this(10, 100, 10, 100);
	}

	

	public PooledEngine (int entityPoolInitialSize, int entityPoolMaxSize, int componentPoolInitialSize, int componentPoolMaxSize) {
		super();

		entityPool = new EntityPool(entityPoolInitialSize, entityPoolMaxSize);
		componentPools = new ComponentPools(componentPoolInitialSize, componentPoolMaxSize);
	}

	
	@Override
	public Entity createEntity () {
		return entityPool.obtain();
	}

	
	@Override
	public <T extends Component> T createComponent (Class<T> componentType) {
		return componentPools.obtain(componentType);
	}

	
	public void clearPools () {
		entityPool.clear();
		componentPools.clear();
	}

	@Override
	protected void removeEntityInternal (Entity entity) {
		super.removeEntityInternal(entity);

		if (entity instanceof PooledEntity) {
			entityPool.free((PooledEntity)entity);
		}
	}

	private class PooledEntity extends Entity implements Poolable {
		@Override
        public Component removeInternal(Class<? extends Component> componentClass) {
			Component removed = super.removeInternal(componentClass);
			if (removed != null) {
				componentPools.free(removed);
			}

			return removed;
		}

		@Override
		public void reset () {
			removeAll();
			flags = 0;
			componentAdded.removeAllListeners();
			componentRemoved.removeAllListeners();
			scheduledForRemoval = false;
			removing = false;
		}
	}

	private class EntityPool extends Pool<PooledEntity> {

		public EntityPool (int initialSize, int maxSize) {
			super(initialSize, maxSize);
		}

		@Override
		protected PooledEntity newObject () {
			return new PooledEntity();
		}
	}

	private class ComponentPools {
		private HashMap<Class<?>, ReflectionPool> pools;
		private int initialSize;
		private int maxSize;

		public ComponentPools (int initialSize, int maxSize) {
			this.pools = new HashMap<Class<?>, ReflectionPool>();
			this.initialSize = initialSize;
			this.maxSize = maxSize;
		}

		public <T> T obtain (Class<T> type) {
			ReflectionPool pool = pools.get(type);

			if (pool == null) {
				pool = new ReflectionPool(type, initialSize, maxSize);
				pools.put(type, pool);
			}

			return (T)pool.obtain();
		}

		public void free (Object object) {
			if (object == null) {
				throw new IllegalArgumentException("object cannot be null.");
			}

			ReflectionPool pool = pools.get(object.getClass());

			if (pool == null) {
				return; // Ignore freeing an object that was never retained.
			}

			pool.free(object);
		}

		public void freeAll (Array objects) {
			if (objects == null) throw new IllegalArgumentException("objects cannot be null.");

			for (int i = 0, n = objects.size; i < n; i++) {
				Object object = objects.get(i);
				if (object == null) continue;
				free(object);
			}
		}

		public void clear () {
			for (Pool pool : pools.values()) {
				pool.clear();
			}
		}
	}
}
