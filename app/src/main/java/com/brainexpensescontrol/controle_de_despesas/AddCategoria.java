package com.brainexpensescontrol.controle_de_despesas;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AddCategoria extends AppCompatActivity {

    EditText titulo_da_categoria_a_ser_adicionada;  // mudar de title_input para titulo_da_categoria_a_ser_adicionada
    Button botao_adicionar_categoria;    // mudar add_button para botao_adicionar_categoria

    private Context context;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_categoria);

        // INIT - ANUNCIO DO BANNER
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // END - ANUNCIO DO BANNER

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("AddCategoria");
        }

        titulo_da_categoria_a_ser_adicionada = findViewById(R.id.titulo_da_categoria_a_ser_adicionada);

        titulo_da_categoria_a_ser_adicionada.addTextChangedListener(new TextWatcher() {
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
                    titulo_da_categoria_a_ser_adicionada.setText(capitalizedText);
                    titulo_da_categoria_a_ser_adicionada.setSelection(capitalizedText.length());
                }
            }
        });




        botao_adicionar_categoria = findViewById(R.id.botao_adicionar_categoria);
        botao_adicionar_categoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDatabaseHelperCategorias myDB_Categoria = new MyDatabaseHelperCategorias(AddCategoria.this);

                String nome_da_categoria = titulo_da_categoria_a_ser_adicionada.getText().toString().trim();

                // Verifique se o título está vazio
                if (nome_da_categoria.isEmpty()) {

                    // título está vazio
                    String textToDisplay_tit = AddCategoria.this.getString(R.string.translate_preencha_titulo);
                    Toast.makeText(AddCategoria.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                }
                else {
                    myDB_Categoria.addBook( nome_da_categoria );

                    if (!BuildConfig.DEBUG) {
                        logFirebaseEvent("adicionou_cartegoria");
                    }

                    Intent intent = new Intent(AddCategoria.this, MinhasCategorias.class);
                    startActivity(intent);
                    finish();
                    // Chame finishAffinity() para encerrar a atividade atual e todas as atividades acima dela
                    //finishAffinity();
                }
            }
        });
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









