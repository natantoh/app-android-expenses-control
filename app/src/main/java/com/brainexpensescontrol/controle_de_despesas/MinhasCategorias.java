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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import android.app.Application;
import android.content.Context;
public class MinhasCategorias extends AppCompatActivity {

    RecyclerView recyclerViewMinhasCategorias1;
    FloatingActionButton add_button_minhas_categorias1;
    MyDatabaseHelperCategorias myDB_Categoria;
    private Context context;
    private FirebaseAnalytics mFirebaseAnalytics;
    private InterstitialAd mInterstitialAd;
    ArrayList<String> array_linha_id, array_nome_da_categoria;
    CustomAdapterMinhasCategorias CustomAdapterMinhasCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minhas_categorias);

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("MinhasCategorias");
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                // Após a inicialização bem-sucedida, carrega o anúncio
                loadInterstitialAd();
            }
        });

        context = this;
        recyclerViewMinhasCategorias1 = findViewById(R.id.recyclerViewMinhasCategorias1);
        add_button_minhas_categorias1 = findViewById(R.id.add_button_minhas_categorias1);

        add_button_minhas_categorias1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showNextPage();
            }
        });

        myDB_Categoria = new MyDatabaseHelperCategorias(MinhasCategorias.this);

        // Limpar os dados do adaptador, definindo uma lista vazia
        array_linha_id = new ArrayList<>();
        array_nome_da_categoria = new ArrayList<>();

        CustomAdapterMinhasCategorias = new CustomAdapterMinhasCategorias(MinhasCategorias.this, MinhasCategorias.this, array_linha_id, array_nome_da_categoria);
        recyclerViewMinhasCategorias1.setAdapter(CustomAdapterMinhasCategorias);

        storeDataInArrays();

        // customAdapter é responsável por mostrar no recyclerView os itens:
        CustomAdapterMinhasCategorias = new CustomAdapterMinhasCategorias(MinhasCategorias.this, MinhasCategorias.this, array_linha_id, array_nome_da_categoria);

        recyclerViewMinhasCategorias1.setAdapter(CustomAdapterMinhasCategorias);
        recyclerViewMinhasCategorias1.setLayoutManager(new LinearLayoutManager(MinhasCategorias.this));

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
            cursor = myDB_Categoria.readAllData();
            if(cursor.getCount() == 0 ){
                Toast.makeText(this,"No Categories Added.",Toast.LENGTH_SHORT).show();
            } else {
                while(cursor.moveToNext()){
                    array_linha_id.add(cursor.getString(0)); // Vai adicionando ao Array  book_id = new ArrayList<>();
                    array_nome_da_categoria.add(cursor.getString(1));
                }
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close(); // Fechando o cursor
            }
            myDB_Categoria.close(); // Fechando o banco de dados
        }
    }

    @Override
    public void onBackPressed() {
        updateRecyclerView();
        super.onBackPressed();
    }

    private void updateRecyclerView() {
        Intent intent43 = new Intent(MinhasCategorias.this, AnotacoesSalvas.class);
        startActivity(intent43);
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

        Intent intent = new Intent(MinhasCategorias.this, AddCategoria.class);
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


}















