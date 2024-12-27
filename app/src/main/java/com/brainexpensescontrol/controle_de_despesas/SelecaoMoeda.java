package com.brainexpensescontrol.controle_de_despesas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.brainexpensescontrol.controle_de_despesas.AnotacoesSalvas;
import com.brainexpensescontrol.controle_de_despesas.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appcheck.AppCheckToken;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;

import java.util.ArrayList;
import java.util.List;



public class SelecaoMoeda extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selecao_moeda);



        // ---- INIT Firebase App Check
        // Inicializa o Firebase no contexto atual do aplicativo
        FirebaseApp.initializeApp(/*context=*/ this);

        // Obtém a instância do FirebaseAppCheck
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();

        // Instala o provedor de verificação de integridade do Play
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        // ---- END Firebase App Check














        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        if (!BuildConfig.DEBUG) {
            logPageViewEvent("SelecaoMoeda");
        }

        // Lista de moedas disponíveis
        List<String> moedas = new ArrayList<>();

        moedas.add("$"); // "US" // Estados Unidos - TRADUÇÃO OK
        moedas.add("€"); // "DE" // Alemanha  - REALIZAR TRADUÇÃO.
        moedas.add("₹"); // "IN"   // Índia
        moedas.add("R$"); // "BR" // Brasil
        moedas.add("£"); //"UK" Reino Unido - Libra esterlina
        moedas.add("¥"); // "JP" // Japão e China
        moedas.add("₩"); // Coreia do Sul (KR)
        moedas.add("kr"); // Noruega (NO) e Dinamarca (DK)
        moedas.add("kn"); // Croácia (HR)
        moedas.add("C$"); // "CA" Canadá
        moedas.add("R"); // "ZA" África do Sul
        moedas.add("Rp"); // Indonésia (ID)
        moedas.add("Ft"); // Hungria (HU)
        moedas.add("₱"); // Filipinas (PH)
        moedas.add("Kz"); // "AO" // Angola
        moedas.add("EC$"); //"AG"  // Antígua e Barbuda  "ATG"   Dólar do Caribe Oriental (XCD)
        moedas.add("֏"); // "AM" // Armênia. Portanto, é o símbolo do dram armênio (AMD), a moeda oficial da Armênia.
        moedas.add("Afl."); // "AW" // Aruba Florim Arubano (AWG), e o símbolo é ƒ ou Afl.
        moedas.add("ƒ"); // "AW" // Aruba Florim Arubano (AWG), e o símbolo é ƒ ou Afl. Usado também em curacao
        moedas.add("₼"); // Azerbaijão (AZ)
        moedas.add("৳"); // Bangladesh (BD)
        moedas.add("Br"); // Bielorrússia (BY)
        moedas.add("P"); // Botsuana (BW) e Macau (MO)
        moedas.add("лв"); // Bulgária (BG)
        moedas.add("KM"); // Bósnia e Herzegovina (BA)
        moedas.add("CFA"); // "CM" Camarões (XAF) Moeda é o Franco CFA.  XAF ou "CFA". e "CI" Costa do Marfim (XOF), "ML" Mali (XOF) ,
        moedas.add("XAF"); // "CM" Camarões (XAF) Moeda é o Franco CFA.  XAF ou "CFA"
        moedas.add("XOF"); // "CI" Costa do Marfim (XOF)
        moedas.add("៛"); // Camboja (KH)
        moedas.add("CF"); // Comores (KM)
        moedas.add("₡"); // Costa Rica (CR)
        moedas.add("Fdj"); // Djibuti (DJ)
        moedas.add("E£"); // Egito (EG)
        moedas.add("Nfk"); // Eritreia (ER)
        moedas.add("¢"); // Gana (GH)
        moedas.add("₾"); // Geórgia (GE)
        moedas.add("Q"); // Guatemala (GT)
        moedas.add("FG"); // Guiné (GN)
        moedas.add("D"); // Gâmbia (GM)
        moedas.add("L"); // Honduras (HN) e Moldávia (MD)
        moedas.add("₪"); // Israel (IL)
        moedas.add("₭"); // Laos (LA)
        moedas.add("CHF"); // Liechtenstein (LI)
        moedas.add("ل.ل"); // Líbano (LB)
        moedas.add("ل.د"); // Líbia (LY)
        moedas.add("ден"); // Macedônia do Norte (MK)
        moedas.add("ރ."); // Maldivas (MV)
        moedas.add("RM");
        moedas.add("ر.س"); // Arábia Saudita (SA)
        moedas.add("د.إ"); // Emirados Árabes Unidos (AE)
        moedas.add("دج"); // "DZ" // Argelia símbolo do Dinar Argelino é "د.ج"
        moedas.add("ر.ق"); // Catar (QA)
        moedas.add("₸"); // Cazaquistão (KZ)
        moedas.add("﷼"); // Iêmen (YE) e Irã (IR)
        moedas.add("د.ا"); // Jordânia (JO)
        moedas.add("د.ك"); // Kuwait (KW)
        moedas.add("د.م."); // Marrocos (MA)
        moedas.add("ع.د"); // Iraque (IQ)
        moedas.add("₨"); // Maurícia (MU)
        moedas.add("K"); // Mianmar (MM)
        moedas.add("MT");
        moedas.add("रू");
        moedas.add("₦");
        moedas.add("₴");
        moedas.add("USh");
        moedas.add("сўм");
        moedas.add("Vt");
        moedas.add("Bs");
        moedas.add("₫");
        moedas.add("Z$");    // Zimbábue
        moedas.add("ZK");
        moedas.add("G");     // Haiti (HT)
        moedas.add("FC");   // República Democrática do Congo (CD)


        // Spinner para selecionar a moeda
        Spinner spinnerMoedas = findViewById(R.id.spinnerMoedas);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, moedas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMoedas.setAdapter(adapter);

        // Botão para confirmar a seleção
        Button btnConfirmar = findViewById(R.id.btnConfirmar);
        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Salve a moeda selecionada nas preferências compartilhadas
                SharedPreferences preferences = getSharedPreferences("MeuAppPreferences", MODE_PRIVATE);
                String moedaSelecionada = moedas.get(spinnerMoedas.getSelectedItemPosition());
                preferences.edit().putString("moedaSelecionada", moedaSelecionada).apply();

                startActivity(new Intent(SelecaoMoeda.this, AnotacoesSalvas.class));
                finish();
            }
        });
    }
    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }







}