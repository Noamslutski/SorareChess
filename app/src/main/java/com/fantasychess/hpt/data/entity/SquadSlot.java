package com.fantasychess.hpt.data.entity;

import androidx.room.Entity;

/** One of the 5 squad slots for a user. Composite key (userId, slotIndex 0..4). */
@Entity(tableName = "squad_slots", primaryKeys = {"userId", "slotIndex"})
public class SquadSlot {
    public long userId;
    public int slotIndex;   // 0..4
    public int playerId;    // federation player id placed in this slot

    public SquadSlot() { }

    public SquadSlot(long userId, int slotIndex, int playerId) {
        this.userId = userId;
        this.slotIndex = slotIndex;
        this.playerId = playerId;
    }
}
