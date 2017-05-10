package com.openu.a2017_app1.screens.ar;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Raz on 03/05/2017.
 */
public class ArDrawableItem {
    private final Bitmap mBitmap;
    private boolean mLoaded = false;
    private int[] textures;

    private static final float[] VERTEX_COORDINATES = new float[] {
            -1, -1, 0.0f,
            +1, -1, 0.0f,
            -1, +1, 0.0f,
            +1, +1, 0.0f
    };

    private static final float[] TEXTURE_COORDINATES = new float[] {
            0.0f, 1,
            1, 1,
            0.0f, 0.0f,
            1, 0.0f
    };

    private static final Buffer TEXCOORD_BUFFER = ByteBuffer.allocateDirect(TEXTURE_COORDINATES.length * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(TEXTURE_COORDINATES).rewind();
    private static final Buffer VERTEX_BUFFER = ByteBuffer.allocateDirect(VERTEX_COORDINATES.length * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(VERTEX_COORDINATES).rewind();

    public ArDrawableItem(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    private void load(GL10 gl) {
        gl.glEnable(GL10.GL_TEXTURE_2D);

        textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, mBitmap, 0);
        //mBitmap.recycle();
        mLoaded = true;
    }

    public void draw(GL10 gl) {
        if (!mLoaded) {
            load(gl);
        }

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        gl.glActiveTexture(GL10.GL_TEXTURE0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, VERTEX_BUFFER);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, TEXCOORD_BUFFER);
        gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
    }

}


