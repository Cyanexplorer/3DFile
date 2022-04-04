package com.example.g3863.a3dfile.View.Object;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.example.g3863.a3dfile.Controller.Manager.ObjManager;
import com.example.g3863.a3dfile.Controller.GlEs2Renderer;

import static com.example.g3863.a3dfile.util.Constants.DRAG;
import static com.example.g3863.a3dfile.util.Constants.NONE;
import static com.example.g3863.a3dfile.util.Constants.ZOOM;

/**
 * Created by g3863 on 2017/11/18.
 */

public class Glsurface_Ext extends GLSurfaceView {
    public final static String SELECT = "SELECT";
    public final static String ROTATE = "ROTATE";
    public final static String MOVE = "MOVE";
    public final static String NOACTION = "NOACTION";
    public final static String STOP = "STOP";
    public final static String DELETE = "DELETE";

    int mode = NONE;
    public String state = NOACTION;

    float oldDist = 0f;
    float oldRotation = 0f;
    float[] oldTrans = new float[2];
    float[] orgPnt = new float[2];
    GlEs2Renderer glEs2Renderer;

    public Glsurface_Ext(Context context) {
        super(context);
    }

    public Glsurface_Ext(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setRenderer(Renderer renderer) {
        super.setRenderer(renderer);
        if(renderer instanceof GlEs2Renderer){
            glEs2Renderer = (GlEs2Renderer) renderer;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        ObjManager.ObjmngMsg objmngMsg = new ObjManager.ObjmngMsg();

        switch (event.getAction() & MotionEvent.ACTION_MASK){

            case MotionEvent.ACTION_DOWN:
                Log.i("test","action");
                oldTrans[0] = event.getX();
                oldTrans[1] = event.getY();
                orgPnt[0] = oldTrans[0];
                orgPnt[1] = oldTrans[1];
                mode = DRAG;
                if(state.equals(MOVE) || state.equals(ROTATE)){
                    objmngMsg.setDmove(oldTrans[0],oldTrans[1]);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if(mode == ZOOM){
                    float newDist = spacing(event);
                    //float newRotation = rotation(event);
                    float[] scale = new float[]{newDist/oldDist};
                    oldDist = newDist;
                    if(scale[0]>1.005f || scale[0]<0.995f){
                        glEs2Renderer.MatrixOperate(mode,scale);
                    }
                }
                else if(mode == DRAG){
                    float[] move = new float[2];
                    move[0] = event.getX() - oldTrans[0];
                    move[1] = event.getY() - oldTrans[1];
                    oldTrans[0] = (int)(event.getX());
                    oldTrans[1] = (int)(event.getY());
                    if(state.equals(MOVE)){
                        objmngMsg.setMove(oldTrans,move);
                    }
                    else if(state.equals(ROTATE)){
                        objmngMsg.setRotate(oldTrans,move);
                    }
                    else {
                        glEs2Renderer.MatrixOperate(mode,move);
                    }
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                Log.i("test","action");
                mode = ZOOM;
                oldDist = spacing(event);
                oldRotation = rotation(event);
                midPoint(event);
                break;

            case MotionEvent.ACTION_UP:
                mode = NONE;
                glEs2Renderer.MatrixOperate(mode,null);
                if(state.equals(SELECT) && event.getX() == orgPnt[0] && event.getY() == orgPnt[1]){
                    objmngMsg.setSelect(orgPnt[0],orgPnt[1]);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                glEs2Renderer.MatrixOperate(mode,null);
                break;

            default:
                break;
        }

        ObjManager.setObjmngMsg(objmngMsg);
        return true;
    }

    private float spacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x*x+y*y);
    }

    private float rotation(MotionEvent event){
        double delta_x = event.getX(0) - event.getX(1);
        double delta_y = event.getY(0) - event.getY(1);
        double degree = Math.atan2(delta_y,delta_x);
        return (float) Math.toDegrees(degree);
    }

    private void midPoint(MotionEvent event){
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
    }

}
