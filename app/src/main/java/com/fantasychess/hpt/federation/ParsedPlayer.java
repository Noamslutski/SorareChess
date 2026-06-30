package com.fantasychess.hpt.federation;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.GameRecord;

import java.util.ArrayList;
import java.util.List;

/** Result of parsing a chess.org.il Player.aspx page. */
public class ParsedPlayer {
    public final ChessPlayer player;
    public final List<GameRecord> games;

    public ParsedPlayer(ChessPlayer player) {
        this.player = player;
        this.games = new ArrayList<>();
    }
}
