package com.forofour.game.gameobjects;
import com.forofour.game.handlers.GameConstants;

import java.util.ArrayList;

/**
 * Team object
 *  Helper to hold the listing of Players within the Team
 *  Tracks the Team score
 *  Score is time based. Exists scalar to define rate of scoring
 */
public class Team {
    private static ArrayList<Team> listOfTeams; // Master listing of all teams within game
    private ArrayList<Player> teamList;
    private float score; // Tracks the score of the team
    private int id;

    public Team(int id){
        this.id = id;
        teamList = new ArrayList<Player>();
        score = 0;

        if(listOfTeams == null)
            listOfTeams = new ArrayList<Team>();
        listOfTeams.add(this);
    }

    public void clear() {
        score = 0;
        teamList.clear();
    }

    // Addition of player into team
    public void addPlayer(Player p){
        teamList.add(p);
        p.setTeamId(id);
//        Gdx.app.log("Team addPlayer", "Team " + id + "Player " + p.getId());
    }
    public void removePlayer(Player p){
        teamList.remove(p);
    }

    // Addition of score(time based amount)
    public void addScore(float amt){
        score += amt;
    }

    public void addScore(){
        score += GameConstants.DEFAULT_SCORE / 100f;
    }
    public void deductScore() {
        score -= GameConstants.DEFAULT_SCORE_PENALTY;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
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
