package com.fantasychess.hpt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.fantasychess.hpt.data.entity.SquadSlot;

import java.util.List;

@Dao
public interface SquadDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void put(SquadSlot slot);

    @Query("DELETE FROM squad_slots WHERE userId = :userId AND slotIndex = :slotIndex")
    void clearSlot(long userId, int slotIndex);

    @Query("SELECT * FROM squad_slots WHERE userId = :userId ORDER BY slotIndex")
    List<SquadSlot> getSquad(long userId);
}
