package com.alice.mel.graphics.materials;

import com.alice.mel.entities.Light;
import com.alice.mel.graphics.Material;
import com.alice.mel.graphics.Texture;

public class BasicMaterial extends Material {

    public final float shineDumper;
	public final float reflectivity;
	
	public final boolean hasTransparency;
	public final boolean useFakeLighting;

    public BasicMaterial(Texture texture, float shineDumper, float reflectivity,  boolean hasTransparency,  boolean useFakeLighting) {
            super(texture, Shaders.BMS);
            this.shineDumper = shineDumper;
            this.reflectivity = reflectivity;
            this.hasTransparency = hasTransparency;
            this.useFakeLighting = useFakeLighting;

        
    }


    public BasicMaterial(Texture texture){
        this(texture, 1,0, false, false);
    }


    public void Load(Light[] lights){
        BasicModelShader shader = (BasicModelShader)this.shader;
        shader.LoadShineValues(shineDumper, reflectivity, useFakeLighting);
        shader.LoadLight(lights);
    }


    @Override
    public int compareTo(Material o) {
        if(o instanceof BasicMaterial){
            BasicMaterial other = (BasicMaterial)o;
            if(shineDumper == other.shineDumper && reflectivity == other.reflectivity &&
                hasTransparency == other.hasTransparency && useFakeLighting == other.useFakeLighting &&
                    texture == other.texture)
                return 0;
            else 
                return -1;
        }else
        return -1;
    }

}
