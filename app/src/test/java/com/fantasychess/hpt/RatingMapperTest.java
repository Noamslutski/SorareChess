package com.fantasychess.hpt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fantasychess.hpt.game.RatingMapper;

import org.junit.Test;

public class RatingMapperTest {

    @Test
    public void boundariesMapToOneAndHundred() {
        assertEquals(1, RatingMapper.toCardRating(1000));
        assertEquals(100, RatingMapper.toCardRating(2800));
    }

    @Test
    public void clampsOutOfRange() {
        assertEquals(1, RatingMapper.toCardRating(500));
        assertEquals(100, RatingMapper.toCardRating(3200));
    }

    @Test
    public void midRangeIsMonotonic() {
        int low = RatingMapper.toCardRating(1500);
        int mid = RatingMapper.toCardRating(2000);
        int high = RatingMapper.toCardRating(2500);
        assertTrue(low < mid);
        assertTrue(mid < high);
    }

    @Test
    public void tierThresholds() {
        assertEquals(RatingMapper.Tier.BRONZE, RatingMapper.tierFor(59));
        assertEquals(RatingMapper.Tier.SILVER, RatingMapper.tierFor(60));
        assertEquals(RatingMapper.Tier.GOLD, RatingMapper.tierFor(75));
        assertEquals(RatingMapper.Tier.SPECIAL, RatingMapper.tierFor(90));
    }

    @Test
    public void rarerTiersHaveLowerWeight() {
        assertTrue(RatingMapper.drawWeight(95) < RatingMapper.drawWeight(50));
    }
}
