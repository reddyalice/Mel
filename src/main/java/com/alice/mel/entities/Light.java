package com.alice.mel.entities;

import org.joml.Vector3f;

public class Light {

    public Vector3f position;
    public Vector3f rotation;
    public float angle;
    public Vector3f color;
    public float range;

    public Light(Vector3f position, Vector3f color, float range){
        this.position = position;
        this.color = color;
        this.range = range;

    }

    
}
