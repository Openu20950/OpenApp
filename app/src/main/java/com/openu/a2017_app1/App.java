package com.openu.a2017_app1;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by Raz on 01/12/2016.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
        .applicationId("testapp1-20950")
        .server("https://testapp1-20950.herokuapp.com/")
        .clientKey("testapp1-20950-master")
        .build());
    }
}