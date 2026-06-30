package com.fantasychess.hpt.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.OwnedCard;

import java.util.List;

@Dao
public interface OwnedCardDao {
    @Insert
    long insert(OwnedCard card);

    @Query("SELECT COUNT(*) FROM owned_cards WHERE userId = :userId AND playerId = :playerId")
    int countOwned(long userId, int playerId);

    /** Distinct players the user owns, joined to player data, ranked by card rating. */
    @Query("SELECT p.* FROM players p " +
            "JOIN owned_cards o ON o.playerId = p.playerId " +
            "WHERE o.userId = :userId " +
            "GROUP BY p.playerId " +
            "ORDER BY p.ratingStandard DESC")
    List<ChessPlayer> ownedPlayers(long userId);

    @Query("SELECT COUNT(*) FROM owned_cards WHERE userId = :userId")
    int totalCards(long userId);
}
