package com.brainexpensescontrol.controle_de_despesas;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieData;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class PieChartFragment extends BottomSheetDialogFragment {

    private Context context;
    private MyDatabaseHelper myDB;

    private MyDatabaseHelper myDB2;
    private Calendar currentCalendar;

    private String selectedMonth; // Adicione esta variável para armazenar o mês selecionado


    List<Integer> colors = new ArrayList<>();
//    colors.add(Color.BLUE);
//    colors.add(Color.GREEN);
//    colors.add(Color.RED);
    // Adicione mais cores conforme necessário

    // Método para configurar o mês
    public void setMonth(String month) {
        selectedMonth = month;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pie_chart, container, false);
        context = getContext(); // Obtendo o contexto
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context);

        myDB2 = new MyDatabaseHelper(getContext());

        //----------- SETA AS VARIÁVEIS despesa_pagas_deste_mes e receita_recebidas_deste_mes

        NumberFormat numberFormat = NumberFormat.getNumberInstance();

        double despesa_pagas_deste_mes_sem_cartao = myDB2.DespesasDoMesPagas(selectedMonth, "DESPESA");
        double despesa_pagas_deste_mes_com_cartao = myDB2.DespesasDoMesPagas(selectedMonth, "DESPESA COM CARTÃO");
        double despesa_pagas_deste_mes = despesa_pagas_deste_mes_sem_cartao + despesa_pagas_deste_mes_com_cartao;


        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum6 = numberFormat.format(despesa_pagas_deste_mes);
        TextView resultTextView6 = view.findViewById(R.id.despesa_pagas_deste_mes);
        String textToDisplay6 = context.getString(R.string.translate_despesas_pagas_deste_mes);
        String concatenatedText6 = textToDisplay6 + " " + moedaSelecionada + " " + formattedtotalSum6;
        resultTextView6.setText(concatenatedText6);


        double receita_recebidas_deste_mes = myDB2.ReceitasRecebidasDoMes(selectedMonth, "RECEITA");

        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum7 = numberFormat.format(receita_recebidas_deste_mes);
        TextView resultTextView7 = view.findViewById(R.id.receita_recebidas_deste_mes);
        String textToDisplay7 = context.getString(R.string.translate_receitas_recebidas_deste_mes);
        String concatenatedText7 = textToDisplay7 + " " + moedaSelecionada + " " + formattedtotalSum7;
        resultTextView7.setText(concatenatedText7);




        double falta_pagar_deste_mes = receita_recebidas_deste_mes - despesa_pagas_deste_mes;

        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum8 = numberFormat.format(falta_pagar_deste_mes);
        TextView resultTextView8 = view.findViewById(R.id.falta_pagar_deste_mes);
        String textToDisplay8 = context.getString(R.string.translate_falta_pagar_deste_mes);
        String concatenatedText8 = textToDisplay8 + " " + moedaSelecionada + " " + formattedtotalSum8;
        resultTextView8.setText(concatenatedText8);



        double despesa_pagas_sem_cartao_acumulada = myDB2.DespesasAcumuladaPagas(selectedMonth, "DESPESA");
        double despesa_pagas_com_cartao_acmulada = myDB2.DespesasAcumuladaPagas(selectedMonth, "DESPESA COM CARTÃO");
        double despesa_pagas_acumulada = despesa_pagas_sem_cartao_acumulada + despesa_pagas_com_cartao_acmulada;
        double receita_recebida_acumulada = myDB2.ReceitasRecebidasAcumulada(selectedMonth, "RECEITA");
        double total_acumulado_considerando_pagos_e_recebidos = receita_recebida_acumulada - despesa_pagas_acumulada;


        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum9 = numberFormat.format(total_acumulado_considerando_pagos_e_recebidos);
        TextView resultTextView9 = view.findViewById(R.id.liquido_acumulado_considerando_pago_e_recebidos);
        String textToDisplay9 = context.getString(R.string.translate_total_acumulado_pagos_e_recebidos);
        String concatenatedText9 = textToDisplay9 + " " + moedaSelecionada + " " + formattedtotalSum9;
        resultTextView9.setText(concatenatedText9);

        // FIM ------------------------------------------------



        // Configurar o PieChart
        PieChart pieChart = view.findViewById(R.id.pieChart);
        setupPieChart(pieChart);

        // Configurar dados do gráfico
        loadChartData(pieChart);

        return view;
    }



    private void setupPieChart(PieChart pieChart) {
        // Configurações do gráfico de pizza, como legendas, cores, etc.
        // ...

        // Exemplo de configuração, ajuste conforme necessário
        pieChart.setUsePercentValues(true);
    }

    private void loadChartData(PieChart pieChart) {

        myDB = new MyDatabaseHelper(getContext());

        // Obter o mês atual
        //int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1; // Janeiro é 0, então adicionamos 1

        currentCalendar = Calendar.getInstance();
        Date currentDate = currentCalendar.getTime();
        int year = currentCalendar.get(Calendar.YEAR);
        int month = currentCalendar.get(Calendar.MONTH) + 1; // Os meses em Calendar são baseados em zero, então somamos 1

        // Formate o mês e o dia com dois dígitos
        String formattedMonth2 = String.format("%02d", month);
        String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth2);

        // Realizar a consulta no banco de dados para obter os dados relevantes

        // Processar os dados do cursor e adicionar ao gráfico
            List<String> categories = myDB.getAllCategories(selectedMonth); // Obtém categorias distintas

        if (categories != null && !categories.isEmpty()) {
            ArrayList<PieEntry> entries = new ArrayList<>();

            for (String category : categories) {
                // Chama o método para obter o valor total para cada categoria
                Cursor categoryCursor = myDB.readDataFilter_despesas_por_categoria(selectedMonth, category);
                float totalValue = 0;

                if (categoryCursor != null && categoryCursor.moveToFirst()) {
                    do {
                        // Obter dados do cursor, por exemplo, usando categoryCursor.getFloat(3) para a coluna 3
                        totalValue += categoryCursor.getFloat(3);
                    } while (categoryCursor.moveToNext());

                    // Obter dados do cursor


                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum20 = numberFormat.format(totalValue);


                    //entries.add(new PieEntry(totalValue, category + ": " , formattedtotalSum20)  );
                    entries.add(new PieEntry(totalValue, category + ": " + String.format(Locale.getDefault(), "%.2f", totalValue)  ));
                    // Adicionar entrada ao gráfico com rótulo personalizado
                    //entries.add(new PieEntry(totalValue, category + ": " + String.format(Locale.getDefault(), "%.2f", totalValue)  ));


                    //colors.add(getRandomColor());
                    // Adicionar entrada ao gráfico
                    //entries.add(new PieEntry(totalValue, category));
                    colors.add(getRandomColor());
                }

                // Fechar o cursor após o uso
                if (categoryCursor != null && !categoryCursor.isClosed()) {
                    categoryCursor.close();
                }
            }

            // Configurar o conjunto de dados
            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setColors(colors);

            // Configurar o formato dos valores no gráfico
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return String.format(Locale.getDefault(), "%.2f%%", value);
                }
            });

            // Configurar o tamanho da fonte para os rótulos das categorias
            dataSet.setValueTextSize(10f); // Ajuste conforme necessário

            PieData pieData = new PieData(dataSet);
            // Exibir dados no gráfico
            pieChart.setData(pieData);
            // Adicionar descrição personalizada acima das labels
            pieChart.getDescription().setEnabled(false);


            String textToDisplay33 = context.getString(R.string.translate_pie_chart_categorias);
            pieChart.setCenterText(textToDisplay33);


            pieChart.getLegend().setOrientation(Legend.LegendOrientation.VERTICAL);
            pieChart.setCenterTextSize(14f);
            pieChart.setExtraOffsets(10f, 10f, 10f, 10f); // Ajuste conforme necessário
            // Atualizar o gráfico

            pieChart.setEntryLabelTextSize(0f); // Textos que ficam sobre o grafico
            pieChart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
            pieChart.setExtraBottomOffset(30f); // esse valor faz com que a legenda que fica embaixo apareça e não fique cortada.
            pieChart.invalidate();
        }
    }

    // Método para obter uma cor aleatória (você pode personalizar isso conforme necessário)
    private int getRandomColor() {
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
    }
}
