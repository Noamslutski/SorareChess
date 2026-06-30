package com.fantasychess.hpt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fantasychess.hpt.data.entity.ChessPlayer;

import java.util.List;

@Dao
public interface PlayerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsertAll(List<ChessPlayer> players);

    @Query("SELECT * FROM players ORDER BY ratingStandard DESC")
    List<ChessPlayer> getAll();

    @Query("SELECT * FROM players WHERE playerId = :id LIMIT 1")
    ChessPlayer getById(int id);

    @Query("SELECT COUNT(*) FROM players")
    int count();
}
