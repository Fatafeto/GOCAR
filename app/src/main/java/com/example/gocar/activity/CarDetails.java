package com.example.gocar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gocar.R;
import com.example.gocar.app.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CarDetails extends AppCompatActivity {

    Button buttonMap;
    Button buttonRoute;
    Button buttonReview;
    public static TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_details);

        buttonReview = (Button) findViewById(R.id.btnReview);
        buttonMap = (Button) findViewById(R.id.btnMap);
        buttonRoute = (Button) findViewById(R.id.btnRoute);
        addListenerOnButton();

        text = (TextView) findViewById(R.id.textView);
        text.setMovementMethod(new ScrollingMovementMethod());

        addInfo();
    }

    public void addInfo() {
        int position = Vehicles.carIndex;

        text.append("- Car Model: " + Vehicles.sortedCars.get(position).getName() + '\n');
        text.append("- Location: " + '\n');
        text.append("  Latitude --> : " + Vehicles.sortedCars.get(position).getLatitude() + '\n');
        text.append("  Longitude --> " + Vehicles.sortedCars.get(position).getLongitude() + '\n');
        text.append("- Reviews: " + '\n');
        getReviews();
    }

    public void getReviews() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_REVIEW_GET, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray reviews = new JSONArray(response);

                    String selected_car_id = Vehicles.sortedCars.get(Vehicles.carIndex).getId();

                    for (int i = 0; i < reviews.length(); i++) {
                        JSONObject vehicleObject = reviews.getJSONObject(i);
                        String user_review = vehicleObject.getString("user_review");
                        int car_id = vehicleObject.getInt("car_id");
                        int user_id = vehicleObject.getInt("user_id");
                        if(selected_car_id.equalsIgnoreCase("" + car_id)) {
                            text.append("  User " + user_id + ": " + user_review + '\n');
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CarDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        ;
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
    }
    public void addListenerOnButton() {
        final Context context = this;
        buttonReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CarDetails.this, Review.class);
                startActivity(myIntent);
            }
        });

        buttonMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CarDetails.this, Map.class);
                startActivity(myIntent);
            }
        });

        buttonRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double lat = Vehicles.sortedCars.get(Vehicles.carIndex).getLatitude();
                Double lon = Vehicles.sortedCars.get(Vehicles.carIndex).getLongitude();

                String q = "q= " + lat + ", " + lon;
                Uri gmmIntentUri = Uri.parse("google.navigation:" + q);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

            }
        });

    }
}
