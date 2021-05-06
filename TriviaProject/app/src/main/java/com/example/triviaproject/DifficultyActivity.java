package com.example.triviaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DifficultyActivity extends AppCompatActivity {
    private SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_difficulty);
        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
    }
    @Override
    public void onBackPressed() {

    }

    public void onClickDifficulty(View view) {
        String chosenDifficulty = ((Button)view).getText().toString();
        switch(chosenDifficulty){
            case "Easy":
                editor.putString(getString(R.string.DIFFICULTY),"Easy");
                break;
            case "Medium":
                editor.putString(getString(R.string.DIFFICULTY),"Medium");
                break;
            case "Hard":
                editor.putString(getString(R.string.DIFFICULTY),"Hard");
                break;
        }
        editor.apply();
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    public void home(View view) {
        Intent intent = new Intent(DifficultyActivity.this , HomePageActivity.class);
        startActivity(intent);
    }
}
