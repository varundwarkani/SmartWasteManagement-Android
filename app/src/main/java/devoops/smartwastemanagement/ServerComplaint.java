package devoops.smartwastemanagement;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServerComplaint extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button btaction;
    ServerComplaintAdapter serverComplaintAdapter;
    ArrayList<String> tvDesc = new ArrayList<>();
    ArrayList<String> tvUid = new ArrayList<>();
    RecyclerView rvcomplaint;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_complaint);

        tvDesc.clear();
        tvUid.clear();
        rvcomplaint = findViewById(R.id.rvservice);
        rvcomplaint.setHasFixedSize(true);
        rvcomplaint.setLayoutManager(new LinearLayoutManager(ServerComplaint.this));
        serverComplaintAdapter = new ServerComplaintAdapter(tvDesc,tvUid);
        rvcomplaint.setAdapter(serverComplaintAdapter);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    uid = user.getUid();

                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("service/Chennai/key");
                    usersRef.orderByChild("percentage").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            tvDesc.clear();
                            tvUid.clear();
                            serverComplaintAdapter = new ServerComplaintAdapter(tvDesc,tvUid);
                            rvcomplaint.setAdapter(serverComplaintAdapter);

                            for (final DataSnapshot postSnapshots: dataSnapshot.getChildren()) {

                                Double lat = Double.valueOf(postSnapshots.child("lat").getValue().toString());
                                Double lon = Double.valueOf(postSnapshots.child("lon").getValue().toString());
                                String percentage = postSnapshots.child("percentage").getValue().toString();

                                Geocoder geocoder;
                                List<Address> addresses;
                                geocoder = new Geocoder(ServerComplaint.this, Locale.getDefault());
                                try {
                                    addresses = geocoder.getFromLocation(lat, lon, 1);
                                    String address = addresses.get(0).getAddressLine(0);

                                    tvDesc.add("Address: "+address);
                                    tvUid.add("Percentage fill: "+percentage);

                                    serverComplaintAdapter = new ServerComplaintAdapter(tvDesc,tvUid);
                                    rvcomplaint.setAdapter(serverComplaintAdapter);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }
}