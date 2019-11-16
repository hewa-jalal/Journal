package com.example.journal;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSave;
    private ProgressBar progressBar;
    private ImageView ivAddPhoto;
    private EditText etTitle;
    private EditText etThought;
    private TextView tvCurrentUser;

    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    // we need storage to save images/videos/audio
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Journal");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);


        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar_post);
        etTitle = findViewById(R.id.et_post_title);
        etThought = findViewById(R.id.et_post_thought);
        tvCurrentUser = findViewById(R.id.tv_post_username);
        btnSave = findViewById(R.id.btn_save_jornal_post);
        ivAddPhoto = findViewById(R.id.postCameraButton);

        btnSave.setOnClickListener(this);
        ivAddPhoto.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_jornal_post:
                //save journal
                break;
            case R.id.postCameraButton:
                // get image
                break;
        }

    }
}
