package com.fantasychess.hpt.game;

/**
 * Maps a national Israeli chess rating (roughly 1000–2800) to a Sorare-style
 * card rating in the 1–100 range, and derives a visual tier from it.
 */
public final class RatingMapper {

    public static final int MIN_NATIONAL = 1000;
    public static final int MAX_NATIONAL = 2800;

    private RatingMapper() { }

    /**
     * Linear map: 1000 -> 1, 2800 -> 100, clamped to [1, 100].
     */
    public static int toCardRating(int nationalRating) {
        double span = MAX_NATIONAL - MIN_NATIONAL; // 1800
        double scaled = (nationalRating - MIN_NATIONAL) / span * 99.0 + 1.0;
        long rounded = Math.round(scaled);
        if (rounded < 1) return 1;
        if (rounded > 100) return 100;
        return (int) rounded;
    }

    public enum Tier {
        BRONZE, SILVER, GOLD, SPECIAL
    }

    /**
     * Card tier drives the frame color of the Sorare-style card.
     * Rarer (higher) cards are drawn less often from packs.
     */
    public static Tier tierFor(int cardRating) {
        if (cardRating >= 90) return Tier.SPECIAL;
        if (cardRating >= 75) return Tier.GOLD;
        if (cardRating >= 60) return Tier.SILVER;
        return Tier.BRONZE;
    }

    /**
     * Relative draw weight for pack generation. Lower-tier cards are more common.
     */
    public static double drawWeight(int cardRating) {
        switch (tierFor(cardRating)) {
            case SPECIAL: return 1.0;
            case GOLD:    return 3.0;
            case SILVER:  return 6.0;
            case BRONZE:
            default:      return 10.0;
        }
    }
}
