package com.openu.a2017_app1.screens;

import android.content.Context;
import android.content.DialogInterface;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.ArItem;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.ModelSaveListener;
import com.openu.a2017_app1.screens.ar.ArDrawableItem;
import com.openu.a2017_app1.utils.LocationService;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * An  full-screen activity that shows the AR
 */
public class AugmentedReality extends AppCompatActivity implements SurfaceHolder.Callback {

    private static final int CAMERA_REQUEST = 1;
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
    private List<ArItem> mItems;

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

        mLocationService = new LocationService(this, new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                mCurrentLocation = mLocationService.lastLocation();
                mLocationService.requestLocationUpdates(new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        synchronized (mCurrentLocationLocker) {
                            mCurrentLocation = LocationPoint.fromLocation(location);
                        }
                    }
                });
            }
            @Override
            public void onConnectionSuspended(int i) {  }
        });

        ((FloatingActionButton)findViewById(R.id.fab)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                synchronized (mCurrentLocationLocker) {
                    if (mCurrentLocation == null) return;
                }
                final Integer[] drawables = new Integer[]{
                        R.drawable.ic_sticker_left,
                        R.drawable.ic_sticker_right,
                        R.drawable.ic_sticker_down
                };
                new AlertDialog.Builder(AugmentedReality.this)
                        .setAdapter(new StickersAdapter(AugmentedReality.this, drawables), new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                final ArItem item = new ArItem();
                                synchronized (mCurrentLocationLocker) {
                                    item.setLocation(mCurrentLocation);
                                }
                                item.setOrientation(mPhoneDirection[0]);
                                item.setSticker(drawables[which]);
                                item.saveAsync(new ModelSaveListener() {
                                    @Override
                                    public void onSave(boolean succeeded, Object id) {
                                        if (succeeded) {
                                            synchronized (mItemsLocker) {
                                                mItems.add(item);
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .show();
            }
        });

        Model.getQuery(ArItem.class).getAllAsync(new GetAllListener<ArItem>() {
            @Override
            public void onItemsReceived(List<ArItem> items) {
                synchronized (mItemsLocker) {
                    mItems = items;
                }
            }
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
                        for (ArItem item : mItems) {
                            ArDrawableItem arItem;
                            if (item.getSticker() > 0) {
                                arItem = new ArDrawableItem(BitmapFactory.decodeResource(getResources(), item.getSticker()));
                            } else {
                                arItem = new ArDrawableItem(textAsBitmap(item.getText()));
                            }
                            float angle = (float) mCurrentLocation.bearingTo(item.getLocation());

                            gl.glPushMatrix();
                            gl.glRotatef(-angle, 0, 1, 0);
                            gl.glTranslatef(0, 0, -20);
                            gl.glRotatef(-item.getOrientation(), 0, 1, 0);
                            arItem.draw(gl);
                            gl.glPopMatrix();
                        }
                    }
                }
            }
        }

        public Bitmap textAsBitmap(String text) {
            Paint paint = new Paint(ANTI_ALIAS_FLAG);
            paint.setTextSize(12);
            paint.setColor(Color.BLACK);
            paint.setTextAlign(Paint.Align.LEFT);
            float baseline = -paint.ascent(); // ascent() is negative
            int width = (int) (paint.measureText(text) + 0.5f); // round
            int height = (int) (baseline + paint.descent() + 0.5f);
            Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(image);
            Paint rectPaint = new Paint();
            rectPaint.setStyle(Paint.Style.FILL);
            rectPaint.setColor(Color.argb(100, 100, 100, 100));
            canvas.drawRect(0, 0, width, height, rectPaint);
            canvas.drawText(text, 0, baseline, paint);
            return image;
        }
    }

    private static class StickersAdapter extends ArrayAdapter<Integer> {

        public StickersAdapter(@NonNull Context context, Integer[] drawables) {
            super(context, 0, drawables);
        }

        public View getView(int position, View convertView, ViewGroup container) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(getItem(position));
            return imageView;
        }
    }
}
