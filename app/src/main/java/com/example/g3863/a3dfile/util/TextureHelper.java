/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.g3863.a3dfile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.nio.ByteBuffer;

import static android.opengl.GLES20.GL_CLAMP_TO_EDGE;
import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_NEAREST;
import static android.opengl.GLES20.GL_RGBA;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_S;
import static android.opengl.GLES20.GL_TEXTURE_WRAP_T;
import static android.opengl.GLES20.GL_UNSIGNED_BYTE;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexImage2D;
import static android.opengl.GLES20.glTexParameterf;
import static android.opengl.GLES20.glTexParameteri;
import static android.opengl.GLUtils.texImage2D;

public class TextureHelper {
    private static final String TAG = "TextureHelper";

    /**
     * Loads a pTexture from a resource ID, returning the OpenGL ID for that
     * pTexture. Returns 0 if the load failed.
     *
     * @return
     */

    public static int getTextureId(){
        int[] textureObjectIds = new int[1];
        glGenTextures(1, textureObjectIds, 0);
        Log.i("gltexture",String.valueOf(textureObjectIds[0]));
        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL pTexture object.");
            }

            return -1;
        }
        return textureObjectIds[0];
    }

    public static boolean loadTexture(Bitmap[] bitmap,int[] id) {
        int[] textureObjectIds = id;

        for(int i = 0;i<bitmap.length && i<textureObjectIds.length;i++){

            if (textureObjectIds[i]<0 || bitmap[i]==null || bitmap[i].isRecycled()) {
                glDeleteTextures(1, new int[]{textureObjectIds[i]}, 0);
                Log.w(TAG,"empty bitmap or has been Recycled");
                continue;
            }

            byte[] bytes = new byte[bitmap[i].getWidth()*bitmap[i].getHeight()*4];
            for(int y = 0;y<bitmap[i].getHeight();y++){
                for(int x = 0;x<bitmap[i].getWidth();x++){
                    int p = bitmap[i].getPixel(x,y);
                    bytes[(y*bitmap[i].getWidth()+x)*4] = (byte)((p>>16)&0xff);
                    bytes[(y*bitmap[i].getWidth()+x)*4+1] = (byte)((p>>8)&0xff);
                    bytes[(y*bitmap[i].getWidth()+x)*4+2] = (byte)(p&0xff);
                }
            }

            ByteBuffer buffer = ByteBuffer.allocateDirect(bitmap[i].getWidth()*bitmap[i].getHeight()*4);
            buffer.put(bytes).position(0);

            glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,bitmap[i].getWidth(),bitmap[i].getHeight(),0,GL_RGBA,GL_UNSIGNED_BYTE,buffer);

            bindTexture(textureObjectIds[i],bitmap[i]);
            Log.i("bind","textureId"+String.valueOf(textureObjectIds[i]));
            bitmap[i].recycle();
        }

        return true;
    }

    private static void bindTexture(int id,Bitmap bitmap){
// Bind to the pTexture in OpenGL
        glBindTexture(GL_TEXTURE_2D, id);

        // Set filtering: a default must be set, or the pTexture will be
        // black.
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        // Load the bitmap into the bound pTexture.
        texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

        // Note: Following code may cause an error to be reported in the
        // ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
        // Failed to generate pTexture mipmap levels (error=3)
        // No OpenGL error will be encountered (glGetError() will return
        // 0). If this happens, just squash the source image to be
        // square. It will look the same because of pTexture coordinates,
        // and mipmap generation will work.
        glGenerateMipmap(GL_TEXTURE_2D);

        // Recycle the bitmap, since its data has been loaded into
        // OpenGL.

        glBindTexture(GL_TEXTURE_2D, 0);
    }
}


