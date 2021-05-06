package com.example.triviaproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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
import java.util.Collections;
import java.util.Random;

import static java.lang.Math.random;

public class GameOverActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private long gameDuration;
    private int minute = 0;
    private int second = 0;
    private int currentScore;
    private FirebaseAuth mAuth;
    private ArrayList<Integer> highScoresArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        prefs = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE);
        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
        TextView scoreTextView = findViewById(R.id.scoreTextView);
        TextView timeTextView = findViewById(R.id.timeTextView);
        currentScore = prefs.getInt(getString(R.string.FINAL_SCORE), 0);
        gameDuration = (prefs.getLong(getString(R.string.END_TIME), 0)) - (prefs.getLong(getString(R.string.START_TIME), 0));
        minute = (int) ((gameDuration / ((long) 60)));
        second = (int) ((gameDuration) % ((long) 60));
        String gameDur = String.format("%02d:%02d", minute, second);
        scoreTextView.setText(((Integer) currentScore).toString());
        timeTextView.setText(gameDur);


        //////save result to firebase:

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String email = firebaseUser.getEmail();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        if(prefs.getBoolean(getString(R.string.IS_ADMIN),false)){
            myRef = database.getReference("administrators").child(email.split("@")[0]);
        }
        else{
            myRef = database.getReference("users").child(email.split("@")[0]);
        }


        //read current score table
        Gson gson = new Gson();
        String jsonUSer = prefs.getString(getString(R.string.CURRENT_USER),
                "{\"email\" : \"no email\", \"highScores\" : [ 0 ],\"name\" : \"no name\",\"password\" : \"no password\"}\n");
        User curUser = gson.fromJson(jsonUSer, User.class);
        highScoresArray = curUser.getHighScores();

        //display a relevant message for the user
        TextView message = findViewById(R.id.message);
        Random rand = new Random();
        int rand_int;
        if (currentScore > highScoresArray.get(0)) { // new record!
            rand_int = rand.nextInt(4);
            switch (rand_int) {
                case 0:
                    message.setText("You made it, excellent!! ");
                    break;
                case 1:
                    message.setText("You are the champion!! ");
                    break;
                case 2:
                    message.setText("Proud of you ! ");
                    break;
                case 3:
                    message.setText("Number one!");
                    break;
            }
        }
        else{
            rand_int = rand.nextInt(3);
            switch (rand_int) {
                case 0:
                    message.setText("We are Sure you will succeed next time");
                    break;
                case 1:
                    message.setText("You almost broke the record");
                    break;
                case 2:
                    message.setText("Next time, try harder");
                    break;

            }
        }
        //insert the new score
        currentScore = prefs.getInt(getString(R.string.FINAL_SCORE), 0);
        if (highScoresArray.size() < 10) {
            highScoresArray.add(currentScore);
        } else {
            if (highScoresArray.get(9) < currentScore) {
                highScoresArray.remove(9);
                highScoresArray.add(currentScore);
            }
        }
        Collections.sort(highScoresArray, Collections.<Integer>reverseOrder());

        //write the score table to firebase
        DatabaseReference myRefScore= database.getReference("users").child(email.split("@")[0]).child("highScores");
        if(prefs.getBoolean(getString(R.string.IS_ADMIN),false)){
            myRefScore = database.getReference("administrators").child(email.split("@")[0]).child("highScores");
        }
        myRefScore.setValue(highScoresArray);

        //update shared preferences
        curUser.setHighScores(highScoresArray);
        String jsonUser = gson.toJson(curUser);
        editor.putString(getString(R.string.CURRENT_USER), jsonUser);
        editor.apply();


    }

    @Override
    public void onBackPressed() {

    }

    public void newgame (View view){
        Intent intent = new Intent(GameOverActivity.this, DifficultyActivity.class);
        startActivity(intent);
    }

    public void homepage (View view){
        Intent intent = new Intent(GameOverActivity.this, HomePageActivity.class);
        startActivity(intent);
    }
}
