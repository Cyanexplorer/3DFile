package com.example.g3863.a3dfile.Model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Matrix;
import android.util.Log;

import com.example.g3863.a3dfile.Builder.ObjectBuilder;
import com.example.g3863.a3dfile.Controller.programs.ColorShaderProgram;
import com.example.g3863.a3dfile.Controller.programs.PointLightShaderProgram;
import com.example.g3863.a3dfile.Controller.programs.ShaderProgram;
import com.example.g3863.a3dfile.util.TextureHelper;

import java.io.FileNotFoundException;
import java.util.List;

import static android.opengl.GLES20.glDeleteTextures;
import static com.example.g3863.a3dfile.util.Constants.BYTES_PER_FLOAT;
import static com.example.g3863.a3dfile.util.Constants.F_FRAG;
import static com.example.g3863.a3dfile.util.Constants.NORMAL_COMPONENT_COUNT;
import static com.example.g3863.a3dfile.util.Constants.POSITION_COMPONENT_COUNT;
import static com.example.g3863.a3dfile.util.Constants.STRIDE;
import static com.example.g3863.a3dfile.util.Constants.S_FRAG;
import static com.example.g3863.a3dfile.util.Constants.TEXTURE_COMPONENT_COUNT;

/**
 * Created by g3863 on 2017/10/20.
 */

public class ModelSettings {
    private final VertexArray vertexArray;
    private final List<ObjectBuilder.DrawCommand> drawList;
    private Bitmap bitmap_Ka,bitmap_Kd,bitmap_Ks;
    private float[] ka,ks,kd;
    private int[] texture;
    private float[] color;
    private Context context;
    private boolean isBpLoad;
    private ObjData objFile;
    private final BitmapFactory.Options options;
    private boolean focused = false;
    //public ObjectBuilder.GeneratedData generatedData;
    private Coordinate coordinate;
    private ShaderProgram shaderProgram;
    public int state;
    public final static int CREATE = 1;
    public final static int WORKING = 2;
    public final static int DESTROY = 3;
    public final static int DEAD = 4;
    public boolean autoScaled;

    public ModelSettings(Context context, ObjectBuilder.GeneratedData generatedData, ShaderProgram shaderProgram){
        this.shaderProgram = shaderProgram;
        this.objFile = generatedData.objFile;
        this.vertexArray = new VertexArray(generatedData.vertexData);
        this.drawList = generatedData.drawList;
        this.coordinate = new Coordinate();
        this.state = CREATE;
        this.color = new float[]{0f,0f,0f,0f};
        this.context = context;
        this.focused = false;
        this.texture = new int[]{-1,-1,-1};
        this.isBpLoad = false;
        this.autoScaled = false;
        this.ka = new float[]{0,0,0,1f};
        this.ks = new float[]{0,0,0,1f};
        this.kd = new float[]{0,0,0,1f};
        options = new BitmapFactory.Options();
        options.inScaled = false;
        setTexture();
        setColor();
    }

    public float getLongestLength(){
        float d=0, nd = 0;
        for(ObjData.Position p :objFile.position){
            nd = p.getDistance();
            if(nd>d){
                d = nd;
            }
        }
        return d;
    }

    public void reloadBp(){
        setTexture();
        setColor();
        isBpLoad = false;
    }

    public void setColor(){
        if(objFile.mtlData!=null){
            this.ka = objFile.mtlData.Ka;
            this.kd = objFile.mtlData.Kd;
            this.ks = objFile.mtlData.Ks;
            Log.i("color","t");
        }
    }

    public void setTexture(){
            try {
                bitmap_Ka = objFile.mtlData.generateKaB(context,150, MtlData.Direction.UpsideDown);
                bitmap_Kd = objFile.mtlData.generateKdB(context,150, MtlData.Direction.UpsideDown);
                bitmap_Ks = objFile.mtlData.generateKsB(context,150, MtlData.Direction.UpsideDown);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                objFile.mtlData=null;
                Log.i("text","f");
            }

            Log.i("test","t");

    }

    //check if obj focused
    public void setFocused(boolean state){
        focused = state;
    }

    public boolean isFocused(){
        return focused;
    }

    public ModelSettings setColor(float[] color){
        this.color = color;
        return this;
    }

    public float[] getColor(){
        return color;
    }

    public void translation(Coordinate coordinate){
        this.coordinate.move(coordinate);
    }

    public void translation(float x,float y, float z){
        this.coordinate.move(x,y,z);
    }

    public void rotation(Coordinate coordinate) {this.coordinate.rotate(coordinate);}

    public void rotation(float rx,float ry, float rz){
        this.coordinate.rotate(rx,ry,rz);
    }

    public ModelSettings setCoordinate(Coordinate coordinate){
        this.coordinate = coordinate;
        return this;
    }

    public ModelSettings setCoordinate(float[] cd){
        if(cd!=null && cd.length>=3)
        this.coordinate = new Coordinate().setMove(cd[0],cd[1],cd[2]);
        return this;
    }

    public Coordinate getCoordinate(){
        return coordinate;
    }
    public ObjData getModel(){
        return objFile;
    }

    public int getAlive(){
        return state;
    }

    public float getDistance(){
        return (float) Math.sqrt(Math.pow(coordinate.getx(),2)+Math.pow(coordinate.gety(),2)+Math.pow(coordinate.getz(),2));
    }

    public void setState(int state){
        this.state = state;
    }

    public void drawObject(){
        MatrixState.pushMatrix();
        MatrixState.translate(coordinate.getx(),coordinate.gety(),coordinate.getz());
        MatrixState.rotation(coordinate.getrMatrix());
        objectState();
        bindData(shaderProgram);
        draw();
        MatrixState.popMatrix();
    }

    public float[] getTRMatrix(){
        float[] currMatrix = new float[16];
        Matrix.setIdentityM(currMatrix,0);
        Matrix.translateM(currMatrix,0,coordinate.getx(),coordinate.gety(),coordinate.getz());
        Matrix.multiplyMM(currMatrix,0,currMatrix,0,coordinate.getrMatrix(),0);
        return currMatrix;
    }

    public void objectState(){
        if(state == CREATE){
            onObjCreate(coordinate);
        }

        else if(state == WORKING){
            onObjAction(coordinate);
        }

        else if(state == DESTROY){
            Log.i("test","destroy");
            onObjDestroy(coordinate);
        }
    }

    public void bindData(ShaderProgram shaderProgram){
        shaderProgram.useProgram();
        if(shaderProgram instanceof PointLightShaderProgram){
            bindData((PointLightShaderProgram)shaderProgram);
        }
        else {
            bindData((ColorShaderProgram)shaderProgram);
        }
    }

    private void bindData(PointLightShaderProgram lightprogram) {
        if(texture[0] == -1 && texture[1] == -1 && texture[2] == -1){
            texture[0] = TextureHelper.getTextureId();
            texture[1] = TextureHelper.getTextureId();
            texture[2] = TextureHelper.getTextureId();
        }

        if(!isBpLoad){
            TextureHelper.loadTexture(new Bitmap[]{bitmap_Ka,bitmap_Kd,bitmap_Ks},texture);
            isBpLoad = true;
            Log.i("test","bitmap load");
        }

        lightprogram.setTexture(texture);
        lightprogram.setUniforms();
        lightprogram.setLight(ka,ks,kd);
        lightprogram.setuTemperature();

        //vertice to glsl
        vertexArray.setVertexAttribPointer(0,
                lightprogram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, STRIDE);
        //pTexture to glsl
        vertexArray.setVertexAttribPointer(F_FRAG,
                lightprogram.getTextureCoordinatesAttributeLocation(),
                TEXTURE_COMPONENT_COUNT,STRIDE);

        //vector to glsl
        vertexArray.setVertexAttribPointer(S_FRAG,
                lightprogram.getNormalAttributeLocation(),NORMAL_COMPONENT_COUNT,
                STRIDE);
    }

    private void bindData(ColorShaderProgram colorShaderProgram) {
        colorShaderProgram.setDistance(coordinate);
        colorShaderProgram.setUniforms(color[0],color[1],color[2],color[3]);

        //vertice to glsl
        vertexArray.setVertexAttribPointer(0,
                colorShaderProgram.getPositionAttributeLocation(),
                POSITION_COMPONENT_COUNT, POSITION_COMPONENT_COUNT*BYTES_PER_FLOAT);
    }
    //store operation
    private void draw() {
        for (ObjectBuilder.DrawCommand drawCommand : drawList) {
            drawCommand.draw();
        }
    }

    public void onObjCreate(Coordinate coordinate){
        setState(WORKING);
    }
    public void onObjAction(Coordinate coordinate){}
    public void onObjDestroy(Coordinate coordinate){
        setState(DEAD);
    }

}
