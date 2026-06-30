package com.fantasychess.hpt.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fantasychess.hpt.game.RatingMapper;

/**
 * A chess player sourced from the Israeli Chess Federation (chess.org.il).
 * {@code playerId} is the federation's Player.aspx Id.
 */
@Entity(tableName = "players")
public class ChessPlayer {
    @PrimaryKey
    public int playerId;

    @NonNull
    public String name = "";

    public String nameEn;

    /** "M" or "F" — selects the avatar. */
    public String gender;

    public int birthYear;

    public String title;       // GM/IM/FM/WFM/CM or ""
    public String fideId;
    public String club;

    public int ratingStandard;
    public int ratingRapid;
    public int ratingBlitz;

    /** Convenience: the 1–100 card rating derived from the national standard rating. */
    public int cardRating() {
        return RatingMapper.toCardRating(ratingStandard);
    }

    @Ignore
    public boolean isFemale() {
        return "F".equalsIgnoreCase(gender);
    }
}
