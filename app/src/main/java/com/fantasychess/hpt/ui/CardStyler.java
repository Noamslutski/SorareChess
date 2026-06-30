package com.fantasychess.hpt.ui;

import android.graphics.Color;

import com.fantasychess.hpt.R;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.game.RatingMapper;

/** Maps a player's tier to the Sorare-style card frame drawable, accent color and avatar. */
public final class CardStyler {

    private CardStyler() { }

    public static int frameBackground(ChessPlayer p) {
        switch (RatingMapper.tierFor(p.cardRating())) {
            case SPECIAL: return R.drawable.card_frame_special;
            case GOLD:    return R.drawable.card_frame_gold;
            case SILVER:  return R.drawable.card_frame_silver;
            case BRONZE:
            default:      return R.drawable.card_frame_bronze;
        }
    }

    public static int accentColor(ChessPlayer p) {
        switch (RatingMapper.tierFor(p.cardRating())) {
            case SPECIAL: return Color.parseColor("#7C4DFF");
            case GOLD:    return Color.parseColor("#F2B705");
            case SILVER:  return Color.parseColor("#C0C7D1");
            case BRONZE:
            default:      return Color.parseColor("#C9824E");
        }
    }

    public static int avatar(ChessPlayer p) {
        return p.isFemale() ? R.drawable.avatar_girl : R.drawable.avatar_boy;
    }

    public static String tierLabel(ChessPlayer p) {
        switch (RatingMapper.tierFor(p.cardRating())) {
            case SPECIAL: return "SPECIAL";
            case GOLD:    return "GOLD";
            case SILVER:  return "SILVER";
            case BRONZE:
            default:      return "BRONZE";
        }
    }
}
