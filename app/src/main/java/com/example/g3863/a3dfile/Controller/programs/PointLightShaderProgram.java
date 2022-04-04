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
import com.example.g3863.a3dfile.Model.MatrixState;

import static android.opengl.GLES10.GL_TEXTURE1;
import static android.opengl.GLES20.GL_TEXTURE0;
import static android.opengl.GLES20.GL_TEXTURE2;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.glActiveTexture;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glLineWidth;
import static android.opengl.GLES20.glUniform1i;
import static android.opengl.GLES20.glUniform3fv;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniformMatrix4fv;

public class PointLightShaderProgram extends ShaderProgram {
    // Uniform locations
    private final int uTextureUnitLocation0;
    private final int uTextureUnitLocation1;
    private final int uTextureUnitLocation2;

    private final int uMVPMatrixHandle;
    private final int uMMatrixHandle;
    private final int maCameraHandle;
    private final int muRHandle;
    private final int maLightLocationHandle;
    private final int maNormalHandle;
    private final int uTemperature;
    private final int uColorLocation;

    private final int uDiffuse;
    private final int uAbient;
    private final int uSpeclar;

    // Attribute locations
    private final int aPositionLocation;
    private final int aTextureCoordinatesLocation;

    public PointLightShaderProgram(Context context) {
        super(context, R.raw.light_vertex_shader,
            R.raw.light_fragment_shader);

        // Retrieve uniform locations for the shader program.
        uMVPMatrixHandle = glGetUniformLocation(program, U_MVPMATRIX);
        uMMatrixHandle =glGetUniformLocation(program,U_MVMATRIX);
        maCameraHandle=glGetUniformLocation(program,U_CAMERA);
        maLightLocationHandle=glGetUniformLocation(program,U_LIGHTLOCATION);
        muRHandle=glGetUniformLocation(program,U_R);

        uDiffuse = glGetUniformLocation(program, U_DIFFUSE);
        uAbient = glGetUniformLocation(program,U_ABIENT);
        uSpeclar = glGetUniformLocation(program,U_SPECULAR);

        uTextureUnitLocation0 = glGetUniformLocation(program, U_TEXTURE_UNIT0);
        uTextureUnitLocation1 = glGetUniformLocation(program, U_TEXTURE_UNIT1);
        uTextureUnitLocation2 = glGetUniformLocation(program, U_TEXTURE_UNIT2);

        uTemperature = glGetUniformLocation(program,U_TEMPERATURE);
        uColorLocation = glGetUniformLocation(program,U_COLOR);

        maNormalHandle=glGetAttribLocation(program,A_NORMAL);
        // Retrieve attribute locations for the shader program.
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        aTextureCoordinatesLocation = glGetAttribLocation(program, A_TEXTURE_COORDINATES);
    }

    public void setUniforms() {
        // Pass the matrix into the shader program.
        glUniformMatrix4fv(uMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        glUniformMatrix4fv(uMMatrixHandle,1,false,MatrixState.getMMatrix(),0);
        glUniform3fv(maCameraHandle, 1, MatrixState.cameraFB);
        //將光源位置傳入著色器程序

        glUniform3fv(maLightLocationHandle, 1, MatrixState.lightPositionFB);
    }

    public void setTexture(int[] textureId) {
        int[] textureSlot = new int[]{GL_TEXTURE0,GL_TEXTURE1,GL_TEXTURE2};
        int[] textureLoc = new int[]{uTextureUnitLocation0,uTextureUnitLocation1,uTextureUnitLocation2};

        for(int i = 0;i<textureId.length;i++){
            if(textureId[i]>-1){
                glActiveTexture(textureSlot[i]);
                glBindTexture(GL_TEXTURE_2D, textureId[i]);
                glUniform1i(textureLoc[i], i);
            }
        }
    }

    public void setLight(float[] a, float[] s, float[] d) {
        glLineWidth(5f);
        glUniform4f(uAbient, a[0], a[1], a[2], a[3]);
        glUniform4f(uDiffuse, d[0],d[1],d[2],d[3]);
        glUniform4f(uSpeclar, s[0], s[1], s[2], s[3]);
    }

    public void setuTemperature(){
        glUniform4f(uTemperature,1.0f,1.0f,1.0f,0.0f);
    }
    
    public int getPositionAttributeLocation() {
        return aPositionLocation;
    }
    public int getTextureCoordinatesAttributeLocation() {
        return aTextureCoordinatesLocation;
    }
    public int getNormalAttributeLocation() {
        return maNormalHandle;
    }
}