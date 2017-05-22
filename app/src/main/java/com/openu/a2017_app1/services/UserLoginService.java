package com.openu.a2017_app1.services;


import android.content.Context;
import android.net.Uri;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by noam on 27/04/2017.
 */

public class UserLoginService implements Serializable {

    private String facebookId;
    private String facebookName;
    private List<String> friendsList;
    private Uri myProfilePicture;
    public UserLoginService(Context context)
    {
        friendsList=new ArrayList<String>();
        facebookId = "";
        facebookName="Guest";
        myProfilePicture = Uri.EMPTY;

    }





    public String getMyFacebookId()
    {
        return facebookId;
    }

    public void setMyFacebookId(String fcId)
    {
        facebookId = fcId;
    }

    public String getMyFacebookName()
    {
        return facebookName;
    }

    public void setMyFacebookName(String name)
    {
        facebookName = name;
    }

    public List<String> getFreindsList()
    {
       return friendsList;
    }

    public Uri getMyProfilePicture()
    {
        return myProfilePicture;
    }

    public void setMyProfilePicture(Uri pic)
    {
        myProfilePicture = pic;
    }

    public void graphRequest()
    {
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        try {
                            JSONArray friendListJSONArray =response.getJSONObject().getJSONArray("data");//.getJSONArray("id");
                            List<String> flist=new ArrayList<String>();
                            for(int i=0;i<friendListJSONArray.length();i++)
                            {
                                friendsList.add(friendListJSONArray.getJSONObject(i).getString("id"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }
}
