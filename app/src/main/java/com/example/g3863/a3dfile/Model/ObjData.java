package com.example.g3863.a3dfile.Model;

import android.util.Log;

import com.example.g3863.a3dfile.util.Vertex;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by g3863 on 2017/12/6.
 */

public class ObjData {
    public Vector<Position> vertice;
    public Vector<Texture> vTexture;
    public Vector<Normal> vNormal;
    public Vector<Face> faces;
    public String mtlPart;

    public ArrayList<Position> position;
    public ArrayList<Texture> pTexture;
    public ArrayList<Normal> pNormal;
    public MtlData mtlData;

    public ObjData(){
        mtlData = new MtlData();
        position = new ArrayList<>();
        pTexture = new ArrayList<>();
        pNormal = new ArrayList<>();
        mtlPart = "";

        vertice = new Vector<>();
        vTexture = new Vector<>();
        vNormal = new Vector<>();
        faces = new Vector<>();
    }

    public int getVerticeNum(){
        return position.size();
    }

    public void putvTexture(float... f){
        vTexture.add(new Texture(f[0],f[1]));
    }

    public void putvNormal(float... f){vNormal.add(new Normal(f[0],f[1],f[2]));}

    public void putFace(int... i){faces.add(new Face(i[0],i[1],i[2]));}

    public void putVertices(float... f){ vertice.add(new Position(f[0],f[1],f[2]));}

    public void loadVertices(){
        ArrayList<Position> buffer = new ArrayList<>();
        for(Face f:faces){
            int[] index = new int[]{f.p,f.t,f.n};
            if(index[0]>-1){
                putPosition(
                        vertice.get(index[0]).px,
                        vertice.get(index[0]).py,
                        vertice.get(index[0]).pz);
            }
            else {
                Log.i("error","no points");
            }
            if(index[1]>-1){
                putTexture(
                        vTexture.get(index[1]).tx,
                        vTexture.get(index[1]).ty
                );
            }
            else {
                putTexture(0,0);
            }
            if(index[2]>-1) {
                putNoremal(
                        vNormal.get(index[2]).nx,
                        vNormal.get(index[2]).ny,
                        vNormal.get(index[2]).nz
                );
            }
            else {
                //putNoremal(vertice.get(index[0]).px,vertice.get(index[0]).py,vertice.get(index[0]).pz);
                buffer.add(vertice.get(index[0]));
                if(buffer.size()==3) {
                    float[] v1 = new float[]{buffer.get(1).px-buffer.get(0).px,buffer.get(1).py-buffer.get(0).py,buffer.get(1).pz-buffer.get(0).pz};
                    float[] v2 = new float[]{buffer.get(2).px-buffer.get(1).px,buffer.get(2).py-buffer.get(1).py,buffer.get(2).pz-buffer.get(1).pz};
                    float[] norm = Vertex.crossProduct(v1,v2);
                    putNoremal(norm[0], norm[1], norm[2]);
                    putNoremal(norm[0], norm[1], norm[2]);
                    putNoremal(norm[0], norm[1], norm[2]);
                    buffer.clear();
                }
            }
        }

    }

    public void putPosition(float... f){
        position.add(new Position(f[0],f[1],f[2]));
    }

    private void putTexture(float... f){
        pTexture.add(new Texture(f[0],f[1]));
    }

    private void putNoremal(float... f){
        pNormal.add(new Normal(f[0],f[1],f[2]));
    }

    public class Position{
        public float px,py,pz;
        public boolean remove;
        public Position(){
            px = 0;
            py = 0;
            pz = 0;
            remove = false;
        }

        public Position(float x, float y,float z){
            px = x;
            py = y;
            pz = z;
            remove = false;
        }

        public float getDistance(){
            double d = Math.pow(px,2)+ Math.pow(py,2)+Math.pow(pz,2);
            return (float) Math.sqrt(d);
        }
    }

    public class Texture{
        public float tx,ty;
        public Texture(){
            tx = 0;
            ty = 0;
        }

        public Texture(float x, float y){
            tx = x;
            ty = y;
        }
    }

    public class Normal{
        public float nx,ny,nz;
        public Normal(){
            nx = 0;
            ny = 0;
            nz = 0;
        }

        public Normal(float x, float y,float z){
            nx = x;
            ny = y;
            nz = z;
        }
    }

    public class Face{
        public int p;
        public int t;
        public int n;
        public Face(){
            p = -1;
            t = -1;
            n = -1;
        }

        public Face(int p, int t,int n){
            this.p = p;
            this.t = t;
            this.n = n;
        }
    }

    public String getMtlName(){
        if(mtlData!=null){
            return mtlData.name;
        }
        return "";
    }

    public boolean isMtlDisable(){
        return  getMtlName()!=null;
    }
}

