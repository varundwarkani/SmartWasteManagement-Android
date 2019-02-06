package devoops.smartwastemanagement;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserComplaint extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    String uid;
    RecyclerView rvcomplaint;
    UserAdapter userAdapter;
    ArrayList<String> tvDesc = new ArrayList<>();
    ArrayList<String> tvStatus = new ArrayList<>();
    ArrayList<String> tvCurrently = new ArrayList<>();
    ArrayList<String> tvDate = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_complaint);

        tvCurrently.clear();
        tvDate.clear();
        tvStatus.clear();
        tvDesc.clear();

        rvcomplaint = findViewById(R.id.rvusercomplaint);
        rvcomplaint.setHasFixedSize(true);
        rvcomplaint.setLayoutManager(new LinearLayoutManager(UserComplaint.this));
        userAdapter = new UserAdapter(tvDesc,tvStatus,tvCurrently,tvDate);
        rvcomplaint.setAdapter(userAdapter);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    uid = user.getUid();
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("profile/"+uid+"/complaint");
                    usersRef.orderByChild("date").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            tvCurrently.clear();
                            tvStatus.clear();
                            tvDate.clear();
                            tvDesc.clear();
                            userAdapter = new UserAdapter(tvDesc,tvStatus,tvCurrently,tvDate);
                            rvcomplaint.setAdapter(userAdapter);

                            for (final DataSnapshot postSnapshots: dataSnapshot.getChildren()) {

                                String status = postSnapshots.child("status").getValue().toString();
                                String currently = postSnapshots.child("currently").getValue().toString();
                                String desc = postSnapshots.child("complaint").getValue().toString();
                                String date = postSnapshots.child("date").getValue().toString();


                                tvDesc.add(desc);
                                tvStatus.add(status);
                                tvDate.add(date);
                                tvCurrently.add(currently);

                                userAdapter = new UserAdapter(tvDesc,tvStatus,tvCurrently,tvDate);
                                rvcomplaint.setAdapter(userAdapter);
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
