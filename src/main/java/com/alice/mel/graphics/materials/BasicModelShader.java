package com.alice.mel.graphics.materials;

import com.alice.mel.entities.Light;
import com.alice.mel.graphics.Shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;


public class BasicModelShader extends Shader {

    private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;

	private int location_lightPosition;
	private int location_lightColor;
	private int location_lightRange;
	private int location_lightCount;

	private int location_shineDumper;
	private int location_reflectivity;
	private int location_useFakeLightning;

	private int location_skyColor;


    public BasicModelShader() {
        super("src/main/java/com/alice/mel/graphics/shaders/BasicModelShader.glsl", false);
    }

    @Override
    protected void BindAttributes() {
        BindAttribute(0, "position");
		BindAttribute(1, "texCoords");
		BindAttribute(2, "normal");
    }

    @Override
    protected void GetAllUniformLocations() {
        location_transformationMatrix = GetUniformLocation("transformationMatrix");
		location_projectionMatrix = GetUniformLocation("projectionMatrix");
		location_viewMatrix = GetUniformLocation("viewMatrix");
		location_lightCount = GetUniformLocation("lightCount");
		location_lightRange = GetUniformLocation("lightRange");
		location_lightPosition = GetUniformLocation("lightPosition");
		location_lightColor = GetUniformLocation("lightColor");
		location_shineDumper = GetUniformLocation("shineDumper");
		location_reflectivity = GetUniformLocation("reflectivity");
		location_useFakeLightning = GetUniformLocation("useFakeLightning");
		location_skyColor = GetUniformLocation("skyColor");
    }

    public void LoadShineValues(float dumper, float reflectivity, boolean useFakeLightning){
		super.LoadFloat(location_shineDumper,dumper);
		super.LoadFloat(location_reflectivity,reflectivity);
		super.LoadBoolean(location_useFakeLightning, useFakeLightning);
	}

	public void LoadSkyColor(Vector3f color){
		super.LoadVector(location_skyColor, color);
	}

	public void LoadLight(Light[] lights){
		int length = lights.length > 20 ? 20 : lights.length;
		Vector3f[] positions = new Vector3f[20];
		Vector3f[] colors = new Vector3f[20];
		float[] ranges = new float[20];
		
		for(int i = 0; i < 20; i++)
		{
			if(i < length){
			positions[i] = lights[i].position;
			colors[i] = lights[i].color;
			ranges[i] = lights[i].range;
			}
		}

		super.LoadFloatArray(location_lightRange, ranges);
		super.LoadVectorArray(location_lightPosition, positions);
		super.LoadVectorArray(location_lightColor, colors);
		super.LoadFloat(location_lightCount, length);
	}
	

	public void LoadTransformationMatrix(Matrix4f matrix){
		super.LoadMatrix(location_transformationMatrix, matrix);
	}
	
	public void loadViewMatrix(Matrix4f matrix){
		super.LoadMatrix(location_viewMatrix, matrix);
	}
	
	public void LoadProjectionMatrix(Matrix4f matrix){
		super.LoadMatrix(location_projectionMatrix, matrix);
	}
	
	
	
	
    
}
