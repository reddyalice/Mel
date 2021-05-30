#shader vertex
#version 400 core

in vec3 position;
in vec2 texCoords;
in vec3 normal;

out vec2 pass_texCoords;
out vec3 surfaceNormal;
out vec3[20] toLightVector;
out vec3 toCameraVector;
out float visibility;
out float _lightCount;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

uniform vec3[20] lightPosition;
uniform float lightCount;


uniform float useFakeLightning;

const float density = 0.007;
const float gradient = 1.5;


void main(void){

	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);
	vec4 positionRelativeToCamera  	= viewMatrix * worldPosition;
	vec3 actualNormal = normal;
	if(useFakeLightning > 0.5){
		actualNormal = vec3(0.0,1.0,0.0);
	}
	
	
	
	gl_Position = projectionMatrix * positionRelativeToCamera;
	
	
	pass_texCoords = texCoords;
	
	surfaceNormal = (transformationMatrix * vec4(normal, 0.0)).xyz;
	
	
	toLightVector[i]  = lightPosition[i] - worldPosition.xyz;
	
	toCameraVector  = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	_lightCount = lightCount;
	float distance = length(positionRelativeToCamera.xyz);
	visibility = exp(-pow((distance*density), gradient));
	visibility = clamp(visibility,0.0,1.0);
	
	
}


#shader fragment
#version 400 core

in vec2 pass_texCoords;
in vec3 surfaceNormal;
in vec3[20] toLightVector;
in float _lightCount;
in vec3 toCameraVector;
in float visibility;
out vec4 out_Color;


uniform sampler2D textureSampler;

uniform vec3[20] lightColor;
uniform float[20] lightRange;

uniform vec3 skyColor;

uniform float shineDumper;
uniform float reflectivity;

void main(void){

	vec3 uNormal = normalize(surfaceNormal);
	vec3 uCameraVector = normalize(toCameraVector);


	vec3[_lightCount] finalSpecular;
	vec3[_lightCount] diffuse;
	vec4 diffuseResult;
	vec4 specularResult;
	for(int i = 0; i < _lightCount; i++){
		float len = lightRange[i] - length(toLightVector[i]);

		vec3 uLightVector = normalize(toLightVector[i]);
		vec3 reflectedLightDirection = reflect(-uLightVector, uNormal);
		float specularFactor = dot(reflectedLightDirection, uCameraVector);
		specularFactor = max(specularFactor, 0.0);
		float dumpedFactor = pow(specularFactor, shineDumper);
		
		finalSpecular[i] = dumpedFactor * lightColor[i];
		if(i > 0)
			specularResult = specularResult + vec4(finalSpecular[i], 1.0);
		else
			specularResult = vec4(finalSpecular[i], 1.0);

		float nDot1 = dot(uNormal, uLightVector);
		float brightness = max(nDot1, 0.2);
		
		if(len > 0)
			diffuse[i] = (len / lightRange[i]) * brightness * lightColor;
		else
			diffuse[i] = 0.0 * brightness * lightColor;
		

		if(i > 0)
				diffuseResult = diffuseResult * vec4( diffuse[i], 1.0);
			else
				diffuseResult = vec4(diffuse[i], 1.0);



	}
	
	
	
	out_Color = diffuseResult * texture(textureSampler, pass_texCoords) + specularResult;
	out_Color = mix(vec4(skyColor, 1.0), out_Color, visibility);

}