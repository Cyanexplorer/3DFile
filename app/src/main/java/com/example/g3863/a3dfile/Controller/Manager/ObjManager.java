package com.example.g3863.a3dfile.Controller.Manager;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.g3863.a3dfile.Builder.ObjectBuilder;
import com.example.g3863.a3dfile.Controller.DAO.ModelDAO;
import com.example.g3863.a3dfile.Model.Coordinate;
import com.example.g3863.a3dfile.Model.ModelSettings;
import com.example.g3863.a3dfile.Model.MtlData;
import com.example.g3863.a3dfile.Model.ObjData;
import com.example.g3863.a3dfile.Controller.StoringData;
import com.example.g3863.a3dfile.View.Fragment.TexManagerFragment;
import com.example.g3863.a3dfile.Controller.programs.ColorShaderProgram;
import com.example.g3863.a3dfile.Controller.programs.PointLightShaderProgram;
import com.example.g3863.a3dfile.Controller.programs.ShaderProgram;
import com.example.g3863.a3dfile.Model.MatrixState;
import com.example.g3863.a3dfile.util.RayPicking;
import com.example.g3863.a3dfile.util.Vertex;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.DELETE;
import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.MOVE;
import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.NOACTION;
import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.SELECT;
import static com.example.g3863.a3dfile.View.Object.Glsurface_Ext.STOP;
import static com.example.g3863.a3dfile.Controller.Manager.ObjManager.ObjmngMsg.DMOVE;
import static com.example.g3863.a3dfile.Controller.Manager.ObjManager.ObjmngMsg.INPORT;
import static com.example.g3863.a3dfile.Controller.Manager.ObjManager.ObjmngMsg.ROTATE;

/**
 * Created by g3863 on 2017/11/17.
 */

public class ObjManager {
    private final static int maxAmount=255;
    private static PointLightShaderProgram pointLightShaderProgram;
    private static ColorShaderProgram colorShaderProgram;
    private static ArrayList<ModelSettings> objBuffer;
    private static ArrayList<ModelSettings> objBufferGrid;
    private static ArrayList<ModelSettings> objBufferIndi;
    private static ArrayList<ModelSettings> objBufferTools;
    private static ArrayList<ModelSettings> selectedBuffer;
    private static MotionIndicate motionIndicate;
    private static RotationIndicate rotationIndicate;

    private static int tools = -1;
    private static boolean isGrid = false;
    private static boolean isShape = false;
    public final static String GRID = "grid";
    public final static String SHAPE = "shape";
    private static ObjmngMsg objmngMsg;

    private static int index = -1;
    private static boolean lock;

    private ObjManager(Context context){
        if(lock) return;

        objmngMsg = new ObjmngMsg();
        objBuffer = new ArrayList<>();
        objBufferIndi = new ArrayList<>();
        objBufferGrid = new ArrayList<>();
        objBufferTools = new ArrayList<>();
        selectedBuffer = new ArrayList<>();
        lock = true;
    }

    public static ObjManager getInstance(Context context){
        return new ObjManager(context);
    }

    public void initialShader(Context context){
        pointLightShaderProgram = new PointLightShaderProgram(context);
        colorShaderProgram = new ColorShaderProgram(context);
    }

    public static void setObjmngMsg(ObjmngMsg objmngMsg){
        ObjManager.objmngMsg = objmngMsg;
    }

    public static class ObjmngMsg{
        public final static String DMOVE = "DMOVE";
        public final static String INPORT = "INPORT";
        public final static String SELECT = "SELECT";
        public final static String ROTATE = "ROTATE";
        public final static String MOVE = "MOVE";
        public final static String NOACTION = "NOACTION";
        public final static String STOP = "STOP";
        public final static String DELETE = "DELETE";

        private String state;
        private String actionMod;
        private float[] farg0;
        private float[] iarg0;
        private float[] target0;

        public ObjmngMsg(){
            actionMod = NOACTION;
            state = NOACTION;
            target0 = new float[]{0,0};
            farg0 = new float[]{0,0,0,0};
            iarg0 = new float[]{0,0,0,0};
        }

        public String getState(){
            return state;
        }

        public void setMove(float[] arg0,float[] arg1){
            actionMod = MOVE;
            if(arg0.length>=2 && arg1.length>=2){
                farg0[0] = arg0[0];
                farg0[1] = arg0[1];
                farg0[2] = arg1[0];
                farg0[3] = arg1[1];
            }
        }

        public void setRotate(float[] arg0,float[] arg1){
            actionMod = ROTATE;
            if(arg0.length>=2 && arg1.length>=2){
                farg0[0] = arg0[0];
                farg0[1] = arg0[1];
                farg0[2] = arg1[0];
                farg0[3] = arg1[1];
            }
        }

        public void setSelect(float pickX,float pickY){
            actionMod = SELECT;
            target0[0] = pickX;
            target0[1] = pickY;
        }

        public void setDmove(float pickX,float pickY){
            actionMod = DMOVE;
            target0[0] = pickX;
            target0[1] = pickY;
        }

        public void setDelete(){
            actionMod = DELETE;
        }
    }

    public void stdTools(Context context){
        ShaderProgram shaderProgram = colorShaderProgram;
        ModelSettings near = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.TOUCHPNT,null),shaderProgram).setColor(new float[]{0.8f,1f,0.8f,1f});
        ModelSettings far = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.TOUCHPNT,null),shaderProgram).setColor(new float[]{0.2f,0.5f,0.2f,1f});
        ModelSettings ground = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.GRD,null),shaderProgram).setColor(new float[]{0.7f,0.7f,0.7f,0.5f});
        objBufferTools.add(ground);
        selectedBuffer.add(near);
        selectedBuffer.add(far);

        motionIndicate = new MotionIndicate(context,shaderProgram);
        rotationIndicate = new RotationIndicate(context,shaderProgram);
    }

    public static void openFile(Context context, Uri obj){
        ArrayList<ModelDAO> modelDAOS = ModelDAO.createTmp(context,obj);
        for(ModelDAO modelDAO : modelDAOS){
            ModelSettings model = modelDAO.generate(pointLightShaderProgram);
            ModelSettings grid = modelDAO.generateGrid(colorShaderProgram);
            ModelSettings indi = modelDAO.generateIndi(colorShaderProgram);

            addMarbleBuilder(model,grid,indi);
        }
    }

    public static class MotionIndicate{

        private static ArrayList<ModelSettings> moveBuffer = new ArrayList<>();
        private static int selected = -1;
        private static float length = 0f;
        public float[] orp = new float[]{0,0,0};

        private final static int UD = 0;
        private final static int RL = 1;
        private final static int FB = 2;
        public MotionIndicate(Context context,ShaderProgram shaderProgram){
            ModelSettings UD = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.MOVEUD,null),shaderProgram)
                    .setColor(new float[]{0.7f,0.2f,0.2f,0.5f});
            ModelSettings RL = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.MOVERL,null),shaderProgram)
                    .setColor(new float[]{0.7f,0.2f,0.2f,0.5f});
            ModelSettings FB = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.MOVEFB,null),shaderProgram)
                    .setColor(new float[]{0.7f,0.2f,0.2f,0.5f});

            moveBuffer.add(UD);
            moveBuffer.add(RL);
            moveBuffer.add(FB);
        }

        public void setCoordinate(Coordinate coordinate, float length){
            length = 10f;
            setLength(length);
            moveBuffer.get(UD).getCoordinate().setMove(coordinate).move(0,0,length);//.translation(0,0,length);
            moveBuffer.get(RL).getCoordinate().setMove(coordinate).move(length,0,0);//.translation(length,0,0);
            moveBuffer.get(FB).getCoordinate().setMove(coordinate).move(0,-length,0);//.translation(0,length,0);
        }

        public void setSelected(int selected,float length){
            MotionIndicate.selected = selected;
            setLength(length);
            orp = new float[]{0,0,0};
        }

        public int getSelected(){
            return MotionIndicate.selected;
        }

        public void setLength(float length){
            MotionIndicate.length = length;
        }

        public void translation(float x,float y){
            float[] v;
            if(selected == UD){
                v = new float[]{0,0,1f};
            }else if(selected == RL){
                v = new float[]{1f,0,0};
            }
            else if(selected == FB){
                v = new float[]{0,1f,0};
            }
            else {
                return;
            }

            RayPicking.Ray nPnt = RayPicking.getRay((int)x,(int)y,MatrixState.width,MatrixState.height,MatrixState.getFinalMatrix());
            float[] u = new float[]{
                    nPnt.farCoord[0] - nPnt.nearCord[0],
                    nPnt.farCoord[1] - nPnt.nearCord[1],
                    nPnt.farCoord[2] - nPnt.nearCord[2]};
            float vv = (float)(Math.pow(v[0],2)+Math.pow(v[1],2)+Math.pow(v[2],2));
            float uv = u[0]*v[0]+u[1]*v[1]+u[2]*v[2];
            float[] nrp = new float[]{
                    (uv/vv)*v[0],
                    (uv/vv)*v[1],
                    (uv/vv)*v[2]};
            if(!Arrays.equals(orp,new float[]{0,0,0})){
                float[] res = new float[]{nrp[0]-orp[0],nrp[1]-orp[1],nrp[2]-orp[2]};
                Log.i("test",String.format(Locale.getDefault(),"%f,%f,%f",res[0],res[1],res[2]));

                moveBuffer.get(UD).translation(res[0],res[1],res[2]);
                moveBuffer.get(RL).translation(res[0],res[1],res[2]);
                moveBuffer.get(FB).translation(res[0],res[1],res[2]);

                ArrayList<ModelSettings> files = getSelectGroup();
                for (int i =0;i<files.size();i++){
                    files.get(i).translation(res[0],res[1],res[2]);
                }
            }
            orp = nrp;
        }

        public void checkCollision(float x,float y){
            int index = Picking(x,y,moveBuffer);
            setSelected(index,10f);
            Log.i("index",String.valueOf(index));
        }

        public void draw(){
            moveBuffer.get(UD).drawObject();
            moveBuffer.get(RL).drawObject();
            moveBuffer.get(FB).drawObject();
        }
    }



    public static class RotationIndicate{

        private static ArrayList<ModelSettings> moveBuffer = new ArrayList<>();
        private static int selected = -1;
        private static float length = 0f;
        public float ords = 0f;
        public float objrds[] = new float[16];

        private final static int UD = 0;
        private final static int RL = 1;
        private final static int FB = 2;
        public RotationIndicate(Context context,ShaderProgram shaderProgram){
            ModelSettings UD = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.ROCIRXY,null),shaderProgram)
                    .setColor(new float[]{0.7f,0.7f,0.2f,0.5f});
            ModelSettings RL = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.ROCIRYZ,null),shaderProgram)
                    .setColor(new float[]{0.2f,0.7f,0.7f,0.5f});
            ModelSettings FB = new ModelSettings(context,ObjectBuilder.build(ObjectBuilder.ROCIRXZ,null),shaderProgram)
                    .setColor(new float[]{0.7f,0.2f,0.7f,0.5f});

            moveBuffer.add(UD);
            moveBuffer.add(RL);
            moveBuffer.add(FB);
        }

        public void setCoordinate(Coordinate coordinate,float length){
            moveBuffer.get(UD).getCoordinate().setMove(coordinate);//.translation(0,0,length);
            moveBuffer.get(RL).getCoordinate().setMove(coordinate);//.translation(length,0,0);
            moveBuffer.get(FB).getCoordinate().setMove(coordinate);//.translation(0,length,0);
        }

        public void translation(float x,float y){
            float[] v = new float[3];
            float nrds = getAngle(x,y,v);
            if(nrds!=361){
                float[] ra = Vertex.scalerProduct(nrds-ords,v);
                ArrayList<ModelSettings> files = getSelectGroup();
                for (int i =0;i<files.size();i++){
                    files.get(i).getCoordinate().rotate(ra[0]+objrds[0],ra[1]+objrds[1],ra[2]+objrds[2]);
                }
                ords = nrds;
            }
            Log.i("RoAngle",String.valueOf(ords));
        }

        public float getAngle(float x,float y,float[] vector){
            float[] v,vc,vs;
            if(selected == UD){
                v = new float[]{0,0,1f};
                vc = new float[]{1f,0,0};
                vs = new float[]{0,1f,0};
            }else if(selected == RL){
                v = new float[]{1f,0,0};
                vc = new float[]{0,1f,0};
                vs = new float[]{0,0,1f};
            }
            else if(selected == FB){
                v = new float[]{0,1f,0};
                vc = new float[]{1f,0,0};
                vs = new float[]{0,0,1f};
            }
            else {
                return 361f;
            }

            if(vector!=null && vector.length == 3){
                vector[0] = v[0];
                vector[1] = v[1];
                vector[2] = v[2];
            }

            Coordinate coordinate = moveBuffer.get(selected).getCoordinate();
            float[] center = coordinate.toFloat();
            RayPicking.Ray ray = RayPicking.getRay(x,y,MatrixState.width,MatrixState.height,MatrixState.getFinalMatrix());
            float[] npnt = RayPicking.crossoint(ray,v,center);

            if(npnt!=null){
                float[] nvec = Vertex.minus(center,npnt);
                float d = Vertex.length(nvec);
                float dc = Vertex.dot(nvec,vc);
                float ds = Vertex.dot(nvec,vs);
                float output = (float) Math.toDegrees(Math.acos(dc/d));
                if(ds<0){
                    output = -output;
                }
                return output;
            }
            return 361f;
        }

        public void checkCollision(float x,float y){
            RotationIndicate.selected = Picking(x,y,moveBuffer);
            if(selected!= -1){
                ords = getAngle(x,y,null);
                objrds = moveBuffer.get(selected).getCoordinate().getRadius();
            }
            Log.i("index",String.valueOf(selected));
        }

        public void draw(){
            moveBuffer.get(UD).drawObject();
            moveBuffer.get(RL).drawObject();
            moveBuffer.get(FB).drawObject();
        }
    }

    public static void addMarbleBuilder(ModelSettings file, ModelSettings grid, ModelSettings indicator){
        if(objBuffer.size()<maxAmount){
            objBuffer.add(file);
            objBufferGrid.add(grid);
            objBufferIndi.add(indicator);
            Log.i("test","new obj create");
        }
    }

    public static void setDrawState(String type, boolean state){
        if(type.equals(GRID)){
            ObjManager.isGrid = state;
        }
        else if(type.equals(SHAPE)){
            ObjManager.isShape = state;
        }
    }

    public static void setTools(int index){
        ArrayList<ModelSettings> file = getSelectGroup();
        if(file.size()>0 && index !=-1){
            Log.i("tool",String.valueOf(tools));
            float length = file.get(0).getLongestLength();
            if(index == 1){
                motionIndicate.setCoordinate(file.get(0).getCoordinate(),length);
            }
            else if(index == 0){
                rotationIndicate.setCoordinate(file.get(0).getCoordinate(),length);
            }
            tools = index;
        }
        else tools = -1;
    }

    public static void TextureManager(android.support.v4.app.FragmentManager fragmentManager){
        Fragment prefragment = fragmentManager.findFragmentByTag("Tex");
        if(prefragment!=null){
            fragmentManager.beginTransaction().remove(prefragment).commit();
        }
        TexManagerFragment fragment = TexManagerFragment.newInstance(getSelectGroup());
        fragment.show(fragmentManager,"Tex");
    }

    public static void setTexture(MtlData mtlData){
        ArrayList<ModelSettings> modelSettingsArrayList = getSelectGroup();
        for(int i = 0; i< modelSettingsArrayList.size(); i++){
            ModelSettings file = modelSettingsArrayList.get(i);
            file.getModel().mtlData = mtlData;
            file.reloadBp();
        }
    }

    private static void action(){
        ArrayList<ModelSettings> file = new ArrayList<>();
        String actionMod = objmngMsg.actionMod;
        float[] pick = objmngMsg.target0;
        float[] farg = objmngMsg.farg0;
        if(!actionMod.equals(NOACTION)){
             file = getSelectGroup();
        }

        switch (actionMod){
            case DMOVE:
                if(file.size()>0){
                    motionIndicate.checkCollision(pick[0],pick[1]);
                    rotationIndicate.checkCollision(pick[0],pick[1]);
                }
                break;
            case SELECT:
                clrSelect();
                int temp = Picking((int)pick[0],(int)pick[1],objBuffer);
                if(temp!=-1){
                    Log.i("test",String.valueOf(temp));
                    objBuffer.get(temp).setFocused(true);
                }
                break;
            case INPORT:
                break;
            case ROTATE:
                rotationIndicate.translation(farg[0],farg[1]);
                break;
            case MOVE:
                motionIndicate.translation(farg[0],farg[1]);
                break;
            case STOP:
                motionIndicate.setSelected(-1,0f);
                break;
            case DELETE:
                if(index == -1){
                    for(ModelSettings target:file){
                        int index = objBuffer.indexOf(target);
                        objBuffer.remove(index);
                        objBufferIndi.remove(index);
                        objBufferGrid.remove(index);
                    }
                }
                break;
        }

        objmngMsg.actionMod = NOACTION;
    }

    //Main Thread
    public static void MarbleDrawer(){

        //pre operate
        action();

        //object
        objBufferTools.get(0).drawObject();
        for(int i = 0;i<objBuffer.size();i++){

            if(objBuffer.get(i).isFocused()){
                objBufferIndi.get(i)
                        .setCoordinate(objBuffer.get(i).getCoordinate())
                        .drawObject();
            }
            if(isShape){
                objBuffer.get(i).drawObject();
            }
            if(isGrid){
                objBufferGrid.get(i)
                        .setCoordinate(objBuffer.get(i).getCoordinate())
                        .drawObject();
            }

        }

        //tools
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT);
        //GLES20.glDepthFunc(GLES20.GL_ALWAYS);
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,GLES20.GL_ONE_MINUS_SRC_ALPHA);
        if(tools == 1){
            motionIndicate.draw();
        }
        else if(tools==0){
            rotationIndicate.draw();
        }
        GLES20.glDisable(GLES20.GL_BLEND);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        //GLES20.glDepthFunc(GLES20.GL_LEQUAL);

        //selectedBuffer.get(0).drawObject();
        //selectedBuffer.get(1).drawObject();
    }

    public static int Picking(float x,float y,ArrayList<ModelSettings> buffer){
        Log.i("test",String.format(Locale.getDefault(),"%f,%f",x,y));
        RayPicking.Ray ray  = RayPicking.getRay(x,y,MatrixState.width,MatrixState.height,MatrixState.getFinalMatrix());

        selectedBuffer.get(0).setCoordinate(ray.nearCord);
        selectedBuffer.get(1).setCoordinate(ray.farCoord);

        float distance = -1;
        int counter = 0;
        int temp = -1;
        float[] cameraPos = MatrixState.getCameraLocation();

        for(ModelSettings file:buffer){
            ArrayList<ObjData.Position> positions = file.getModel().position;
            float[] rt = file.getTRMatrix();
            float[] output = new float[]{0,0,0};
            for(int i = 0; i<file.getModel().getVerticeNum(); i+=3){
                float[] merge = new float[9];
                for(int j = 0;j<3;j++){
                    float[] tmpPos = new float[]{positions.get(i+j).px,positions.get(i+j).py,positions.get(i+j).pz,1f};
                    Matrix.multiplyMV(tmpPos,0,rt,0,tmpPos,0);
                    merge[3*j] = tmpPos[0];
                    merge[3*j+1] = tmpPos[1];
                    merge[3*j+2] = tmpPos[2];
                }

                RayPicking.Triangle T = new RayPicking.Triangle(
                        new float[]{merge[0],merge[1],merge[2]},
                        new float[]{merge[3],merge[4],merge[5]},
                        new float[]{merge[6],merge[7],merge[8]});
                int res = RayPicking.intersection(ray,T,output);
                if(res == 1){
                    float tdis = (float)(Math.pow(cameraPos[0]-output[0],2)+
                            Math.pow(cameraPos[1]-output[1],2)+
                            Math.pow(cameraPos[2]-output[2],2));
                    Log.i("test",String.format(Locale.getDefault(),"%f,%f,%f",output[0],output[1],output[2]));
                    if(distance <0 || distance>tdis){
                        distance = tdis;
                        temp = counter;
                    }
                }
            }
            counter++;
        }

        return temp;
    }

    public static ObjData readModel(int index){
        return objBuffer.get(index).getModel();
    }

    public static ModelSettings getSelect(){
        ArrayList<ModelSettings> selects = getSelectGroup();
        if(selects.size()>0 && selects.get(0)!=null){
            return selects.get(0);
        }
        return null;
    }

    public static ArrayList<ModelSettings> getSelectGroup(){
        ArrayList<ModelSettings> files = new ArrayList<>();
        for(int i = 0;i<objBuffer.size();i++){
            ModelSettings file = objBuffer.get(i);
            if(file.isFocused()){
                files.add(file);
            }
        }
        Log.i("select",String.valueOf(files.size()));
        return files;
    }

    public static void clrSelect(){
        for(int i = 0;i<objBuffer.size();i++){
            ModelSettings file = objBuffer.get(i);
            if(file.isFocused()){
                file.setFocused(false);
            }
        }
    }

    public static void writeToObjFile(Context context, String name){
        try {
            OutputStream outputStream = context.openFileOutput(name,Context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
            //writeProcess(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveToObjFile(Context context, String name){
        if(!isExStorageWritable()){
            return;
        }
        StoringData process = new StoringData(context,objBuffer);
        process.execute(name,name);
    }

    public static boolean isExStorageWritable(){
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    public static void destroyAll(){
        objBuffer.clear();
        objBufferIndi.clear();
        objBufferGrid.clear();
    }


}
