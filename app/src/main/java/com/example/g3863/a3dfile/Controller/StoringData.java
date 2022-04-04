package com.example.g3863.a3dfile.Controller;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.opengl.Matrix;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.g3863.a3dfile.Model.ObjData;
import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Model.ModelSettings;
import com.example.g3863.a3dfile.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Vector;

/**
 * Created by g3863 on 2017/12/15.
 */

public class StoringData extends AsyncTask<String,String,Boolean> {

    private static ArrayList<ModelSettings> modelSettings;
    private static ArrayList<MtlData> mtlFiles;
    private static ProgressDialog progressDialog;
    private static Context context;

    public static File getFileCountName(){
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        if(!dir.exists() || dir.listFiles()==null){
            dir.mkdir();
        }
        int count = dir.listFiles().length;
        for(int i = 0;i<count;i++){
            File ndir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+File.separator+"Project"+String.valueOf(i));
            if(!ndir.exists()){
                boolean out = ndir.mkdirs();
                Log.i("test", String.valueOf(out));
                return ndir;
            }
        }
        return null;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        File dir = getFileCountName();
        File fileobj = new File(dir,params[0]+".obj");
        if(dir==null){
            return false;
        }
        if(!fileobj.exists()){
            try {
                fileobj.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        OutputStream outputStream0 = null;
        try {
            outputStream0 = new FileOutputStream(fileobj);

            writeProcess(params[0],outputStream0);
            outputStream0.close();

            writeTexture(params[1],dir);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public StoringData(Context context,@NonNull ArrayList<ModelSettings> arrayList) {
        super();
        modelSettings = arrayList;
        mtlFiles = new ArrayList<>();
        for(ModelSettings modelSettings : StoringData.modelSettings){
            if(!modelSettings.getModel().isMtlDisable()){
                mtlFiles.add(modelSettings.getModel().mtlData);
            }
        }

        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        progressDialog.setTitle("Saving...");
        progressDialog.setMessage("");
        progressDialog.setCancelable(false);
        progressDialog.create();
        StoringData.context = context;
    }

    private static void setMessage(final String msg){
        if(progressDialog!=null && progressDialog.isShowing())
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setMessage(msg);
                }
            });
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Boolean aBoolean) {
        super.onCancelled(aBoolean);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        progressDialog.dismiss();
    }

    public static boolean writeProcess(String name,OutputStream outputStream){

        int[] count = new int[]{0,0,0};
        String data = "";

        data = data.concat("mtllib"+" "+name+".mtl\n");
        for(int i = 0; i< modelSettings.size(); i++){

            setMessage(String.format(Locale.getDefault(),"obj file: %d/%d",i, modelSettings.size()));

            Log.i("test",String.valueOf(i));
            ObjData objData = modelSettings.get(i).getModel();
            data = data.concat("o Object "+String.valueOf(i)+"\n");
            String mtlname = modelSettings.get(i).getModel().getMtlName();
            Vector<ObjData.Position> vec = objData.vertice;
            Vector<ObjData.Texture> tex = objData.vTexture;
            Vector<ObjData.Normal> nor = objData.vNormal;
            Vector<ObjData.Face> fac = objData.faces;

            float[] trMatrix = modelSettings.get(i).getTRMatrix();

            for(int j = 0;j<vec.size();j++){
                float[] temp = new float[]{vec.get(j).px,vec.get(j).py,vec.get(j).pz,1f};
                Matrix.multiplyMV(temp,0,trMatrix,0,temp,0);
                data = data.concat(String.format(Locale.getDefault(),
                        "v %f %f %f\n",temp[0],temp[1],temp[2]));
            }

            for(int j = 0;j<nor.size();j++){
                data = data.concat(String.format(Locale.getDefault(),
                        "vn %f %f %f\n",nor.get(j).nx,nor.get(j).ny,nor.get(j).nz));
            }

            for(int j = 0;j<tex.size();j++){
                data = data.concat(String.format(Locale.getDefault(),
                        "vt %f %f\n",tex.get(j).tx,tex.get(j).ty));
            }

            if(mtlname!=null && !mtlname.isEmpty()){
                data = data.concat("usemtl "+mtlname+"\n");
            }

            for(int j = 0;j<fac.size();j+=3){
                String[] merge = new String[]{"","",""};
                for(int k = 0;k<3;k++){
                    ObjData.Face face = fac.get(j+k);
                    merge[k]=String.valueOf(face.p+count[0]+1);
                    if(face.t!=-1){
                        merge[k] = merge[k].concat("/"+String.valueOf(face.t+count[1]+1));
                    }
                    if(face.n!=-1){
                        merge[k] = merge[k].concat("/"+String.valueOf(face.n+count[2]+1));
                    }

                }
                data = data.concat(String.format(Locale.getDefault(),
                        "f %s %s %s\n",merge[0],merge[1],merge[2]));

            }

            count[0] += vec.size();
            count[1] += tex.size();
            count[2] += nor.size();
        }

        try {
            outputStream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean writeTexture(String name,File dir){
        File file = new File(dir,name+".mtl");
        if (!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            OutputStream outputStream = new FileOutputStream(file);
            for(int i =0;i<mtlFiles.size();i++){
                setMessage(String.format(Locale.getDefault(),"mtl file: %d/%d",i,mtlFiles.size()));

                String data = "";
                MtlData mtlData = mtlFiles.get(i);

                File image = new File(dir,mtlData.getFileName(mtlData.mKa));
                if(!image.exists()){
                    image.createNewFile();
                    OutputStream imageStream = new FileOutputStream(image);
                    copyImageFile(mtlData,imageStream);
                    imageStream.close();
                }

                image = new File(dir,mtlData.getFileName(mtlData.mKd));
                if(!image.exists()){
                    image.createNewFile();
                    OutputStream imageStream = new FileOutputStream(image);
                    copyImageFile(mtlData,imageStream);
                    imageStream.close();
                }

                image = new File(dir,mtlData.getFileName(mtlData.mKs));
                if(!image.exists()){
                    image.createNewFile();
                    OutputStream imageStream = new FileOutputStream(image);
                    copyImageFile(mtlData,imageStream);
                    imageStream.close();
                }

                data = data.concat("newmtl "+mtlData.name+"\n");
                data = data.concat("Ns "+mtlData.Ns+"\n");
                data = data.concat("Ni "+mtlData.Ni+"\n");
                data = data.concat("d "+mtlData.d+"\n");
                data = data.concat("Tr "+mtlData.Tr+"\n");
                data = data.concat("Tf "+mtlData.Tf[0]+" "+mtlData.Tf[1]+" "+mtlData.Tf[2]+"\n");
                data = data.concat("illum "+mtlData.illum+"\n");
                data = data.concat("Ka "+mtlData.Ka[0]+" "+mtlData.Ka[1]+" "+mtlData.Ka[2]+"\n");
                data = data.concat("Ks "+mtlData.Ks[0]+" "+mtlData.Ks[1]+" "+mtlData.Ks[2]+"\n");
                data = data.concat("Kd "+mtlData.Kd[0]+" "+mtlData.Kd[1]+" "+mtlData.Kd[2]+"\n");
                data = data.concat("map_Ka "+mtlData.getFileName(mtlData.mKa)+"\n");
                data = data.concat("map_Kd "+mtlData.getFileName(mtlData.mKd)+"\n");
                data = data.concat("map_Ks "+mtlData.getFileName(mtlData.mKs)+"\n");
                outputStream.write(data.getBytes());
            }
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private static void copyImageFile(MtlData mtlData, OutputStream outputStream){

        try {
            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse("file:///"+mtlData.mKd));

            byte[] b = new byte[1024];
            int len;

            if(inputStream!=null){
                while ((len = inputStream.read(b))>0){
                    outputStream.write(b,0,len);
                }
                inputStream.close();
            }
            else {
                Toast.makeText(context,"Save fail",Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
