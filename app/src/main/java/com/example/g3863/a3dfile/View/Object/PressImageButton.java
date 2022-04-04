package com.example.g3863.a3dfile.View.Object;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.lang.reflect.Array;
import java.util.Arrays;

/**
 * Created by g3863 on 2017/11/26.
 */

public class PressImageButton extends android.support.v7.widget.AppCompatImageButton {
    private int[] imageId = new int[]{0,0};
    private final static int PRESS = 0;
    private final static int UNPRESS = 1;
    public PressImageButton(Context context) {
        super(context);
    }

    public PressImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PressImageButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if(Arrays.equals(imageId,new int[]{0,0,})){
            return;
        }
        if(selected){
            setImageResource(imageId[PRESS]);
        }
        else {
            setImageResource(imageId[UNPRESS]);
        }
    }

    public void setImageId(int press, int unpress){
        imageId = new int[]{press,unpress};
        setSelected(isSelected());
    }

}
