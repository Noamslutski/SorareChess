package com.fantasychess.hpt.federation;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Integration with the Israeli Chess Federation site (chess.org.il).
 *
 * There is no public JSON API, so we fetch the player HTML page and parse it with Jsoup
 * ({@link PlayerPageParser}). The roster of Hapoel Petah Tikva is defined by the player ids
 * in the bundled snapshot; each is enriched from the live page when the device is online,
 * and falls back to the snapshot otherwise.
 */
public class FederationClient {

    private static final String TAG = "FederationClient";
    private static final String PLAYER_URL = "https://www.chess.org.il/Players/Player.aspx?Id=";
    private static final String UA =
            "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/120.0 Mobile Safari/537.36";

    private final Context context;
    private final OkHttpClient http;

    public FederationClient(Context context) {
        this.context = context.getApplicationContext();
        this.http = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(8, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Returns the full Hapoel Petah Tikva roster. Tries to refresh each player from the live
     * federation page; any player that can't be fetched keeps its snapshot data so the app is
     * always usable offline.
     */
    public List<ParsedPlayer> loadRoster() {
        List<ParsedPlayer> snapshot = SnapshotLoader.loadFromAssets(context);
        List<ParsedPlayer> result = new ArrayList<>(snapshot.size());
        for (ParsedPlayer base : snapshot) {
            ParsedPlayer live = tryFetch(base.player.playerId);
            result.add(merge(base, live));
        }
        return result;
    }

    /** Fetches and parses a single player page, or null on any failure. */
    public ParsedPlayer tryFetch(int playerId) {
        Request req = new Request.Builder()
                .url(PLAYER_URL + playerId)
                .header("User-Agent", UA)
                .header("Accept-Language", "he-IL,he;q=0.9,en;q=0.8")
                .build();
        try (Response resp = http.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) return null;
            String html = resp.body().string();
            ParsedPlayer parsed = PlayerPageParser.parse(playerId, html);
            // Reject obviously empty parses (e.g. blocked / changed DOM).
            if (parsed.player.ratingStandard <= 0 && parsed.games.isEmpty()) return null;
            return parsed;
        } catch (Exception e) {
            Log.w(TAG, "Live fetch failed for " + playerId + ", using snapshot", e);
            return null;
        }
    }

    /** Prefer live values when present; otherwise keep the snapshot's. */
    private static ParsedPlayer merge(ParsedPlayer snapshot, ParsedPlayer live) {
        if (live == null) return snapshot;
        if (live.player.ratingStandard > 0) snapshot.player.ratingStandard = live.player.ratingStandard;
        if (live.player.ratingRapid > 0) snapshot.player.ratingRapid = live.player.ratingRapid;
        if (live.player.ratingBlitz > 0) snapshot.player.ratingBlitz = live.player.ratingBlitz;
        if (live.player.name != null && !live.player.name.isEmpty()) snapshot.player.name = live.player.name;
        if (!live.games.isEmpty()) {
            snapshot.games.clear();
            snapshot.games.addAll(live.games);
        }
        return snapshot;
    }
}
