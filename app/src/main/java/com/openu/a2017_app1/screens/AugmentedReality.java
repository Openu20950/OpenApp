package com.openu.a2017_app1.screens;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.screens.ar.ArDrawableItem;
import com.openu.a2017_app1.utils.LocationService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * An  full-screen activity that shows the AR
 */
public class AugmentedReality extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_REQUEST = 1;
    private static final int DISTANCE = 50;
    private SurfaceHolder mSurfaceHolder;
    private GLSurfaceView mGlSurface;
    private LocationService mLocationService;

    private Camera mCamera;
    private boolean mIsCameraviewOn;
    private SensorManager mSensorManager;

    private float[] mPhoneDirection;
    private Object mCurrentLocationLocker = new Object();
    private LocationPoint mCurrentLocation;
    private Object mItemsLocker = new Object();
    private List<ArDrawableItem> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!checkCameraHardware(this)) {
            Toast.makeText(this, R.string.no_camera, Toast.LENGTH_LONG).show();
            finish();
        }

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorManager.registerListener(new SensorListener(), mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_NORMAL);

        setContentView(R.layout.activity_augmented_reality);
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.ar_camera);
        mSurfaceHolder = surfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mGlSurface = (GLSurfaceView) findViewById(R.id.ar_gl_view);
        mGlSurface.setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );
        mGlSurface.getHolder().setFormat( PixelFormat.TRANSLUCENT );
        mGlSurface.setRenderer(new Renderer());

        final Bitmap defaultIcon =  BitmapFactory.decodeResource(getResources(), R.drawable.ic_place);
        mLocationService = new LocationService(this, new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mCurrentLocation = mLocationService.lastLocation();
                mLocationService.requestLocationUpdates(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        synchronized (mCurrentLocationLocker) {
                            mCurrentLocation = LocationPoint.fromLocation(location);
                            Model.getQuery(Place.class)/*.whereNear(Place.FIELD_LOCATION, mCurrentLocation, DISTANCE)*/.getAllAsync(new GetAllListener<Place>() {
                                @Override
                                public void onItemsReceived(List<Place> items) {
                                    synchronized (mItemsLocker) {
                                        mItems = new ArrayList<>();
                                        for (Place item : items) {
                                            Bitmap bitmap = item.getPhoto() != null ? item.getPhoto().copy(Bitmap.Config.ARGB_8888, true) : defaultIcon.copy(Bitmap.Config.ARGB_8888, true);
                                            textOnBitmap(item.getName(), bitmap);
                                            mItems.add(new ArDrawableItem(bitmap, item.getLocation()));
                                        }
                                    }
                                }
                            });
                        }
                    }
                });
            }
            @Override
            public void onConnectionSuspended(int i) {  }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationService.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationService.disconnect();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        createCamera();
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mIsCameraviewOn) {
            mCamera.stopPreview();
            mIsCameraviewOn = false;
        }
        connectCamera();
    }
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            mIsCameraviewOn = false;
        }
    }

    private void createCamera() {
        if (mCamera != null) {
            return;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.CAMERA  }, CAMERA_REQUEST );
        } else {
            mCamera = Camera.open();
            mCamera.setDisplayOrientation(90);
        }
    }

    private void connectCamera() {
        createCamera();
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(mSurfaceHolder);
                mCamera.startPreview();
                mIsCameraviewOn = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connectCamera();
                } else {
                    Toast.makeText(this, "Cannot continue without camera permission!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void textOnBitmap(String text, Bitmap bitmap) {
        Paint paintFront = new Paint(ANTI_ALIAS_FLAG);
        paintFront.setTextSize(36);
        paintFront.setColor(Color.WHITE);
        paintFront.setTextAlign(Paint.Align.LEFT);
        Paint paintBack = new Paint(ANTI_ALIAS_FLAG);
        paintBack.setTextSize(36);
        paintBack.setColor(Color.BLACK);
        paintBack.setTextAlign(Paint.Align.LEFT);
        float baseline = -paintFront.ascent(); // ascent() is negative
        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(text, 2, baseline + 2, paintBack);
        canvas.drawText(text, 0, baseline, paintFront);
    }

    private class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent event) {
            mPhoneDirection = event.values;
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int accuracy) {

        }
    }

    private class Renderer implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            gl.glDisable(GL10.GL_DITHER);
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,
                    GL10.GL_FASTEST);

            gl.glClearColor(0,0,0,0);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);
        }
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glViewport(0, 0, width, height);

            float ratio = (float) width / height;
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            GLU.gluPerspective(gl, 45.0f, ratio, 0.01f, 100.0f);
        }
        @Override
        public void onDrawFrame(GL10 gl) {
           gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();

            if (mPhoneDirection != null) {
                float azimut = mPhoneDirection[0];
                float x = (float)Math.sin(azimut * Math.PI / 180);
                float z = (float)-Math.cos(azimut * Math.PI / 180);
                GLU.gluLookAt(gl, 0, 0, 0, x, 0, z, 0, 1, 0);
            }

            synchronized (mCurrentLocationLocker) {
                synchronized (mItemsLocker) {
                    if (mCurrentLocation != null && mItems != null) {
                        for (ArDrawableItem item : mItems) {
                            float angle = (float) mCurrentLocation.bearingTo(item.getLocation());
                            gl.glPushMatrix();
                            gl.glRotatef(-angle, 0, 1, 0);
                            gl.glTranslatef(0, 0, -20f);//(float) -(mCurrentLocation.distanceTo(item.getLocation()) * 1000 * 0.6 + 20));
                            item.draw(gl);
                            gl.glPopMatrix();
                        }
                    }
                }
            }
        }
    }
}
