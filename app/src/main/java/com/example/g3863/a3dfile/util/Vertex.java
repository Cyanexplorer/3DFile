package com.example.g3863.a3dfile.util;

/**
 * Created by g3863 on 2017/11/27.
 */

public class Vertex {

    public static float dot(float[] u,float[] v){
        return u[0]*v[0]+u[1]*v[1]+u[2]*v[2];
    }
    public static float[] minus(float[] u,float[] v){
        return new float[]{
                u[0]-v[0],u[1]-v[1],u[2]-v[2]
        };
    }
    public static float[] addition(float[] u,float[] v){
        return new float[]{
                u[0]+v[0],u[1]+v[1],u[2]+v[2]
        };
    }
    public static float[] scalerProduct(float r,float[] u){
        return new float[]{
                u[0]*r,u[1]*r,u[2]*r
        };
    }

    public static float[] product(float[] u,float[] v){
        return new float[]{
                u[0]*v[0],u[1]*v[1],u[2]*v[2]
        };
    }

    public static float[] crossProduct(float[] u,float[] v){
        return new float[]{
                u[1]*v[2]-u[2]*v[1],u[2]*v[0]-u[0]*v[2],u[0]*v[1]-u[1]*v[0]
        };
    }
    public static float length(float[] u){
        return (float)(Math.sqrt(Math.pow(u[0],2)+Math.pow(u[1],2)+Math.pow(u[2],2)));
    }

}
