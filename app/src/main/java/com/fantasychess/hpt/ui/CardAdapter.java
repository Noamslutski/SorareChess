package com.fantasychess.hpt.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fantasychess.hpt.data.entity.ChessPlayer;
import com.fantasychess.hpt.databinding.ItemPlayerCardBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Grid adapter rendering Sorare-style player cards. */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.VH> {

    public interface OnCardClick {
        void onClick(ChessPlayer player);
    }

    private final List<ChessPlayer> items = new ArrayList<>();
    private final OnCardClick listener;

    public CardAdapter(OnCardClick listener) {
        this.listener = listener;
    }

    public void submit(List<ChessPlayer> players) {
        items.clear();
        items.addAll(players);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPlayerCardBinding binding = ItemPlayerCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ChessPlayer p = items.get(position);
        h.bind(p);
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(p);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemPlayerCardBinding b;

        VH(ItemPlayerCardBinding b) {
            super(b.getRoot());
            this.b = b;
        }

        void bind(ChessPlayer p) {
            b.cardFrame.setBackgroundResource(CardStyler.frameBackground(p));
            b.avatar.setImageResource(CardStyler.avatar(p));
            b.rating.setText(String.valueOf(p.cardRating()));
            b.rating.setTextColor(CardStyler.accentColor(p));
            b.tier.setText(CardStyler.tierLabel(p));
            b.name.setText(p.name);
            String sub = (p.title != null && !p.title.isEmpty() ? p.title + " · " : "")
                    + "תקני " + p.ratingStandard;
            b.subtitle.setText(sub);
            b.breakdown.setText(String.format(Locale.US, "מהיר %d · בזק %d",
                    p.ratingRapid, p.ratingBlitz));
        }
    }
}
