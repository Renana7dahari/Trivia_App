package com.example.triviaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

public class HighScoreActivity extends AppCompatActivity {
    private SharedPreferences prefs;
    private int currentScore;
    private ArrayList<TextView> TextViewScoreArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        prefs = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE);
        TextViewScoreArray = new ArrayList<>();
        TextViewScoreArray.add((TextView) findViewById(R.id.score1));
        TextViewScoreArray.add((TextView) findViewById(R.id.score2));
        TextViewScoreArray.add((TextView) findViewById(R.id.score3));
        TextViewScoreArray.add((TextView) findViewById(R.id.score4));
        TextViewScoreArray.add((TextView) findViewById(R.id.score5));
        TextViewScoreArray.add((TextView) findViewById(R.id.score6));
        TextViewScoreArray.add((TextView) findViewById(R.id.score7));
        TextViewScoreArray.add((TextView) findViewById(R.id.score8));
        TextViewScoreArray.add((TextView) findViewById(R.id.score9));
        TextViewScoreArray.add((TextView) findViewById(R.id.score10));

        Gson gson = new Gson();
        String jsonUser = prefs.getString(getString(R.string.CURRENT_USER),
                "{\"email\" : \"no email\", \"highScores\" : [ 0 ],\"name\" : \"no name\",\"password\" : \"no password\"}\n" );
        User curUser = gson.fromJson(jsonUser,User.class);
        for (int i=0; i<curUser.getHighScores().size();i++){
            TextViewScoreArray.get(i).setText(curUser.getHighScores().get(i).toString());
        }
    }
    @Override
    public void onBackPressed() {

    }


    public void homepage(View view) {
        Intent intent = new Intent(HighScoreActivity.this , HomePageActivity.class);
        startActivity(intent);
    }
}
