package com.theraiway.database;

import java.util.ArrayList;

/**
 * Club Object for the table :- Clubs
 */
public class Club {
    String _id;
    String name;
    String description;
    ArrayList<String> activities ;
    ArrayList<String> achievements ;
    ArrayList<String> heads ;

    public Club(){
        activities = new ArrayList<>();
        achievements = new ArrayList<>();
        heads = new ArrayList<>();
    }

    public Club(String s_id , String sname , String s_description , ArrayList<String> s_activities , ArrayList<String> s_achievements , ArrayList<String> sheads){
        _id = s_id;
        name = sname;
        description = s_description;
        activities = s_activities;
        achievements = s_achievements;
        heads = sheads;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAchievements(ArrayList<String> achievements) {
        this.achievements = achievements;
    }

    public void setHeads(ArrayList<String> heads) {
        this.heads = heads;
    }

    public void setActivities(ArrayList<String> activities) {
        this.activities = activities;
    }

    public String getName() {
        return name;
    }

    public String get_id() {
        return _id;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getActivities() {
        return activities;
    }

    public ArrayList<String> getAchievements() {
        return achievements;
    }

    public ArrayList<String> getHeads() {
        return heads;
    }

    @Override
    public String toString() {
        return _id +" ," + name;
    }
}
