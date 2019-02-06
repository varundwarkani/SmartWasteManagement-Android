package devoops.smartwastemanagement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Main2Activity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    Spinner spLocation;
    ArrayAdapter<String> spLocationAdapter;
    List<String> spLocationList;
    Button btContinue,btComplaint,btService;
    public static final String MAPS = "maps";
    private SharedPreferences categoriesPref;
    private SharedPreferences.Editor editor;
    Button btlogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        btComplaint = findViewById(R.id.btComplaint);
        btComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //alert builder
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                View v = null;
                v = LayoutInflater.from(Main2Activity.this).inflate(R.layout.custom_complaint,null,false);
                builder.setView(v);
                Button btMaps,btGarbage;
                btMaps = v.findViewById(R.id.btCustomMaps);
                btGarbage = v.findViewById(R.id.btCustomGarbage);
                builder.setCancelable(true);
                btMaps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //open maps
                        Intent intent = new Intent (Main2Activity.this, Complaint.class);
                        startActivity(intent);
                    }
                });
                btGarbage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //open garbage
                        Intent intent = new Intent (Main2Activity.this, ServerComplaint.class);
                        startActivity(intent);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        btService = findViewById(R.id.btService);
        btService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Main2Activity.this, Service.class);
                startActivity(intent);
            }
        });

        btlogout = findViewById(R.id.btlogout);
        btlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(Main2Activity.this)
                        .setMessage("Are you sure you want to logout?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mAuth.signOut();
                                Intent intent = new Intent (Main2Activity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                             //   Toast.makeText(Main2Activity.this, "Logout", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        btContinue = findViewById(R.id.btContinue);
        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences sharedPreferences = getSharedPreferences(Main2Activity.MAPS, Context.MODE_PRIVATE);
                categoriesPref = getSharedPreferences(MAPS, Context.MODE_PRIVATE);
                editor = categoriesPref.edit();
                editor.putString("CITY", String.valueOf(spLocation.getSelectedItem()));
                editor.commit();

                //alert builder
                final AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                View v = null;
                v = LayoutInflater.from(Main2Activity.this).inflate(R.layout.custom_select,null,false);
                builder.setView(v);
                Button btMaps,btGarbage,btUser;
                btMaps = v.findViewById(R.id.btCustomMaps);
                btGarbage = v.findViewById(R.id.btCustomGarbage);
                btUser = v.findViewById(R.id.btComplaint);
                builder.setCancelable(true);
                btMaps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                          //open maps
                        Intent intent = new Intent (Main2Activity.this, Maps.class);
                        startActivity(intent);
                    }
                });
                btGarbage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //open garbage
                        Intent intent = new Intent (Main2Activity.this, Garbage.class);
                        startActivity(intent);
                    }
                });

                btUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent (Main2Activity.this, UserComplaint.class);
                        startActivity(intent);
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        spLocation = findViewById(R.id.spLocation);
        spLocationList = new ArrayList<String>();
        spLocationList.add("Chennai");
        spLocationList.add("Coimbatore");
        spLocationList.add("Tuticorin");
        spLocationAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_item, spLocationList);
        spLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLocation.setAdapter(spLocationAdapter);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!= null) {

                    uid = user.getUid();

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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        moveTaskToBack(true);
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
