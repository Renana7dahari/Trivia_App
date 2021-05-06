package com.example.triviaproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;


import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {
    private JSONObject jsonObj;
    private String question;
    private ArrayList<String> questionsArray = new ArrayList<String>();
    private JSONObject questionBlock;
    private JSONArray incorrect_answers;
    private int currentQuestionIndex = 0;
    private int score =0;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private URL url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        RetrieveFeedTask r = new RetrieveFeedTask();
        editor = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE).edit();
        score = 0;
        currentQuestionIndex = 0;
        editor.putLong(getString(R.string.START_TIME),(System.currentTimeMillis())/1000);
        editor.apply();
        prefs = getSharedPreferences(getString(R.string.MY_PREFS), MODE_PRIVATE);

        try {
            setDifficulty();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        r.execute();

    }

    @Override
    public void onBackPressed() {

    }


    private void setDifficulty() throws MalformedURLException {
        String dif = prefs.getString(getString(R.string.DIFFICULTY),"Easy");
        switch(dif){
            case "Easy":
                url = new URL("https://opentdb.com/api.php?amount=50&difficulty=easy&type=multiple");
                break;
            case "Medium":
                url = new URL("https://opentdb.com/api.php?amount=50&difficulty=medium&type=multiple");
                break;
            case "Hard":
                url = new URL("https://opentdb.com/api.php?amount=50&difficulty=hard&type=multiple");
                break;
        }
    }

    private void getNextQuestion() throws JSONException {

        questionBlock = (JSONObject)((JSONArray)jsonObj.get("results")).get(currentQuestionIndex);
        question = questionBlock.get("question").toString();
        incorrect_answers =(JSONArray)questionBlock.get("incorrect_answers");
        questionsArray.clear();
        questionsArray.add(incorrect_answers.get(0).toString());
        questionsArray.add(incorrect_answers.get(1).toString());
        questionsArray.add(incorrect_answers.get(2).toString());
        questionsArray.add(questionBlock.get("correct_answer").toString());

        currentQuestionIndex++;
        displayQuestion();
    }
    private void displayQuestion(){
        TextView questionTextView = findViewById(R.id.QuestionsTextView);
        int randIndex = new Random().nextInt(4);
        Button answer1 = findViewById(R.id.answerOneButton);
        Button answer2 = findViewById(R.id.answerTwoButton);
        Button answer3 = findViewById(R.id.answerThreeButton);
        Button answer4 = findViewById(R.id.answerFourButton);
        questionTextView.setText(unEscapeHtmlEntities(question));
        answer1.setText(unEscapeHtmlEntities(questionsArray.get((0+randIndex)%4)));
        answer2.setText(unEscapeHtmlEntities(questionsArray.get((1+randIndex)%4)));
        answer3.setText(unEscapeHtmlEntities(questionsArray.get((2+randIndex)%4)));
        answer4.setText(unEscapeHtmlEntities(questionsArray.get((3+randIndex)%4)));
    }
    private String unEscapeHtmlEntities(String str){
        // The method get a string and convert all HTML entities to characters
        str = str.replaceAll("&quot;","\"");
        str = str.replaceAll("&amp;","&");
        str = str.replaceAll("&#039;","'");
        str = str.replaceAll("&rsquo;","'");
        str = str.replaceAll("&oacute;","ó");
        str = str.replaceAll("&lt;","<");
        str = str.replaceAll("&gt;",">");
        str = str.replaceAll("&cent;","¢");
        str = str.replaceAll("&pound;","£");
        str = str.replaceAll("&yen;","¥");
        str = str.replaceAll("&euro;","ó");
        str = str.replaceAll("&oacute;","€");
        str = str.replaceAll("&copy;","©");
        str = str.replaceAll("&reg;","®");
        str = str.replaceAll("&shy;"," ");
        str = str.replaceAll("&eacute;","é");
        str = str.replaceAll("&ldquo;","“");
        str = str.replaceAll("&uuml;","ü");
        return str;
    }

    public void onClickAnswer(View view) {
        String answerButton = ((Button)view).getText().toString();
        Intent intent = new Intent(this, GameOverActivity.class);
        if(answerButton.equals(questionsArray.get(3))) {//correct answer
            try{
                Toast.makeText(this, "correct answer!", Toast.LENGTH_SHORT).show();
                score++;
                getNextQuestion();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{ // wrong answer
            editor.putInt(getString(R.string.FINAL_SCORE),score);
            editor.apply();
            editor.putLong(getString(R.string.END_TIME),(System.currentTimeMillis())/1000);
            editor.apply();
            startActivity(intent);
        }
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;
        protected void onPreExecute() {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);

            findViewById(R.id.QuestionsTextView).setVisibility(View.INVISIBLE);
            findViewById(R.id.answerOneButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.answerTwoButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.answerThreeButton).setVisibility(View.INVISIBLE);
            findViewById(R.id.answerFourButton).setVisibility(View.INVISIBLE);
        }

        protected String doInBackground(Void... urls) {

            try {
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            Log.i("INFO", response);
            try {
                jsonObj = new JSONObject(response);
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                findViewById(R.id.QuestionsTextView).setVisibility(View.VISIBLE);
                findViewById(R.id.answerOneButton).setVisibility(View.VISIBLE);
                findViewById(R.id.answerTwoButton).setVisibility(View.VISIBLE);
                findViewById(R.id.answerThreeButton).setVisibility(View.VISIBLE);
                findViewById(R.id.answerFourButton).setVisibility(View.VISIBLE);

                getNextQuestion();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
