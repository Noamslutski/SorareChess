package com.fantasychess.hpt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.fantasychess.hpt.data.entity.PackGrant;

@Dao
public interface PackGrantDao {
    @Insert
    void insert(PackGrant grant);

    @Query("SELECT COUNT(*) FROM pack_grants WHERE userId = :userId AND weekKey = :weekKey")
    int hasGrant(long userId, String weekKey);
}
