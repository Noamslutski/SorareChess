package com.fantasychess.hpt.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.fantasychess.hpt.R;
import com.fantasychess.hpt.auth.Session;
import com.fantasychess.hpt.data.Repository;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.data.entity.SquadSlot;
import com.fantasychess.hpt.databinding.FragmentSquadBinding;
import com.fantasychess.hpt.databinding.ViewSquadSlotBinding;
import com.fantasychess.hpt.game.AppExecutors;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SquadFragment extends Fragment {

    private FragmentSquadBinding b;
    private ViewSquadSlotBinding[] slots;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentSquadBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        slots = new ViewSquadSlotBinding[]{b.slot0, b.slot1, b.slot2, b.slot3, b.slot4};
        for (int i = 0; i < slots.length; i++) {
            final int index = i;
            slots[i].getRoot().setOnClickListener(v -> onSlotTapped(index));
        }
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        long userId = Session.userId(requireContext());
        AppExecutors.io(() -> {
            Repository repo = new Repository(requireContext());
            List<SquadSlot> squad = repo.squad(userId);
            ChessPlayer[] filled = new ChessPlayer[Repository.SQUAD_SIZE];
            int totalRating = 0, count = 0;
            for (SquadSlot s : squad) {
                if (s.slotIndex < 0 || s.slotIndex >= filled.length) continue;
                ChessPlayer p = repo.player(s.playerId);
                filled[s.slotIndex] = p;
                if (p != null) { totalRating += p.cardRating(); count++; }
            }
            final int avg = count == 0 ? 0 : Math.round(totalRating / (float) count);
            final int filledCount = count;
            AppExecutors.main(() -> {
                if (b == null) return;
                for (int i = 0; i < slots.length; i++) bindSlot(i, filled[i]);
                b.squadRating.setText(String.valueOf(avg));
                b.squadFilled.setText(String.format(Locale.US, "%d/5 שחקנים", filledCount));
            });
        });
    }

    private void bindSlot(int index, ChessPlayer p) {
        ViewSquadSlotBinding s = slots[index];
        if (p == null) {
            s.emptyState.setVisibility(View.VISIBLE);
            s.filledState.setVisibility(View.GONE);
            return;
        }
        s.emptyState.setVisibility(View.GONE);
        s.filledState.setVisibility(View.VISIBLE);
        s.cardFrame.setBackgroundResource(CardStyler.frameBackground(p));
        s.avatar.setImageResource(CardStyler.avatar(p));
        s.rating.setText(String.valueOf(p.cardRating()));
        s.rating.setTextColor(CardStyler.accentColor(p));
        s.name.setText(p.name);
    }

    private void onSlotTapped(int index) {
        long userId = Session.userId(requireContext());
        AppExecutors.io(() -> {
            Repository repo = new Repository(requireContext());
            List<ChessPlayer> owned = repo.ownedPlayers(userId);
            AppExecutors.main(() -> {
                if (b == null) return;
                showPicker(index, owned);
            });
        });
    }

    private void showPicker(int index, List<ChessPlayer> owned) {
        List<String> labels = new ArrayList<>();
        labels.add("— רוקן משבצת —");
        for (ChessPlayer p : owned) {
            labels.add(String.format(Locale.US, "%s  (%d)", p.name, p.cardRating()));
        }
        if (owned.isEmpty()) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.squad_pick_title)
                    .setMessage("אין לך עדיין קלפים. פתח מארז כדי לקבל שחקנים!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.squad_pick_title)
                .setItems(labels.toArray(new String[0]), (dialog, which) -> {
                    long userId = Session.userId(requireContext());
                    AppExecutors.io(() -> {
                        Repository repo = new Repository(requireContext());
                        if (which == 0) {
                            repo.clearSquadSlot(userId, index);
                        } else {
                            repo.setSquadSlot(userId, index, owned.get(which - 1).playerId);
                        }
                        AppExecutors.main(this::load);
                    });
                })
                .show();
    }
}
