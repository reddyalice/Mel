package com.alice.mel.graphics.materials;


import com.alice.mel.components.AffectedLightsComponent;
import com.alice.mel.components.ComponentType;
import com.alice.mel.components.Light;
import com.alice.mel.components.Transform;
import com.alice.mel.core.Engine;
import com.alice.mel.core.Family;
import com.alice.mel.entities.Entity;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Texture;
import com.alice.mel.maths.MathUtils;
import com.alice.mel.utils.collections.ImmutableArray;
import com.alice.mel.utils.collections.ObjectSet;

import org.joml.Vector3f;
import org.joml.Matrix4f;


public class BasicMaterial extends Material {

    public final float shineDumper;
	public final float reflectivity;
	
	public final boolean hasTransparency;
	public final boolean useFakeLighting;

    public BasicMaterial(float shineDumper, float reflectivity,  boolean hasTransparency,  boolean useFakeLighting) {
            super(Shaders.BMS);
            this.shineDumper = shineDumper;
            this.reflectivity = reflectivity;
            this.hasTransparency = hasTransparency;
            this.useFakeLighting = useFakeLighting;

        
    }


    public BasicMaterial(Texture texture){
        this(1,0, false, false);
    }

    @Override
    public void Load(Entity entity){

            if(entity.hasComponent(ComponentType.getFor(Transform.class))){
                BasicModelShader shader = (BasicModelShader)this.shader;
                shader.LoadShineValues(shineDumper, reflectivity, useFakeLighting);
                
                if(entity.hasComponent(ComponentType.getFor(AffectedLightsComponent.class)))
                {
                    AffectedLightsComponent al = entity.getComponent(AffectedLightsComponent.class);
                    shader.LoadLight(al.positions.toArray(), al.lights.toArray());
                }

                Transform transform = entity.getComponent(Transform.class);
                Matrix4f transformationMatrix = MathUtils.CreateTransformationMatrix(transform.position, transform.rotation, transform.scale);

                shader.LoadTransformationMatrix(transformationMatrix);
            }
        
    }


    @Override
    public boolean equals(Object o) {
        if(o instanceof BasicMaterial){
            BasicMaterial other = (BasicMaterial)o;
                return shineDumper == other.shineDumper && reflectivity == other.reflectivity &&
                hasTransparency == other.hasTransparency && useFakeLighting == other.useFakeLighting;
        }else
        return false;
    }

}
