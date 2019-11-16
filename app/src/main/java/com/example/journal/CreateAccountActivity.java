package com.example.journal;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateAccountActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // Firestore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    private Button btnLogin;
    private Button btnCreateAcc;
    private EditText etEmail;
    private EditText etPassword;
    private ProgressBar progressBar;
    private EditText etUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        btnCreateAcc = findViewById(R.id.create_account_btn_acc);

        progressBar = findViewById(R.id.create_acc_progress);
        etEmail = findViewById(R.id.et_email_acc);
        etPassword = findViewById(R.id.et_password_acc);
        etUserName = findViewById(R.id.username_acc);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();

                if (currentUser != null) {
                    // user is already logged in
                } else {
                    //  no login
                }
            }
        };

        btnCreateAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString();
                String username = etUserName.getText().toString();

                if (email.isEmpty()) {
                    etEmail.setError("Email required");
                    etEmail.requestFocus();
                }

                if (password.isEmpty()) {
                    etPassword.setError("Password is required");
                    etPassword.requestFocus();
                }

                if (username.isEmpty()) {
                    etUserName.setError("username is required");
                    etUserName.requestFocus();
                }

                createUserEmailAccount(email, password, username);

            }


        });


    }

    private void createUserEmailAccount(String email, String password, final String username) {
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(username)) {

            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // user is created successfully
                                currentUser = firebaseAuth.getCurrentUser();

                                assert currentUser != null;
                                final String currentUserId = currentUser.getUid();


                                // firestore stuff
                                Map<String, String> userObj = new HashMap<>();
                                userObj.put("userId", currentUserId);
                                userObj.put("username", username);

                                collectionReference.add(userObj)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                documentReference.get()
                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                // "task" will contain our document
                                                                if (Objects.requireNonNull(task.getResult()).exists()) {
                                                                    progressBar.setVisibility(View.INVISIBLE);
                                                                    String name = task.getResult()
                                                                            .getString("username");

                                                                    JournalApi journalApi = JournalApi.getInstance();
                                                                    journalApi.setUserId(currentUserId);
                                                                    journalApi.setUsername(name);

                                                                    Intent intent = new Intent(CreateAccountActivity.this,
                                                                            PostJournalActivity.class);
                                                                    intent.putExtra("username", name);
                                                                    intent.putExtra("userId",currentUserId);
                                                                    startActivity(intent);
                                                                }

                                                            }
                                                        });
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            } else {
                                // not successful
                                progressBar.setVisibility(View.INVISIBLE);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
