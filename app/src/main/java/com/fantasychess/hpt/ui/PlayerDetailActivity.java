package com.fantasychess.hpt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fantasychess.hpt.R;
import com.fantasychess.hpt.data.Repository;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.GameRecord;
import com.fantasychess.hpt.databinding.ActivityPlayerDetailBinding;
import com.fantasychess.hpt.databinding.ItemGameRowBinding;
import com.fantasychess.hpt.game.AppExecutors;

import java.util.List;
import java.util.Locale;

/** Enlarged Sorare-style card plus the player's games during the current week. */
public class PlayerDetailActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER_ID = "playerId";

    private ActivityPlayerDetailBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPlayerDetailBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.btnBack.setOnClickListener(v -> finish());

        int id = getIntent().getIntExtra(EXTRA_PLAYER_ID, -1);
        AppExecutors.io(() -> {
            Repository repo = new Repository(this);
            ChessPlayer p = repo.player(id);
            List<GameRecord> week = repo.gamesThisWeek(id);
            List<GameRecord> all = repo.allGames(id);
            AppExecutors.main(() -> bind(p, week, all));
        });
    }

    private void bind(ChessPlayer p, List<GameRecord> week, List<GameRecord> all) {
        if (p == null) { finish(); return; }
        b.cardFrame.setBackgroundResource(CardStyler.frameBackground(p));
        b.avatar.setImageResource(CardStyler.avatar(p));
        b.rating.setText(String.valueOf(p.cardRating()));
        b.rating.setTextColor(CardStyler.accentColor(p));
        b.tier.setText(CardStyler.tierLabel(p));
        b.name.setText(p.name);
        b.club.setText(p.club);
        b.title.setText(p.title == null || p.title.isEmpty() ? "—" : p.title);
        b.ratingStandard.setText(String.valueOf(p.ratingStandard));
        b.ratingRapid.setText(String.valueOf(p.ratingRapid));
        b.ratingBlitz.setText(String.valueOf(p.ratingBlitz));
        b.birthYear.setText(p.birthYear > 0 ? String.valueOf(p.birthYear) : "—");

        List<GameRecord> toShow = week.isEmpty() ? all : week;
        b.gamesHeader.setText(week.isEmpty()
                ? getString(R.string.recent_games)
                : getString(R.string.games_this_week));

        b.gamesContainer.removeAllViews();
        if (toShow.isEmpty()) {
            b.noGames.setVisibility(View.VISIBLE);
        } else {
            b.noGames.setVisibility(View.GONE);
            LayoutInflater inflater = LayoutInflater.from(this);
            for (GameRecord g : toShow) {
                ItemGameRowBinding row = ItemGameRowBinding.inflate(inflater, b.gamesContainer, false);
                row.gameDate.setText(g.date);
                row.gameEvent.setText(g.event);
                row.gameOpponent.setText(String.format(Locale.US, "%s %s",
                        "W".equals(g.color) ? "⚪" : "⚫", g.opponent));
                row.gameResult.setText(prettyResult(g.result));
                colorResult(row.gameResult, g);
                b.gamesContainer.addView(row.getRoot());
            }
        }
    }

    private String prettyResult(String r) {
        if (r == null) return "";
        return r.replace("1/2", "½");
    }

    /** Green win / red loss / grey draw from the player's perspective. */
    private void colorResult(TextView tv, GameRecord g) {
        boolean white = "W".equals(g.color);
        String r = g.result == null ? "" : g.result;
        int color;
        if ((white && r.startsWith("1-0")) || (!white && r.startsWith("0-1"))) {
            color = 0xFF2E7D32; // win
        } else if (r.contains("1/2")) {
            color = 0xFF757575; // draw
        } else {
            color = 0xFFC62828; // loss
        }
        tv.setTextColor(color);
    }
}
