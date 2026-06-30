package com.fantasychess.hpt.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.fantasychess.hpt.R;
import com.fantasychess.hpt.auth.Session;
import com.fantasychess.hpt.data.Repository;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.databinding.ActivityMainBinding;
import com.fantasychess.hpt.game.AppExecutors;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Session.isLoggedIn(this)) {
            finish();
            return;
        }

        b = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_packs) return show(new PacksFragment());
            if (id == R.id.nav_collection) return show(new CollectionFragment());
            if (id == R.id.nav_squad) return show(new SquadFragment());
            return false;
        });

        if (savedInstanceState == null) {
            b.bottomNav.setSelectedItemId(R.id.nav_packs);
        }

        seedAndGrantPacks();
    }

    private boolean show(Fragment f) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, f)
                .commit();
        return true;
    }

    /** Loads the roster (federation + snapshot) and grants any due packs on app open. */
    private void seedAndGrantPacks() {
        long userId = Session.userId(this);
        AppExecutors.io(() -> {
            Repository repo = new Repository(this);
            repo.ensurePlayersLoaded(true);
            List<ChessPlayer> revealed = repo.grantDuePacks(userId);
            if (!revealed.isEmpty()) {
                int[] ids = new int[revealed.size()];
                for (int i = 0; i < revealed.size(); i++) ids[i] = revealed.get(i).playerId;
                AppExecutors.main(() -> {
                    Intent intent = new Intent(this, PackOpenActivity.class);
                    intent.putExtra(PackOpenActivity.EXTRA_PLAYER_IDS, ids);
                    startActivity(intent);
                });
            }
        });
    }
}
