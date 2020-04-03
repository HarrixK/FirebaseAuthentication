package com.example.hotelapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText emailET, paswwordET;
    private Button signup, signin, googlesignin, facebook, LoginButtonm, login_butt;

    private ProgressBar bar;
    private FirebaseAuth objectFirebaseAuth;

    private TextView text, text_user;
    private GoogleSignInClient mGoogleSignInClient;

    private final static int RC_SIGN_IN = 231;
    private FirebaseAuth mAuth;

    private CallbackManager mCallbackManager;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        connectXML();

        objectFirebaseAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();

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

        createRequest();
        googlesignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
                bar.setVisibility(View.VISIBLE);
            }
        });

    }

    private void FacebookRequest() {
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this, "facebook:onSuccess:" + loginResult, Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "facebook:onCancel:", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this, "facebook:onError:", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken" + token);
        Toast.makeText(this, "handleFacebookAccessToken" + token, Toast.LENGTH_SHORT).show();

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Authentication failed. signInWithCredential:failure" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
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

            text = findViewById(R.id.text);
            text_user = findViewById(R.id.text_user);

            facebook = findViewById(R.id.Facebook);
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
        //Creating a send request to open a Pop-up so that user can Log-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
    }

    private void signIn() {
        //Intent in which you can select your Google account
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        bar.setVisibility(View.VISIBLE);

        try {
            bar.setVisibility(View.INVISIBLE);
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception ex) {
            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "On Activity: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                bar.setVisibility(View.INVISIBLE);
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Login Failed: " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(MainActivity.this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent = new Intent(getApplicationContext(), LoginPage.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "signInWithCredential:failure" + task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
