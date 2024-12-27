package com.brainexpensescontrol.controle_de_despesas;

import static android.content.ContentValues.TAG;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import androidx.appcompat.app.AppCompatActivity;

public class AnotacoesSalvasFiltroDespesasFixas extends AppCompatActivity {

    RecyclerView recyclerView_Filtro_Itens_fixos;
    MyDatabaseHelper myDB10;
    private Context context10;
    ArrayList<String> book_id10, book_title10, book_author10,book_pages10,book_date10,book_year_month10,book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10;
    CustomAdapter customAdapter;
    private TextView txtMonth10;
    private Calendar currentCalendar10;
    private LinearLayout buttonsContainer;
    private Button extraButton1;
    private Button extraButton2;
    private PopupWindow popupWindow;
    private String nome_cartao;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String year_monthh10;
    private SubMenu filtroCartoesSubMenu;
    private String gasto_fixo = "SIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacoes_salvas_filtro_despesas_fixas);

        // Inicializar o Firebase Analytics e Registrar evento de visualização da página
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("AnotacoesSalvasFiltroDespesasFixas");
        }

        context10 = this;

        txtMonth10 = findViewById(R.id.txtMonth10);
        Button btnNext = findViewById(R.id.btnNext_itens_fixos);
        Button btnBefore = findViewById(R.id.btnBefore_itens_fixos);
        currentCalendar10 = Calendar.getInstance();
        recyclerView_Filtro_Itens_fixos = findViewById(R.id.recyclerView_Filtro_Itens_fixos);

        myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroDespesasFixas.this);
        // Obter a data atual
        Date currentDate = currentCalendar10.getTime();
        int year = currentCalendar10.get(Calendar.YEAR);
        int month = currentCalendar10.get(Calendar.MONTH) + 1; // Os meses em Calendar são baseados em zero, então somamos 1

        // Formate o mês e o dia com dois dígitos
        String formattedMonth2 = String.format("%02d", month);
        year_monthh10 = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth2);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthText = sdf.format(currentDate);

        // Converter a primeira letra para maiúscula
        monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
        txtMonth10.setText(monthText);

        // Limpar os dados do adaptador, definindo uma lista vazia
        book_id10 = new ArrayList<>();
        book_title10 = new ArrayList<>();
        book_author10 = new ArrayList<>();
        book_pages10 = new ArrayList<>();
        book_date10 = new ArrayList<>();
        book_year_month10 = new ArrayList<>();
        book_categoria10 = new ArrayList<>();
        book_pago_ou_nao_pago10 = new ArrayList<>();
        book_red_flag_notification10 = new ArrayList<>();

        customAdapter = new CustomAdapter(AnotacoesSalvasFiltroDespesasFixas.this,AnotacoesSalvasFiltroDespesasFixas.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
        recyclerView_Filtro_Itens_fixos.setAdapter(customAdapter);

        storeDataInArrays_using_filter_only_gasto_fixo(year_monthh10,gasto_fixo);

        // Formate o mês e o dia com dois dígitos
        String formattedMonth4 = String.format("%02d", month);

        // Formatar a data no formato desejado (ano-mês)
        String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth4);

        // Obtenha a moeda selecionada usando PreferencesUtil
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context10);


        // ----------------- INIT - SETANDO A DESPESA E RECEITA FIXA, BEM COMO O LIQUIDO ----------------------//

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);

        double total_despesa_fixa_do_mes = myDB10.DespesaFixaDoMes(year_monthh10,gasto_fixo);
        String formattedtotalSum = numberFormat.format(total_despesa_fixa_do_mes);
        TextView resultTextView1 = findViewById(R.id.textViewDespesaFixaMes);


        Context context01 = getApplicationContext();
        String texto_despesa_fixa = context01.getString(R.string.translate_despesa_fixa);
        String concatenatedText = texto_despesa_fixa + " " + moedaSelecionada + " " + formattedtotalSum;
        resultTextView1.setText(concatenatedText);


        double total_receita_fixa_do_mes = myDB10.ReceitaFixaDoMes(year_monthh10,gasto_fixo);
        String formattedtotalSum2 = numberFormat.format(total_receita_fixa_do_mes);
        TextView resultTextView2 = findViewById(R.id.textViewReceitaFixaMes);


        Context context02 = getApplicationContext();
        String texto_receita_fixa = context02.getString(R.string.translate_receita_fixa);
        String concatenatedText2 = texto_receita_fixa + " " + moedaSelecionada + " " + formattedtotalSum2;
        resultTextView2.setText(concatenatedText2);

        double total_liquido_fixo_do_mes = total_receita_fixa_do_mes - total_despesa_fixa_do_mes;
        String formattedtotalSum3 = numberFormat.format(total_liquido_fixo_do_mes);
        TextView resultTextView3 = findViewById(R.id.textViewLiquidoFixoMes);


        Context context03 = getApplicationContext();
        String texto_liquido_fixo = context03.getString(R.string.translate_liquido_fixo);
        String concatenatedText3 = texto_liquido_fixo + " " + moedaSelecionada + " " + formattedtotalSum3;
        resultTextView3.setText(concatenatedText3);

        // ----------------- FIM - SETANDO A DESPESA E RECEITA FIXA, BEM COMO O LIQUIDO ----------------------//


        // customAdapter é responsável por mostrar no recyclerView os itens:
        customAdapter = new CustomAdapter(AnotacoesSalvasFiltroDespesasFixas.this,AnotacoesSalvasFiltroDespesasFixas.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
        recyclerView_Filtro_Itens_fixos.setAdapter(customAdapter);
        recyclerView_Filtro_Itens_fixos.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroDespesasFixas.this));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentCalendar10.add(Calendar.MONTH, 1);
                Date currentDate = currentCalendar10.getTime();
                int year = currentCalendar10.get(Calendar.YEAR);
                int month = currentCalendar10.get(Calendar.MONTH) + 1; // Os meses em Calendar são baseados em zero, então somamos 1

                // Formate o mês e o dia com dois dígitos
                String formattedMonth4 = String.format("%02d", month);
                String year_month10 = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth4);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String monthText = sdf.format(currentDate);

                // Converter a primeira letra para maiúscula
                monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
                txtMonth10.setText(monthText);

                // Limpar os dados do adaptador, definindo uma lista vazia
                book_id10 = new ArrayList<>();
                book_title10 = new ArrayList<>();
                book_author10 = new ArrayList<>();
                book_pages10 = new ArrayList<>();
                book_date10 = new ArrayList<>();
                book_year_month10 = new ArrayList<>();
                book_despesa_ou_receita10 = new ArrayList<>();
                book_categoria10 = new ArrayList<>();
                book_pago_ou_nao_pago10 = new ArrayList<>();
                book_red_flag_notification10 = new ArrayList<>();

                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroDespesasFixas.this,AnotacoesSalvasFiltroDespesasFixas.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Itens_fixos.setAdapter(customAdapter);


                storeDataInArrays_using_filter_only_gasto_fixo(year_month10,gasto_fixo);


                // ----------------- INIT - SETANDO A DESPESA E RECEITA FIXA, BEM COMO O LIQUIDO ----------------------//
                myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroDespesasFixas.this);
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                double total_despesa_fixa_do_mes11 = myDB10.DespesaFixaDoMes(year_month10,gasto_fixo);
                String formattedtotalSum11 = numberFormat.format(total_despesa_fixa_do_mes11);

                Context context011 = getApplicationContext();
                String texto_despesa_fixa = context011.getString(R.string.translate_despesa_fixa);
                String concatenatedText11 = texto_despesa_fixa + " " + moedaSelecionada + " " + formattedtotalSum11;
                TextView resultTextView11 = findViewById(R.id.textViewDespesaFixaMes);
                resultTextView11.setText(concatenatedText11);


                double total_receita_fixa_do_mes22 = myDB10.ReceitaFixaDoMes(year_month10,gasto_fixo);
                String formattedtotalSum22 = numberFormat.format(total_receita_fixa_do_mes22);

                Context context022 = getApplicationContext();
                String texto_receita_fixa = context022.getString(R.string.translate_receita_fixa);
                String concatenatedText22 = texto_receita_fixa + " " + moedaSelecionada + " " + formattedtotalSum22;
                TextView resultTextView22 = findViewById(R.id.textViewReceitaFixaMes);
                resultTextView22.setText(concatenatedText22);

                double total_liquido_fixo_do_mes23 = total_receita_fixa_do_mes22 - total_despesa_fixa_do_mes11;
                String formattedtotalSum33 = numberFormat.format(total_liquido_fixo_do_mes23);

                Context context033 = getApplicationContext();
                String texto_liquido_fixo = context033.getString(R.string.translate_liquido_fixo);
                String concatenatedText33 = texto_liquido_fixo + " " + moedaSelecionada + " " + formattedtotalSum33;
                TextView resultTextView33 = findViewById(R.id.textViewLiquidoFixoMes);
                resultTextView33.setText(concatenatedText33);

                // ----------------- FIM - SETANDO A DESPESA E RECEITA FIXA, BEM COMO O LIQUIDO ----------------------//


                // customAdapter é responsável por mostrar no recyclerView os itens:
                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroDespesasFixas.this,AnotacoesSalvasFiltroDespesasFixas.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Itens_fixos.setAdapter(customAdapter);
                recyclerView_Filtro_Itens_fixos.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroDespesasFixas.this));
            }
        });

        btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentCalendar10.add(Calendar.MONTH, -1);
                Date currentDate = currentCalendar10.getTime();

                int year = currentCalendar10.get(Calendar.YEAR);
                int month = currentCalendar10.get(Calendar.MONTH) + 1;

                // Formate o mês e o dia com dois dígitos
                String formattedMonth5 = String.format("%02d", month);
                String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth5);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String monthText = sdf.format(currentDate);
                monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);

                txtMonth10.setText(monthText);

                // Limpar os dados do adaptador, definindo uma lista vazia
                book_id10 = new ArrayList<>();
                book_title10 = new ArrayList<>();
                book_author10 = new ArrayList<>();
                book_pages10 = new ArrayList<>();
                book_date10 = new ArrayList<>();
                book_year_month10 = new ArrayList<>();
                book_despesa_ou_receita10 = new ArrayList<>();
                book_categoria10 = new ArrayList<>();
                book_pago_ou_nao_pago10 = new ArrayList<>();
                book_red_flag_notification10 = new ArrayList<>();



                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroDespesasFixas.this,AnotacoesSalvasFiltroDespesasFixas.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Itens_fixos.setAdapter(customAdapter);

                storeDataInArrays_using_filter_only_gasto_fixo(year_month,gasto_fixo);


                // ----------------- INIT - SETANDO A DESPESA E RECEITA FIXA, BEM COMO O LIQUIDO ----------------------//
                myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroDespesasFixas.this);
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                double total_despesa_fixa_do_mes44 = myDB10.DespesaFixaDoMes(year_month,gasto_fixo);
                String formattedtotalSum44 = numberFormat.format(total_despesa_fixa_do_mes44);

                Context context0111 = getApplicationContext();
                String texto_despesa_fixa = context0111.getString(R.string.translate_despesa_fixa);
                String concatenatedText44 = texto_despesa_fixa + " " + moedaSelecionada + " " + formattedtotalSum44;
                TextView resultTextView44 = findViewById(R.id.textViewDespesaFixaMes);
                resultTextView44.setText(concatenatedText44);


                double total_receita_fixa_do_mes44 = myDB10.ReceitaFixaDoMes(year_month,gasto_fixo);
                String formattedtotalSum48 = numberFormat.format(total_receita_fixa_do_mes44);
                TextView resultTextView48 = findViewById(R.id.textViewReceitaFixaMes);

                Context context0222 = getApplicationContext();
                String texto_receita_fixa = context0222.getString(R.string.translate_receita_fixa);
                String concatenatedText48 = texto_receita_fixa + " " + moedaSelecionada + " " + formattedtotalSum48;
                resultTextView48.setText(concatenatedText48);

                double total_liquido_fixo_do_mes55 = total_receita_fixa_do_mes44 - total_despesa_fixa_do_mes44;
                String formattedtotalSum55 = numberFormat.format(total_liquido_fixo_do_mes55);
                TextView resultTextView55 = findViewById(R.id.textViewLiquidoFixoMes);

                Context context0333 = getApplicationContext();
                String texto_liquido_fixo = context0333.getString(R.string.translate_liquido_fixo);
                String concatenatedText55 = texto_liquido_fixo + " " + moedaSelecionada + " " + formattedtotalSum55;
                resultTextView55.setText(concatenatedText55);
                // ----------------- FIM - SETANDO A DESPESA E RECEITA FIXA, BEM COMO O LIQUIDO ----------------------//

                // customAdapter é responsável por mostrar no recyclerView os itens:
                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroDespesasFixas.this,AnotacoesSalvasFiltroDespesasFixas.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Itens_fixos.setAdapter(customAdapter);
                recyclerView_Filtro_Itens_fixos.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroDespesasFixas.this));
            }
        });

    }//onCreate

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            recreate();
        }
    }

    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    void storeDataInArrays_using_filter_only_gasto_fixo(String year_monthhh, String gasto_fixo2 ){

        MyDatabaseHelper myDB_geral = new MyDatabaseHelper(AnotacoesSalvasFiltroDespesasFixas.this);
        Cursor cursor = myDB_geral.readDataFilter_gasto_fixo_do_mes(year_monthhh, gasto_fixo2 );

        try {
            // Limpar os dados do adaptador, definindo uma lista vazia
            book_id10 = new ArrayList<>();
            book_title10 = new ArrayList<>();
            book_author10 = new ArrayList<>();
            book_pages10 = new ArrayList<>();
            book_date10 = new ArrayList<>();
            book_year_month10 = new ArrayList<>();
            book_despesa_ou_receita10 = new ArrayList<>();
            book_categoria10 = new ArrayList<>();
            book_pago_ou_nao_pago10 = new ArrayList<>();
            book_red_flag_notification10 = new ArrayList<>();


            if(cursor.getCount() == 0 ){
                //Toast.makeText(this,"no data.",Toast.LENGTH_SHORT).show();
            }else{
                while(cursor.moveToNext()){
                    book_id10.add(cursor.getString(0)); // Vai adicionando ao Array  book_id = new ArrayList<>();
                    book_title10.add(cursor.getString(1));
                    book_author10.add(cursor.getString(2));
                    book_pages10.add(cursor.getString(3));
                    book_date10.add(cursor.getString(4));
                    book_despesa_ou_receita10.add(cursor.getString(8));
                    book_categoria10.add(cursor.getString(10));
                    book_pago_ou_nao_pago10.add(cursor.getString(11));
                    book_red_flag_notification10.add(cursor.getString(13));
                }
            }
        } finally {
            // Certifique-se de fechar o cursor antes de sair do método
            if (cursor != null) {
                cursor.close();
            }
            // Fechar o banco de dados
            if (myDB_geral != null) {
                myDB_geral.close();
            }
        }
    }


    @Override
    public void onBackPressed() {
        // Realize as ações necessárias para atualizar a tela antes de chamar super.onBackPressed()
        // Por exemplo, você pode chamar o método que atualiza os dados na RecyclerView.
        updateRecyclerView();

        // Chame o método super.onBackPressed() para continuar com o comportamento padrão.
        super.onBackPressed();
    }

    // Método para atualizar os dados na RecyclerView
    private void updateRecyclerView() {
        // Coloque aqui o código para atualizar os dados na RecyclerView.
        // Por exemplo, você pode chamar o método storeDataInArrays_using_filter_only_gasto_fixo com os parâmetros atuais.

        Intent intent33 = new Intent(AnotacoesSalvasFiltroDespesasFixas.this, AnotacoesSalvas.class);
        startActivity(intent33);

    }

} // public class AnotacoesSalvas extends AppCompatActivity








