package com.alice.mel.systems;

import com.alice.mel.core.Engine;

public abstract class ComponentSystem {
    public int priority;
    private boolean processing;
	private Engine engine;

    public ComponentSystem () {
		this(0);
	}

	public ComponentSystem (int priority) {
		this.priority = priority;
		this.processing = true;
	}

    public void addedToEngine (Engine engine) {
	}
	
	public void removedFromEngine (Engine engine) {
	}

	
	public void update (float deltaTime) {
    }

    public boolean checkProcessing () {
		return processing;
	}

	
	public void setProcessing (boolean processing) {
		this.processing = processing;
	}
	

	public Engine getEngine () {
		return engine;
	}
	
	public final void addedToEngineInternal(Engine engine) {
		this.engine = engine;
		addedToEngine(engine);
	}
	
	public final void removedFromEngineInternal(Engine engine) {
		this.engine = null;
		removedFromEngine(engine);
	}

}
