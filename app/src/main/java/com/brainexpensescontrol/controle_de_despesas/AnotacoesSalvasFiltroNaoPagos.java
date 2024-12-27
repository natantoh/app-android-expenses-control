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

public class AnotacoesSalvasFiltroNaoPagos extends AppCompatActivity {

    RecyclerView recyclerView10;
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
    private String pago_ou_nao_pago = "NAO_PAGO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacoes_salvas_filtro_nao_pagos);

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("AnotacoesSalvasFiltroNaoPagos");
        }

        context10 = this; // ou use getApplicationContext() se necessário
        txtMonth10 = findViewById(R.id.txtMonth10);
        Button btnNext = findViewById(R.id.btnNext10);
        Button btnBefore = findViewById(R.id.btnBefore10);
        currentCalendar10 = Calendar.getInstance();
        recyclerView10 = findViewById(R.id.recyclerView10);


        myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroNaoPagos.this);

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


        customAdapter = new CustomAdapter(AnotacoesSalvasFiltroNaoPagos.this,AnotacoesSalvasFiltroNaoPagos.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
        recyclerView10.setAdapter(customAdapter);

        storeDataInArrays_using_filter_nao_pagos_e_nao_recebidos_do_mes(year_monthh10, pago_ou_nao_pago);

        // Formate o mês e o dia com dois dígitos
        String formattedMonth4 = String.format("%02d", month);

        // Formatar a data no formato desejado (ano-mês)
        String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth4);


        // Obtenha a moeda selecionada usando PreferencesUtil
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context10);

        // ----------------- INIT - SETANDO O VALOR ---------------------//

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);

        double total_despesasNaoPagasDoMes = myDB10.despesasNaoPagasDoMes(year_monthh10);
        String formattedtotalSum = numberFormat.format(total_despesasNaoPagasDoMes);
        TextView resultTextView3 = findViewById(R.id.textViewNaoPagosDesteMes);


        Context context03 = getApplicationContext();
        String texto_despesas_nao_pagas = context03.getString(R.string.translate_despesas_nao_pagas);
        String concatenatedText = texto_despesas_nao_pagas + " " + moedaSelecionada + " " + formattedtotalSum;
        resultTextView3.setText(concatenatedText);

        double total_ReceitasNaoRecebidasDoMes = myDB10.receitasNaoRecebidasDoMes(year_monthh10);
        String formattedtotalSum59 = numberFormat.format(total_ReceitasNaoRecebidasDoMes);
        TextView resultTextView59 = findViewById(R.id.textViewNaoRecebidosDesteMes);

        Context context04 = getApplicationContext();
        String texto_receitas_nao_recebidas = context04.getString(R.string.translate_receitas_nao_recebidas);
        String concatenatedText59 = texto_receitas_nao_recebidas + " " + moedaSelecionada + " " + formattedtotalSum59;
        resultTextView59.setText(concatenatedText59);

        // ----------------- FIM - SETANDO O VALOR ----------------------//


        // customAdapter é responsável por mostrar no recyclerView os itens:
        customAdapter = new CustomAdapter(AnotacoesSalvasFiltroNaoPagos.this,AnotacoesSalvasFiltroNaoPagos.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
        recyclerView10.setAdapter(customAdapter);
        recyclerView10.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroNaoPagos.this));

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

                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroNaoPagos.this,AnotacoesSalvasFiltroNaoPagos.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView10.setAdapter(customAdapter);

                storeDataInArrays_using_filter_nao_pagos_e_nao_recebidos_do_mes(year_month10, pago_ou_nao_pago);


                // ----------------- INIT - SETANDO O VALOR ---------------------//

                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                double total_despesasNaoPagasDoMes = myDB10.despesasNaoPagasDoMes(year_month10);
                String formattedtotalSum = numberFormat.format(total_despesasNaoPagasDoMes);
                TextView resultTextView3 = findViewById(R.id.textViewNaoPagosDesteMes);


                Context context033 = getApplicationContext();
                String texto_despesas_nao_pagas = context033.getString(R.string.translate_despesas_nao_pagas);
                String concatenatedText = texto_despesas_nao_pagas + " " + moedaSelecionada + " " + formattedtotalSum;
                resultTextView3.setText(concatenatedText);

                double total_ReceitasNaoRecebidasDoMes = myDB10.receitasNaoRecebidasDoMes(year_month10);
                String formattedtotalSum59 = numberFormat.format(total_ReceitasNaoRecebidasDoMes);
                TextView resultTextView59 = findViewById(R.id.textViewNaoRecebidosDesteMes);

                Context context044 = getApplicationContext();
                String texto_receitas_nao_recebidas = context044.getString(R.string.translate_receitas_nao_recebidas);
                String concatenatedText59 = texto_receitas_nao_recebidas + " " + moedaSelecionada + " " + formattedtotalSum59;
                resultTextView59.setText(concatenatedText59);
                // ----------------- FIM - SETANDO O VALOR ----------------------//



                // customAdapter é responsável por mostrar no recyclerView os itens:
                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroNaoPagos.this,AnotacoesSalvasFiltroNaoPagos.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView10.setAdapter(customAdapter);
                recyclerView10.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroNaoPagos.this));
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



                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroNaoPagos.this,AnotacoesSalvasFiltroNaoPagos.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView10.setAdapter(customAdapter);

                storeDataInArrays_using_filter_nao_pagos_e_nao_recebidos_do_mes(year_month, pago_ou_nao_pago);



                // ----------------- INIT - SETANDO O VALOR ---------------------//

                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                double total_despesasNaoPagasDoMes = myDB10.despesasNaoPagasDoMes(year_month);
                String formattedtotalSum = numberFormat.format(total_despesasNaoPagasDoMes);
                TextView resultTextView3 = findViewById(R.id.textViewNaoPagosDesteMes);

                Context context0333 = getApplicationContext();
                String texto_despesas_nao_pagas = context0333.getString(R.string.translate_despesas_nao_pagas);
                String concatenatedText = texto_despesas_nao_pagas + " " + moedaSelecionada + " " + formattedtotalSum;
                resultTextView3.setText(concatenatedText);

                double total_ReceitasNaoRecebidasDoMes = myDB10.receitasNaoRecebidasDoMes(year_month);
                String formattedtotalSum59 = numberFormat.format(total_ReceitasNaoRecebidasDoMes);
                TextView resultTextView59 = findViewById(R.id.textViewNaoRecebidosDesteMes);

                Context context048 = getApplicationContext();
                String texto_receitas_nao_recebidas = context048.getString(R.string.translate_receitas_nao_recebidas);
                String concatenatedText59 = texto_receitas_nao_recebidas + " " + moedaSelecionada + " " + formattedtotalSum59;
                resultTextView59.setText(concatenatedText59);

                // ----------------- FIM - SETANDO O VALOR ----------------------//


                // customAdapter é responsável por mostrar no recyclerView os itens:
                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroNaoPagos.this,AnotacoesSalvasFiltroNaoPagos.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView10.setAdapter(customAdapter);
                recyclerView10.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroNaoPagos.this));
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

    void storeDataInArrays_using_filter_nao_pagos_e_nao_recebidos_do_mes(String year_month, String pago_ou_nao_pago){

        Cursor cursor = myDB10.readDataFilter_nao_pagos_e_nao_recebidos_do_mes(year_month, pago_ou_nao_pago);

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


            if (cursor.getCount() == 0) {
                //Toast.makeText(this,"no data.",Toast.LENGTH_SHORT).show();
            } else {
                while (cursor.moveToNext()) {
                    book_id10.add(cursor.getString(0)); // Vai adicionando ao Array  book_id = new ArrayList<>();
                    book_title10.add(cursor.getString(1));
                    book_author10.add(cursor.getString(2));
                    book_pages10.add(cursor.getString(3));
                    book_date10.add(cursor.getString(4));
                    book_despesa_ou_receita10.add(cursor.getString(8));
                    book_categoria10.add(cursor.getString(10));
                    book_pago_ou_nao_pago10.add(cursor.getString(11));
                    book_red_flag_notification10.add(cursor.getString(13));
                    //book_year_month.add(cursor.getString(5));
                }
            }
        } finally {
            // Certifique-se de fechar o cursor antes de sair do método
            if (cursor != null) {
                cursor.close();
            }
            // Fechar o banco de dados
            if (myDB10 != null) {
                myDB10.close();
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
        // Por exemplo, você pode chamar o método storeDataInArrays_using_filter com os parâmetros atuais.

        Intent intent33 = new Intent(AnotacoesSalvasFiltroNaoPagos.this, AnotacoesSalvas.class);
        startActivity(intent33);

    }

} // public class AnotacoesSalvas extends AppCompatActivity








