package com.example.gocar.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gocar.R;
import com.example.gocar.app.AppConfig;
import com.example.gocar.app.AppController;
import com.example.gocar.helper.SQLiteHandler;
import com.example.gocar.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.gocar.activity.Register.nationality;

public class Review extends AppCompatActivity {

    private static final String TAG = Register.class.getSimpleName();
    public SQLiteHandler db;
    EditText editText;
    Button btnPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        db = new SQLiteHandler(getApplicationContext());

        editText = (EditText) findViewById(R.id.reviewText);

        btnPost = (Button) findViewById(R.id.btnPost);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReview(editText.getText().toString().trim());
            }
        });
    }

    public void addReview(final String review) {
        String tag_string_req = "req_review";
        StringRequest strReq = new StringRequest(Request.Method.POST, AppConfig.URL_REVIEW_POST, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");
                        JSONObject review = jObj.getJSONObject("review");
                        String car_id = review.getString("car_id");
                        String user_id = review.getString("user_id");
                        String user_review = review.getString("user_review");

                        // Inserting row in reviews table
                        db.addReview(uid , user_id , car_id , user_review);

                        CarDetails.text.append("  User " + user_id + ": " + user_review + '\n');

                        Intent intent = new Intent(Review.this, CarDetails.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Error in posting the review: " + error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            protected java.util.Map<String, String> getParams() {

                // Fetching user details from sqlite
                HashMap<String, String> user = db.getUserDetails();

                String user_id = user.get("id");
                String car_id = Vehicles.sortedCars.get(Vehicles.carIndex).getId();

                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("car_id", car_id);
                params.put("user_review", review);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq , tag_string_req);
    }
}
