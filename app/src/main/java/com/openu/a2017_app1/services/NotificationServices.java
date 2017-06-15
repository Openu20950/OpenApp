package com.openu.a2017_app1.services;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.data.GetAllListener;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;
import com.openu.a2017_app1.screens.PlacesAround;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by noam on 15/05/2017.
 */

public class  NotificationServices extends Service  {
    public static boolean IS_SERVICE_RUNNING = false;

    private NotificationCompat.Builder mBuilder;
    private NotificationManager mNotificationManager;
    private LocationManager locationManager;
    private boolean all_or_friends;
    private int radius;
    private List<Place> places;
    private Handler h ;

    private LocationListener locationListener = new  LocationListener() {
        @Override
        public synchronized void onLocationChanged(final Location location) {
            h.postDelayed (new Runnable(){

                public void run(){

                    SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(NotificationServices.this);

                    all_or_friends = prefs.getBoolean("friend_only",false);
                    String radius_notif = prefs.getString("place_around_radius","100");

                    radius=Integer.parseInt(radius_notif);

                    if(location!=null && IS_SERVICE_RUNNING ) {
                        LocationPoint myLocation=new LocationPoint();
                        myLocation.setLatitude(location.getLatitude());
                        myLocation.setLongitude(location.getLongitude());


                        Model.getQuery(Place.class).whereNear(Place.FIELD_LOCATION, myLocation, radius).getAllAsync(new GetAllListener<Place>() {
                            @Override
                            public void onItemsReceived(List<Place> items) {
                                if(all_or_friends)
                                {
                                    if(PlacesAround.list!=null && items.size()>0)
                                    {
                                        List<Place> newItems=new ArrayList<Place>();
                                        List<Review> reviews=new ArrayList<Review>();
                                        for(Place p:items)
                                        {
                                            reviews=p.getReviews().getAll();
                                            for (int i=0;i<PlacesAround.list.size();i++)
                                            {
                                                String id=PlacesAround.list.get(i);

                                                if(p.getFacebookId().equals(id))
                                                {
                                                    newItems.add(p);
                                                }

                                                for(Review r:reviews)
                                                {
                                                    if(r.getFacebookId().equals(id))
                                                    {
                                                        newItems.add(p);
                                                    }
                                                }
                                            }
                                        }

                                        if(newItems.size()>0 && listsAreEquivelent(newItems,places)==false)
                                        {
                                            places = new ArrayList<Place>();
                                            places.addAll(newItems) ;

                                            Intent activityIntent = new Intent(NotificationServices.this, PlacesAround.class);
                                            activityIntent.putExtra("radius_notif",radius+"m");
                                            activityIntent.putExtra("friend_filter",true);
                                            PendingIntent contentIntent = PendingIntent.getActivity(NotificationServices.this, 0,
                                                    activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                                            mBuilder.setContentIntent(contentIntent);
                                            mBuilder.setAutoCancel(true);


                                            mNotificationManager =
                                                    (NotificationManager) getSystemService(
                                                            Context.NOTIFICATION_SERVICE);
                                            mNotificationManager.notify(1, mBuilder.build());

                                        }
                                    }

                                }else{
                                    if(items.size()>0 && listsAreEquivelent(items,places)==false)
                                    {
                                        places = new ArrayList<Place>();
                                        places.addAll(items) ;

                                        Intent activityIntent = new Intent(NotificationServices.this, PlacesAround.class);
                                        activityIntent.putExtra("radius_notif",radius+"m");
                                        activityIntent.putExtra("friend_filter",false);

                                        PendingIntent contentIntent = PendingIntent.getActivity(NotificationServices.this, 0,
                                                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                                        mBuilder.setContentIntent(contentIntent);
                                        mBuilder.setAutoCancel(true);


                                        mNotificationManager =
                                                (NotificationManager) getSystemService(
                                                        Context.NOTIFICATION_SERVICE);
                                        mNotificationManager.notify(1, mBuilder.build());


                                    }

                                }

                            }
                        });


                    }

                }

            }, 1000);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        IS_SERVICE_RUNNING = true;
        h = new Handler();
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        places=new ArrayList<Place>() ;
        setmNotificationManager();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                            PackageManager.PERMISSION_GRANTED) {
                return START_NOT_STICKY;
            }
        }
        locationManager.removeUpdates(locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0,
                locationListener);

        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        if(mNotificationManager!=null)
        {
            mNotificationManager.cancelAll();
        }
        if(locationManager!=null)
        {
            locationManager.removeUpdates(locationListener);
            locationManager=null;
        }

    }


    private boolean listsAreEquivelent(List<Place> a, List<Place> b) {
        if (a == null) {
            if (b == null) {
                //Here 2 null lists are equivelent. You may want to change this.
                return true;
            } else {
                return false;
            }
        }
        if (b == null) {
            return false;
        }
        if (b.size() != a.size())
            return false;

        int count=0;
        for(int i=0;i<a.size();i++)
        {
            LocationPoint loc = a.get(i).getLocation();
            for(int j=0;j<b.size();j++)
            {
                if(loc.distanceTo(b.get(i).getLocation())>0.1)
                {
                    count++;
                }
            }
            if(count==b.size())
            {
                return false;
            }else{
                count=0;
            }
        }
        return true;
    }


    private void setmNotificationManager()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(NotificationServices.this);
        String strRingtonePreference = prefs.getString("notifications_new_message_ringtone", "DEFAULT_SOUND");
        Uri defaultSoundUri = Uri.parse(strRingtonePreference);
        boolean vib = prefs.getBoolean("notifications_new_message_vibrate", true);
        long [] v={0,0,0};
        if (vib)
        {
            v [0] = 500;
            v[1] = 1000;
            v[2] = 500;
        }
        mBuilder =
                new NotificationCompat.Builder(NotificationServices.this)
                        .setSmallIcon(R.drawable.ic_place_notification)
                        .setContentTitle("Notification")
                        .setContentText("There are new places to visit around you")
                        .setSound(defaultSoundUri)
                        .setVibrate(v);
    }


}
