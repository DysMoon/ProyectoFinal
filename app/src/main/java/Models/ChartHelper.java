package Models;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartHelper {
    public static void setupPieChart(PieChart pieChart) {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setCenterText("Distribución");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawCenterText(true);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setDrawInside(false);
        legend.setTextSize(12f);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(10f);
    }

    public static void updatePieChartWithCategories(PieChart pieChart, Map<Categoria, Double> categoriaDoubleMap) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (Map.Entry<Categoria, Double> entry : categoriaDoubleMap.entrySet()) {
            Categoria categoria = entry.getKey();
            Double cantidad = entry.getValue();

            if (cantidad > 0) {
                entries.add(new PieEntry(cantidad.floatValue(), categoria.getNombre()));
                colors.add(getColorForCategory(categoria.getNombre()));
            }
        }

        PieDataSet dataSet;
        if (pieChart.getData() != null && pieChart.getData().getDataSetCount() > 0) {
            dataSet = (PieDataSet)pieChart.getData().getDataSetByIndex(0);
            dataSet.setValues(entries);
            dataSet.setColors(colors);
            pieChart.getData().notifyDataChanged();
            pieChart.notifyDataSetChanged();
        }
        else {
            dataSet = new PieDataSet(entries, "Categorias");
            dataSet.setColors(colors);
            dataSet.setValueTextSize(12f);
            dataSet.setValueTextColor(Color.WHITE);
            dataSet.setValueFormatter(new PercentFormatter(pieChart));
            dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);

            PieData data = new PieData(dataSet);
            pieChart.setData(data);
        }
        pieChart.invalidate();
        pieChart.animateY(1000);
    }

    private static Integer getColorForCategory(String nombreCategoria) {
        switch (nombreCategoria.toLowerCase()) {
            case "vehículo":
                return Color.rgb(65,105,225);
            case "comida y bebida":
                return Color.rgb(50,205,50);
            case "trabajo":
                return Color.rgb(128,0,128);
            case "ropa":
                return Color.rgb(255,165,0);
            case "salario":
                return Color.rgb(0,128,0);
            case "bonificaciones":
                return Color.rgb(0,191,255);
            default:
                return ColorTemplate.MATERIAL_COLORS[Math.abs(nombreCategoria.hashCode()) % ColorTemplate.MATERIAL_COLORS.length];
        }
    }
}
