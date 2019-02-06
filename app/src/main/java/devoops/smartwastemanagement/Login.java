package devoops.smartwastemanagement;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Login extends AppCompatActivity {

    EditText etloginemail,etloginpass;
    ImageView ivgooglesigin;
    Button btlog,btLoginSignup;
    FirebaseAuth mAuth;
    ProgressBar empty_progress_bar;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Toast.makeText(Login.this, "Welcome Back!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent (Login.this, Main2Activity.class);
                    startActivity(intent);
                    finish();
                } else {
                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Login.this, gso);

        empty_progress_bar = findViewById(R.id.empty_progress_bar);
        empty_progress_bar.setVisibility(View.GONE);
        etloginemail = findViewById(R.id.etloginmail);
        etloginpass = findViewById(R.id.etloginpass);
        ivgooglesigin = findViewById(R.id.ivgooglesignin);
        btlog = findViewById(R.id.btlog);
        btLoginSignup = findViewById(R.id.btLoginSignup);

        btLoginSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Login.this, Signup.class);
                startActivity(intent);
            }
        });

        ivgooglesigin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //login using google.
                signIn();
                empty_progress_bar.setVisibility(View.VISIBLE);
            }
        });

        btlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empty_progress_bar.setVisibility(View.VISIBLE);
                String mail = etloginemail.getText().toString();
                String pass = etloginpass.getText().toString();

                if (!TextUtils.isEmpty(mail)&&!TextUtils.isEmpty(pass))
                {
                    mAuth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                empty_progress_bar.setVisibility(View.GONE);
                                Intent intent = new Intent (Login.this, Main2Activity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(Login.this,"Succesfully logged in.",Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                empty_progress_bar.setVisibility(View.GONE);
                                Toast.makeText(Login.this,"Try again.",Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                else
                {
                    empty_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Please enter email id and password", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void signIn() {
        FirebaseAuth.getInstance().signOut();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                empty_progress_bar.setVisibility(View.GONE);
                Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(Login.this, "Sign in success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String uid = user.getUid();
                            String mail = acct.getEmail();
                            String name = acct.getDisplayName();

                            final String referralcode;
                            Random random = new Random();
                            int x = random.nextInt(1000);
                            String addition = String.valueOf(x);
                            String partstring = mail.substring(0,4);
                            referralcode = partstring+addition;

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference databaseReference = database.getReference();
                            databaseReference.child("profile/"+uid+"/userdata/email").setValue(mail);

                            empty_progress_bar.setVisibility(View.GONE);
                            Intent intent = new Intent (Login.this, Main2Activity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            empty_progress_bar.setVisibility(View.GONE);
                            Toast.makeText(Login.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
