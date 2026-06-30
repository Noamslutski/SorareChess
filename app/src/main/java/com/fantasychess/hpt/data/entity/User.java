package com.fantasychess.hpt.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "users", indices = {@Index(value = "username", unique = true)})
public class User {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String username = "";

    public String email;

    /** PBKDF2 hash, Base64 encoded. */
    @NonNull
    public String passwordHash = "";

    /** Per-user salt, Base64 encoded. */
    @NonNull
    public String salt = "";

    public long createdAt;
}
