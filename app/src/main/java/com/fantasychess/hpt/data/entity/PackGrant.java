package com.fantasychess.hpt.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Tracks free packs granted to a user so we issue exactly one weekly pack per ISO week
 * and the starter packs only once. weekKey == "STARTER" for the initial grant.
 */
@Entity(tableName = "pack_grants", primaryKeys = {"userId", "weekKey"})
public class PackGrant {
    public long userId;

    @NonNull
    public String weekKey = "";   // e.g. "2026-W26" or "STARTER"

    public int packsGranted;
    public long grantedAt;

    public PackGrant() { }

    public PackGrant(long userId, @NonNull String weekKey, int packsGranted, long grantedAt) {
        this.userId = userId;
        this.weekKey = weekKey;
        this.packsGranted = packsGranted;
        this.grantedAt = grantedAt;
    }
}
