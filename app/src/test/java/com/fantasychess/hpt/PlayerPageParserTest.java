package com.fantasychess.hpt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.fantasychess.hpt.federation.ParsedPlayer;
import com.fantasychess.hpt.federation.PlayerPageParser;

import org.junit.Test;

import java.io.InputStream;
import java.util.Scanner;

public class PlayerPageParserTest {

    private String fixture() {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream("player_fixture.html");
             Scanner s = new Scanner(in, "UTF-8")) {
            return s.useDelimiter("\\A").next();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void parsesNameAndRatings() {
        ParsedPlayer pp = PlayerPageParser.parse(2685, fixture());
        assertEquals("דניאל כהן", pp.player.name);
        assertEquals(2418, pp.player.ratingStandard);
        assertEquals(2375, pp.player.ratingRapid);
        assertEquals(2330, pp.player.ratingBlitz);
        assertEquals(1996, pp.player.birthYear);
    }

    @Test
    public void parsesGames() {
        ParsedPlayer pp = PlayerPageParser.parse(2685, fixture());
        assertFalse(pp.games.isEmpty());
        assertEquals("מיכאל לוין", pp.games.get(0).opponent);
        assertEquals("W", pp.games.get(0).color);
        assertEquals("1-0", pp.games.get(0).result);
    }
}
