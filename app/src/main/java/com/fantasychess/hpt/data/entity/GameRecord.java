package com.fantasychess.hpt.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** A single game a player played, scraped from Player.aspx (or the bundled snapshot). */
@Entity(tableName = "game_records", indices = {@Index("playerId")})
public class GameRecord {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public int playerId;
    public String date;       // ISO yyyy-MM-dd
    public String event;
    public String opponent;
    public String color;      // "W" or "B"
    public String result;     // "1-0", "0-1", "1/2-1/2"

    public GameRecord() { }
}
