package com.brainexpensescontrol.controle_de_despesas;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class UpdateCategoria extends AppCompatActivity {

    EditText titulo_da_categoria_a_ser_atualizada;
    Button botao_atualizar_categoria,botao_deletar_categoria;
    String id, title;

    MyDatabaseHelperCategorias myDB_Categoria;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_categoria);

        // INIT - ANUNCIO DO BANNER
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // END - ANUNCIO DO BANNER

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("UpdateCategoria");
        }

        titulo_da_categoria_a_ser_atualizada = findViewById(R.id.titulo_da_categoria_a_ser_atualizada);
        botao_atualizar_categoria = findViewById(R.id.botao_atualizar_categoria);
        botao_deletar_categoria = findViewById(R.id.botao_deletar_categoria);

        // Primeiro chamamos isso
        getAndSetIntentData();

        titulo_da_categoria_a_ser_atualizada.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método é chamado antes de o texto ser alterado.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método é chamado quando o texto está sendo alterado.
            }

            @Override
            public void afterTextChanged(Editable s) {
                String inputText = s.toString();
                String capitalizedText = inputText.toUpperCase();

                if (!inputText.equals(capitalizedText)) {
                    // Evita recursão infinita ao definir o texto capitalizado
                    titulo_da_categoria_a_ser_atualizada.setText(capitalizedText);
                    titulo_da_categoria_a_ser_atualizada.setSelection(capitalizedText.length());
                }
            }
        });

        //Definir o título da barra de ação após o método getAndSetIntentData
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.translate_dados_da_categoria));  // Usando a string do strings.xml
        }

        botao_atualizar_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDatabaseHelperCategorias myDB_Categoria = new MyDatabaseHelperCategorias(UpdateCategoria.this);

                title = titulo_da_categoria_a_ser_atualizada.getText().toString().trim();

                // Verifique se o título ou descrição não são vazios
                if (title.isEmpty() ) {
                    String textToDisplay_tit_desc = UpdateCategoria.this.getString(R.string.translate_preencha_titulo);
                    Toast.makeText(UpdateCategoria.this,textToDisplay_tit_desc,Toast.LENGTH_SHORT).show();
                }
                else {
                    myDB_Categoria.updateData(id,
                            title
                    );

                    if (!BuildConfig.DEBUG) {
                        logFirebaseEvent("atualizou_categoria");
                    }

                    Intent intent = new Intent(UpdateCategoria.this,MinhasCategorias.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        botao_deletar_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
    }

    void getAndSetIntentData(){
        if( getIntent().hasExtra("id") && getIntent().hasExtra("title") ){

            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");

            titulo_da_categoria_a_ser_atualizada.setText(title);

        }else{
            Toast.makeText(this, "No data.", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("DELETE");

        Context context = getApplicationContext();
        String textToDisplay = context.getString(R.string.translate_esta_certo_que_voce_quer_deletar);
        builder.setMessage(textToDisplay);


        Context context_yes = getApplicationContext();
        String textToDisplay_yes = context_yes.getString(R.string.translate_sim);

        builder.setPositiveButton(textToDisplay_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                MyDatabaseHelperCategorias myDB_Categoria = new MyDatabaseHelperCategorias(UpdateCategoria.this);
                myDB_Categoria.deleteOneRow(id);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("deletou_categoria");
                }
                finish();
            }
        });

        Context context_not = getApplicationContext();
        String textToDisplay_not = context_not.getString(R.string.translate_nao);

        builder.setNegativeButton(textToDisplay_not, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();
    }

    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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