package com.example.hotelapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

public class download_page extends AppCompatActivity {

    private Button back, imageDownloadBtn;
    private EditText imagenameET;

    private ImageView imageToDownloadIV;
    private TextView gallery;

    private ProgressBar bar;
    private FirebaseFirestore objectFirebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_page);
        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        back = findViewById(R.id.back);
        objectFirebaseFirestore = FirebaseFirestore.getInstance();

        connectXMLToJava();
    }

    private void connectXMLToJava() {
        try {
            imageToDownloadIV = findViewById(R.id.imageToDownloadIV);
            imageDownloadBtn = findViewById(R.id.imageDownloadBtn);

            imagenameET = findViewById(R.id.imageNameET);
            gallery = findViewById(R.id.textgallery);

            bar = findViewById(R.id.ProgressBar);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    change();
                }
            });

            imageDownloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bar.setVisibility(View.VISIBLE);
                    gallery.setVisibility(View.INVISIBLE);
                    Download();
                }
            });
        } catch (Exception e) {
            Toast.makeText(this, "connectXMLToJava:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void change() {
        startActivity(new Intent(download_page.this, LoginPage.class));
        finish();
    }

    private void Download() {
        try {
            if (!imagenameET.getText().toString().isEmpty()) {
                bar.setVisibility(View.VISIBLE);
                objectFirebaseFirestore.collection("Gallery")
                        .document(imagenameET.getText().toString())
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    bar.setVisibility(View.INVISIBLE);
                                    String url = documentSnapshot.getString("URL");
                                    Glide.with(download_page.this)
                                            .load(url)
                                            .into(imageToDownloadIV);
                                    Toast.makeText(download_page.this, "Image Downloaded", Toast.LENGTH_SHORT).show();

                                } else {
                                    bar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(download_page.this, "No Such File Exists", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        bar.setVisibility(View.INVISIBLE);
                        Toast.makeText(download_page.this, "Failed To Retrieve Image" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                bar.setVisibility(View.INVISIBLE);
                Toast.makeText(this, "Please Enter Name of The Image To Download", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            bar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "DownloadError: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
