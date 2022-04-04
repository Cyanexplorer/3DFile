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

import com.example.g3863.a3dfile.util.ShaderHelper;
import com.example.g3863.a3dfile.util.TextResourceReader;

import static android.opengl.GLES20.glUseProgram;

public abstract class ShaderProgram {
    // Uniform constants
    protected static final String U_MATRIX = "uMatrix";
    protected static final String U_MVPMATRIX = "uMVPMatrix";
    protected static final String U_MVMATRIX = "uMMatrix";
    protected static final String U_LIGHTLOCATION = "uLightLocation";
    protected static final String U_CAMERA = "uCamera";
    protected static final String U_COLOR = "uColor";
    protected static final String U_DISTANCE = "uDistance";
    protected static final String U_R = "uR";
    protected static final String U_TEMPERATURE = "uTemperature";

    protected static final String U_ABIENT = "uAbient";
    protected static final String U_DIFFUSE = "uDiffuse";
    protected static final String U_SPECULAR = "uSpecular";

    protected static final String U_TEXTURE_UNIT0 = "uTextureUnit0";
    protected static final String U_TEXTURE_UNIT1 = "uTextureUnit1";
    protected static final String U_TEXTURE_UNIT2 = "uTextureUnit2";

    // Attribute constants
    protected static final String A_NORMAL = "aNormal";
    protected static final String A_POSITION = "aPosition";
    protected static final String A_TEXTURE_COORDINATES = "aTextureCoordinates";

    // Shader program
    protected final int program;

    protected ShaderProgram(Context context, int vertexShaderResourceId,
        int fragmentShaderResourceId) {
        // Compile the shaders and link the program.
        program = ShaderHelper.buildProgram(
            TextResourceReader
                .readTextFileFromResource(context, vertexShaderResourceId),
            TextResourceReader
                .readTextFileFromResource(context, fragmentShaderResourceId));
    }        

    public void useProgram() {
        // Set the current OpenGL shader program to this program.
        glUseProgram(program);
    }
}
