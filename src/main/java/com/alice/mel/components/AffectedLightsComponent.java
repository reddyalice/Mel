package com.alice.mel.components;

import com.alice.mel.entities.Entity;
import com.alice.mel.utils.collections.ObjectSet;

import org.joml.Vector3f;

public final class AffectedLightsComponent implements Component {

    public ObjectSet<Vector3f> positions = new ObjectSet<Vector3f>();
    public ObjectSet<Light> lights = new ObjectSet<Light>();

    
}
