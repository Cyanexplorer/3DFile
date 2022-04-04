/***
 * Excerpted from "OpenGL ES for Android",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/kbogla for more book information.
***/
package com.example.g3863.a3dfile.util;

public class Constants {
    public static final int BYTES_PER_FLOAT = 4;
    public static final int FLOATS_PER_VERTEX = 8;
    public static final int POSITION_COMPONENT_COUNT = 3;
    public static final int TEXTURE_COMPONENT_COUNT = 2;
    public static final int NORMAL_COMPONENT_COUNT=3;
    public static final int COLOR_COMPONENT_COUNT=4;
    public static final int POINT_COMPONENT_COUNT=3;
    public static final int F_FRAG = POSITION_COMPONENT_COUNT;
    public static final int S_FRAG = POSITION_COMPONENT_COUNT+TEXTURE_COMPONENT_COUNT;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COMPONENT_COUNT +NORMAL_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    public static final String DBname="Record";
    public static final String TBname="Score";

    public final static int totalSkil = 3;
    public final static int sklLimitCount = 255;
    public final static String STRFRG = "StartFragment";
    public final static String RDFRG = "RecordFragment";
    public final static String STGFRG = "StageFragment";

    public final static int ZOOM = 1;
    public final static int DRAG = 2;
    public final static int NONE = 0;

    public final static int NORMAL = 0;
    public final static int GRID = 1;
}
