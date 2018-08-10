package com.example.raydengame.entity;

/**
 * player_info表对应的实体类
 */

public class PlayerInfo {
    private int id;
    private String name;
    private int score;
    private String password;

    public PlayerInfo() {
    }

    public PlayerInfo(int id, String name, int score, String password) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.password = password;
    }

    public PlayerInfo(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "PlayerInfo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", score=" + score +
                ", password='" + password + '\'' +
                '}';
    }
}
