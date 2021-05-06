package com.example.triviaproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.arch.core.util.Function;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import java.util.ArrayList;

public class UsersManagementActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private ArrayList<String> userNamesArr;
    private ArrayList<String> userEmailsArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_management);

        // Initialize Shared Preferences editors
        pref = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE);
        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        Gson gson = new Gson();
        String jsonStringUserName = pref.getString(getString(R.string.USERS_NAME_ARRAY),"Error");
        String jsonStringUserEmail = pref.getString(getString(R.string.USERS_EMAIL_ARRAY),"Error");
        userNamesArr = gson.fromJson(jsonStringUserName,ArrayList.class);
        userEmailsArr = gson.fromJson(jsonStringUserEmail,ArrayList.class);

        TextView usersTextView = findViewById(R.id.usersDetails);
        String userDetailsStr = "";
        for (int i = 0; i < userNamesArr.size(); i++) {
            userDetailsStr += (i+1) + ". " + "Name: " + userNamesArr.get(i) + "\n   Email: " + userEmailsArr.get(i) + "\n\n";
        }
        usersTextView.setText(userDetailsStr);

    }
    @Override
    public void onBackPressed() {

    }


    public void homepage(View view) {
        Intent intent = new Intent(UsersManagementActivity.this , HomePageActivity.class);
        startActivity(intent);
    }


    private void getDataFromDatabase(){
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
            public void onChildMoved(DataSnapshot dataSnapshot, String prevChildKey) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }



}
