package com.fantasychess.hpt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.game.PackService;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PackServiceTest {

    private ChessPlayer player(int id, int rating) {
        ChessPlayer p = new ChessPlayer();
        p.playerId = id;
        p.ratingStandard = rating;
        return p;
    }

    private List<ChessPlayer> pool() {
        List<ChessPlayer> pool = new ArrayList<>();
        pool.add(player(1, 1200));
        pool.add(player(2, 1800));
        pool.add(player(3, 2200));
        pool.add(player(4, 2600));
        return pool;
    }

    @Test
    public void packHasFixedSize() {
        PackService svc = new PackService(new Random(42));
        assertEquals(PackService.CARDS_PER_PACK, svc.drawPack(pool()).size());
    }

    @Test
    public void emptyPoolYieldsEmptyPack() {
        PackService svc = new PackService(new Random(1));
        assertTrue(svc.drawPack(new ArrayList<>()).isEmpty());
    }

    @Test
    public void lowerRatedPlayersAppearMoreOften() {
        PackService svc = new PackService(new Random(7));
        int common = 0, rare = 0;
        for (int i = 0; i < 2000; i++) {
            for (ChessPlayer p : svc.drawPack(pool())) {
                if (p.playerId == 1) common++;
                if (p.playerId == 4) rare++;
            }
        }
        assertTrue("common bronze should out-draw special", common > rare);
    }
}
