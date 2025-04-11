// Ya listo: ReporteActivity.java
// (Este código es funcional y sincronizado con el XML actual que me compartiste)

package com.example.billeteradigital;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.AppDataBase;
import Models.Movimiento;

public class ReporteActivity extends AppCompatActivity {

    private BarChart barChart;
    private Spinner spinnerAnios;
    private TextView tvSemana, tvInfoFecha, tvTotalIngresos, tvTotalGastos, tvBalance;
    private ImageView iconoReport, btnPrevWeek, btnNextWeek;

    private int semanaActual;
    private int anioActual;
    private boolean mostrarSemanal = false;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reporte);
        initViews();
        configurarSpinnerAnios();
        cargarGraficoMensual(Calendar.getInstance().get(Calendar.YEAR)); // Vista por defecto
    }

    private void initViews() {
        barChart = findViewById(R.id.barReportes);
        spinnerAnios = findViewById(R.id.spinnerAnios);
        tvSemana = findViewById(R.id.tvSemana);
        tvInfoFecha = findViewById(R.id.tvInfoFecha);
        tvTotalIngresos = findViewById(R.id.tvTotalIngresos);
        tvTotalGastos = findViewById(R.id.tvTotalGastos);
        tvBalance = findViewById(R.id.tvBalance);
        btnPrevWeek = findViewById(R.id.btnPrevWeek);
        btnNextWeek = findViewById(R.id.btnNextWeek);

        Calendar calendar = Calendar.getInstance();
        anioActual = calendar.get(Calendar.YEAR);
        semanaActual = calendar.get(Calendar.WEEK_OF_YEAR);

        tvSemana.setText("Sem " + semanaActual);
        btnPrevWeek.setOnClickListener(v -> cambiarSemana(-1));
        btnNextWeek.setOnClickListener(v -> cambiarSemana(1));
    }

    private void configurarSpinnerAnios() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> anios = new ArrayList<>();
        anios.add("Todos"); // opción para vista mensual
        for (int i = currentYear; i >= currentYear - 10; i--) {
            anios.add(String.valueOf(i));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, anios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnios.setAdapter(adapter);
        spinnerAnios.setSelection(0); // mes por defecto

        spinnerAnios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mostrarSemanal = false;
                    cargarGraficoMensual(currentYear);
                } else {
                    anioActual = Integer.parseInt(anios.get(position));
                    semanaActual = 1;
                    mostrarSemanal = true;
                    cargarGraficoSemanal(anioActual, semanaActual);
                    tvSemana.setText("Sem " + semanaActual);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void cambiarSemana(int paso) {
        if (!mostrarSemanal) return;

        semanaActual += paso;
        if (semanaActual < 1) semanaActual = 52;
        if (semanaActual > 52) semanaActual = 1;

        tvSemana.setText("Sem " + semanaActual);
        cargarGraficoSemanal(anioActual, semanaActual);
    }

    private void cargarGraficoSemanal(int anio, int semana) {
        executor.execute(() -> {
            List<Movimiento> movimientos = AppDataBase.getDataBase(getApplicationContext())
                    .movimientoDAO().getMovimientosRaw();

            float[] ingresos = new float[7];
            float[] gastos = new float[7];
            float totalIngresos = 0, totalGastos = 0;

            for (Movimiento m : movimientos) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(m.getFecha());
                int y = cal.get(Calendar.YEAR);
                int s = cal.get(Calendar.WEEK_OF_YEAR);
                int d = cal.get(Calendar.DAY_OF_WEEK) - 1; // 0=Domingo

                if (y == anio && s == semana) {
                    if (m.isGasto()) {
                        gastos[d] += m.getMonto();
                        totalGastos += m.getMonto();
                    } else {
                        ingresos[d] += m.getMonto();
                        totalIngresos += m.getMonto();
                    }
                }
            }

            List<BarEntry> entriesIn = new ArrayList<>();
            List<BarEntry> entriesOut = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                entriesIn.add(new BarEntry(i, ingresos[i]));
                entriesOut.add(new BarEntry(i, gastos[i]));
            }

            BarDataSet setIn = new BarDataSet(entriesIn, "Ingresos");
            BarDataSet setOut = new BarDataSet(entriesOut, "Gastos");
            setIn.setColor(getResources().getColor(R.color.colorToolbar));
            setOut.setColor(getResources().getColor(R.color.dangerbtnColor));

            BarData data = new BarData(setIn, setOut);
            data.setBarWidth(0.4f);

            float finalIngresos = totalIngresos;
            float finalGastos = totalGastos;
            runOnUiThread(() -> {
                barChart.setData(data);
                barChart.groupBars(0, 0.2f, 0.02f);
                barChart.invalidate();
                configurarEjesSemanal();
                actualizarResumen(anio, semana, finalIngresos, finalGastos);
            });
        });
    }

    private void cargarGraficoMensual(int anio) {
        executor.execute(() -> {
            List<Movimiento> movimientos = AppDataBase.getDataBase(getApplicationContext())
                    .movimientoDAO().getMovimientosRaw();

            float[] ingresos = new float[12];
            float[] gastos = new float[12];
            float totalIngresos = 0, totalGastos = 0;

            for (Movimiento m : movimientos) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(m.getFecha());
                if (cal.get(Calendar.YEAR) == anio) {
                    int mes = cal.get(Calendar.MONTH);
                    if (m.isGasto()) {
                        gastos[mes] += m.getMonto();
                        totalGastos += m.getMonto();
                    } else {
                        ingresos[mes] += m.getMonto();
                        totalIngresos += m.getMonto();
                    }
                }
            }

            List<BarEntry> entriesIn = new ArrayList<>();
            List<BarEntry> entriesOut = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                entriesIn.add(new BarEntry(i, ingresos[i]));
                entriesOut.add(new BarEntry(i, gastos[i]));
            }

            BarDataSet setIn = new BarDataSet(entriesIn, "Ingresos");
            BarDataSet setOut = new BarDataSet(entriesOut, "Gastos");
            setIn.setColor(getResources().getColor(R.color.colorToolbar));
            setOut.setColor(getResources().getColor(R.color.dangerbtnColor));

            BarData data = new BarData(setIn, setOut);
            data.setBarWidth(0.4f);

            float finalIngresos = totalIngresos;
            float finalGastos = totalGastos;
            runOnUiThread(() -> {
                barChart.setData(data);
                barChart.groupBars(0, 0.2f, 0.02f);
                barChart.invalidate();
                configurarEjesMensual();
                actualizarResumen(anio, -1, finalIngresos, finalGastos);
            });
        });
    }

    private void configurarEjesSemanal() {
        final String[] dias = {"Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                int index = Math.round(v);
                return index >= 0 && index < 7 ? dias[index] : "";
            }
        });
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
    }

    private void configurarEjesMensual() {
        final String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        XAxis xAxis = barChart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float v) {
                int index = Math.round(v);
                return index >= 0 && index < 12 ? meses[index] : "";
            }
        });
        barChart.getAxisRight().setEnabled(false);
        barChart.getDescription().setEnabled(false);
    }

    private void actualizarResumen(int anio, int semana, float ingresos, float gastos) {
        float balance = ingresos - gastos;
        tvInfoFecha.setText(semana > 0 ?
                String.format("Año %d - Semana %d", anio, semana) :
                String.format("Resumen anual %d", anio));
        tvTotalIngresos.setText(String.format(Locale.getDefault(), "S/ %.2f", ingresos));
        tvTotalGastos.setText(String.format(Locale.getDefault(), "S/ %.2f", gastos));
        tvBalance.setText(String.format(Locale.getDefault(), "S/ %.2f", balance));
    }
}
