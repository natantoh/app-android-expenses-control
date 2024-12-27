package com.brainexpensescontrol.controle_de_despesas;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

public class UpdateCartaoCredito extends AppCompatActivity {
    EditText title_input,prices_input;
    Button update_button,delete_button;
    TextView textViewSelectedDate;
    final Calendar calendar = Calendar.getInstance();
    String id, title, prices,date;
    private String year_month,selectedDate,authorOld,authorNew;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_cartao_credito);

        // INIT - ANUNCIO DO BANNER
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // END - ANUNCIO DO BANNER

        // Inicializar o Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("UpdateCartaoCredito");
        }

        title_input = findViewById(R.id.title_input);
        prices_input = findViewById(R.id.prices_input);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);

        //calendarView = findViewById(R.id.calendarView4);

//        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                // Ação a ser executada quando o usuário selecionar uma data no calendário
//                String formattedMonth = String.format("%02d", month + 1); // Formata o mês com dois dígitos
//                String formattedDay = String.format("%02d", dayOfMonth); // Formata o dia com dois dígitos
//                selectedDate = year + "-" + formattedMonth + "-" + formattedDay;
//                year_month = year + "-" + formattedMonth;
//                // Chame um método para inserir 'selectedDate' no banco de dados SQLite
//            }
//        });

        // Primeiro chamamos isso
        getAndSetIntentData();

        try {
            SimpleDateFormat intentDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = intentDateFormat.parse(selectedDate);  // Converter para Date
            calendar.setTime(date);  // Definir a data no calendário
            // Formatar e exibir a data no formato DD/MMM/YYYY
            SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
            textViewSelectedDate.setText(displayDateFormat.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Ao clicar no TextView, abrir o DatePickerDialog para alterar a data
        textViewSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter o ano, mês e dia atuais do calendário
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Criar e mostrar o DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        new ContextThemeWrapper(UpdateCartaoCredito.this, R.style.CustomDatePickerDialog),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Formatar a data e atualizar as variáveis
                                String formattedMonth = String.format("%02d", selectedMonth + 1);  // Mês é zero-indexado
                                String formattedDay = String.format("%02d", selectedDay);

                                selectedDate = selectedYear + "-" + formattedMonth + "-" + formattedDay;
                                year_month = selectedYear + "-" + formattedMonth;

                                // Atualizar o calendário com a nova data
                                calendar.set(Calendar.YEAR, selectedYear);
                                calendar.set(Calendar.MONTH, selectedMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                                // Atualizar o TextView com a nova data no formato DD/MMM/YYYY
                                SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
                                textViewSelectedDate.setText(displayDateFormat.format(calendar.getTime()));
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });


        title_input.addTextChangedListener(new TextWatcher() {
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
                StringBuilder formattedText = new StringBuilder();
                boolean capitalizeNext = true;

                for (char c : inputText.toCharArray()) {
                    if (Character.isWhitespace(c)) {
                        capitalizeNext = true;
                        formattedText.append(c);
                    } else if (capitalizeNext) {
                        formattedText.append(Character.toUpperCase(c));
                        capitalizeNext = false;
                    } else {
                        formattedText.append(c);
                    }
                }

                if (!inputText.equals(formattedText.toString())) {
                    // Evita recursão infinita ao definir o texto capitalizado
                    title_input.setText(formattedText.toString());
                    title_input.setSelection(formattedText.length());
                }
            }

        });

        //Definir o título da barra de ação após o método getAndSetIntentData
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.translate_dados_do_cartao));  // Usando a string do strings.xml
        }

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelperCartoesCredito dbHelper = new MyDatabaseHelperCartoesCredito(UpdateCartaoCredito.this);

                if (selectedDate == null || selectedDate.isEmpty()) {
                    // Obtenha o ano, mês e dia da currentDate.
//                    Calendar calendar = Calendar.getInstance();
//
//                    int year = calendar.get(Calendar.YEAR);
//                    int month = calendar.get(Calendar.MONTH) + 1; // Note que os meses são base 0, então adicionamos 1.
//                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                    // Formate o mês e o dia com dois dígitos
//                    String formattedMonth = String.format("%02d", month);
//                    String formattedDayOfMonth = String.format("%02d", dayOfMonth);
//                    // Atualize selectedDate e year_month
//                    selectedDate = year + "-" + formattedMonth + "-" + formattedDayOfMonth;
//                    year_month = year + "-" + formattedMonth;

                    String textToDisplay_data = UpdateCartaoCredito.this.getString(R.string.translate_selecione_o_vencimento);
                    Toast.makeText(UpdateCartaoCredito.this, textToDisplay_data, Toast.LENGTH_SHORT).show();
                    return;

                }

                title = title_input.getText().toString().trim();

                //---------INICIO FORMATAÇÃO DOS VALORES-----------------------------------------------------------------
                String prices_Formatado_virgula = prices_input.getText().toString();

                //---------FIM FORMATAÇÃO DOS VALORES-----------------------------------------------------------------

                String year2 = year_month.substring(0, 4);

                if (title.isEmpty() || title.equals(".") ) {

                    String textToDisplay_tit_desc = UpdateCartaoCredito.this.getString(R.string.translate_preencha_titulo);
                    Toast.makeText(UpdateCartaoCredito.this,textToDisplay_tit_desc,Toast.LENGTH_SHORT).show();

                } else if ( prices_Formatado_virgula.isEmpty() || prices_Formatado_virgula.equals(".") || Double.parseDouble(prices_Formatado_virgula) == 0.0 ) {

                    String textToDisplay_price = UpdateCartaoCredito.this.getString(R.string.translate_preco_nao_pode_ser_vazio_ou_zero);
                    Toast.makeText(UpdateCartaoCredito.this,textToDisplay_price,Toast.LENGTH_SHORT).show();

                } else if ( !isValidNumber(prices_Formatado_virgula) ) {

                    String textToDisplay_invalid = UpdateCartaoCredito.this.getString(R.string.translate_o_numero_invalido);
                    Toast.makeText(UpdateCartaoCredito.this, textToDisplay_invalid, Toast.LENGTH_SHORT).show();

                } else {
                    long result = dbHelper.updateData(id,
                            title,
                            prices_Formatado_virgula,
                            selectedDate
                    );

                    if (result != -1) {

                        MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateCartaoCredito.this);

                        authorNew = title;
                        myDB.updateAuthor(authorOld, authorNew);

                        double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(title, "NAO_PAGO");
                        String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                        MyDatabaseHelperCartoesCredito dbHelper2 = new MyDatabaseHelperCartoesCredito(UpdateCartaoCredito.this);
                        dbHelper2.atualizarLimiteUtilizado(title, string_despesa_total_do_cartao_nao_paga);
                        double limite_do_cartao = dbHelper2.obterLimiteDoCartao(title);
                        double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                        String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                        dbHelper2.atualizarLimiteDisponivel(title, string_limite_disponivel_do_cartao);

                        if (!BuildConfig.DEBUG) {
                            logFirebaseEvent("atualizou_cartao_de_credito");
                        }
                    }
                }

                Intent intent = new Intent(UpdateCartaoCredito.this,MeusCartoesDeCredito.class);
                startActivity(intent);
                finish();
            }
        });

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });
    }

    void getAndSetIntentData(){
        if( getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("prices") && getIntent().hasExtra("book_date") ){

            // Getting Data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            prices = getIntent().getStringExtra("prices");
            selectedDate = getIntent().getStringExtra("book_date");

            authorOld = title;

            title_input.setText(title);
            prices_input.setText(String.valueOf(prices)); // Certifique-se de que esta linha esteja correta

            String[] dateParts = selectedDate.split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]) - 1; // Lembre-se que o mês em Java começa em 0 (janeiro é 0)
            int day = Integer.parseInt(dateParts[2]);

            int month2 = month + 1;
            String formattedMonth = String.format("%02d", month2);
            year_month = year + "-" + formattedMonth;
            //calendarView = findViewById(R.id.calendarView4);

            // Criar um objeto Calendar e definir a data nele
            //Calendar calendar = Calendar.getInstance();
            //calendar.set(year, month, day);

            // Definir a data no calendarView
            //calendarView.setDate(calendar.getTimeInMillis());

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
                MyDatabaseHelperCartoesCredito dbHelper = new MyDatabaseHelperCartoesCredito(UpdateCartaoCredito.this);
                dbHelper.deleteOneRow(id);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("deletou_cartao_de_credito");
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

    @Override
    public void onBackPressed() {
        Intent intent89 = new Intent(UpdateCartaoCredito.this, MeusCartoesDeCredito.class);
        startActivity(intent89);
        super.onBackPressed();
    }
    private boolean isValidNumber(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
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
