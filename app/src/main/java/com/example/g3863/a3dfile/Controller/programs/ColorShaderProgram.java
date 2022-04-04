/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.g3863.a3dfile.Controller.programs;

import android.content.Context;


import com.example.g3863.a3dfile.R;
import com.example.g3863.a3dfile.Model.Coordinate;
import com.example.g3863.a3dfile.Model.MatrixState;

import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1f;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class ColorShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uMatrixLocation;
    private final int uColorLocation;
    private final int uDistance;
    
    // Attribute locations
    private final int aPositionLocation;

    public ColorShaderProgram(Context context) {
        super(context, R.raw.simple_vertex_shader,
            R.raw.simple_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        uColorLocation = glGetUniformLocation(program, U_COLOR);
        uDistance = glGetUniformLocation(program,U_DISTANCE);
        
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
    }

    public void setUniforms(float r, float g, float b, float a) {
        glLineWidth(5f);
        glUniformMatrix4fv(uMatrixLocation, 1, false, MatrixState.getFinalMatrix(), 0);
        glUniform4f(uColorLocation, r, g, b, a);
    }

    public void setDistance(Coordinate coordinate){
        float[] cameraLoc = MatrixState.getCameraLocation();
        float[] objLoc = new float[]{coordinate.getx(),coordinate.gety(),coordinate.getz()};
        float output = 0f;
        for(int i = 0;i<3;i++){
            output+=Math.pow(cameraLoc[i]-objLoc[i],2);
        }
        glUniform1f(uDistance,(float) Math.sqrt(output));
    }

    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
}
