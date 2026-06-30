package com.fantasychess.hpt.federation;

import android.content.Context;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.GameRecord;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the bundled Hapoel Petah Tikva roster snapshot (assets/petah_tikva_players.json).
 * Used as the offline fallback for the federation integration and to seed the demo.
 */
public final class SnapshotLoader {

    public static final String ASSET = "petah_tikva_players.json";

    private SnapshotLoader() { }

    public static List<ParsedPlayer> loadFromAssets(Context context) {
        try (InputStream in = context.getAssets().open(ASSET)) {
            return parse(readAll(in));
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** Pure JSON parsing — unit-testable without Android. */
    public static List<ParsedPlayer> parse(String json) {
        List<ParsedPlayer> out = new ArrayList<>();
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();
        String club = optString(root, "club", "הפועל פתח תקווה");
        JsonArray players = root.getAsJsonArray("players");
        for (JsonElement el : players) {
            JsonObject o = el.getAsJsonObject();
            ChessPlayer p = new ChessPlayer();
            p.playerId = o.get("playerId").getAsInt();
            p.name = optString(o, "name", "");
            p.nameEn = optString(o, "nameEn", "");
            p.gender = optString(o, "gender", "M");
            p.birthYear = optInt(o, "birthYear", 0);
            p.title = optString(o, "title", "");
            p.fideId = optString(o, "fideId", "");
            p.club = club;
            p.ratingStandard = optInt(o, "ratingStandard", 0);
            p.ratingRapid = optInt(o, "ratingRapid", 0);
            p.ratingBlitz = optInt(o, "ratingBlitz", 0);

            ParsedPlayer pp = new ParsedPlayer(p);
            if (o.has("games")) {
                for (JsonElement ge : o.getAsJsonArray("games")) {
                    JsonObject g = ge.getAsJsonObject();
                    GameRecord rec = new GameRecord();
                    rec.playerId = p.playerId;
                    rec.date = optString(g, "date", "");
                    rec.event = optString(g, "event", "");
                    rec.opponent = optString(g, "opponent", "");
                    rec.color = optString(g, "color", "");
                    rec.result = optString(g, "result", "");
                    pp.games.add(rec);
                }
            }
            out.add(pp);
        }
        return out;
    }

    private static String optString(JsonObject o, String k, String def) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsString() : def;
    }

    private static int optInt(JsonObject o, String k, int def) {
        return o.has(k) && !o.get(k).isJsonNull() ? o.get(k).getAsInt() : def;
    }

    private static String readAll(InputStream in) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append('\n');
        }
        return sb.toString();
    }
}
