package com.example.g3863.a3dfile.Model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by g3863 on 2017/12/6.
 */

public class Scene {
    private Vector<Float> normal;
    private Vector<Float> textures;
    private Vector<Float> vertices;
    private Vector<String> faces;
    private String usemtl;
    public String mtlpart;
    public ArrayList<ObjData> objectArrayList;

    public String folderPath;


    public Scene(String path){
        folderPath = "";
        usemtl = "";
        normal = new Vector<>();
        textures = new Vector<>();
        vertices = new Vector<>();
        faces = new Vector<>();
        mtlpart = "";
        objectArrayList = new ArrayList<>();

        folderPath = path;
        Log.i("test",folderPath);
    }

    public String getMtlPath(){

        if(folderPath.isEmpty() || usemtl.isEmpty()){
            return "";
        }

        String[] patrs = usemtl.split("/");

        if(patrs.length == 1){
            return folderPath.concat(usemtl);
        }

        else if(patrs[0].equals(".")){
            String output = "";
            output = output.concat(folderPath);
            for(int i = 1;i<patrs.length;i++){
                output = output.concat(patrs[1]);
            }
            return output;
        }

        else {
            return usemtl;
        }
    }

    public void setUsemtl(String usemtl){
        this.usemtl = usemtl;
    }

    public void setMtlpart(String mtlpart){
        this.mtlpart = mtlpart;
    }

    public void putVertices(float... f){
        vertices.add(f[0]);
        vertices.add(f[1]);
        vertices.add(f[2]);
    }

    public void putTexture(float... f){
        textures.add(f[0]);
        textures.add(f[1]);
    }

    public void putNormal(float... f){
        normal.add(f[0]);
        normal.add(f[1]);
        normal.add(f[2]);
    }

    public void putFace(String... s){
        for(int i = 2;i<s.length;i++){
            faces.add(s[0]);
            faces.add(s[i-1]);
            faces.add(s[i]);
        }
    }

    public void generate(){
        ObjData objData = new ObjData();
        objData.mtlPart = mtlpart;
        for(int i= 0;i<vertices.size();i+=3){
            objData.putVertices(vertices.get(i),vertices.get(i+1),vertices.get(i+2));
        }
        for(int i= 0;i<textures.size();i+=2){
            objData.putvTexture(textures.get(i),textures.get(i+1));
        }
        for(int i= 0;i<normal.size();i+=3){
            objData.putvNormal(normal.get(i),normal.get(i+1),normal.get(i+2));
        }
        for(int i = 0;i<faces.size();i++){
            String[] parts = faces.get(i).split("/");
            int[] index = new int[]{-1,-1,-1};
            for(int j = 0;j<parts.length;j++){
                index[j] = Integer.parseInt(parts[j])-1;
            }

            objData.putFace(index[0],index[1],index[2]);
        }
        objData.loadVertices();
        objectArrayList.add(objData);
        faces.clear();
    }

    public boolean newRoutine(){
        return !faces.isEmpty();
    }

}
