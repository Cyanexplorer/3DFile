/*
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
*/
package com.example.g3863.a3dfile.Builder;


import com.example.g3863.a3dfile.Model.Coordinate;
import com.example.g3863.a3dfile.Model.ObjData;

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.g3863.a3dfile.util.Constants.FLOATS_PER_VERTEX;
import static com.example.g3863.a3dfile.util.Constants.POINT_COMPONENT_COUNT;

public class ObjectBuilder {

    public interface DrawCommand {
        void draw();
    }

    public static class GeneratedData {
        public final float[] vertexData;
        public final List<DrawCommand> drawList;
        public final ObjData objFile;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList, ObjData objFile) {
            this.vertexData = vertexData;
            this.drawList = drawList;
            if(objFile == null){
                this.objFile = new ObjData();
                for(int i=0;i<vertexData.length;i+=3){
                    this.objFile.putPosition(vertexData[i],vertexData[i+1],vertexData[i+2]);
                    //this.objFile.loadVertices();
                }
            }
            else {
                this.objFile = objFile;
            }
        }
    }

    private static float logestRange(ObjData file){
        float d=0, nd = 0;
        for(ObjData.Position p :file.position){
            nd = p.getDistance();
            if(nd>d){
                d = nd;
            }
        }
        return d;
    }

    private static float[] vertexData;
    private static List<DrawCommand> drawList = new ArrayList<>();
    public final static String BOX = "BOX";
    public final static String ROCIRXY = "ROCIRXY";
    public final static String ROCIRYZ = "ROCIRYZ";
    public final static String ROCIRXZ = "ROCIRXZ";
    public final static String GRID = "GRID";
    public final static String POLY = "POLY";
    public final static String TOUCHPNT = "TOUCHPNT";
    public final static String GRD = "GRD";
    public final static String MOVEFB = "MOVEFB";
    public final static String MOVEUD = "MOVEUD";
    public final static String MOVERL = "MOVERL";

    private static void appendPlygen(ObjData file){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = file.getVerticeNum();
        vertexData = new float[numVertices* FLOATS_PER_VERTEX];

        for(int i = 0;i<numVertices;i++){
            vertexData[offset++]=file.position.get(i).px;
            vertexData[offset++]=file.position.get(i).py;
            vertexData[offset++]=file.position.get(i).pz;

            vertexData[offset++]=file.pTexture.get(i).tx;
            vertexData[offset++]=file.pTexture.get(i).ty;

            vertexData[offset++]=file.pNormal.get(i).nx;
            vertexData[offset++]=file.pNormal.get(i).ny;
            vertexData[offset++]=file.pNormal.get(i).nz;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES, startVertex, numVertices);
            }
        });
    }

    private static void appendGrid(ObjData file){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = file.getVerticeNum();
        for(int i = 0;i<numVertices;i++){
            vertexData[offset++]=file.position.get(i).px;
            vertexData[offset++]=file.position.get(i).py;
            vertexData[offset++]=file.position.get(i).pz;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_POINTS, startVertex, numVertices);
            }
        });
    }

    private static void appendBox(Coordinate coordinate, float maxSize){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(90);
        final double diff = Math.toRadians(45);
        maxSize *=2;
        for(int i = 0;i<4;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+diff));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+diff));
            vertexData[offset++]=coordinate.getz()+maxSize/2;
        }

        for(int i = 0;i<4;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+diff));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+diff));
            vertexData[offset++]=coordinate.getz()-maxSize/2;
        }

        for(int i = 0;i<4;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+diff));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+diff));
            vertexData[offset++]=coordinate.getz()+maxSize/2;

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+diff));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+diff));
            vertexData[offset++]=coordinate.getz()-maxSize/2;
        }

        vertexData[offset++]= coordinate.getx();
        vertexData[offset++]= coordinate.gety();
        vertexData[offset]= coordinate.getz();

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_LINE_LOOP, startVertex, 4);
                glDrawArrays(GL_LINE_LOOP, 4, 4);
                glDrawArrays(GL_LINES, 8, 8);
                glDrawArrays(GL_POINTS, 16, 1);
            }
        });
    }

    private static void appendGround(Coordinate coordinate){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int row = 20;
        final int column = 20;
        final float hlength = 200;
        final float gap = hlength/row;
        final int total = 4*(row+column)-4;
        vertexData = new float[total*POINT_COMPONENT_COUNT];

        for(int i = 0;i<row;i++){
            vertexData[offset++]= -hlength;
            vertexData[offset++]= gap*i;
            vertexData[offset++]= 0;

            vertexData[offset++]= hlength;
            vertexData[offset++]= gap*i;
            vertexData[offset++]= 0;

        }

        for(int i = 1;i<row;i++){
            vertexData[offset++]= -hlength;
            vertexData[offset++] = -gap*i;
            vertexData[offset++]= 0;

            vertexData[offset++]= hlength;
            vertexData[offset++] = -gap*i;
            vertexData[offset++]= 0;

        }

        for(int i = 0;i<column;i++){
            vertexData[offset++] = -gap*i;
            vertexData[offset++]= -hlength;
            vertexData[offset++]= 0;

            vertexData[offset++] = -gap*i;
            vertexData[offset++]= hlength;
            vertexData[offset++]= 0;
        }

        for(int i = 1;i<column;i++){
            vertexData[offset++]= gap*i;
            vertexData[offset++]= -hlength;
            vertexData[offset++]= 0;

            vertexData[offset++]= gap*i;
            vertexData[offset++]= hlength;
            vertexData[offset++]= 0;
        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_LINES, startVertex, total);
            }
        });
    }

    private static void appendMoveUD(){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(120);
        final double diff = Math.toRadians(90);
        vertexData = new float[3*POINT_COMPONENT_COUNT];

        final float size = 10f;
        final float length = 50f;

        for(int i = 0;i<3;i++){
            vertexData[offset++]= (float) (Math.cos(ra*i+diff)*size);
            vertexData[offset++]= 0;
            vertexData[offset++]= (float) (Math.sin(ra*i+diff)*size)+length;
        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES,startVertex,3);
            }
        });
    }

    private static void appendMoveRL(){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(120);
        vertexData = new float[3*POINT_COMPONENT_COUNT];

        final float size = 10f;
        final float length = 50f;

        for(int i = 0;i<3;i++){
            vertexData[offset++]= (float) (Math.cos(ra*i)*size)+length;
            vertexData[offset++]= (float) (Math.sin(ra*i)*size);
            vertexData[offset++]= 0f;
        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES,startVertex,3);
            }
        });
    }

    private static void appendMoveFB(){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(120);
        final double diff = Math.toRadians(-90);
        vertexData = new float[3*POINT_COMPONENT_COUNT];

        final float size = 10f;
        final float length = 50f;

        for(int i = 0;i<3;i++){
            vertexData[offset++]= (float) (Math.cos(ra*i+diff)*size);
            vertexData[offset++]= (float) (Math.sin(ra*i+diff)*size)-length;
            vertexData[offset++]= 0f;
        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES,startVertex,3);
            }
        });
    }

    private static void appendCircleXY(Coordinate coordinate){
        int offset = 0;
        final float maxSize = 50f;
        final float minSize = 40f;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(10);
        final int count = 36;
        vertexData = new float[6*count*POINT_COMPONENT_COUNT];
        for(int i = count;i<count*2;i++){

            vertexData[offset++]= (float) (coordinate.getx()+minSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+minSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz();

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz();

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));
            vertexData[offset++]=coordinate.getz();

            vertexData[offset++]= (float) (coordinate.getx()+minSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+minSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz();

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));
            vertexData[offset++]=coordinate.getz();

            vertexData[offset++]= (float) (coordinate.getx()+minSize*Math.cos(ra*i+ra));
            vertexData[offset++]= (float) (coordinate.gety()+minSize*Math.sin(ra*i+ra));
            vertexData[offset++]=coordinate.getz();

        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES, startVertex, 6*count);
            }
        });
    }

    private static void appendCircleYZ(Coordinate coordinate){
        int offset = 0;
        final float maxSize = 50f;
        final float minSize = 40f;
        final float width = 5f;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(10);
        final int count = 36;
        vertexData = new float[6*count*POINT_COMPONENT_COUNT];
        for(int i = count/2;i<count*3/2;i++){

            vertexData[offset++]=coordinate.getz()+width;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));

            vertexData[offset++]=coordinate.getz()-width;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));

            vertexData[offset++]=coordinate.getz()+width;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));

            vertexData[offset++]=coordinate.getz()-width;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));

            vertexData[offset++]=coordinate.getz()-width;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));

            vertexData[offset++]=coordinate.getz()+width;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));

        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES, startVertex, 6*count);
            }
        });
    }

    private static void appendCircleXZ(Coordinate coordinate){
        int offset = 0;
        final float maxSize = 50f;
        final float minSize = 40f;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final double ra = Math.toRadians(10);
        final int count = 36;
        vertexData = new float[6*count*POINT_COMPONENT_COUNT];
        for(int i = 0;i<count;i++){

            vertexData[offset++]= (float) (coordinate.getx()+minSize*Math.cos(ra*i));
            vertexData[offset++]=coordinate.getz();
            vertexData[offset++]= (float) (coordinate.gety()+minSize*Math.sin(ra*i));

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]=coordinate.getz();
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]=coordinate.getz();
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));

            vertexData[offset++]= (float) (coordinate.getx()+minSize*Math.cos(ra*i));
            vertexData[offset++]=coordinate.getz();
            vertexData[offset++]= (float) (coordinate.gety()+minSize*Math.sin(ra*i));

            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i+ra));
            vertexData[offset++]=coordinate.getz();
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i+ra));

            vertexData[offset++]= (float) (coordinate.getx()+minSize*Math.cos(ra*i+ra));
            vertexData[offset++]=coordinate.getz();
            vertexData[offset++]= (float) (coordinate.gety()+minSize*Math.sin(ra*i+ra));

        }

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLES, startVertex, 6*count);
            }
        });
    }

    private static void appendDot(Coordinate coordinate){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        vertexData[offset++]= coordinate.getx();
        vertexData[offset++]= coordinate.gety();
        vertexData[offset]= coordinate.getz();

        drawList.add(new ObjectBuilder.DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_POINTS,startVertex,1);
            }
        });
    }

    public static GeneratedData build(String type, ObjData objFile){
        return build(type,new Coordinate(),objFile);
    }

    public static GeneratedData build(String type, Coordinate coordinate, ObjData objFile) {
        drawList = new ArrayList<>();
        switch (type){
            case GRID:
                vertexData = new float[objFile.getVerticeNum() * POINT_COMPONENT_COUNT];
                appendGrid(objFile);
                break;
            case BOX:
                vertexData = new float[17*POINT_COMPONENT_COUNT];
                appendBox(coordinate,logestRange(objFile));
                break;
            case ROCIRXY:
                appendCircleXY(coordinate);
                break;
            case ROCIRYZ:
                appendCircleYZ(coordinate);
                break;
            case ROCIRXZ:
                appendCircleXZ(coordinate);
                break;
            case TOUCHPNT:
                vertexData = new float[POINT_COMPONENT_COUNT];
                appendDot(coordinate);
                break;
            case GRD:
                appendGround(coordinate);
                break;
            case MOVEUD:
                appendMoveUD();
                break;
            case MOVEFB:
                appendMoveFB();
                break;
            case MOVERL:
                appendMoveRL();
                break;
            default:
                appendPlygen(objFile);
                break;
        }
        return new GeneratedData(vertexData, drawList, objFile);
    }
}
