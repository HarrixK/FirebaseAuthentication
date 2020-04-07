package com.example.hotelapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private Button Back;

    TextView name, mail;
    Button logout;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        Back = findViewById(R.id.back);
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change();
            }
        });

        logout = findViewById(R.id.logout);
        name = findViewById(R.id.name);

        mail = findViewById(R.id.mail);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null) {
            name.setText(signInAccount.getDisplayName());
            mail.setText(signInAccount.getEmail());
            Uri photo = signInAccount.getPhotoUrl();
        }

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                try {
                    GoogleSignIn.getClient(getApplicationContext(),
                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut();
                } catch (Exception ex) {
                    Toast.makeText(LoginPage.this, "Logging Out Error", Toast.LENGTH_SHORT).show();
                }
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void change() {
        startActivity(new Intent(LoginPage.this, MainActivity.class));
        finish();
    }

    public void openUploadPage(View view) {
        try {
            startActivity(new Intent(LoginPage.this, upload_page.class));
        } catch (Exception e) {
            Toast.makeText(this, "OpeningUploadPage:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void openDownloadPage(View view) {
        try {
            startActivity(new Intent(LoginPage.this, download_page.class));
        } catch (Exception e) {
            Toast.makeText(this, "OpeningDownloadPage:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
