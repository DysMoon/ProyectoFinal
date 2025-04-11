package Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.billeteradigital.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import Models.Categoria;
public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {
    public interface OnCategoriaClickListener {
        void onCategoriaClick(Categoria categoria);       // Para seleccionar
        void onCategoriaEditar(Categoria categoria);      // Para editar
        void onCategoriaEliminar(Categoria categoria);    // Para eliminar
    }


    private List<Categoria> categorias;
    private OnCategoriaClickListener listener;

    public CategoriaAdapter(List<Categoria> categorias, OnCategoriaClickListener listener) {
        this.categorias = categorias;
        this.listener = listener;
    }

    public void setData(List<Categoria> nuevasCategorias) {
        this.categorias = nuevasCategorias;
        notifyDataSetChanged(); // 🔁 Actualiza la vista
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recycler_categoria, parent, false);
        return new ViewHolder(view);
    }



    @Override
    public int getItemCount() {
        return categorias != null ? categorias.size() : 0;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria c = categorias.get(position);
        holder.tvCategoria.setText(c.getNombre());

        // Mostrar botones
        holder.btnEditar.setVisibility(View.VISIBLE);
        holder.btnEliminar.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onCategoriaClick(c);
        });

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) listener.onCategoriaEditar(c);
        });

        holder.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onCategoriaEliminar(c);
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoria;
        Button btnEditar, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoria = itemView.findViewById(R.id.tvCategoria);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
