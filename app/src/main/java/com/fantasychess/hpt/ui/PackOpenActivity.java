package com.fantasychess.hpt.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fantasychess.hpt.data.Repository;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.databinding.ActivityPackOpenBinding;
import com.fantasychess.hpt.game.AppExecutors;

import java.util.ArrayList;
import java.util.List;

/** Shows the cards revealed from a freshly opened pack. */
public class PackOpenActivity extends AppCompatActivity {

    public static final String EXTRA_PLAYER_IDS = "playerIds";

    private ActivityPackOpenBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityPackOpenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        int[] ids = getIntent().getIntArrayExtra(EXTRA_PLAYER_IDS);
        b.btnContinue.setOnClickListener(v -> finish());

        CardAdapter adapter = new CardAdapter(p -> { });
        b.recycler.setLayoutManager(new GridLayoutManager(this, 3));
        b.recycler.setAdapter(adapter);

        if (ids == null || ids.length == 0) {
            finish();
            return;
        }
        b.title.setText(getString(com.fantasychess.hpt.R.string.pack_revealed, ids.length));

        AppExecutors.io(() -> {
            Repository repo = new Repository(this);
            List<ChessPlayer> players = new ArrayList<>();
            for (int id : ids) {
                ChessPlayer p = repo.player(id);
                if (p != null) players.add(p);
            }
            AppExecutors.main(() -> adapter.submit(players));
        });
    }
}
