package com.example.triviaproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onBackPressed() {

    }


    public void register(View view) {
        EditText editTextName = findViewById(R.id.editTextName);
        EditText editTextEmail = findViewById(R.id.editTextEmail);
        EditText editTextPassword = findViewById(R.id.editTextPassword);

        String name = editTextName.getText().toString();
        String email = (editTextEmail.getText().toString()).toLowerCase();
        String password = editTextPassword.getText().toString();

        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
        if(!email.equals("") && !email.equals("") && !email.equals("")) {

            mAuth.createUserWithEmailAndPassword(email, password)
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

                                Intent intent = new Intent(RegistrationActivity.this, HomePageActivity.class);
                                startActivity(intent);
                                ;


                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegistrationActivity.this, "Registration failed.",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("users").child(email.split("@")[0]);
            ArrayList<Integer> arr = new ArrayList<>();
            arr.add(0);
            User user = new User(name, email,password,arr);
            myRef.setValue(user);
        }
        else{
            Toast.makeText(this, "You must fill all fields", Toast.LENGTH_SHORT).show();

        }



    }

    public void back(View view) {
        Intent intent = new Intent(RegistrationActivity.this , LoginActivity.class);
        startActivity(intent);
    }
}
