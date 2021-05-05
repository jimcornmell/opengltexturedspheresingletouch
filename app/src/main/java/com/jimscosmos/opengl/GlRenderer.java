package com.jimscosmos.opengl;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.view.MotionEvent;

import com.jimcosmos.opengl.R;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Simple class to render an object in a suitable view port.
 *
 * @author Jim Cornmell
 * @since July 2013
 */
class GlRenderer implements Renderer {
    /** Factors to rotate sphere. */
    private static final float FACTOR_ROTATION_LONGITUDE = 50.0f;
    private static final float FACTOR_ROTATION_LATITUDE = 50.0f;

    /** Factors to scale sphere. */
    private static final float FACTOR_HYPOT = 500.0f;
    private static final float SPHERE_SCALE_MAX = 4.0f;
    private static final float SPHERE_SCALE_MIN = 0.5f;

    /** Object distance on the screen. */
    private static final float OBJECT_DISTANCE = -10.0f;

    /** Clear colour, alpha component. */
    private static final float CLEAR_RED = 0.0f;

    /** Clear colour, alpha component. */
    private static final float CLEAR_GREEN = 0.0f;

    /** Clear colour, alpha component. */
    private static final float CLEAR_BLUE = 0.0f;

    /** Clear colour, alpha component. */
    private static final float CLEAR_ALPHA = 0.5f;

    /** Perspective setup, field of view component. */
    private static final float FIELD_OF_VIEW_Y = 45.0f;

    /** Perspective setup, near component. */
    private static final float Z_NEAR = 0.1f;

    /** Perspective setup, far component. */
    private static final float Z_FAR = 100.0f;

    /** The earth's sphere. */
    private final Sphere mEarth;

    /** The context. */
    private final Context mContext;

    /** The spheres rotation angle. */
    private float mRotationAngle;

    /** The spheres tilt angle. */
    private float mAxialTiltAngle;

    /** Scaling of the sphere. */
    private float mSphereScale = 1.0f;

    /**
     * Change the rotation angles for the sphere.
     *
     * @param pointA Start point.
     * @param pointB End point.
     */
    public void setMoveDelta(MotionEvent.PointerCoords pointA, MotionEvent.PointerCoords pointB) {
        mRotationAngle -= (pointA.x - pointB.x)/ FACTOR_ROTATION_LONGITUDE;
        mAxialTiltAngle -= (pointA.y - pointB.y)/ FACTOR_ROTATION_LATITUDE;
    }

    /**
     * Constructor to set the handed over context.
     * @param context The context.
     */
    public GlRenderer(final Context context) {
        this.mContext = context;
        this.mEarth = new Sphere(3, 2);
        this.mRotationAngle = 0.0f;
        this.mAxialTiltAngle = 0.0f;
    }

    @Override
    public void onDrawFrame(final GL10 gl) {
        gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glTranslatef(0.0f, 0.0f, OBJECT_DISTANCE);
		gl.glScalef(mSphereScale, mSphereScale, mSphereScale);
        gl.glRotatef(this.mAxialTiltAngle, 1, 0, 0);
        gl.glRotatef(this.mRotationAngle, 0, 1, 0);
        this.mEarth.draw(gl);
    }

    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        final float aspectRatio = (float) width / (float) (height == 0 ? 1 : height);

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, FIELD_OF_VIEW_Y, aspectRatio, Z_NEAR, Z_FAR);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        this.mEarth.loadGLTexture(gl, this.mContext, R.drawable.earth);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearColor(CLEAR_RED, CLEAR_GREEN, CLEAR_BLUE, CLEAR_ALPHA);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }
}
