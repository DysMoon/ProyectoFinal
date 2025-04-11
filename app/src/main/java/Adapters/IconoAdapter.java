package Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.billeteradigital.R;

public class IconoAdapter extends BaseAdapter {

    private Context context;
    private String[] iconos;
    private int selectedPosition = -1;

    public IconoAdapter(Context context, String[] iconos) {
        this.context = context;
        this.iconos = iconos;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged();
    }

    public String getSelectedIcon() {
        return iconos[selectedPosition];
    }

    @Override
    public int getCount() {
        return iconos.length;
    }

    @Override
    public Object getItem(int position) {
        return iconos[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ImageView iv;

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_icono, parent, false);
        } else {
            view = convertView;
        }

        iv = view.findViewById(R.id.iv_icono);

        int resId = context.getResources().getIdentifier(iconos[position], "drawable", context.getPackageName());
        iv.setImageResource(resId);

        // Efecto visual de selección
        float scale = selectedPosition == position ? 1.3f : 1f;
        iv.setScaleX(scale);
        iv.setScaleY(scale);
        iv.setBackgroundResource(selectedPosition == position ? R.drawable.bg_selected : 0);

        return view;
    }
}
