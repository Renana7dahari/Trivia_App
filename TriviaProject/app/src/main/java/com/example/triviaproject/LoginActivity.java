package com.example.triviaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Shared Preferences editor
        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
    }

    public void login(View view) {

        String email = ((EditText)findViewById(R.id.editTextEmail)).getText().toString().toLowerCase();
        String password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
        if(!email.equals("")&& !password.equals("")) {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                //check if the user has administrator permissions
                                String userEmail = user.getEmail();
                                if (userEmail.equals("adaradmin@gmail.com")) {
                                    editor.putBoolean(getString(R.string.IS_ADMIN), true);

                                } else {
                                    editor.putBoolean(getString(R.string.IS_ADMIN), false);
                                }
                                editor.apply();
                                Intent intent = new Intent(LoginActivity.this, HomePageActivity.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(LoginActivity.this, "Wrong username or password",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
        else{
            Toast.makeText(this, "You must enter email and password", Toast.LENGTH_SHORT).show();
        }


    }
    @Override
    public void onBackPressed() {

    }


    public void register(View view) {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    public void exit(View view) {
        this.finishAffinity();
    }
}


