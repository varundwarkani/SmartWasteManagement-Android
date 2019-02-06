package devoops.smartwastemanagement;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Garbage extends AppCompatActivity implements OnMapReadyCallback {

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    private GoogleMap mMap;
    LatLng latLng;
    private List<LatLng> pontos;
    ProgressDialog dialog;
    String origin = "64.711696,12.170481";
    String destination = "34.711696,2.170481";
    Double currentlat,currentlon;
    int i = 0;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    String city;

    Button btmaps;
    String name,complaint,number,uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_garbage);

        SharedPreferences sharedPreferencesCategory  = getSharedPreferences(Main2Activity.MAPS, Context.MODE_PRIVATE);
        city = sharedPreferencesCategory.getString("CITY","none");


        btmaps = findViewById(R.id.btmaps);
        btmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(Garbage.this);
                View v = null;
                v = LayoutInflater.from(Garbage.this).inflate(R.layout.complaint_layout,null,false);
                builder.setView(v);
                final EditText etComplaint,etComplaintNumber,etComplaintName;
                Button btComplaint;
                etComplaintName = v.findViewById(R.id.etComplaintName);
                etComplaintNumber = v.findViewById(R.id.etComplaintNumber);
                etComplaint = v.findViewById(R.id.etComplaint);
                btComplaint = v.findViewById(R.id.btComplaint);
                builder.setCancelable(true);
                btComplaint.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        name = etComplaintName.getText().toString();
                        complaint = etComplaint.getText().toString();
                        number = etComplaintNumber.getText().toString();

                        if (name.equals("") && complaint.equals("") && number.equals(""))
                        {
                            Toast.makeText(Garbage.this, "Enter all details", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {

                            Geocoder geocoder;
                            List<Address> addresses;
                            geocoder = new Geocoder(Garbage.this, Locale.getDefault());

                            try {
                                addresses = geocoder.getFromLocation(currentlat, currentlon, 1);
                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                final DatabaseReference databaseReference = database.getReference();
                                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss MM/dd/yyyy", Locale.US);
                                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                                String date = sdf.format(new Date());

                                SimpleDateFormat sdff = new SimpleDateFormat("HHmmssMMddyyyy", Locale.US);
                                sdff.setTimeZone(TimeZone.getTimeZone("UTC"));
                                String datee = sdff.format(new Date());

                                databaseReference.child("profile/"+uid+"/complaint/"+datee+"/complaint").setValue(complaint+" at "+address);
                                databaseReference.child("profile/"+uid+"/complaint/"+datee+"/status").setValue("pending");
                                databaseReference.child("profile/"+uid+"/complaint/"+datee+"/currently").setValue("municipality");
                                databaseReference.child("profile/"+uid+"/complaint/"+datee+"/date").setValue(date);

                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/date").setValue(date);
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/status").setValue("pending");
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/currently").setValue("municipality");
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/name").setValue(name);
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/complaint").setValue(complaint+" at "+address);
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/number").setValue(number);
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/uid").setValue(uid);
                                databaseReference.child("complaint/garbage/Chennai/key/municipality/"+uid+"/uiddate").setValue(datee);

                                Toast.makeText(Garbage.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent (Garbage.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    uid = user.getUid();
                    new CountDownTimer(300000000, 10000000) {

                        public void onTick(long millisUntilFinished) {

                            if (i==1)
                            {
                            }
                            if (i==0)
                            {
                                mMap.clear();

                                FirebaseDatabase.getInstance().getReference().child("garbage/"+"Chennai")
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    mMap.clear();
                                                    String lati = snapshot.child("lat").getValue().toString();
                                                    String longi = snapshot.child("lon").getValue().toString();
                                                    String percent = snapshot.child("percentage").getValue().toString();
                                                    String color = "";
                                                    int i = Integer.parseInt(percent);
                                                    if (i>80)
                                                    {
                                                        color="red";
                                                    }
                                                    else
                                                    {
                                                        color="normal";
                                                    }
                                                    Double la = Double.valueOf(lati);
                                                    String name = snapshot.getKey();
                                                    Double lo = Double.valueOf(longi);
                                                    addCustomMarker(la,lo,name,percent,color);

                                                    //calc distance and if within 2km radius ring!!!!

                                                    Double distance = distance(currentlat,currentlon,la,lo);

                                                    if (distance<2)
                                                    {
                                                        if (i>80)
                                                        {
                                                            Toast.makeText(Garbage.this, "No empty box within 2 km.", Toast.LENGTH_SHORT).show();
                                                        }
                                                        else
                                                        {
                                                            Toast.makeText(Garbage.this, "Empty box within 2 km.", Toast.LENGTH_SHORT).show();
                                                            NotificationCompat.Builder mBuilder =
                                                                    new NotificationCompat.Builder(Garbage.this);
                                                            mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
                                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                                            r.play();
                                                        }
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                            }
                        }

                        public void onFinish() {
                            start();
                        }
                    }.start();
                }
            }
        };

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mappp);
        mapFragment.getMapAsync(this);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(120000); // two minute interval
        mLocationRequest.setFastestInterval(120000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }

    }


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                currentlat = location.getLatitude();
                currentlon = location.getLongitude();
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                //Place current location marker
                latLng = new LatLng(location.getLatitude(), location.getLongitude());


                Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    String add = "";
                    if (addresses.size() > 0) {
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }


            }
        }
    };


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(Garbage.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        MapsInitializer.initialize(this);

    }


    private void addCustomMarker(Double lati, Double longi, String name, String percent, String color) {
        if (mMap == null) {
            return;
        }

        latLng = new LatLng(lati, longi);

        if (color.equals("red"))
        {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title("Percentage Full")
                    .snippet(percent)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
            marker.setTag(name);
        }
        else
        {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title("Percentage Full")
                    .snippet(percent)
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            );
            marker.setTag(name);
        }



        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        i = 0;
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        i = 1;
        finish();
        mAuth.removeAuthStateListener(mAuthListener);
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}