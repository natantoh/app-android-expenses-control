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
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import android.app.Application;
import android.content.Context;
public class MeusCartoesDeCredito extends AppCompatActivity {

    RecyclerView recyclerView3;
    FloatingActionButton add_button;
    MyDatabaseHelperCartoesCredito myDBC;
    private Context context;
    private InterstitialAd mInterstitialAd;
    ArrayList<String> array_linha_id, array_nome_do_cartao ,array_limite_do_cartao,array_limite_utilizado_do_cartao,array_limite_disponivel_do_cartao,array_data_venc_cartao;
    CustomAdapterCartaoCredito customAdapter54;
    private Calendar currentCalendar;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_cartoes_de_credito);

        // Deleta linhas vazias ou null ( limite cartao for null ou vazio )
        myDBC = new MyDatabaseHelperCartoesCredito(this);
        myDBC.deleteRowsWithNullOrEmptyPrices();

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("MeusCartoesDeCredito");
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Após a inicialização bem-sucedida, carrega o anúncio
                loadInterstitialAd();
            }
        });

        context = this;
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context);

        //Toast.makeText(this," Cadastre uma forma de pagamento em que deseja filtrar.",Toast.LENGTH_SHORT).show();

        currentCalendar = Calendar.getInstance();
        recyclerView3 = findViewById(R.id.recyclerView3);
        add_button = findViewById(R.id.add_button);

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNextPage();
            }
        });

        myDBC = new MyDatabaseHelperCartoesCredito(MeusCartoesDeCredito.this);

        // Limpar os dados do adaptador, definindo uma lista vazia
        array_linha_id = new ArrayList<>();
        array_nome_do_cartao = new ArrayList<>();
        array_limite_do_cartao = new ArrayList<>();
        array_limite_utilizado_do_cartao = new ArrayList<>();
        array_limite_disponivel_do_cartao = new ArrayList<>();
        array_data_venc_cartao = new ArrayList<>();

        customAdapter54 = new CustomAdapterCartaoCredito(MeusCartoesDeCredito.this, MeusCartoesDeCredito.this, array_linha_id, array_nome_do_cartao,array_limite_do_cartao, array_limite_utilizado_do_cartao, array_limite_disponivel_do_cartao, array_data_venc_cartao);
        recyclerView3.setAdapter(customAdapter54);

        storeDataInArrays();

        // customAdapter é responsável por mostrar no recyclerView os itens:
        customAdapter54 = new CustomAdapterCartaoCredito(MeusCartoesDeCredito.this, MeusCartoesDeCredito.this, array_linha_id, array_nome_do_cartao,array_limite_do_cartao, array_limite_utilizado_do_cartao, array_limite_disponivel_do_cartao, array_data_venc_cartao);

        recyclerView3.setAdapter(customAdapter54);
        recyclerView3.setLayoutManager(new LinearLayoutManager(MeusCartoesDeCredito.this));

        ////--------------INIT------

        double limite_total_todos_cartoes = myDBC.calculaSomaDosItensDaColuna("limite_do_cartao");

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        Context context = getApplicationContext();

        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum20 = numberFormat.format(limite_total_todos_cartoes);
        TextView resultTextView20 = findViewById(R.id.limite_total_cartoes);
        String textToDisplay20 = context.getString(R.string.translate_limite_total_todos_cartoes_2);
        String concatenatedText20 = textToDisplay20 + " " + moedaSelecionada + " " + formattedtotalSum20;
        resultTextView20.setText(concatenatedText20);

        double limite_utilizado_todos_cartoes = myDBC.calculaSomaDosItensDaColuna("limite_utilizado_do_cartao");
        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum21 = numberFormat.format(limite_utilizado_todos_cartoes);
        TextView resultTextView21 = findViewById(R.id.total_utilizado_todos_cartoes);
        String textToDisplay21 = context.getString(R.string.translate_limite_total_utilizado_todos_cartoes2);
        String concatenatedText21 = textToDisplay21 + " " + moedaSelecionada + " " + formattedtotalSum21;
        resultTextView21.setText(concatenatedText21);

        double limite_disponivel_todos_cartoes = myDBC.calculaSomaDosItensDaColuna("limite_disponivel_do_cartao");
        numberFormat.setMinimumFractionDigits(2);
        String formattedtotalSum22 = numberFormat.format(limite_disponivel_todos_cartoes);
        TextView resultTextView22 = findViewById(R.id.limite_disponível_total);
        String textToDisplay22 = context.getString(R.string.translate_limite_total_disponivel_todos_cartoes2);
        String concatenatedText22 = textToDisplay22 + " " + moedaSelecionada + " " + formattedtotalSum22;
        resultTextView22.setText(concatenatedText22);
        ////-------------FIM---------

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

    void storeDataInArrays(){
        Cursor cursor = null;
        try {
            cursor = myDBC.readAllData();
            if(cursor.getCount() == 0 ){
                String textToDisplay_tit2 = this.getString(R.string.translate_nenhum_cartao_adicionado);
                Toast.makeText(this, textToDisplay_tit2, Toast.LENGTH_SHORT).show();
            } else {
                while(cursor.moveToNext()){
                    array_linha_id.add(cursor.getString(0)); // Vai adicionando ao Array  book_id = new ArrayList<>();
                    array_nome_do_cartao.add(cursor.getString(1));
                    array_limite_do_cartao.add(cursor.getString(2));
                    array_limite_utilizado_do_cartao.add(cursor.getString(3));
                    array_limite_disponivel_do_cartao.add(cursor.getString(4));
                    array_data_venc_cartao.add(cursor.getString(5));
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (myDBC != null) {
                myDBC.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent122 = new Intent(MeusCartoesDeCredito.this, AnotacoesSalvas.class);
        startActivity(intent122);
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

        Intent intent = new Intent(MeusCartoesDeCredito.this, AddCartaoCredito.class);
        startActivity(intent);
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);

            if (!BuildConfig.DEBUG) {
                logFirebaseEvent("visualizou_intersticial");
            }

            mInterstitialAd = null;
        }
    }



    private void logFirebaseEvent(String eventName) {
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (mFirebaseAnalytics != null) {
            mFirebaseAnalytics.logEvent(eventName, null);  // Sem dados extras
        } else {
            Log.e("Firebase Analytics", "Firebase Analytics não inicializado corretamente.");
        }
    }


} // public class AnotacoesSalvas extends AppCompatActivity















