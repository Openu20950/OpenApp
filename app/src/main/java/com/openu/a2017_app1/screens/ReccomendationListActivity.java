package com.openu.a2017_app1.screens;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.openu.a2017_app1.R;
import com.openu.a2017_app1.models.Place;
import com.openu.a2017_app1.models.PlaceImp;
import com.openu.a2017_app1.models.Recommendation;
import com.openu.a2017_app1.models.RecommendationImp;

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

        selectedplace =
                (Place) bundle.getSerializable("value");

        list = (ListView) findViewById(R.id.recommendationList);

        recommendationList = selectedplace.getRecommendations();

        /*for (int i = 0; i < 5; i++){
            Place p = new PlaceImp();
            p.setName("Place" + i);
            p.setCategory("Category of " + i);
            p.addRecommendation(new RecommendationImp());
            places.add(p);
        }*/

        listviewadapter = new RecommendationsListViewAdapter(this, R.layout.recommendation_listview_item,
                recommendationList);

        // Binds the Adapter to the ListView
        list.setAdapter(listviewadapter);

        Button newbutton = (Button) findViewById(R.id.addReviewBtn);
        newbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btnNewClicked(v);
            }
        });
        //list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        // Capture ListView item click

        /*list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // selected item
                Place selectedplace = listviewadapter.getPlaces().get(position);

                // Launching new Activity on selecting single List Item
                //SingleListItem will be changed to the name of the activity displaying the recommendations
                Intent i = new Intent(getApplicationContext(), ReccomendationListActivity.class); //SingleListItem
                // sending data to new activity
                Bundle bundle = new Bundle();
                bundle.putSerializable("value", selectedplace);
                i.putExtras(bundle);
                //i.putExtra("com.openu.a2017_app1.screens.Place", selectedplace);
                //i.putExtra("Place", selectedplace);
                startActivity(i);

            }
        });*/
    }

    void btnNewClicked(View v) {
        Intent i = new Intent(getApplicationContext(), PostReviewActivity.class); //SingleListItem
        // sending data to new activity
        Bundle bundle = new Bundle();
        bundle.putBoolean("existinglocation", true);
        bundle.putSerializable("value", selectedplace);
        i.putExtras(bundle);
        startActivity(i);
    }
}
