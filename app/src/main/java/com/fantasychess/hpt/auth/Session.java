package com.fantasychess.hpt.auth;

import android.content.Context;
import android.content.SharedPreferences;

/** Tracks the currently logged-in user id in SharedPreferences. */
public final class Session {

    private static final String PREFS = "session";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";

    private Session() { }

    private static SharedPreferences prefs(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void login(Context ctx, long userId, String username) {
        prefs(ctx).edit().putLong(KEY_USER_ID, userId).putString(KEY_USERNAME, username).apply();
    }

    public static void logout(Context ctx) {
        prefs(ctx).edit().clear().apply();
    }

    public static boolean isLoggedIn(Context ctx) {
        return prefs(ctx).getLong(KEY_USER_ID, -1) > 0;
    }

    public static long userId(Context ctx) {
        return prefs(ctx).getLong(KEY_USER_ID, -1);
    }

    public static String username(Context ctx) {
        return prefs(ctx).getString(KEY_USERNAME, "");
    }
}
