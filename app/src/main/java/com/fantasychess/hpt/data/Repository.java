package com.fantasychess.hpt.data;

import android.content.Context;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.GameRecord;
import com.fantasychess.hpt.data.entity.OwnedCard;
import com.fantasychess.hpt.data.entity.PackGrant;
import com.fantasychess.hpt.data.entity.SquadSlot;
import com.fantasychess.hpt.federation.FederationClient;
import com.fantasychess.hpt.federation.ParsedPlayer;
import com.fantasychess.hpt.federation.SnapshotLoader;
import com.fantasychess.hpt.game.PackService;
import com.fantasychess.hpt.game.WeekKey;

import java.util.ArrayList;
import java.util.List;

/**
 * Central data access for the UI. All methods are blocking and must be called off the main
 * thread (the UI uses a background executor). Combines the local Room store with the federation
 * integration (live scrape + snapshot fallback).
 */
public class Repository {

    public static final int SQUAD_SIZE = 5;

    private final Context context;
    private final AppDatabase db;
    private final PackService packService = new PackService();

    public Repository(Context context) {
        this.context = context.getApplicationContext();
        this.db = AppDatabase.get(context);
    }

    public AppDatabase db() {
        return db;
    }

    // --- Players / federation ---------------------------------------------

    /** Seeds the players table from the federation (live + snapshot). Falls back to snapshot only. */
    public void ensurePlayersLoaded(boolean tryNetwork) {
        if (db.playerDao().count() > 0) return;
        refreshPlayers(tryNetwork);
    }

    public void refreshPlayers(boolean tryNetwork) {
        List<ParsedPlayer> roster;
        if (tryNetwork) {
            roster = new FederationClient(context).loadRoster();
        } else {
            roster = SnapshotLoader.loadFromAssets(context);
        }
        List<ChessPlayer> players = new ArrayList<>();
        for (ParsedPlayer pp : roster) {
            players.add(pp.player);
            db.gameRecordDao().clearForPlayer(pp.player.playerId);
            if (!pp.games.isEmpty()) db.gameRecordDao().insertAll(pp.games);
        }
        db.playerDao().upsertAll(players);
    }

    public List<ChessPlayer> allPlayers() {
        return db.playerDao().getAll();
    }

    public ChessPlayer player(int id) {
        return db.playerDao().getById(id);
    }

    public List<GameRecord> gamesThisWeek(int playerId) {
        return db.gameRecordDao().forPlayerSince(playerId, WeekKey.startOfCurrentWeekIso());
    }

    public List<GameRecord> allGames(int playerId) {
        return db.gameRecordDao().forPlayer(playerId);
    }

    // --- Packs -------------------------------------------------------------

    /** Grants starter packs on first login and the weekly pack when due. Returns cards revealed. */
    public List<ChessPlayer> grantDuePacks(long userId) {
        List<ChessPlayer> revealed = new ArrayList<>();
        if (db.packGrantDao().hasGrant(userId, "STARTER") == 0) {
            revealed.addAll(openPacks(userId, PackService.STARTER_PACKS, "STARTER"));
        }
        String week = WeekKey.current();
        if (db.packGrantDao().hasGrant(userId, week) == 0) {
            revealed.addAll(openPacks(userId, PackService.WEEKLY_PACKS, week));
        }
        return revealed;
    }

    public boolean weeklyPackAvailable(long userId) {
        return db.packGrantDao().hasGrant(userId, WeekKey.current()) == 0;
    }

    private List<ChessPlayer> openPacks(long userId, int packs, String weekKey) {
        List<ChessPlayer> pool = allPlayers();
        List<ChessPlayer> revealed = new ArrayList<>();
        long now = System.currentTimeMillis();
        for (int i = 0; i < packs; i++) {
            for (ChessPlayer p : packService.drawPack(pool)) {
                db.ownedCardDao().insert(new OwnedCard(userId, p.playerId, now));
                revealed.add(p);
            }
        }
        db.packGrantDao().insert(new PackGrant(userId, weekKey, packs, now));
        return revealed;
    }

    // --- Collection / squad ------------------------------------------------

    public List<ChessPlayer> ownedPlayers(long userId) {
        return db.ownedCardDao().ownedPlayers(userId);
    }

    public int totalCards(long userId) {
        return db.ownedCardDao().totalCards(userId);
    }

    public boolean owns(long userId, int playerId) {
        return db.ownedCardDao().countOwned(userId, playerId) > 0;
    }

    public List<SquadSlot> squad(long userId) {
        return db.squadDao().getSquad(userId);
    }

    /** Places an owned player into a slot. No-op if not owned. */
    public boolean setSquadSlot(long userId, int slotIndex, int playerId) {
        if (!owns(userId, playerId)) return false;
        db.squadDao().put(new SquadSlot(userId, slotIndex, playerId));
        return true;
    }

    public void clearSquadSlot(long userId, int slotIndex) {
        db.squadDao().clearSlot(userId, slotIndex);
    }
}
