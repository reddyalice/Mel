package com.alice.mel.graphics;


public abstract class Material implements Comparable<Material>{

    public final Texture texture;
	public final Shader shader;
	

    public Material(Texture texture, Shader shader)
    {
        this.texture = texture;
        this.shader = shader;
    }



    
}
