package com.alice.mel.components;

import org.joml.Vector3f;

public final class Light implements Component{

    public float angle;
    public Vector3f color;
    public float range;

    public Light(Vector3f color, float range){
        this.color = color;
        this.range = range;

    }

    
}
