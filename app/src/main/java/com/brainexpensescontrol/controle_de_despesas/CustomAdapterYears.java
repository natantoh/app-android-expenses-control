package com.brainexpensescontrol.controle_de_despesas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CustomAdapterYears extends RecyclerView.Adapter<CustomAdapterYears.MyViewHolder> {
    private Context context;
    private List<String> total_years, total_sum_for_every_years_despesa, total_sum_for_every_years_receita, total_sum_for_every_years_liquido;
    private List<String> array_moedas, array_moedas2, array_moedas3;

    public CustomAdapterYears(Context context, List<String> total_years, List<String> total_sum_for_every_years_despesa, List<String> total_sum_for_every_years_receita, List<String> total_sum_for_every_years_liquido, List<String> array_moedas, List<String> array_moedas2, List<String> array_moedas3 ) {
        this.context = context;

        this.total_years = total_years;
        this.total_sum_for_every_years_despesa = total_sum_for_every_years_despesa;
        this.total_sum_for_every_years_receita = total_sum_for_every_years_receita;
        this.total_sum_for_every_years_liquido = total_sum_for_every_years_liquido;

        this.array_moedas =  array_moedas;
        this.array_moedas2 = array_moedas2;
        this.array_moedas3 = array_moedas3;

    }

    // Métodos necessários do RecyclerView.Adapter

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Infla o layout do item da lista e cria um ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_row_total_every_years, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // Vincula os dados aos elementos de interface do usuário
        String ano = total_years.get(position);
        String resultado_despesa = total_sum_for_every_years_despesa.get(position);
        String resultado_receita = total_sum_for_every_years_receita.get(position);
        String resultado_liquido = total_sum_for_every_years_liquido.get(position);

        String set_moeda_year = array_moedas.get(position);
        String set_moeda_year2 = array_moedas2.get(position);
        String set_moeda_year3 = array_moedas3.get(position);

        holder.textViewAno.setText(ano);

        holder.textViewResultado_despesa.setText(  String.valueOf(resultado_despesa)  );
        holder.textViewResultado_receita.setText(  String.valueOf(resultado_receita)   );
        holder.textViewResultado_liquido.setText(  String.valueOf(resultado_liquido)  );

        holder.moeda_year.setText(set_moeda_year);
        holder.moeda_year2.setText(set_moeda_year2);
        holder.moeda_year3.setText(set_moeda_year3);

    }

    @Override
    public int getItemCount() {
        return total_years.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewAno, textViewResultado_despesa, textViewResultado_receita, textViewResultado_liquido;
        TextView moeda_year,moeda_year2,moeda_year3;

        public MyViewHolder(View itemView) {
            super(itemView);
            textViewAno = itemView.findViewById(R.id.year_txt);
            textViewResultado_despesa = itemView.findViewById(R.id.prices_total_year_txt);
            textViewResultado_receita = itemView.findViewById(R.id.prices_total_year_txt2);
            textViewResultado_liquido = itemView.findViewById(R.id.prices_total_year_txt3);

            moeda_year = itemView.findViewById(R.id.moeda_year);
            moeda_year2 = itemView.findViewById(R.id.moeda_year2);
            moeda_year3 = itemView.findViewById(R.id.moeda_year3);

        }
    }
}
