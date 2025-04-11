package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.billeteradigital.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Models.Categoria;
import Models.Movimiento;

public class MovimientoAdapter extends RecyclerView.Adapter<MovimientoAdapter.MovimientoViewHolder> {

    private List<Movimiento> movimientoList = new ArrayList<>();
    private Map<Integer, Categoria> categoriaMap = new HashMap<>();
    private final Context context;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public MovimientoAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MovimientoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_dash, parent, false);
        return new MovimientoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovimientoViewHolder holder, int position) {
        Movimiento movimiento = movimientoList.get(position);
        Categoria categoria = categoriaMap.get(movimiento.getCategoriaId());

        if (categoria != null) {
            holder.tv_cat_dash.setText(categoria.getNombre());
            int recursoId = context.getResources().getIdentifier(categoria.getIcon(), "drawable", context.getPackageName());

            if (recursoId != 0) {
                holder.iv_icon_category.setImageResource(recursoId);
            } else {
                holder.iv_icon_category.setImageResource(R.drawable.logo); // Recurso por defecto
            }
        } else {
            holder.tv_cat_dash.setText("Categoría Desconocida");
            holder.iv_icon_category.setImageResource(R.drawable.logo);
        }

        holder.tv_fec_dash.setText(dateFormat.format(movimiento.getFecha()));

        String cantidadText = String.format(Locale.getDefault(), "S/ %.2f", movimiento.getMonto());
        holder.tv_monto_mov_dash.setText(cantidadText);

        if (movimiento.isGasto()) {
            holder.tv_monto_mov_dash.setTextColor(ContextCompat.getColor(context, R.color.dangerbtnColor));
        } else {
            holder.tv_monto_mov_dash.setTextColor(ContextCompat.getColor(context, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return movimientoList.size();
    }

    public void setMovimientoList(List<Movimiento> movimientoList) {
        this.movimientoList = movimientoList;
        notifyDataSetChanged();
    }

    public void setCategoria(List<Categoria> categoriaList) {
        categoriaMap.clear();
        for (Categoria categoria : categoriaList) {
            categoriaMap.put(categoria.getId(), categoria);
        }
        notifyDataSetChanged();
    }

    public static class MovimientoViewHolder extends RecyclerView.ViewHolder {
        private final ImageView iv_icon_category;
        private final TextView tv_cat_dash;
        private final TextView tv_fec_dash;
        private final TextView tv_monto_mov_dash;

        public MovimientoViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cat_dash = itemView.findViewById(R.id.tv_cat_dash);
            tv_fec_dash = itemView.findViewById(R.id.tv_fec_dash);
            tv_monto_mov_dash = itemView.findViewById(R.id.tv_monto_mov_dash);
            iv_icon_category = itemView.findViewById(R.id.iv_icon_category);
        }
    }
}
