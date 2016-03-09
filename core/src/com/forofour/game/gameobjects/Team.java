package com.forofour.game.gameobjects;

import com.forofour.game.handlers.GameConstants;

import java.util.ArrayList;

/**
 * Created by seanlim on 9/3/2016.
 */
public class Team {
    private static ArrayList<Team> listOfTeams;
    private ArrayList<Player> teamList;
    private int score;
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
    }
    public void removePlayer(Player p){
        teamList.remove(p);
    }
    public void addScore(int amt){
        score += amt;
    }
    public void addScore(){
        score += GameConstants.DEFAULT_SCORE;
    }
    public void deductScore() {
        score -= GameConstants.DEFAULT_SCORE_PENALTY;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<Player> getTeamList() {
        return teamList;
    }
}
