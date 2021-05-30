package com.alice.mel.graphics;

import com.alice.mel.core.Engine;
import com.alice.mel.core.Family;
import com.alice.mel.entities.Entity;

public abstract class Material{

	public final Shader shader;


    public abstract void Load(Entity entity);
	
    public Material(Shader shader)
    {
        this.shader = shader;
    }



    
}
