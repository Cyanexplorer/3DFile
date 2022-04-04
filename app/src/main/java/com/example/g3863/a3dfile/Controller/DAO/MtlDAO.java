package com.example.g3863.a3dfile.Controller.DAO;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Model.Scene;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by g3863 on 2017/12/6.
 */

public class MtlDAO {
    public static ArrayList<MtlData> mtlDataArrayList = new ArrayList<>();
    public static String folderPath;

    //initial parameters
    public static void loadMtl(Context context, Scene scene){
        mtlDataArrayList.clear();

        Uri path = Uri.parse("file:///"+scene.getMtlPath());
        folderPath = scene.folderPath;

        try {
            InputStream inputStream = context
                    .getContentResolver()
                    .openInputStream(path);
            loadTexture(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //seach suitable mtl data for specified obj model
    public static MtlData searchData(String name){
        MtlData mtlData = new MtlData();
        for(MtlData data:mtlDataArrayList){
            Log.i("mtl_name",data.name);
            if(data.name.equals(name)){
                mtlData = data;
            }
        }
        return mtlData;
    }

    //get mtl info from the mtl file
    private static void loadTexture(InputStream inputStream){

        MtlData mtlData = new MtlData();

        BufferedReader reader = null;
        try {

            InputStreamReader in = new InputStreamReader(inputStream);
            reader = new BufferedReader(in);

            String line;
            Vector<String> p = new Vector<>();
            while ((line = reader.readLine())!=null){
                p.clear();
                StringTokenizer tokenizer = new StringTokenizer(line);
                if(tokenizer.hasMoreTokens()){
                    while (tokenizer.hasMoreTokens()){
                        p.add(tokenizer.nextToken());
                    }
                    String[] parts = p.toArray(new String[p.size()]);
                    switch (parts[0]){
                        case "newmtl":
                            if(!mtlData.name.isEmpty() && !mtlData.name.equals("Default")){
                                mtlDataArrayList.add(mtlData);
                            }
                            mtlData = new MtlData();
                            mtlData.folderPath = folderPath;
                            mtlData.name = parts[1];
                            break;
                        case "Ka":
                            mtlData.Ka[0] = Float.parseFloat(parts[1]);
                            mtlData.Ka[1] = Float.parseFloat(parts[2]);
                            mtlData.Ka[2] = Float.parseFloat(parts[3]);
                            break;
                        case "Kd":
                            mtlData.Kd[0] = Float.parseFloat(parts[1]);
                            mtlData.Kd[1] = Float.parseFloat(parts[2]);
                            mtlData.Kd[2] = Float.parseFloat(parts[3]);
                            break;
                        case "Ks":
                            mtlData.Ks[0] = Float.parseFloat(parts[1]);
                            mtlData.Ks[1] = Float.parseFloat(parts[2]);
                            mtlData.Ks[2] = Float.parseFloat(parts[3]);
                            break;
                        case "Tf":
                            mtlData.Tf[0] = Float.parseFloat(parts[1]);
                            mtlData.Tf[1] = Float.parseFloat(parts[2]);
                            mtlData.Tf[2] = Float.parseFloat(parts[3]);
                            break;
                        case "illum":
                            mtlData.illum = Integer.parseInt(parts[1]);
                            break;
                        case "d":
                            mtlData.d = Float.parseFloat(parts[1]);
                            break;
                        case "Ns":
                            mtlData.Ns = Float.parseFloat(parts[1]);
                            break;
                        case "sharpness":
                            break;
                        case "Ni":
                            mtlData.Ni = Float.parseFloat(parts[1]);
                            break;
                        case "map_Ka":
                            if(parts.length>1){
                                for(int i = 1;i<parts.length;i++){
                                    mtlData.mKa = mtlData.mKa.concat(parts[i]+" ");
                                }
                                String[] mKaParts = mtlData.mKa.split(" ");
                                String[] paths = mKaParts[mKaParts.length-1].replace("\\","\\\\").split("\\\\");
                                mtlData.mKa = folderPath.concat(paths[paths.length-1]);
                            }

                            break;
                        case "map_Kd":
                            if(parts.length>1){
                                for(int i = 1;i<parts.length;i++){
                                    mtlData.mKd = mtlData.mKd.concat(parts[i]+" ");
                                }
                                String[] mKaParts = mtlData.mKd.split(" ");
                                String[] paths = mKaParts[mKaParts.length-1].replace("\\","\\\\").split("\\\\");
                                mtlData.mKd = folderPath.concat(paths[paths.length-1]);
                            }
                            break;
                        case "map_Ks":
                            if(parts.length>1){
                                for(int i = 1;i<parts.length;i++){
                                    mtlData.mKs = mtlData.mKs.concat(parts[i]+" ");
                                }
                                String[] mKaParts = mtlData.mKs.split(" ");
                                String[] paths = mKaParts[mKaParts.length-1].replace("\\","\\\\").split("\\\\");
                                mtlData.mKs = folderPath.concat(paths[paths.length-1]);
                            }
                        case "map_Ns":
                            break;
                        case "map_d":
                            break;
                        case "disp":
                            break;
                        case "decal":
                            break;
                        case "bump":case "map_bump":
                            break;
                        default:
                            break;
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (reader!=null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        mtlDataArrayList.add(mtlData);
    }

}
