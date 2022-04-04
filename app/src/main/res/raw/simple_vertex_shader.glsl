uniform mat4 uMatrix;
uniform float uDistance;
attribute vec3 aNormal;
attribute vec4 aPosition;

void main()                    
{
    gl_PointSize = 5.0;//500.0 / uDistance;
    gl_Position = uMatrix * aPosition;
}          