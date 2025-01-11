package com.brainexpensescontrol.controle_de_despesas;
import static android.content.ContentValues.TAG;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

public class AnotacoesSalvas extends AppCompatActivity {
    RecyclerView recyclerView;
    private FloatingActionButton add_button;
    MyDatabaseHelper myDB;
    MyDatabaseHelperCartoesCredito myDBC;
    private Context context;
    ArrayList<String> book_id, book_title, book_author,book_pages,book_date,book_year_month,book_despesa_ou_receita, book_categoria,book_pago_ou_nao_pago,book_red_flag_notification;
    CustomAdapter customAdapter;
    private TextView txtMonth;
    private Calendar currentCalendar;
    private LinearLayout buttonsContainer;
    private PopupWindow popupWindow;
    private String nome_cartao,year_monthh;
    private SubMenu filtroCartoesSubMenu,filtroCategoriasSubMenu,filtroDespesasFixasSubMenu,filtroReceitasSubMenu,filtroNaoPagosSubMenu,filtroHojeSubMenu, filtroNext7DaysSubMenu;
    private InterstitialAd mInterstitialAd;
    private FirebaseAnalytics mFirebaseAnalytics;
    //private static final long REPEAT_TIME = 4 * 60 * 60 * 1000; // 4 horas em milissegundos
    private static final long REPEAT_TIME = 16 * 60 * 1000; // 2 minutos em milissegundos
    private static final int JOB_ID = 12345; // Identificador único para o JobScheduler
    private static final String WORK_TAG = "notification_work";

    // Declaração de variáveis para identificadores únicos de itens de submenu
    private static final int MENU_ID_CARTOES_BASE = 100;
    private static final int MENU_ID_CATEGORIAS_BASE = 200;

    private LinearLayout linearLayoutParaOcultar;

    //--------------CRIANDO O MENU E AÇÃO DE CLIQUE ------------------------//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_principal, menu);

        Context context54 = getApplicationContext();
        String textToDisplay54 = context54.getString(R.string.translate_submenu_filtro_de_cartoes);
        String texto_filtro_categorias = context54.getString(R.string.translate_submenu_filtro_categorias);
        String texto_filtro_despesas_fixas = context54.getString(R.string.translate_submenu_filtro_despesas_fixas);

        String texto_filtro_receitas = context54.getString(R.string.translate_submenu_filtro_receitas);

        String texto_filtro_nao_pagos = context54.getString(R.string.translate_submenu_filtro_nao_pagos);
        String texto_filtro_hoje = context54.getString(R.string.translate_submenu_filtro_hoje);
        String texto_filtro_proximos_7_dias = context54.getString(R.string.translate_submenu_filtro_proximos_7_dias);

        filtroCartoesSubMenu = menu.addSubMenu(  getString(R.string.translate_submenu_filtro_de_cartoes)  );
        for (int i = 0; i < obterOpcoesDoBancoDeDados().size(); i++) {
            filtroCartoesSubMenu.add(Menu.NONE, MENU_ID_CARTOES_BASE + i, Menu.NONE, obterOpcoesDoBancoDeDados().get(i));
        }

        filtroCategoriasSubMenu = menu.addSubMenu(getString(R.string.translate_submenu_filtro_categorias) );
        for (int i = 0; i < obterOpcoesDoBancoDeDados_categorias().size(); i++) {
            filtroCategoriasSubMenu.add(Menu.NONE, MENU_ID_CATEGORIAS_BASE + i, Menu.NONE, obterOpcoesDoBancoDeDados_categorias().get(i));
        }

        filtroDespesasFixasSubMenu = menu.addSubMenu(texto_filtro_despesas_fixas);
        filtroReceitasSubMenu = menu.addSubMenu(texto_filtro_receitas);
        filtroNaoPagosSubMenu = menu.addSubMenu(texto_filtro_nao_pagos);
        filtroHojeSubMenu = menu.addSubMenu(texto_filtro_hoje);
        filtroNext7DaysSubMenu = menu.addSubMenu(texto_filtro_proximos_7_dias);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        if (itemID == R.id.action_custom) {
            // Verifique se o LinearLayout está visível
            boolean isLinearLayoutVisible = PreferencesUtil.getLinearLayoutVisibility(this);

            // Alterne a visibilidade do LinearLayout
            if (isLinearLayoutVisible) {
                // Se estiver visível, oculte-o e salve a opção nas preferências compartilhadas
                linearLayoutParaOcultar.setVisibility(View.GONE);
                PreferencesUtil.setLinearLayoutVisibility(this, false);
            } else {
                // Se estiver oculto, mostre-o e salve a opção nas preferências compartilhadas
                linearLayoutParaOcultar.setVisibility(View.VISIBLE);
                PreferencesUtil.setLinearLayoutVisibility(this, true);
            }
            return true;
        }


        // Verifica se o item pertence aos itens do menu principal
        switch (itemID) {
            case R.id.filtro_gastos_por_ano:
                startActivity(new Intent(AnotacoesSalvas.this, GastosPorAno.class));
                return true;
            case R.id.filtro_meus_cartoes_de_credito:
                startActivity(new Intent(AnotacoesSalvas.this, MeusCartoesDeCredito.class));
                return true;
            case R.id.filtro_minhas_categorias:
                startActivity(new Intent(AnotacoesSalvas.this, MinhasCategorias.class));
                return true;
            case R.id.filtro_selecao_moeda:
                startActivity(new Intent(AnotacoesSalvas.this, SelecaoMoeda.class));
                return true;
            default:
                break;
        }

        // Verifica se o item pertence ao submenu de Cartões
        if (itemID >= MENU_ID_CARTOES_BASE && itemID < MENU_ID_CATEGORIAS_BASE) {
            handleCartoesSubMenu(item);
            return true;
        }

        // Verifica se o item pertence ao submenu de Categorias
        if (itemID >= MENU_ID_CATEGORIAS_BASE) {
            handleCategoriasSubMenu(item);
            return true;
        }

        // Verifica se o item pertence aos outros submenus sem itens dinâmicos
        if (item == filtroDespesasFixasSubMenu.getItem()) {
            startActivity(new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroDespesasFixas.class));
            return true;
        }
        if (item == filtroReceitasSubMenu.getItem()) {
            startActivity(new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroReceita.class));
            return true;
        }
        if (item == filtroNaoPagosSubMenu.getItem()) {
            startActivity(new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroNaoPagos.class));
            return true;
        }
        if (item == filtroHojeSubMenu.getItem()) {
            startActivity(new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroHoje.class));
            return true;
        }

        if (item == filtroNext7DaysSubMenu.getItem()) {
            startActivity(new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroNext7Days.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Método para lidar com os itens do submenu de Cartões
    private void handleCartoesSubMenu(MenuItem item) {
        String nome_cartao = item.getTitle().toString();
        Intent intent11 = new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroCartao.class);
        intent11.putExtra("chave_string", nome_cartao);
        startActivity(intent11);
    }

    // Método para lidar com os itens do submenu de Categorias
    private void handleCategoriasSubMenu(MenuItem item) {
        String categoria = item.getTitle().toString();
        Intent intentCategorias = new Intent(AnotacoesSalvas.this, AnotacoesSalvasFiltroCategorias.class);
        intentCategorias.putExtra("chave_string", categoria);
        startActivity(intentCategorias);
    }


    private List<String> obterOpcoesDoBancoDeDados() {
        // Método de exemplo para obter opções do banco de dados
        MyDatabaseHelperCartoesCredito myDB_cartoes = new MyDatabaseHelperCartoesCredito(this);
        List<String> opcoesCartoesCredito = myDB_cartoes.getOptionsFromDatabase();

        return opcoesCartoesCredito;
    }

    private List<String> obterOpcoesDoBancoDeDados_categorias() {

        MyDatabaseHelperCategorias myDB_Categoria = new MyDatabaseHelperCategorias(this);
        List<String> opcoesCategorias = myDB_Categoria.getOptionsFromDatabase();

        return opcoesCategorias;
    }

    //--------------FIM DA CRIAÇÃO  DO MENU E AÇÃO DE CLIQUE ---------------//


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        myDB = new MyDatabaseHelper(this);
        myDB.deleteRowsWithNullOrEmptyPrices();

        // Verifique se a moeda já foi selecionada
        SharedPreferences preferences = getSharedPreferences("MeuAppPreferences", MODE_PRIVATE);
        String moedaSelecionada = preferences.getString("moedaSelecionada", null);

        if (moedaSelecionada == null) {
            // Se a moeda ainda não foi selecionada, inicie a tela de seleção de moeda
            startActivity(new Intent(this, SelecaoMoeda.class));
            finish();
        } else {
            // Se a moeda já foi selecionada anteriormente, continue normalmente
            // Restante do código da sua atividade principal...

            setContentView(R.layout.activity_anotacoes_salvas);

            // Encontre a referência para o LinearLayout que deseja ocultar
            linearLayoutParaOcultar = findViewById(R.id.linear_layout2);

            // Verifique o estado atual da visibilidade do LinearLayout nas preferências compartilhadas
            boolean isLinearLayoutVisible = PreferencesUtil.getLinearLayoutVisibility(this);

            // Configure a visibilidade do LinearLayout de acordo com o estado atual
            if (isLinearLayoutVisible) {
                linearLayoutParaOcultar.setVisibility(View.VISIBLE);
            } else {
                linearLayoutParaOcultar.setVisibility(View.GONE);
            }

            //Log.d("NotificationChecker", "Chamando a função scheduleNotificationCheck.");
            //scheduleNotificationCheck(this);

            // Inicializar o Firebase Analytics
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            if (!BuildConfig.DEBUG) {
                logPageViewEvent("AnotacoesSalvas");
            }

            MobileAds.initialize(this, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    // Após a inicialização bem-sucedida, carrega o anúncio
                    loadInterstitialAd();
                }
            });

            //String moedaAtual = PreferencesUtil.getMoedaSelecionada(context);

            txtMonth = findViewById(R.id.txtMonth);
            Button btnNext = findViewById(R.id.btnNext);
            Button btnBefore = findViewById(R.id.btnBefore);
            currentCalendar = Calendar.getInstance();
            recyclerView = findViewById(R.id.recyclerView);

            add_button = findViewById(R.id.add_button);
            add_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showNextPage();

                }
            });

            myDB = new MyDatabaseHelper(AnotacoesSalvas.this);

            // Obter a data atual
            Date currentDate = currentCalendar.getTime();
            int year = currentCalendar.get(Calendar.YEAR);
            int month = currentCalendar.get(Calendar.MONTH) + 1; // Os meses em Calendar são baseados em zero, então somamos 1

            // Formate o mês e o dia com dois dígitos
            String formattedMonth2 = String.format("%02d", month);
            year_monthh = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth2);

            SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
            String monthText = sdf.format(currentDate);

            //System.out.println("O valor é: " + sdf);
            //Toast.makeText(AnotacoesSalvas.this,"TESTANDO.",Toast.LENGTH_SHORT).show();
            //Toast.makeText(AnotacoesSalvas.this, "Variavel sdf é: " + sdf, Toast.LENGTH_SHORT).show();
            //Toast.makeText(AnotacoesSalvas.this, "Variavel sdf é: " + sdf, Toast.LENGTH_SHORT).show();
            //Toast.makeText(AnotacoesSalvas.this, "Variavel year_monthh eh: " + year_monthh, Toast.LENGTH_SHORT).show();
            //Toast.makeText(AnotacoesSalvas.this, "Variavel year eh: " + year, Toast.LENGTH_SHORT).show();
            //Toast.makeText(AnotacoesSalvas.this, "Variavel monthText eh: " + monthText, Toast.LENGTH_SHORT).show();
        /*/
        // Inserir o valor da variável em uma mensagem de log
        //Log.d("TAG", "O valor de year é " + year);
        //Log.d("TAG", "O valor de month é " + month);
        //Log.d("TAG", "O valor de year_monthh é " + year_monthh);
        //Log.d("TAG", "O valor de formattedMonth é " + formattedMonth2);
        //Log.d("TAG", "O valor de monthText é " + monthText);
        /*/

            // Converter a primeira letra para maiúscula
            monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
            txtMonth.setText(monthText);

            // Limpar os dados do adaptador, definindo uma lista vazia
            book_id = new ArrayList<>();
            book_title = new ArrayList<>();
            book_author = new ArrayList<>();
            book_pages = new ArrayList<>();
            book_date = new ArrayList<>();
            book_year_month = new ArrayList<>();
            book_categoria = new ArrayList<>();

            customAdapter = new CustomAdapter(AnotacoesSalvas.this,AnotacoesSalvas.this, book_id, book_title,book_author,book_pages,book_date,book_despesa_ou_receita,book_categoria,book_pago_ou_nao_pago,book_red_flag_notification);
            recyclerView.setAdapter(customAdapter);

            storeDataInArrays_using_filter(year_monthh);


            // Formate o mês e o dia com dois dígitos
            String formattedMonth4 = String.format("%02d", month);

            // Formatar a data no formato desejado (ano-mês)
            String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth4);

            //-----------------------------------------------------------------//

            double despesa_deste_mes_sem_cartao = myDB.sumPricesForYearMonthAndType(year_month, "DESPESA");
            double despesa_deste_mes_com_cartao = myDB.sumPricesForYearMonthAndType(year_month, "DESPESA COM CARTÃO");
            double despesa_deste_mes = despesa_deste_mes_sem_cartao + despesa_deste_mes_com_cartao;


            // CERTO
            NumberFormat numberFormat = NumberFormat.getNumberInstance();
            Context context = getApplicationContext();

            numberFormat.setMinimumFractionDigits(2);
            String formattedtotalSum10 = numberFormat.format(despesa_deste_mes);
            TextView resultTextView10 = findViewById(R.id.despesa_deste_mes);
            String textToDisplay10 = context.getString(R.string.translate_total_deste_mes_2);
            String concatenatedText10 = textToDisplay10 + " " + moedaSelecionada + " " + formattedtotalSum10;
            resultTextView10.setText(concatenatedText10);

            double receita_deste_mes = myDB.sumPricesForYearMonthAndType(year_month, "RECEITA");
            numberFormat.setMinimumFractionDigits(2);
            String formattedtotalSum11 = numberFormat.format(receita_deste_mes);
            TextView resultTextView11 = findViewById(R.id.receita_deste_mes);
            String textToDisplay11 = context.getString(R.string.translate_receita_deste_mes2);
            String concatenatedText11 = textToDisplay11 + " " + moedaSelecionada + " " + formattedtotalSum11;
            resultTextView11.setText(concatenatedText11);

            double saldo_liquido_deste_mes = receita_deste_mes - despesa_deste_mes;

            numberFormat.setMinimumFractionDigits(2);
            String formattedtotalSum12 = numberFormat.format(saldo_liquido_deste_mes);
            TextView resultTextView12 = findViewById(R.id.saldo_liquido_deste_mes);
            String textToDisplay12 = context.getString(R.string.translate_saldo_liquido_deste_mes2);
            String concatenatedText12 = textToDisplay12 + " " + moedaSelecionada + " " + formattedtotalSum12;
            resultTextView12.setText(concatenatedText12);


//SÓ DESCOMENTAR SE QUISER USAR
//            //------init ----- Defina a cor com base no valor do saldo, se positivo verde e se negativo vermelho
//            if (saldo_liquido_deste_mes >= 0) {
//                resultTextView3.setTextColor(ContextCompat.getColor(this, R.color.verde));
//            } else {
//                resultTextView3.setTextColor(ContextCompat.getColor(this, R.color.vermelho));
//            }
//            //------fim-----


            double despesa_ate_aqui_sem_cartao = myDB.somaPrecosAteAqui(year_month, "DESPESA");
            double despesa_ate_aqui_com_cartao = myDB.somaPrecosAteAqui(year_month, "DESPESA COM CARTÃO");
            double despesa_ate_aqui = despesa_ate_aqui_sem_cartao + despesa_ate_aqui_com_cartao;

            double receita_ate_aqui = myDB.somaPrecosAteAqui(year_month, "RECEITA");
            double totalSum2 = receita_ate_aqui - despesa_ate_aqui;


            numberFormat.setMinimumFractionDigits(2);
            String formattedtotalSum13 = numberFormat.format(totalSum2);
            TextView resultTextView13 = findViewById(R.id.resultTextView);
            String textToDisplay13 = context.getString(R.string.translate_total_ate_aqui_2);
            String concatenatedText13 = textToDisplay13 + " " + moedaSelecionada + " " + formattedtotalSum13;
            resultTextView13.setText(concatenatedText13);



//            //SÓ DESCOMENTAR SE QUISER USAR
//            //------init ----- Defina a cor com base no valor do saldo, se positivo verde e se negativo vermelho
//            if (totalSum2 >= 0) {
//                resultTextView4.setTextColor(ContextCompat.getColor(this, R.color.verde));
//            } else {
//                resultTextView4.setTextColor(ContextCompat.getColor(this, R.color.vermelho));
//            }
//            //------fim-----



            //-----------------------------------------------------------------//

            // customAdapter é responsável por mostrar no recyclerView os itens:
            customAdapter = new CustomAdapter(AnotacoesSalvas.this,AnotacoesSalvas.this, book_id, book_title,book_author,book_pages,book_date,book_despesa_ou_receita,book_categoria,book_pago_ou_nao_pago,book_red_flag_notification);
            recyclerView.setAdapter(customAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(AnotacoesSalvas.this));


            // Exemplo de como chamar a função expandBottomSheet
            findViewById(R.id.expandButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PieChartFragment pieChartFragment = new PieChartFragment();
                    // Configurar o mês antes de mostrar o fragmento
                    String selectedMonth = year_month; // Substitua isso pelo mês desejado
                    pieChartFragment.setMonth(selectedMonth);
                    pieChartFragment.show(getSupportFragmentManager(), pieChartFragment.getTag());
                }
            });




            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    currentCalendar.add(Calendar.MONTH, 1);
                    Date currentDate = currentCalendar.getTime();
                    int year = currentCalendar.get(Calendar.YEAR);
                    int month = currentCalendar.get(Calendar.MONTH) + 1; // Os meses em Calendar são baseados em zero, então somamos 1

                    // Formate o mês e o dia com dois dígitos
                    String formattedMonth4 = String.format("%02d", month);
                    String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth4);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                    String monthText = sdf.format(currentDate);


                    // Converter a primeira letra para maiúscula
                    monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
                    txtMonth.setText(monthText);

                    // Limpar os dados do adaptador, definindo uma lista vazia
                    book_id = new ArrayList<>();
                    book_title = new ArrayList<>();
                    book_author = new ArrayList<>();
                    book_pages = new ArrayList<>();
                    book_date = new ArrayList<>();
                    book_year_month = new ArrayList<>();
                    book_despesa_ou_receita = new ArrayList<>();
                    book_categoria = new ArrayList<>();

                    customAdapter = new CustomAdapter(AnotacoesSalvas.this,AnotacoesSalvas.this, book_id, book_title,book_author,book_pages,book_date,book_despesa_ou_receita,book_categoria,book_pago_ou_nao_pago,book_red_flag_notification);
                    recyclerView.setAdapter(customAdapter);

                    storeDataInArrays_using_filter(year_month);

                    //-----------------------------------------------------------------//


                    //-----------------------------------------------------------------//

                    double despesa_deste_mes_sem_cartao = myDB.sumPricesForYearMonthAndType(year_month, "DESPESA");
                    double despesa_deste_mes_com_cartao = myDB.sumPricesForYearMonthAndType(year_month, "DESPESA COM CARTÃO");
                    double despesa_deste_mes = despesa_deste_mes_sem_cartao + despesa_deste_mes_com_cartao;


                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    Context context = getApplicationContext();
                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum14 = numberFormat.format(despesa_deste_mes);
                    TextView resultTextView14 = findViewById(R.id.despesa_deste_mes);
                    String textToDisplay14 = context.getString(R.string.translate_total_deste_mes_2);
                    String concatenatedText14 = textToDisplay14 + " " + moedaSelecionada + " " + formattedtotalSum14;
                    resultTextView14.setText(concatenatedText14);


                    double receita_deste_mes = myDB.sumPricesForYearMonthAndType(year_month, "RECEITA");
                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum15 = numberFormat.format(receita_deste_mes);
                    TextView resultTextView15 = findViewById(R.id.receita_deste_mes);
                    String textToDisplay15 = context.getString(R.string.translate_receita_deste_mes2);
                    String concatenatedText15 = textToDisplay15 + " " + moedaSelecionada + " " + formattedtotalSum15;
                    resultTextView15.setText(concatenatedText15);


                    double saldo_liquido_deste_mes = receita_deste_mes - despesa_deste_mes;
                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum16 = numberFormat.format(saldo_liquido_deste_mes);
                    TextView resultTextView16 = findViewById(R.id.saldo_liquido_deste_mes);
                    String textToDisplay16 = context.getString(R.string.translate_saldo_liquido_deste_mes2);
                    String concatenatedText16 = textToDisplay16 + " " + moedaSelecionada + " " + formattedtotalSum16;
                    resultTextView16.setText(concatenatedText16);


                        //NO JEITO PARA USAR
//                    //------init ----- Defina a cor com base no valor do saldo, se positivo verde e se negativo vermelho
//                    if (saldo_liquido_deste_mes >= 0) {
//                        resultTextView3.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.verde));
//                    } else {
//                        resultTextView3.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.vermelho));
//                    }
//                    //------fim-----



                    double despesa_ate_aqui_sem_cartao = myDB.somaPrecosAteAqui(year_month, "DESPESA");
                    double despesa_ate_aqui_com_cartao = myDB.somaPrecosAteAqui(year_month, "DESPESA COM CARTÃO");
                    double despesa_ate_aqui = despesa_ate_aqui_sem_cartao + despesa_ate_aqui_com_cartao;
                    double receita_ate_aqui = myDB.somaPrecosAteAqui(year_month, "RECEITA");
                    double totalSum2 = receita_ate_aqui - despesa_ate_aqui;


                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum17 = numberFormat.format(totalSum2);
                    TextView resultTextView17 = findViewById(R.id.resultTextView);
                    String textToDisplay17 = context.getString(R.string.translate_total_ate_aqui_2);
                    String concatenatedText17 = textToDisplay17 + " " + moedaSelecionada + " " + formattedtotalSum17;
                    resultTextView17.setText(concatenatedText17);



//                    //NO JEITO PARA USAR
//                    //------init ----- Defina a cor com base no valor do saldo, se positivo verde e se negativo vermelho
//                    if (totalSum2 >= 0) {
//                        resultTextView4.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.verde));
//                    } else {
//                        resultTextView4.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.vermelho));
//                    }
//                    //------fim-----

                    //-----------------------------------------------------------------//



                    // Exemplo de como chamar a função expandBottomSheet
                    findViewById(R.id.expandButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PieChartFragment pieChartFragment = new PieChartFragment();
                            // Configurar o mês antes de mostrar o fragmento
                            String selectedMonth = year_month; // Substitua isso pelo mês desejado
                            pieChartFragment.setMonth(selectedMonth);
                            pieChartFragment.show(getSupportFragmentManager(), pieChartFragment.getTag());
                        }
                    });


                    //-----------------------------------------------------------------//
                    // customAdapter é responsável por mostrar no recyclerView os itens:
                    customAdapter = new CustomAdapter(AnotacoesSalvas.this,AnotacoesSalvas.this, book_id, book_title,book_author,book_pages,book_date, book_despesa_ou_receita, book_categoria,book_pago_ou_nao_pago,book_red_flag_notification);
                    recyclerView.setAdapter(customAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(AnotacoesSalvas.this));
                }
            });

            btnBefore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    currentCalendar.add(Calendar.MONTH, -1);
                    Date currentDate = currentCalendar.getTime();

                    int year = currentCalendar.get(Calendar.YEAR);
                    int month = currentCalendar.get(Calendar.MONTH) + 1;

                    // Formate o mês e o dia com dois dígitos
                    String formattedMonth5 = String.format("%02d", month);
                    String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth5);
                    SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                    String monthText = sdf.format(currentDate);
                    monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);

                    txtMonth.setText(monthText);

                    // Limpar os dados do adaptador, definindo uma lista vazia
                    book_id = new ArrayList<>();
                    book_title = new ArrayList<>();
                    book_author = new ArrayList<>();
                    book_pages = new ArrayList<>();
                    book_date = new ArrayList<>();
                    book_year_month = new ArrayList<>();
                    book_despesa_ou_receita = new ArrayList<>();
                    book_categoria = new ArrayList<>();



                    customAdapter = new CustomAdapter(AnotacoesSalvas.this,AnotacoesSalvas.this, book_id, book_title,book_author,book_pages,book_date, book_despesa_ou_receita, book_categoria,book_pago_ou_nao_pago,book_red_flag_notification);
                    recyclerView.setAdapter(customAdapter);

                    storeDataInArrays_using_filter(year_month);

                    //-----------------------------------------------------------------//

                    double despesa_deste_mes_sem_cartao = myDB.sumPricesForYearMonthAndType(year_month, "DESPESA");
                    double despesa_deste_mes_com_cartao = myDB.sumPricesForYearMonthAndType(year_month, "DESPESA COM CARTÃO");
                    double despesa_deste_mes = despesa_deste_mes_sem_cartao + despesa_deste_mes_com_cartao;

                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    Context context = getApplicationContext();

                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum20 = numberFormat.format(despesa_deste_mes);
                    TextView resultTextView20 = findViewById(R.id.despesa_deste_mes);
                    String textToDisplay20 = context.getString(R.string.translate_total_deste_mes_2);
                    String concatenatedText20 = textToDisplay20 + " " + moedaSelecionada + " " + formattedtotalSum20;
                    resultTextView20.setText(concatenatedText20);

                    double receita_deste_mes = myDB.sumPricesForYearMonthAndType(year_month, "RECEITA");
                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum21 = numberFormat.format(receita_deste_mes);
                    TextView resultTextView21 = findViewById(R.id.receita_deste_mes);
                    String textToDisplay21 = context.getString(R.string.translate_receita_deste_mes2);
                    String concatenatedText21 = textToDisplay21 + " " + moedaSelecionada + " " + formattedtotalSum21;
                    resultTextView21.setText(concatenatedText21);


                    double saldo_liquido_deste_mes = receita_deste_mes - despesa_deste_mes;
                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum22 = numberFormat.format(saldo_liquido_deste_mes);
                    TextView resultTextView22 = findViewById(R.id.saldo_liquido_deste_mes);
                    String textToDisplay22 = context.getString(R.string.translate_saldo_liquido_deste_mes2);
                    String concatenatedText22 = textToDisplay22 + " " + moedaSelecionada + " " + formattedtotalSum22;
                    resultTextView22.setText(concatenatedText22);


//                    //NO JEITO PARA USAR
//                    //------init ----- Defina a cor com base no valor do saldo, se positivo verde e se negativo vermelho
//                    if (saldo_liquido_deste_mes >= 0) {
//                        resultTextView3.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.verde));
//                    } else {
//                        resultTextView3.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.vermelho));
//                    }
//                    //------fim-----



                    double despesa_ate_aqui_sem_cartao = myDB.somaPrecosAteAqui(year_month, "DESPESA");
                    double despesa_ate_aqui_com_cartao = myDB.somaPrecosAteAqui(year_month, "DESPESA COM CARTÃO");
                    double despesa_ate_aqui = despesa_ate_aqui_sem_cartao + despesa_ate_aqui_com_cartao;
                    double receita_ate_aqui = myDB.somaPrecosAteAqui(year_month, "RECEITA");
                    double totalSum2 = receita_ate_aqui - despesa_ate_aqui;

                    numberFormat.setMinimumFractionDigits(2);
                    String formattedtotalSum23 = numberFormat.format(totalSum2);
                    TextView resultTextView23 = findViewById(R.id.resultTextView);
                    String textToDisplay23 = context.getString(R.string.translate_total_ate_aqui_2);
                    String concatenatedText23 = textToDisplay23 + " " + moedaSelecionada + " " + formattedtotalSum23;
                    resultTextView23.setText(concatenatedText23);


//                    //NO JEITO PARA USAR
//                    //------init ----- Defina a cor com base no valor do saldo, se positivo verde e se negativo vermelho
//                    if (totalSum2 >= 0) {
//                        resultTextView4.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.verde));
//                    } else {
//                        resultTextView4.setTextColor(ContextCompat.getColor(AnotacoesSalvas.this, R.color.vermelho));
//                    }
//                    //------fim-----

                    //-----------------------------------------------------------------//


                    // Exemplo de como chamar a função expandBottomSheet
                    findViewById(R.id.expandButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PieChartFragment pieChartFragment = new PieChartFragment();
                            // Configurar o mês antes de mostrar o fragmento
                            String selectedMonth = year_month; // Substitua isso pelo mês desejado
                            pieChartFragment.setMonth(selectedMonth);
                            pieChartFragment.show(getSupportFragmentManager(), pieChartFragment.getTag());
                        }
                    });



                    //-----------------------------------------------------------------//
                    // customAdapter é responsável por mostrar no recyclerView os itens:
                    customAdapter = new CustomAdapter(AnotacoesSalvas.this,AnotacoesSalvas.this, book_id, book_title,book_author,book_pages,book_date, book_despesa_ou_receita, book_categoria,book_pago_ou_nao_pago,book_red_flag_notification);
                    recyclerView.setAdapter(customAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(AnotacoesSalvas.this));
                }
            });





        }

        // -------------FIM-------------------------------
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

    void storeDataInArrays_using_filter(String year_month){

        Cursor cursor = myDB.readDataFilter(year_month);

        try {
            // Limpar os dados do adaptador, definindo uma lista vazia
            book_id = new ArrayList<>();
            book_title = new ArrayList<>();
            book_author = new ArrayList<>();
            book_pages = new ArrayList<>();
            book_date = new ArrayList<>();
            book_year_month = new ArrayList<>();
            book_despesa_ou_receita = new ArrayList<>();
            book_categoria = new ArrayList<>();
            book_pago_ou_nao_pago = new ArrayList<>();
            book_red_flag_notification = new ArrayList<>();

            if(cursor.getCount() == 0 ){
                //Toast.makeText(this,"no data.",Toast.LENGTH_SHORT).show();
            } else {
                while(cursor.moveToNext()){
                    book_id.add(cursor.getString(0)); // Vai adicionando ao Array  book_id = new ArrayList<>();
                    book_title.add(cursor.getString(1));
                    book_author.add(cursor.getString(2));
                    book_pages.add(cursor.getString(3));
                    book_date.add(cursor.getString(4));
                    book_despesa_ou_receita.add(cursor.getString(8));
                    book_categoria.add(cursor.getString(10));
                    book_pago_ou_nao_pago.add(cursor.getString(11));
                    book_red_flag_notification.add(cursor.getString(13));
                }
            }
        } finally {
            // Certifique-se de fechar o cursor antes de sair do método
            if (cursor != null) {
                cursor.close();
            }
            // Fechar o banco de dados
            if (myDB != null) {
                myDB.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Realize as ações necessárias para atualizar a tela antes de chamar super.onBackPressed()
        // Por exemplo, você pode chamar o método que atualiza os dados na RecyclerView.
        finishAffinity();
        // Chame o método super.onBackPressed() para continuar com o comportamento padrão.
        super.onBackPressed();
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        String adUnitId = BuildConfig.AD_UNIT_ID; // Pega o ID do anúncio conforme o build

        // Exibir logs apenas no modo debug
        if (BuildConfig.DEBUG) {
            Log.d("AdMob", "Ad Unit ID usado: " + adUnitId);
        }

        InterstitialAd.load(this, adUnitId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    private void showNextPage() {

        Intent intent = new Intent(this, AddActivity.class);
        startActivity(intent);

        if (mInterstitialAd != null) {

            mInterstitialAd.show(this);

            if (!BuildConfig.DEBUG) {
                logFirebaseEvent("visualizou_intersticial");
            }
            mInterstitialAd = null;
        }
    }

    private void scheduleNotificationCheck(Context context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG);

        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .build();

        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                NotificationCheckerWorker.class,
                4,
                TimeUnit.HOURS
        )
                .setInitialDelay(50, TimeUnit.MINUTES)
                .addTag(WORK_TAG)
                .setConstraints(constraints) // Adicionando as restrições
                .build();

        WorkManager.getInstance(context).enqueue(workRequest);
    }


    private void logFirebaseEvent(String eventName) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent(eventName, null);  // Sem dados extras
        } else {
            Log.e("Firebase Analytics", "Firebase Analytics não inicializado corretamente.");
        }
    }


    // Método para limpar o cache
    private void clearCache() {
        try {
            File cacheDir = getCacheDir();
            if (cacheDir != null && cacheDir.isDirectory()) {
                deleteDir(cacheDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Função auxiliar para deletar arquivos e diretórios
    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

} // public class AnotacoesSalvas extends AppCompatActivity








