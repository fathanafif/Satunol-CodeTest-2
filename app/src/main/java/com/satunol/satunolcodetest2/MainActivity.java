package com.satunol.satunolcodetest2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.satunol.satunolcodetest2.Model.Users;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    MaterialButton btnSignIn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 40;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnSignIn = findViewById(R.id.btnSignIn);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken((getString(R.string.default_web_client_id)))
                        .requestEmail().build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn.setOnClickListener(v -> btnSignInHandler());
    }

    private void btnSignInHandler() {
        Log.d("valerie", "button pressed");

        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount>task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult((ApiException.class));
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();

                        Users users = new Users();
                        assert user != null;
                        users.setUserId(user.getUid());
                        users.setEmail(user.getEmail());
                        users.setName(user.getDisplayName());
                        users.setPhotoProfile(Objects.requireNonNull(user.getPhotoUrl()).toString());
                        firebaseDatabase.getReference().child("Users").child(user.getUid()).setValue(users);
                        Intent intent = new Intent(MainActivity.this, ScheduleListActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}