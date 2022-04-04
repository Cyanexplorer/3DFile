package com.example.g3863.a3dfile.util;

import android.opengl.Matrix;
import android.util.Log;

import com.example.g3863.a3dfile.Model.MatrixState;

import java.util.Arrays;
import java.util.Locale;

/**
 * Created by g3863 on 2017/11/25.
 */

public class RayPicking {
    public static float[] unproject(float x,float y,float z,float w,float h,float[] pMatrix){
        float[] viewport = new float[]{0,0, MatrixState.width,MatrixState.height};
        float[] output = new float[16];
        Matrix.invertM(output,0,MatrixState.getFinalMatrix(),0);

        float winy = viewport[3] - y;
        float[] result = new float[4];
        float vx = viewport[0];
        float vy = viewport[1];
        float vw = viewport[2];
        float vh = viewport[3];

        float[] rnsVec = {2*(x-vx)/vw-1,2*(winy-vy)/vh-1,2*z-1,1};
        Matrix.multiplyMV(result,0,output,0,rnsVec,0);
        float d = 1/result[3];
        return new float[]{result[0]*d,result[1]*d,result[2]*d};
    }
    public static Ray getRay(float x,float y,int width, int height,float[] FM){
        float[] nearCoOrds = unproject(x,y,-1f,width,height,FM);
        float[] farCoOrds = unproject(x,y,1f,width,height,FM);

        Log.i("test",String.format(Locale.getDefault(),"%f,%f,%f/%f,%f,%f",nearCoOrds[0],nearCoOrds[1],nearCoOrds[2],farCoOrds[0],farCoOrds[1],farCoOrds[2]));
        return new Ray(nearCoOrds,farCoOrds);
    }

    public static float[] crossoint(Ray ray,float[] n,float[] c){
        float[] dir,w0;
        float r,a,b;

        dir = Vertex.minus(ray.farCoord,ray.nearCord);
        w0 = Vertex.minus(ray.nearCord,c);
        a = Vertex.dot(n,w0);
        b = -Vertex.dot(n,dir);
        if(Math.abs(b)<0.00000001f){
            if(a == 0){
                return null;
            }
            else {
                return null;
            }
        }

        r = a/b;
        if(r<0f){
            return null;
        }

        return Vertex.addition(ray.nearCord,Vertex.scalerProduct(r,dir));
    }

    public static int intersection(Ray ray,Triangle T,float[] I){
        float[] u,v,n;
        float[] dir,w0;
        float r,a,b;

        u = Vertex.minus(T.v2,T.v1);
        v = Vertex.minus(T.v3,T.v1);
        n = Vertex.crossProduct(u,v);

        if(Arrays.equals(n,new float[]{0,0,0})){
            return  -1;
        }

        dir = Vertex.minus(ray.farCoord,ray.nearCord);
        w0 = Vertex.minus(ray.nearCord,T.v1);
        a = Vertex.dot(n,w0);
        b = -Vertex.dot(n,dir);

        if(Math.abs(b)<0.00000001f){
            if(a == 0){
                return 2;
            }
            else {
                return 0;
            }
        }

        r = a/b;
        if(r<0f){
            return 0;
        }

        float[] tempI = Vertex.addition(ray.nearCord,Vertex.scalerProduct(r,dir));
        I[0] = tempI[0];
        I[1] = tempI[1];
        I[2] = tempI[2];

        float uu = Vertex.dot(u,u);
        float uv = Vertex.dot(u,v);
        float vv = Vertex.dot(v,v);
        float[] w = Vertex.minus(I,T.v1);
        float wv = Vertex.dot(w,v);
        float wu = Vertex.dot(w,u);
        float D = uv*uv - uu*vv;



        float s,t;
        s = (uv*wv - vv*wu)/D;
        if(s<0f||s>1f){
            return 0;
        }
        t = (uv*wu-uu*wv)/D;
        if(t<0||(s+t)>1f){
            return 0;
        }
        return 1;

    }

    public static class Triangle{
        public float[] v1,v2,v3;
        public Triangle(){
            v1=v2=v3=new float[]{0,0,0};
        }
        public Triangle(float[] v1,float[] v2,float[] v3){
            this.v1 = v1;
            this.v2 = v2;
            this.v3 = v3;
        }
    }

    public static class Ray{
        public float[] farCoord;
        public float[] nearCord;
        public Ray(){
            farCoord = new float[]{0,0,0};
            nearCord = new float[]{0,0,0};
        }

        public Ray(float[] near,float[] far){
            farCoord = far;
            nearCord = near;
        }
    }
}
