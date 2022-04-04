package com.example.g3863.a3dfile.Controller.DAO;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.g3863.a3dfile.Builder.ObjectBuilder;
import com.example.g3863.a3dfile.Controller.Manager.MtlManager;
import com.example.g3863.a3dfile.Controller.programs.ShaderProgram;
import com.example.g3863.a3dfile.Model.Coordinate;
import com.example.g3863.a3dfile.Model.ObjData;
import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Model.ModelSettings;
import com.example.g3863.a3dfile.Model.Scene;

import java.util.ArrayList;

/**
 * Created by User on 2016/12/26.
 */

public class ModelDAO {

    private ObjData file;
    private Context context;
    private Coordinate coordinate;

    public static ArrayList<ModelDAO> createTmp(Context context, Uri obj){
        Scene scene;
        ArrayList<ModelDAO> modelDAOS = new ArrayList<>();
        if(obj!=null){
            scene = ObjDAO.loadModel(context,obj,1f);
        }
        else {
            return null;
        }

        if(scene!=null){
            MtlDAO.loadMtl(context,scene);
        }
        else {
            return null;
        }

        for(int i = 0;i<scene.objectArrayList.size();i++){
            ObjData objData = scene.objectArrayList.get(i);
            objData.mtlData = MtlDAO.searchData(objData.mtlPart);
            modelDAOS.add(create(context, objData));
        }

        for(ModelDAO data : modelDAOS){
            MtlData data1 = data.file.mtlData;
            data1.name = "texture"+String.valueOf(MtlManager.mtlOption.size());
            MtlManager.mtlOption.add(data1);
        }

        return modelDAOS;
    }

    private static ModelDAO create(Context context, ObjData file){
        return new ModelDAO(
                file,
                context
        );
    }

    private ModelDAO(ObjData file, Context context){
        this.file = file;
        this.context = context;
        this.coordinate = new Coordinate();
    }

    public ModelSettings generate(ShaderProgram shaderProgram){
        Log.i("t",String.valueOf(file.faces.get(0).p));
        return new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.POLY,file),shaderProgram);
    }

    public ModelSettings generateIndi(ShaderProgram shaderProgram){
        return new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.BOX,file),shaderProgram).setColor(new float[]{0.8f,0.8f,0.2f,1f});
    }

    public ModelSettings generateGrid(ShaderProgram shaderProgram){
        return new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.GRID,file),shaderProgram).setColor(new float[]{0.8f,0.2f,0.2f,1f});
    }

}
