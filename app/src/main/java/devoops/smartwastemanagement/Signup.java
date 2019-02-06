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

public class Signup extends AppCompatActivity {

    Button btsignup;
    EditText etsignupmail,etsignuppassword;
    ImageView ivgooglesignup;
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    ProgressBar empty_progress_bar;
    String uid,name,mail,pass,type="",phone,referral;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();

        empty_progress_bar = findViewById(R.id.empty_progress_bar);
        empty_progress_bar.setVisibility(View.GONE);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(Signup.this, gso);

        btsignup = findViewById(R.id.btsignup);
        etsignupmail = findViewById(R.id.etsignupmail);
        etsignuppassword = findViewById(R.id.etsignuppassword);
        ivgooglesignup = findViewById(R.id.ivgooglesignup);

        btsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                empty_progress_bar.setVisibility(View.VISIBLE);
                mail = etsignupmail.getText().toString();
                pass = etsignuppassword.getText().toString();

                if (!TextUtils.isEmpty(mail)&&!TextUtils.isEmpty(pass))
                {

                    Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful())
                            {
                                FirebaseUser user = mAuth.getCurrentUser();
                                uid = user.getUid();
                                type = "normal";
                                setvalue();
                            }
                            else
                            {

                                empty_progress_bar.setVisibility(View.GONE);
                                Toast.makeText(Signup.this, "Signup failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else
                {
                    empty_progress_bar.setVisibility(View.GONE);
                    Toast.makeText(Signup.this, "Please enter all the details", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ivgooglesignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                empty_progress_bar.setVisibility(View.VISIBLE);
                signIn();
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
                Log.i("GOOGLEFAIL", String.valueOf(e));
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
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
                            name = acct.getDisplayName();
                            mail = acct.getEmail();
                            type = "google";
                            empty_progress_bar.setVisibility(View.GONE);
                            setvalue();
                        } else {
                            // If sign in fails, display a message to the user.
                            empty_progress_bar.setVisibility(View.GONE);
                            Toast.makeText(Signup.this, "Sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setvalue(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference();
        databaseReference.child("profile/"+uid+"/userdata/email").setValue(mail);
    }
}
