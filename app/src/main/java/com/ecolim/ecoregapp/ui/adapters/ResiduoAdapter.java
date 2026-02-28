package com.ecolim.ecoregapp.ui.adapters;

import android.content.Context;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.ecolim.ecoregapp.R;
import com.ecolim.ecoregapp.data.local.entity.Residuo;

public class ResiduoAdapter extends ListAdapter<Residuo, ResiduoAdapter.ViewHolder> {

    public interface OnItemClick { void onClick(Residuo r); }
    private final OnItemClick listener;

    public ResiduoAdapter(OnItemClick listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_residuo, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
        h.bind(getItem(pos), listener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTipo, tvPeso, tvFecha, tvZona, tvEstado, tvEmoji;

        ViewHolder(View v) {
            super(v);
            tvTipo   = v.findViewById(R.id.tv_tipo);
            tvPeso   = v.findViewById(R.id.tv_peso);
            tvFecha  = v.findViewById(R.id.tv_fecha);
            tvZona   = v.findViewById(R.id.tv_zona);
            tvEstado = v.findViewById(R.id.tv_estado);
            tvEmoji  = v.findViewById(R.id.tv_emoji);
        }

        void bind(Residuo r, OnItemClick listener) {
            Context ctx = itemView.getContext();

            // Emoji por tipo
            String emoji;
            int colorRes;
            switch (r.tipo != null ? r.tipo.toLowerCase() : "") {
                case "plastico":  emoji = "♻️";  colorRes = R.color.tipo_plastico;  break;
                case "organico":  emoji = "🌿";  colorRes = R.color.tipo_organico;  break;
                case "papel":     emoji = "📄";  colorRes = R.color.tipo_papel;     break;
                case "metal":     emoji = "🔩";  colorRes = R.color.tipo_metal;     break;
                case "vidrio":    emoji = "🫙";  colorRes = R.color.tipo_vidrio;    break;
                case "peligroso": emoji = "⚠️";  colorRes = R.color.tipo_peligroso; break;
                default:          emoji = "🗑️";  colorRes = R.color.text_hint;      break;
            }
            tvEmoji.setText(emoji);
            tvTipo.setText(capitalizar(r.tipo));
            tvPeso.setText(String.format("%.2f kg", r.pesoKg));
            tvFecha.setText(r.fecha != null ? r.fecha.substring(0, Math.min(10, r.fecha.length())) : "-");
            tvZona.setText(r.zona != null && !r.zona.isEmpty() ? r.zona : r.ubicacion);

            // Estado sync
            if (r.sincronizado) {
                tvEstado.setText("✓ Sync");
                tvEstado.setBackgroundResource(R.drawable.bg_badge_green);
            } else {
                tvEstado.setText("⏳");
                tvEstado.setBackgroundResource(R.drawable.bg_badge_yellow);
            }

            itemView.setOnClickListener(v -> listener.onClick(r));
        }

        private String capitalizar(String s) {
            if (s == null || s.isEmpty()) return s;
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
    }

    private static final DiffUtil.ItemCallback<Residuo> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Residuo>() {
                @Override
                public boolean areItemsTheSame(@NonNull Residuo a, @NonNull Residuo b) {
                    return a.id == b.id;
                }
                @Override
                public boolean areContentsTheSame(@NonNull Residuo a, @NonNull Residuo b) {
                    return a.sincronizado == b.sincronizado &&
                           a.pesoKg == b.pesoKg &&
                           (a.tipo != null && a.tipo.equals(b.tipo));
                }
            };
}
