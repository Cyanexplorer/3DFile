
precision mediump float;

varying vec3 vPosition;
varying vec4 vAmbient;
varying vec4 vDiffuse;
varying vec4 vSpecular;
varying vec4 vShadowCoord;

uniform sampler2D uTextureUnit0;
uniform sampler2D uTextureUnit1;
uniform sampler2D uTextureUnit2;
uniform vec4 uTemperature;
varying vec2 vTextureCoordinates;

void main() {
    vec4 finalColor0=texture2D(uTextureUnit0, vTextureCoordinates)*vAmbient;
    vec4 finalColor1=texture2D(uTextureUnit1, vTextureCoordinates)*vDiffuse;
    vec4 finalColor2=texture2D(uTextureUnit2, vTextureCoordinates)*vSpecular;
    gl_FragColor=(finalColor0+finalColor1+finalColor2)*uTemperature;
}
