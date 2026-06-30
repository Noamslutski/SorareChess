package com.fantasychess.hpt.data.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** A card a user owns (obtained from a pack). Owning the card is required to field the player. */
@Entity(tableName = "owned_cards",
        indices = {@Index(value = {"userId", "playerId"})})
public class OwnedCard {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public long userId;
    public int playerId;
    public long acquiredAt;

    public OwnedCard() { }

    public OwnedCard(long userId, int playerId, long acquiredAt) {
        this.userId = userId;
        this.playerId = playerId;
        this.acquiredAt = acquiredAt;
    }
}
