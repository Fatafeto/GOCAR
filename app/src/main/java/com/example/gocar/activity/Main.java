package com.example.gocar.activity;

import com.example.gocar.R;
import com.example.gocar.helper.SQLiteHandler;
import com.example.gocar.helper.SessionManager;
import java.util.HashMap;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class Main extends Activity {

    private TextView txtName;
    private TextView txtEmail;
    private Button btnLogout;
    private Button btnMap;
    private Button btnFind;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    LocationManager locationManager;
    boolean locationStatus;
    boolean locationGranted;

    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getLocationPermission();

        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        txtName = (TextView) findViewById(R.id.name);
        txtEmail = (TextView) findViewById(R.id.email);
        btnLogout = (Button) findViewById(R.id.btnLogout);
        btnMap = (Button) findViewById(R.id.btnMap);
        btnFind = (Button) findViewById(R.id.btnFind);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Displaying the user details on the screen
        txtName.setText(name);
        txtEmail.setText(email);

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(locationGranted == true) {
                    locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if(locationStatus == false) {
                        checkLocation();
                    }
                    else {
                        Intent intent = new Intent(Main.this, MapsCurrentLocation.class);
                        startActivity(intent);
                    }
                }
                else if(locationGranted == false) {
                    getLocationPermission();
                }
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(locationGranted == true) {

                    if(locationStatus == false) {
                        checkLocation();
                    }
                    else {
                        Intent intent = new Intent(Main.this, Vehicles.class);
                        startActivity(intent);
                    }
                }
                else if(locationGranted == false) {
                    getLocationPermission();
                }
            }
        });
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(Main.this, Login.class);
        startActivity(intent);
        finish();
    }

    public void checkLocation() {
        locationStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (locationStatus == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Location is Disabled");
            builder.setMessage("Please enable location in settings.");
            builder.setPositiveButton("Go to Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Show location settings when the user acknowledges the alert dialog
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            }
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationGranted = true;
        }
        else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        locationGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationGranted = true;
                }
            }
        }
    }

}