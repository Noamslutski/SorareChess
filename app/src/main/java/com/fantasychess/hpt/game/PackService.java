package com.fantasychess.hpt.game;

import com.fantasychess.hpt.data.entity.ChessPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Draws cards for packs. Each pack contains {@link #CARDS_PER_PACK} cards drawn with weighting
 * so that lower-rated (common) players appear far more often than top GMs — Sorare-style scarcity.
 */
public final class PackService {

    public static final int CARDS_PER_PACK = 3;
    public static final int STARTER_PACKS = 3;
    public static final int WEEKLY_PACKS = 1;

    private final Random random;

    public PackService() {
        this(new Random());
    }

    public PackService(Random random) {
        this.random = random;
    }

    /** Draws one pack worth of players (with possible duplicates — duplicates are still cards). */
    public List<ChessPlayer> drawPack(List<ChessPlayer> pool) {
        List<ChessPlayer> drawn = new ArrayList<>(CARDS_PER_PACK);
        if (pool == null || pool.isEmpty()) return drawn;
        for (int i = 0; i < CARDS_PER_PACK; i++) {
            drawn.add(weightedPick(pool));
        }
        return drawn;
    }

    private ChessPlayer weightedPick(List<ChessPlayer> pool) {
        double total = 0;
        for (ChessPlayer p : pool) {
            total += RatingMapper.drawWeight(p.cardRating());
        }
        double r = random.nextDouble() * total;
        double acc = 0;
        for (ChessPlayer p : pool) {
            acc += RatingMapper.drawWeight(p.cardRating());
            if (r <= acc) return p;
        }
        return pool.get(pool.size() - 1);
    }
}
