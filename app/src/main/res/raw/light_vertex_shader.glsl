
uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
uniform vec3 uLightLocation;
uniform vec3 uCamera;
uniform vec4 uAbient;
uniform vec4 uDiffuse;
uniform vec4 uSpecular;
attribute vec3 aPosition;
attribute vec3 aNormal;
varying vec3 vPosition;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying vec4 vShadowCoord;

attribute vec2 aTextureCoordinates;
varying vec2 vTextureCoordinates;

void directionLight(
    in vec3 normal,
    inout vec4 ambient,
    inout vec4 diffuse,
    inout vec4 specular,
    in vec3 lightDirection,
    in vec4 lightAmbient,
    in vec4 lightDiffuse,
    in vec4 lightSpecular
){
    ambient = lightAmbient;

    vec3 normalTarget=aPosition+normal;
    vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
    newNormal=normalize(newNormal);
    vec3 eye=normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
    vec3 vp=normalize(lightDirection);
    vec3 halfVector=normalize(vp+eye);

    float shininess=100.0;
    float nDotViewPPosition=max(0.0,dot(newNormal,vp));
    diffuse=lightDiffuse*nDotViewPPosition;

    float nDotViewHalfVector=dot(newNormal,halfVector);
    float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));
    specular=lightSpecular*powerFactor;
}

void pointLight(
    in vec3 normal,
    inout vec4 ambient,
    inout vec4 diffuse,
    inout vec4 specular,
    in vec3 lightLocation,
    in vec4 lightAmbient,
    in vec4 lightDiffuse,
    in vec4 lightSpecular
){
    ambient = lightAmbient;

    vec3 normalTarget=aPosition+normal;
    vec3 newNormal=(uMMatrix*vec4(normalTarget,1)).xyz-(uMMatrix*vec4(aPosition,1)).xyz;
    newNormal=normalize(newNormal);
    vec3 eye=normalize(uCamera-(uMMatrix*vec4(aPosition,1)).xyz);
    vec3 vp=normalize(lightLocation-(uMMatrix*vec4(aPosition,1)).xyz);
    vec3 halfVector=normalize(vp+eye);

    float shininess=80.0;
    float nDotViewPPosition=max(0.0,dot(newNormal,vp));
    diffuse=lightDiffuse*nDotViewPPosition;

    float nDotViewHalfVector=dot(newNormal,halfVector);
    float powerFactor=max(0.0,pow(nDotViewHalfVector,shininess));
    specular=lightSpecular*powerFactor;
}

void main() {
    vTextureCoordinates = aTextureCoordinates;
    gl_Position=uMVPMatrix*vec4(aPosition,1);
    //vShadowCoord = uMVPMatrix*vec4(aPosition,1);
    vec4 ambientTemp,diffuseTemp,specularTemp;

    directionLight(normalize(aNormal),ambientTemp,diffuseTemp,specularTemp,uLightLocation,
    uAbient,uDiffuse,uSpecular);

    vAmbient=ambientTemp*vec4(0.3,0.3,0.3,1.0);
    vDiffuse=diffuseTemp*vec4(0.8,0.8,0.8,1.0);
    vSpecular=specularTemp;
    vPosition=aPosition;
}
