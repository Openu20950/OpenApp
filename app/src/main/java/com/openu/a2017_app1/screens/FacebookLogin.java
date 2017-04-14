package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.ModelSaveListener;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookLogin  extends AppCompatActivity {

    private TextView info;
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private Button guestLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        LoginManager.getInstance().logOut();
        //FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_facebook_login);

        //use the app without facebook account
        guestLogin = (Button) findViewById(R.id.guest_button);

        guestLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(FacebookLogin.this, PlacesAround.class);
                myIntent.putExtra(Place.FIELD_FACEBOOK_ID,"guest");
                FacebookLogin.this.startActivity(myIntent);
            }
        });



        info = (TextView)findViewById(R.id.info);

        loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {

                Intent myIntent = new Intent(FacebookLogin.this, PlacesAround.class);
                myIntent.putExtra(Place.FIELD_FACEBOOK_ID,loginResult.getAccessToken().getUserId());

                FacebookLogin.this.startActivity(myIntent);


            }

            @Override
            public void onCancel() {
                info.setText("Login attempt canceled.");

            }

            @Override
            public void onError(FacebookException e) {
                info.setText("Login attempt failed.");

            }
        });
    }

    @Override
   /* protected void onResume() {
        super.onResume();
        Intent myIntent = new Intent(FacebookLogin.this, FacebookLogin.class);

        FacebookLogin.this.startActivity(myIntent);
    }*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

}
