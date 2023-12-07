package com.satunol.satunolcodetest2;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ScheduleListActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    ShapeableImageView profilePict;
    MaterialTextView nameTv, emailTv;
    MaterialButton btnSignOut;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_list);

        nameTv = findViewById(R.id.nameTv);
        emailTv = findViewById(R.id.emailTv);
        profilePict = findViewById(R.id.profilePicture);
        btnSignOut = findViewById(R.id.btnSignOut);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            nameTv.setText(firebaseUser.getDisplayName());
            emailTv.setText(firebaseUser.getEmail());
            Glide.with(ScheduleListActivity.this).load(firebaseUser.getPhotoUrl()).circleCrop().into(profilePict);
        }

        // Initialize sign in client
        googleSignInClient = GoogleSignIn.getClient(ScheduleListActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);

        btnSignOut.setOnClickListener(v -> {
            googleSignInClient.signOut().addOnCompleteListener(task -> {
                // Check condition
                if (task.isSuccessful()) {
                    // When task is successful sign out from firebase
                    firebaseAuth.signOut();
                    // Display Toast
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();
                    // Finish activity
                    finish();
                }
            });
        });
    }
}