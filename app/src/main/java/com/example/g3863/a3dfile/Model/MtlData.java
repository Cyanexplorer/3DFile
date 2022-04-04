package com.example.g3863.a3dfile.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import java.io.FileNotFoundException;

/**
 * Created by g3863 on 2017/12/6.
 */

public class MtlData {
    public String name;
    public String folderPath;
    public String mKa,mKd,mKs,mKe;
    public float Ns,Ni,d,Tr;
    public int illum;
    public float[] Tf;
    public float[] Ka;
    public float[] Kd;
    public float[] Ks;
    public float[] Ke;

    public MtlData(){
        name = "Default";
        folderPath = "";
        mKa = "";
        mKd = "";
        mKs = "";
        mKe = "";
        d = 0.5000f;
        Tr = 0.5000f;
        Tf = new float[]{0.5000f, 0.5000f, 0.5000f};
        illum = 2;
        Ns = 10f;
        Ni = 1.5f;
        Ka = new float[]{0.55f,0.55f,0.55f,0.55f};
        Kd = new float[]{1f,1f,1f,1f};
        Ks = new float[]{0f,0f,0f,0f};
    }

    public void setmKa(String path){
        mKd = path;
        Log.i("mtl_texture",path);
    }

    public void setColor(float[] Ka, float[] Kd){
        this.Kd = Kd;
    }

    public Bitmap generateKaB(Context context,int size, Direction direction) throws FileNotFoundException{
        return generateB(mKa,context,size, direction);
    }

    public Bitmap generateKdB(Context context,int size, Direction direction) throws FileNotFoundException{
        return generateB(mKd,context,size, direction);
    }
    public Bitmap generateKsB(Context context,int size, Direction direction) throws FileNotFoundException{
        return generateB(mKs,context,size, direction);
    }

    public Bitmap generateB(String path, Context context,int size, Direction direction) throws FileNotFoundException{
        BitmapFactory.Options options = new BitmapFactory.Options();

        if(!path.isEmpty()){

            Log.i("ImageLoad: ",path);
            options.inJustDecodeBounds = false;
            options.inSampleSize = options.outWidth/size;
            options.outHeight = options.outHeight*size;
            options.outWidth = options.outWidth*size;

            return flip(BitmapFactory.decodeFile(path,options),direction);
        }
        else{
            Log.i("ImageLoad: ","default");
            Bitmap b = Bitmap.createBitmap(100,100, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(b);
            canvas.drawColor(Color.WHITE);
            return b;
        }
    }

    public static enum Direction {UpsideDown, LeftRight, None};
    public static Bitmap flip(Bitmap src, Direction type) {
        if(src == null){
            return null;
        }

        Matrix matrix = new Matrix();

        if(type == Direction.UpsideDown) {
            matrix.preScale(1.0f, -1.0f);
        }
        else if(type == Direction.LeftRight) {
            matrix.preScale(-1.0f, 1.0f);
        } else {
            return src;
        }

        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }

    public void resetTxr(){
        mKa="";
        mKd="";
        mKe="";
        mKs="";
    }

    public void resetLight(){
        Ka = new float[]{1f,1f,1f,1f};
        Kd = new float[]{1f,1f,1f,1f};
        Ks = new float[]{1f,1f,1f,1f};
    }

    public String getFileName(String s){
        String[] parts = s.split("/");
        return parts[parts.length-1];
    }

}
