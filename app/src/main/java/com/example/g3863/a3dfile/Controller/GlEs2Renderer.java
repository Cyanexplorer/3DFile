/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.g3863.a3dfile.Controller;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;

import com.example.g3863.a3dfile.Controller.Manager.ObjManager;
import com.example.g3863.a3dfile.Model.MatrixState;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static com.example.g3863.a3dfile.util.Constants.DRAG;
import static com.example.g3863.a3dfile.util.Constants.ZOOM;

public class GlEs2Renderer implements Renderer{
    //private final FloatBuffer vertexData;
    private final Context context;
    private ObjManager objectSample;

    public static float[] mc = new float[]{0f,0f,0f,1f,0f};
    private final float touchSpeed = 0.1f;
    private final float scale = 1.05f;

    public GlEs2Renderer(Context context) {
        this.context = context;
        objectSample = ObjManager.getInstance(context);
    }


    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.8f, 0.8f, 0.8f, 0.2f);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        GLES20.glDepthMask(true);

        objectSample.initialShader(context);
        objectSample.stdTools(context);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {                
        // Set the OpenGL viewport to fill the entire surface.
        float Ratio=1f;
        glViewport(0, 0, width, height);
        final float aspectRatio = (float) width / (float) height;

        MatrixState.width = width;
        MatrixState.height = height;
        MatrixState.setProjectFrustum(-aspectRatio*Ratio,aspectRatio*Ratio,-Ratio,Ratio,1f,8000f);
        MatrixState.setCamera(0f, -100f, 0f, 0f, 0f, 0f, 0f, 0f, 1f);
        //MatrixState.setCamera(0f, 12f, 0f, 0f, 0f, 0f, 0f, 1f, 0f);
        MatrixState.setInitStack();
        MatrixState.setLightLocation(100f,-100f,100f);
        mc[1] = 45f;
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        MatrixState.pushMatrix();
        MatrixState.transView(0,mc[2],0);
        MatrixState.rotation(mc[1],1f,0,0);
        MatrixState.rotation(mc[0],0,0,1f);


        MatrixState.pushMatrix();
        //GLES20.glClearDepthf(1.0f);
        GLES20.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        drawFrag();
        MatrixState.popMatrix();
        MatrixState.popMatrix();
    }

    private void drawFrag(){
        ObjManager.MarbleDrawer();
    }

    public void MatrixOperate(int type,float[] para){
        if(type == ZOOM && para.length >= 1){
            if(para[0]<1f){
                mc[2]+=scale;
            }
            else mc[2]-=scale;

        }
        else if(type == DRAG && para.length >= 2){

            mc[0]+=touchSpeed*para[0];
            if(Math.abs(mc[1]+touchSpeed*para[1])<90){
                mc[1]+=touchSpeed*para[1];
            }

        }
    }

}