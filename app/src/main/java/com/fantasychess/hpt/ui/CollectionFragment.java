package com.fantasychess.hpt.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.fantasychess.hpt.auth.Session;
import com.fantasychess.hpt.data.Repository;
import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.databinding.FragmentCollectionBinding;
import com.fantasychess.hpt.game.AppExecutors;

import java.util.List;

public class CollectionFragment extends Fragment {

    private FragmentCollectionBinding b;
    private CardAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        b = FragmentCollectionBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adapter = new CardAdapter(this::openDetail);
        b.recycler.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        b.recycler.setAdapter(adapter);
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void openDetail(ChessPlayer p) {
        Intent intent = new Intent(requireContext(), PlayerDetailActivity.class);
        intent.putExtra(PlayerDetailActivity.EXTRA_PLAYER_ID, p.playerId);
        startActivity(intent);
    }

    private void load() {
        long userId = Session.userId(requireContext());
        AppExecutors.io(() -> {
            Repository repo = new Repository(requireContext());
            List<ChessPlayer> owned = repo.ownedPlayers(userId);
            AppExecutors.main(() -> {
                if (b == null) return;
                adapter.submit(owned);
                b.emptyState.setVisibility(owned.isEmpty() ? View.VISIBLE : View.GONE);
            });
        });
    }
}
