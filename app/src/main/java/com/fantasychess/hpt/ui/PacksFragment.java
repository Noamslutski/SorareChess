package com.fantasychess.hpt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fantasychess.hpt.auth.Session;
import com.fantasychess.hpt.data.Repository;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.databinding.FragmentPacksBinding;
import com.fantasychess.hpt.game.AppExecutors;

import java.util.List;
import java.util.Locale;

public class PacksFragment extends Fragment {

    private FragmentPacksBinding b;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentPacksBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        b.greeting.setText(getString(com.fantasychess.hpt.R.string.greeting_user,
                Session.username(requireContext())));
        b.btnOpenWeekly.setOnClickListener(v -> openWeekly());
        refreshStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshStatus();
    }

    private void refreshStatus() {
        long userId = Session.userId(requireContext());
        AppExecutors.io(() -> {
            Repository repo = new Repository(requireContext());
            int total = repo.totalCards(userId);
            boolean weekly = repo.weeklyPackAvailable(userId);
            AppExecutors.main(() -> {
                if (b == null) return;
                b.cardCount.setText(String.format(Locale.US, "%d", total));
                b.btnOpenWeekly.setEnabled(weekly);
                b.btnOpenWeekly.setText(weekly
                        ? "פתח מארז שבועי חינם 🎁"
                        : "המארז השבועי כבר נפתח ✓");
                b.weeklyHint.setText(weekly
                        ? "יש לך מארז חינם שמחכה!"
                        : "מארז חדש יחכה לך בשבוע הבא");
            });
        });
    }

    private void openWeekly() {
        long userId = Session.userId(requireContext());
        b.btnOpenWeekly.setEnabled(false);
        AppExecutors.io(() -> {
            Repository repo = new Repository(requireContext());
            List<ChessPlayer> revealed = repo.grantDuePacks(userId);
            AppExecutors.main(() -> {
                if (b == null) return;
                if (!revealed.isEmpty()) {
                    int[] ids = new int[revealed.size()];
                    for (int i = 0; i < revealed.size(); i++) ids[i] = revealed.get(i).playerId;
                    Intent intent = new Intent(requireContext(), PackOpenActivity.class);
                    intent.putExtra(PackOpenActivity.EXTRA_PLAYER_IDS, ids);
                    startActivity(intent);
                }
                refreshStatus();
            });
        });
    }
}
