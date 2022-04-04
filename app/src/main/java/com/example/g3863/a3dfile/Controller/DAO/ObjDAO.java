package com.example.g3863.a3dfile.Controller.DAO;

import android.content.Context;
import android.net.Uri;

import com.example.g3863.a3dfile.Model.Scene;
import com.example.g3863.a3dfile.util.FileInputPath;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Created by g3863 on 2017/10/18.
 */

public class ObjDAO {

    public static Scene loadModel(Context a, Uri path, float scale){
        try {
            InputStream inputStream = a.getContentResolver().openInputStream(path);

            //load default path
            String formerPath;
            String[] parts = null;
            String pathStr = FileInputPath.getPathFromUri(a,path);
            if(pathStr!=null){
                parts = pathStr.split("/");
                formerPath = pathStr;
            }
            else{
                parts = path.getPath().split("/");
                formerPath = path.getPath();
            }

            if(parts.length>0){
                formerPath = formerPath.replace(parts[parts.length-1],"");
            }

            Scene scene = new Scene(formerPath);
            loadVertice(inputStream,scene,scale);
            return scene;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Vector<String > putValueF(Vector<String> vector, String[] s, int startOffset){
        for(int i = startOffset;i<s.length;i++){
            vector.add(s[i]);
        }
        return vector;
    }

    private static void loadVertice(InputStream inputStream,Scene scene,float scale){
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
                        case "mtllib":
                            scene.setUsemtl(parts[1]);
                            break;
                        case "usemtl":
                            scene.setMtlpart(parts[1]);
                            break;
                        case "v":
                            if(scene.newRoutine()){
                                scene.generate();
                            }
                            scene.putVertices(
                                    Float.parseFloat(parts[1])*scale,
                                    Float.parseFloat(parts[2])*scale,
                                    Float.parseFloat(parts[3])*scale
                            );
                            break;
                        case "vt":
                            scene.putTexture(
                                    Float.parseFloat(parts[1]),
                                    Float.parseFloat(parts[2])
                            );
                            break;
                        case "vn":
                            scene.putNormal(
                                    Float.parseFloat(parts[1]),
                                    Float.parseFloat(parts[2]),
                                    Float.parseFloat(parts[3])
                            );
                            break;
                        case "f":
                            scene.putFace(Arrays.copyOfRange(parts,1,parts.length));
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
        scene.generate();
    }
}
