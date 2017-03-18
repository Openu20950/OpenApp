package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.Model;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.Recommendation;

import java.util.ArrayList;
import java.util.List;

public class ReccomendationListActivity extends AppCompatActivity {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reccomendation_list);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        Place selectedplace =
                (Place) bundle.getSerializable("value");
    }*/

    ListView list;
    Place selectedplace;
    RecommendationsListViewAdapter listviewadapter;
    List<Recommendation> recommendationList = new ArrayList<Recommendation>();;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reccomendation_list);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        if(bundle.getString("value") != null) {
            Toast.makeText(this, "ID is " + bundle.getString("value"), Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "ID is was null", Toast.LENGTH_LONG).show();

        }

        selectedplace =
               Model.getQuery(Place.class).find(bundle.getString("value"));//.where(new Place().getPrimaryKey() ,"==" ,bundle.getString("value")).get();
       //bundle.getSerializable("value");

        list = (ListView) findViewById(R.id.recommendationList);

        Toast.makeText(this, "Place gotten was valid? " + (selectedplace != null), Toast.LENGTH_LONG).show();
        recommendationList = selectedplace.getRecommendations().getAll();

        /*for (int i = 0; i < 5; i++){
            Place p = new PlaceImp();
            p.setName("Place" + i);
            p.setCategory("Category of " + i);
            p.addRecommendation(new RecommendationImp());
            places.add(p);
        }*/

        listviewadapter = new RecommendationsListViewAdapter(this, R.layout.recommendation_listview_item,recommendationList);

        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);

        Button newbutton = (Button) findViewById(R.id.addReviewBtn);
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
        bundle.putBoolean("existinglocation", true);
        bundle.putString("id", selectedplace.getId());
        i.putExtras(bundle);
        startActivity(i);
    }
}
