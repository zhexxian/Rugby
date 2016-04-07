package com.forofour.game.gameobjects;

import com.badlogic.gdx.Gdx;
import com.forofour.game.handlers.GameConstants;

import java.util.ArrayList;

/**
 * Created by seanlim on 9/3/2016.
 */
public class Team {
    private static ArrayList<Team> listOfTeams;
    private ArrayList<Player> teamList;
    private float score;
    private int id;

    public Team(int id){
        this.id = id;
        teamList = new ArrayList<Player>();
        score = 0;

        if(listOfTeams == null)
            listOfTeams = new ArrayList<Team>();
        listOfTeams.add(this);
    }
    public void addPlayer(Player p){
        teamList.add(p);
        p.setTeamId(id);
//        Gdx.app.log("Team addPlayer", "Team " + id + "Player " + p.getId());
    }
    public void removePlayer(Player p){
        teamList.remove(p);
    }
    public void addScore(float amt){
        score += amt;
    }

    public void addScore(){
        score += GameConstants.DEFAULT_SCORE / 100f;
    }
    public void deductScore() {
        score -= GameConstants.DEFAULT_SCORE_PENALTY;
    }

    public int getScore() {
        return (int) score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<Player> getTeamList() {
        return teamList;
    }

    public static ArrayList<Team> getListOfTeams() {
        return listOfTeams;
    }

    public int getId() {
        return id;
    }
}
