package com.example.triviaproject;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String password;
    private ArrayList<Integer> highScores;

    public User(String name, String email, String password, ArrayList<Integer> highScores) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.highScores = highScores;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {

        return password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setHighScores(ArrayList<Integer> highScores) {
        this.highScores = highScores;
    }

    public ArrayList<Integer> getHighScores() {
        return highScores;
    }
}
