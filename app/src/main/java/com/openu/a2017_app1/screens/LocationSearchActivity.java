package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.LocationPoint;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.services.LocationService;

import java.util.ArrayList;
import java.util.List;

public class LocationSearchActivity extends AppCompatActivity {

    ListView list;
    LocationsListViewAdapter listviewadapter;
    List<Place> places = new ArrayList<Place>();;
    private LocationService locservice;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationPoint currentloc;
        setContentView(R.layout.activity_location_search);

        locservice = new LocationService(this);
        locservice.mGoogleApiClient.connect();
        currentloc = locservice.GetLocationPoint();
        //places = getformserver; TODO get places from server here
        //places = getQuery(Place.class).where("name", "==" , "falafal")..getAll();

        /*long t= System.currentTimeMillis();
        long end = t+100000;
        while(System.currentTimeMillis() < end) {
            // do something
            // pause to avoid churning

            try {
                currentloc = locservice.GetLocationPoint();
                if(currentloc == null) {
                    Thread.sleep(500);
                }
                else break;
            } catch (InterruptedException ex) {}
        }*/

        list = (ListView) findViewById(R.id.LocationList);
        places.addAll(Model.getQuery(Place.class).whereNear("Location",currentloc, (double) 500));

        Toast.makeText(this, "We got data from the server it contained " + places.size() + " Places", Toast.LENGTH_LONG).show();

        // BasicModel.getQuery(Place.class).whereNear("Location",locservice.GetLocationPoint(), (double) 500);
        //public static <T extends Model> QueryBuilder<T> getQuery(Class<T> clas)
        //places = getformserver;
        //places = getQuery(Place.class).where("name", "==" , "falafal")..getAll();

        //Demo Data
       /* for (int i = 0; i < 5; i++) {
            Place p = new PlaceImp();
            p.setName("Place" + i);
            p.setCategory("Category of " + i);
            RecommendationImp temprec = new RecommendationImp();
            temprec.setTitle(p.getName() + "is great! " + i);
            temprec.setDescription("I went to " + p.getName() + " " + (i+17) +" times in a row!");
            p.addRecommendation(temprec);

            places.add(p);
        }*/

        listviewadapter = new LocationsListViewAdapter(this, R.layout.location_listview_item,places);

        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);
        //list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                Place selectedplace = listviewadapter.getPlaces().get(position);

                // Launching new Activity on selecting single List Item
                //SingleListItem will be changed to the name of the activity displaying the recommendations
                Intent i = new Intent(getApplicationContext(), ReccomendationListActivity.class); //SingleListItem
                // sending data to new activity
                Bundle bundle = new Bundle();
                //bundle.putBoolean("existinglocation", true);
                bundle.putSerializable("value", selectedplace);
                i.putExtras(bundle);
                //i.putExtra("com.openu.a2017_app1.screens.Place", selectedplace);
                //i.putExtra("Place", selectedplace);
                startActivity(i);

            }
        });

        Button newbutton = (Button) findViewById(R.id.newButton);
        newbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnNewClicked(v);
            }
        });
    }

    void btnNewClicked(View v) {
        Intent i = new Intent(getApplicationContext(), PostReviewActivity.class); //SingleListItem
        // sending data to new activity
        Bundle bundle = new Bundle();
        bundle.putBoolean("existinglocation", false);
        //bundle.putSerializable("value", selectedplace);
        i.putExtras(bundle);
        startActivity(i);
    }

}