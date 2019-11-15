package com.example.gocar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gocar.R;
import com.example.gocar.app.AppConfig;
import com.example.gocar.helper.SessionManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.util.Log;


public class Vehicles extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String KEY_LOCATION = "location";
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    LocationManager locationManager;

    public static double userLatitude;
    public static double userLongitude;

    ListView listView;
    Button btnMap;

    ArrayList<Car> cars = new ArrayList<Car>();
    ArrayList<Float> carsDistances = new ArrayList<Float>();
    public static ArrayList<Car> sortedCars = new ArrayList<Car>();
    public static ArrayList<Float> carsSortedDistances = new ArrayList<Float>();
    ArrayList<String> model = new ArrayList<String>();
    ArrayList<String> year = new ArrayList<String>();
    ArrayList<String> fuel = new ArrayList<String>();
    ArrayList<String> distance = new ArrayList<String>();
    ArrayList<String> imagesPaths = new ArrayList<String>();

    float smallest;
    int smallestIndex;

    public static int carIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicles);

        getCarDetails();

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLocationPermission();

        listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                carIndex = position;
                Intent myIntent = new Intent(view.getContext(), CarDetails.class);
                startActivity(myIntent);
            }

        });

        btnMap = (Button) findViewById(R.id.btnMap);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Vehicles.this, CarsLocations.class);
                startActivity(myIntent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_nearby_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.car_nearby) {
            calculateDistance();
        }
        return true;
    }

    public void getCarDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, AppConfig.URL_VEHICLE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray vehicles = new JSONArray(response);

                    for (int i = 0; i < vehicles.length(); i++) {
                        JSONObject vehicleObject = vehicles.getJSONObject(i);

                        String car_id = vehicleObject.getString("id");
                        String name = vehicleObject.getString("name");
                        int production_year = vehicleObject.getInt("production_year");
                        int fuel_level = vehicleObject.getInt("fuel_level");
                        double longitude = vehicleObject.getDouble("longitude");
                        double latitude = vehicleObject.getDouble("latitude");
                        String image_path = vehicleObject.getString("image_path");
                        Car car = new Car(car_id, name, production_year, fuel_level, longitude, latitude, image_path);
                        cars.add(car);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Vehicles.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        ;
                    }
                });
        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getUserLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getUserLocation();
                } else {
                    getLocationPermission();
                }
            }
        }
    }

    public void getUserLocation() {

        Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    // Set the map's camera position to the current location of the device.
                    mLastKnownLocation = task.getResult();
                    userLatitude = mLastKnownLocation.getLatitude();
                    userLongitude = mLastKnownLocation.getLongitude();
                }
            }
        });
    }

    public void getInitialLocation() {
        @SuppressLint("MissingPermission") Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        @SuppressLint("MissingPermission") Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        @SuppressLint("MissingPermission") Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        if (locationGPS != null) {
            userLatitude = locationGPS.getLatitude();
            userLongitude = locationGPS.getLongitude();

            String lat = String.valueOf(userLatitude);
            String lon = String.valueOf(userLongitude);

            Toast.makeText(getApplicationContext(), "Longitude: " + lon + "Latitude: " + lat, Toast.LENGTH_LONG).show();

        } else if (locationNetwork != null) {
            userLatitude = locationNetwork.getLatitude();
            userLongitude = locationNetwork.getLongitude();

            String lat = String.valueOf(userLatitude);
            String lon = String.valueOf(userLongitude);

            Toast.makeText(getApplicationContext(), "Longitude: " + lon + "Latitude: " + lat, Toast.LENGTH_LONG).show();

        } else if (locationPassive != null) {
            userLatitude = locationPassive.getLatitude();
            userLongitude = locationPassive.getLongitude();

            String lat = String.valueOf(userLatitude);
            String lon = String.valueOf(userLongitude);

            Toast.makeText(getApplicationContext(), "Longitude: " + lon + "Latitude: " + lat, Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "Can't get your location. Please enable your location.", Toast.LENGTH_LONG).show();
        }
    }


    public void calculateDistance() {       //result is in meters
        for (int i = 0; i < cars.size(); i++) {
            float distances[] = new float[5];
            Location.distanceBetween(userLatitude, userLongitude, cars.get(i).getLatitude(), cars.get(i).getLongitude(), distances);
            carsDistances.add(distances[0]);
        }

        int x = cars.size();
        for (int i = 0; i < x; i++) {
            if (cars.size() == 1) {
                sortedCars.add(cars.get(0));
                carsSortedDistances.add(carsDistances.get(0));
                model.add("Model: " + cars.get(0).getName());
                fuel.add("Fuel level: " + cars.get(0).getFuel_level() + " %");
                year.add("Prod. year: " + cars.get(0).getProduction_year());
                imagesPaths.add(cars.get(0).getImage_path());
                distance.add("Distance: " + carsDistances.get(0).toString() + " meters");
                break;
            }

            smallest = carsDistances.get(0);
            smallestIndex = 0;

            for (int j = 1; j < carsDistances.size(); j++) {
                if (carsDistances.get(j) <= smallest) {
                    smallest = carsDistances.get(j);
                    smallestIndex = j;
                }
            }

            sortedCars.add(cars.get(smallestIndex));
            carsSortedDistances.add(carsDistances.get(smallestIndex));
            model.add("Model: " + cars.get(smallestIndex).getName());
            fuel.add("Fuel level: " + cars.get(smallestIndex).getFuel_level() + " %");
            year.add("Prod. year: " + cars.get(smallestIndex).getProduction_year());
            imagesPaths.add(cars.get(smallestIndex).getImage_path());
            distance.add("Distance: " + carsDistances.get(smallestIndex).toString() + " meters");
            cars.remove(smallestIndex);
            carsDistances.remove(smallestIndex);
        }

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return imagesPaths.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = getLayoutInflater().inflate(R.layout.image_list, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
            TextView fuelTextView = (TextView) view.findViewById(R.id.fuelTextView);
            TextView yearTextView = (TextView) view.findViewById(R.id.yearTextView);
            TextView distanceTextView = (TextView) view.findViewById(R.id.distanceTextView);
            TextView modelTextView = (TextView) view.findViewById(R.id.modelTextView);

            modelTextView.setText(model.get(position));
            fuelTextView.setText(fuel.get(position));
            distanceTextView.setText(distance.get(position));
            yearTextView.setText(year.get(position));

            String path = "http://192.168.1.3/" + imagesPaths.get(position);
            Picasso.get().load(path).into(imageView);

            return view;
        }
    }
}
