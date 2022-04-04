package com.example.g3863.a3dfile.Model;

import android.opengl.Matrix;

/**
 * Created by g3863 on 2017/10/20.
 */

public class Coordinate {
    private float x,y,z;
    private float[] rMatrix = new float[16];

    public Coordinate(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        Matrix.setRotateM(rMatrix,0,0,1,0,0);
    }

    public Coordinate setRotate(float rx,float ry,float rz){
        Matrix.setRotateM(rMatrix,0,0,1,0,0);
        rotate(rx,ry,rz);
        return this;
    }

    public Coordinate setMove(float x,float y,float z){
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Coordinate setMove(Coordinate coordinate){
        this.x = coordinate.getx();
        this.y = coordinate.gety();
        this.z = coordinate.getz();
        return this;
    }

    public float getx(){
        return this.x;
    }
    public float gety(){
        return this.y;
    }
    public float getz(){
        return this.z;
    }
    public float[] getrMatrix(){
        return rMatrix;
    }

    public void move(Coordinate coordinate){
        x+=coordinate.getx();
        y+=coordinate.gety();
        z+=coordinate.getz();
    }

    public Coordinate move(float x,float y,float z){
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    public void move(Coordinate coordinate,float x,float y,float z){
        this.x+=coordinate.getx()+x;
        this.y+=coordinate.gety()+y;
        this.z+=coordinate.getz()+z;
    }

    public void rotate(Coordinate coordinate){
        rotate(coordinate.getx(),coordinate.gety(),coordinate.getz());
    }

    public float[] getRadius(){
        return new float[]{
                (float) Math.atan(rMatrix[2]/rMatrix[10]),
                (float) Math.asin(-rMatrix[6]),
                (float) Math.atan(rMatrix[4]/rMatrix[5])
        };
    }

    public void rotate(float rx,float ry,float rz){
        float[] mx = new float[16];
        float[] my = new float[16];
        float[] mz = new float[16];
        Matrix.setRotateM(mx,0,rx,1f,0,0);
        Matrix.setRotateM(my,0,ry,0,1f,0);
        Matrix.setRotateM(mz,0,rz,0,0,1f);
        Matrix.multiplyMM(rMatrix,0,mx,0,rMatrix,0);
        Matrix.multiplyMM(rMatrix,0,my,0,rMatrix,0);
        Matrix.multiplyMM(rMatrix,0,mz,0,rMatrix,0);
    }

    public float getDistance(){
        return (float) Math.sqrt(Math.pow(x,2)+Math.pow(y,2)+Math.pow(z,2));
    }

    public float[] toFloat(){
        return new float[]{
                x,y,z
        };
    }

}
