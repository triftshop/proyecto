package com.trif.mitfg_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LuresAdapter extends RecyclerView.Adapter<LuresAdapter.LureViewHolder> {

    private List<LureItem> lures;
    private OnLureClickListener listener;

    public interface OnLureClickListener {
        void onLureClick(LureItem lure);
    }

    public LuresAdapter(List<LureItem> lures, OnLureClickListener listener) {
        this.lures = lures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lure_horizontal, parent, false); // NUEVO LAYOUT
        return new LureViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LureViewHolder holder, int position) {
        LureItem lure = lures.get(position);
        holder.bind(lure, listener);
    }

    @Override
    public int getItemCount() {
        return lures.size();
    }

    static class LureViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgLureIcon;
        private TextView txtLureName;
        private TextView txtLureDescription;

        public LureViewHolder(@NonNull View itemView) {
            super(itemView);
            imgLureIcon = itemView.findViewById(R.id.imgLureIcon);
            txtLureName = itemView.findViewById(R.id.txtLureName);
            txtLureDescription = itemView.findViewById(R.id.txtLureDescription);
        }

        public void bind(LureItem lure, OnLureClickListener listener) {
            txtLureName.setText(lure.getName());
            txtLureDescription.setText(lure.getDescription());

            // Cargar icono del señuelo
            int iconResId = itemView.getContext().getResources()
                    .getIdentifier(lure.getIconName(), "drawable", itemView.getContext().getPackageName());

            if (iconResId != 0) {
                imgLureIcon.setImageResource(iconResId);
            } else {
                imgLureIcon.setImageResource(R.drawable.ic_lure_default);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLureClick(lure);
                }
            });
        }
    }
}
