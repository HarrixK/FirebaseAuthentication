package com.example.hotelapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.GoogleApiAvailabilityCache;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private EditText emailET, paswwordET;
    private Button signup, signin, googlesignin, googlesignout;

    private ProgressBar bar;
    private FirebaseAuth objectFirebaseAuth;

    private static final String TAG = "MainActivity";

    private TextView text;
    private GoogleSignInClient mGoogleSignInClient;

    private final static int RC_SIGN_IN = 123;
    private final static int GOOGLE_SIGN = 123;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        connectXML();
        objectFirebaseAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkuser();
            }
        });

        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signinuser();
            }

        });

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIntent=mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signIntent,GOOGLE_SIGN);

                SignInGoogle();
            }
        });
        googlesignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout();
            }
        });
    }

    void SignInGoogle() {
        bar.setVisibility(View.VISIBLE);
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, GOOGLE_SIGN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_SIGN || requestCode == RC_SIGN_IN) {
            bar.setVisibility(View.INVISIBLE);
            Task<GoogleSignInAccount> task = GoogleSignIn
                    .getSignedInAccountFromIntent(data);
            try{
                try {
                    bar.setVisibility(View.INVISIBLE);
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    if (account != null){
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Firebase Auth", Toast.LENGTH_SHORT).show();
                        firebaseAuthWithGoogle(account);
                    }
                } catch (ApiException e) {
                    bar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "OnActivity Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }catch(Exception ex){
                Toast.makeText(this, "onActivity :" + ex.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "FirebaseAuthWithGoogle: " + account.getId());
        Toast.makeText(MainActivity.this, "Firebase Auth With Google " + account.getId() , Toast.LENGTH_SHORT).show();

        AuthCredential credential = GoogleAuthProvider
                .getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bar.setVisibility(View.INVISIBLE);
                            Log.d("TAG", "Sign-In Success");
                            Toast.makeText(MainActivity.this, "Sign-in Successfull", Toast.LENGTH_SHORT).show();

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            bar.setVisibility(View.INVISIBLE);
                            Log.w("TAG", "Sign-In Failure", task.getException());

                            Toast.makeText(MainActivity.this, "Sign-in Failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            startActivity(new Intent(MainActivity.this, LoginPage.class));
            finish();
        } else {
            startActivity(new Intent(MainActivity.this, LoginPage.class));
            finish();
            Toast.makeText(this, "UpdateUI Error", Toast.LENGTH_SHORT).show();
        }
    }

    void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                updateUI(null);
            }
        });
    }

    private void connectXML() {
        try {
            emailET = findViewById(R.id.EmailET);
            paswwordET = findViewById(R.id.PasswordET);

            signup = findViewById(R.id.Okbtn);
            bar = findViewById(R.id.ProgressBar);

            googlesignin = findViewById(R.id.GoogleSignIn);
            signin = findViewById(R.id.Signbtn);

            googlesignout = findViewById(R.id.GoogleSignOut);
            text = findViewById(R.id.text);
        } catch (Exception ex) {
            Toast.makeText(this, "Connect To XML Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkuser() {
        try {
            if (!emailET.getText().toString().isEmpty()) {
                if (objectFirebaseAuth != null) {

                    bar.setVisibility(View.VISIBLE);
                    signup.setEnabled(false);

                    objectFirebaseAuth.fetchSignInMethodsForEmail(emailET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check = task.getResult().getSignInMethods().isEmpty();
                                    if (!check) {
                                        signup.setEnabled(true);
                                        bar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(MainActivity.this, "User Already Exists", Toast.LENGTH_SHORT).show();

                                    } else if (check) {

                                        signup.setEnabled(true);
                                        bar.setVisibility(View.INVISIBLE);

                                        Signup();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            signup.setEnabled(true);
                            bar.setVisibility(View.INVISIBLE);

                            Toast.makeText(MainActivity.this, "Fails To Check If User Exists" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                emailET.requestFocus();
                signup.setEnabled(true);
                Toast.makeText(this, "Email and Password is Empty", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

            signup.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);

            Toast.makeText(this, "Check User Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void Signup() {
        try {

            if (!emailET.getText().toString().isEmpty()
                    &&
                    !paswwordET.getText().toString().isEmpty()) {
                if (objectFirebaseAuth != null) {

                    bar.setVisibility(View.VISIBLE);
                    signup.setEnabled(false);

                    objectFirebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(),
                            paswwordET.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(MainActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            if (authResult.getUser() != null) {

                                objectFirebaseAuth.signOut();
                                emailET.setText("");

                                paswwordET.setText("");
                                emailET.requestFocus();

                                signup.setEnabled(true);
                                bar.setVisibility(View.INVISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            signup.setEnabled(true);
                            emailET.requestFocus();

                            bar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Failed To Create User" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (emailET.getText().toString().isEmpty()) {
                signup.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (paswwordET.getText().toString().isEmpty()) {
                signup.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                paswwordET.requestFocus();
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception ex) {

            signup.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);

            emailET.requestFocus();
            Toast.makeText(this, "Signup Error" + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void signinuser() {
        try {
            bar.setVisibility(View.VISIBLE);
            if (!emailET.getText().toString().isEmpty() && !paswwordET.getText().toString().isEmpty()) {

                if (objectFirebaseAuth.getCurrentUser() != null) {

                    bar.setVisibility(View.INVISIBLE);

                    objectFirebaseAuth.signOut();
                    signin.setEnabled(false);

                    Toast.makeText(this, "User Logged Out Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    objectFirebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(),
                            paswwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    bar.setVisibility(View.INVISIBLE);
                                    signin.setEnabled(true);

                                    Toast.makeText(MainActivity.this, "User Logged In", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, LoginPage.class));

                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            signin.setEnabled(true);
                            emailET.requestFocus();

                            bar.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Fails To Sig-in User: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else if (emailET.getText().toString().isEmpty()) {
                signin.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
            } else if (paswwordET.getText().toString().isEmpty()) {
                signin.setEnabled(true);
                bar.setVisibility(View.INVISIBLE);

                paswwordET.requestFocus();
                Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {

            signin.setEnabled(true);
            emailET.requestFocus();

            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Logging In Error" + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void createRequest() {


        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}
