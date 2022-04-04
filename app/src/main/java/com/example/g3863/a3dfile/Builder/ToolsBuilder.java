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

import java.util.ArrayList;
import java.util.List;

import static android.opengl.GLES20.GL_LINE_LOOP;
import static android.opengl.GLES20.glDrawArrays;
import static com.example.g3863.a3dfile.util.Constants.FLOATS_PER_VERTEX;
import static com.example.g3863.a3dfile.util.Constants.POINT_COMPONENT_COUNT;

public class ToolsBuilder {
    public final static String BOX = "BOX";
    public final static String ROCIR = "ROCIR";

    public interface DrawCommand {
        void draw();
    }

    public static class GeneratedData {
        public final float[] vertexData;
        public final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    public static ToolsBuilder createPlygen(Coordinate coordinate,float size,String type){
        return new ToolsBuilder(type, coordinate, size);
    }

    private float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<>();

    private ToolsBuilder(String type,Coordinate coordinate,float size) {

        switch (type){
            case BOX:
                vertexData = new float[12*POINT_COMPONENT_COUNT];
                appendBox(coordinate,size);
                break;
            case ROCIR:
                vertexData = new float[108*POINT_COMPONENT_COUNT];
                appendCircle(coordinate,size);
                break;
            default:break;
        }
    }

    private void appendBox(Coordinate coordinate, float maxSize){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = 8;
        final int ra = 90;
        for(int i = 0;i<4;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz()+maxSize;
        }

        for(int i = 0;i<4;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz()-maxSize;
        }

        for(int i = 0;i<4;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz()+maxSize;
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz()-maxSize;
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_LINE_LOOP, startVertex, numVertices);
                glDrawArrays(GL_LINE_LOOP, numVertices, 8);
            }
        });
    }

    private void appendCircle(Coordinate coordinate, float maxSize){
        int offset = 0;
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int ra = 10;
        final int count = 360/ra;
        for(int i = 0;i<count;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.sin(ra*i));
            vertexData[offset++]=coordinate.getz();
        }

        for(int i = 0;i<count;i++){
            vertexData[offset++]= (float) (coordinate.getx()+maxSize*Math.cos(ra*i));
            vertexData[offset++]= coordinate.gety();
            vertexData[offset++]=(float) (coordinate.getz()+maxSize*Math.sin(ra*i));
        }

        for(int i = 0;i<count;i++){
            vertexData[offset++]= coordinate.getx();
            vertexData[offset++]= (float) (coordinate.gety()+maxSize*Math.cos(ra*i));
            vertexData[offset++]=(float) (coordinate.getz()+maxSize*Math.sin(ra*i));
        }

        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_LINE_LOOP, startVertex, count);
                glDrawArrays(GL_LINE_LOOP,count,count);
                glDrawArrays(GL_LINE_LOOP,count*2,count);
            }
        });
    }
    public GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }
}
