package com.brainexpensescontrol.controle_de_despesas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GastosPorAno extends AppCompatActivity {

    RecyclerView recyclerViewYear11;
    MyDatabaseHelper myDB;
    private Context context;

    private Calendar currentCalendar;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos_por_ano);

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("GastosPorAno");
        }

        context = this;

        // Obtenha a moeda selecionada usando PreferencesUtil

        String textTodisplay_translate_despesas = context.getString(R.string.translate_despesas);
        String textToDisplay_translate_receitas = context.getString(R.string.translate_receitas);
        String textToDisplay_translate_saldo = context.getString(R.string.translate_saldo);

        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context);
        String frase_moedaSelecionada = textTodisplay_translate_despesas + " " + moedaSelecionada;
        String frase2_moedaSelecionada = textToDisplay_translate_receitas + " " + moedaSelecionada;
        String frase3_moedaSelecionada = textToDisplay_translate_saldo + " " + moedaSelecionada;


        myDB = new MyDatabaseHelper(GastosPorAno.this);

        List<String> total_years = myDB.obterAnosDistintos();

        List<Double> vector_total_sum_for_every_years_despesa = new ArrayList<>();
        List<Double> vector_total_sum_for_every_years_receita = new ArrayList<>();
        List<Double> vector_total_sum_for_every_years_liquido = new ArrayList<>();


        List<String> stringArray_despesa = new ArrayList<>();
        List<String> stringArray_receita = new ArrayList<>();
        List<String> stringArray_liquido = new ArrayList<>();

        List<String> stringArray_moedas = new ArrayList<>();
        List<String> stringArray_moedas2 = new ArrayList<>();
        List<String> stringArray_moedas3 = new ArrayList<>();


        for (String year : total_years) {

            double totalGastoPorAno_despesa_1 = myDB.sumPricesForYearReceitaDespesa(year,"DESPESA");
            double totalGastoPorAno_despesa_2 = myDB.sumPricesForYearReceitaDespesa(year,"DESPESA COM CARTÃO");
            double totalGastoPorAno_despesa = totalGastoPorAno_despesa_1 + totalGastoPorAno_despesa_2 ;
            double totalGastoPorAno_receita = myDB.sumPricesForYearReceitaDespesa(year,"RECEITA");
            double totalGastoPorAno_liquido = totalGastoPorAno_receita - totalGastoPorAno_despesa;

            vector_total_sum_for_every_years_despesa.add(totalGastoPorAno_despesa);
            vector_total_sum_for_every_years_receita.add(totalGastoPorAno_receita);
            vector_total_sum_for_every_years_liquido.add(totalGastoPorAno_liquido);


            stringArray_moedas.add(frase_moedaSelecionada);
            stringArray_moedas2.add(frase2_moedaSelecionada);
            stringArray_moedas3.add(frase3_moedaSelecionada);


            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMinimumFractionDigits(2);

            String formattedValue_despesa = numberFormat.format(totalGastoPorAno_despesa);
            stringArray_despesa.add(formattedValue_despesa);

            String formattedValue_receita = numberFormat.format(totalGastoPorAno_receita);
            stringArray_receita.add(formattedValue_receita);

            String formattedValue_liquido = numberFormat.format(totalGastoPorAno_liquido);
            stringArray_liquido.add(formattedValue_liquido);
        }

            // Suponha que você tenha as listas total_years e total_sum_for_every_years preenchidas.
            CustomAdapterYears adapter = new CustomAdapterYears(context, total_years, stringArray_despesa,stringArray_receita,stringArray_liquido,stringArray_moedas, stringArray_moedas2, stringArray_moedas3);
            RecyclerView recyclerViewYear11 = findViewById(R.id.recyclerViewYear11);
            recyclerViewYear11.setAdapter(adapter);
            recyclerViewYear11.setLayoutManager(new LinearLayoutManager(this)); // Use o layout manager apropriado.

    }


    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

}











