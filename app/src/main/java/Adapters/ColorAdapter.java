package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.billeteradigital.R;

import java.util.List;
public class ColorAdapter extends BaseAdapter {
    private Context context;
    private List<Integer> colores;
    private int selectedPosition = -1;

    public ColorAdapter(Context context, List<Integer> colores) {
        this.context = context;
        this.colores = colores;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public int getSelectedColor() {
        return colores.get(selectedPosition);
    }

    @Override
    public int getCount() {
        return colores.size();
    }

    @Override
    public Object getItem(int position) {
        return colores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_color, parent, false);
        View vColor = view.findViewById(R.id.v_color);
        vColor.setBackgroundColor(colores.get(position));

        // Efecto visual
        vColor.setScaleX(selectedPosition == position ? 1.3f : 1f);
        vColor.setScaleY(selectedPosition == position ? 1.3f : 1f);

        return view;
    }
}

