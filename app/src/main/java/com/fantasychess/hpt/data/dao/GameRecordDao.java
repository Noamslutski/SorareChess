package com.fantasychess.hpt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.fantasychess.hpt.data.entity.GameRecord;

import java.util.List;

@Dao
public interface GameRecordDao {
    @Insert
    void insertAll(List<GameRecord> games);

    @Query("DELETE FROM game_records WHERE playerId = :playerId")
    void clearForPlayer(int playerId);

    @Query("SELECT * FROM game_records WHERE playerId = :playerId ORDER BY date DESC")
    List<GameRecord> forPlayer(int playerId);

    /** Games on/after a given ISO date — used to show "this week's" games. */
    @Query("SELECT * FROM game_records WHERE playerId = :playerId AND date >= :sinceIsoDate ORDER BY date DESC")
    List<GameRecord> forPlayerSince(int playerId, String sinceIsoDate);
}
