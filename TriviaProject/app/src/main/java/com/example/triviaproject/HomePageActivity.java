package com.example.triviaproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private ArrayList<String> userNamesArr;
    private ArrayList<String> userEmailsArr;

    private boolean sound_flag =true; //false=mute ,true=sound
    private MediaPlayer ring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        ring = MediaPlayer.create(HomePageActivity.this,R.raw.music);
        ring.start();

        // Initialize Shared Preferences editors
        pref = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE);
        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        DatabaseReference myRef;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String email = firebaseUser.getEmail();

        //turn the music on
        ring= MediaPlayer.create(HomePageActivity.this,R.raw.music);
        ring.start();

        //display Admin content for admin users
        Button b = findViewById(R.id.AdminButton);
        if(pref.getBoolean(getString(R.string.IS_ADMIN),false)){//admin
            b.setVisibility(View.VISIBLE);
            getUsersFromDatabase();
            myRef = database.getReference("administrators").child(email.split("@")[0]);
        }
        else{//regular user
            b.setVisibility(View.INVISIBLE);
            myRef = database.getReference("users").child(email.split("@")[0]);
        }

        //display user name
        TextView t = findViewById(R.id.WelcomeTextView);
        t.setVisibility(View.INVISIBLE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User tempUser = dataSnapshot.getValue(User.class);
                TextView t = findViewById(R.id.WelcomeTextView);
                t.setText("Welcome " + tempUser.getName());
                t.setVisibility(View.VISIBLE);
                ProgressBar pb = findViewById(R.id.welcomeProgressBar);
                pb.setVisibility(View.GONE);
                Gson gson = new Gson();
                String jsonUser = gson.toJson(tempUser);
                editor.putString(getString(R.string.CURRENT_USER),jsonUser);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void play(View view) {
        Intent intent = new Intent(this, DifficultyActivity.class);
        startActivity(intent);
    }

    public void goToHighScore(View view) {
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }
    public void logout(View view) {
        Intent intent = new Intent(HomePageActivity.this , LoginActivity.class);
        startActivity(intent);
    }

    private void getUsersFromDatabase() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        userNamesArr = new ArrayList<>();
        userEmailsArr = new ArrayList<>();
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                User user = dataSnapshot.getValue(User.class);
                userNamesArr.add(user.getName());
                userEmailsArr.add(user.getEmail());

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void goToUserManagement(View view) {
        //write user details to shared preference
        Gson gson = new Gson();
        String jsonStringUserNames = gson.toJson(userNamesArr);
        String jsonStringUserEmails = gson.toJson(userEmailsArr);
        editor.putString(getString(R.string.USERS_NAME_ARRAY),jsonStringUserNames);
        editor.putString(getString(R.string.USERS_EMAIL_ARRAY),jsonStringUserEmails);
        editor.apply();
        Intent intent = new Intent(this, UsersManagementActivity.class);
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {

    }


    public void onClickSound(View view) {
        sound_flag = !sound_flag;
        if(sound_flag == true){
            view.setBackground(getResources().getDrawable(R.drawable.sound_foreground));
            ring.start();
            //  editor.putBoolean(getString(R.string.SOUND_FLAG),true);
            // editor.apply();
        }
        else {
            view.setBackground(getResources().getDrawable(R.drawable.mute_foreground));
            ring.pause();
            //editor.putBoolean(getString(R.string.SOUND_FLAG),false);
            //editor.apply();
        }

    }


    public void exit(View view) {
        this.finishAffinity();
    }

}
