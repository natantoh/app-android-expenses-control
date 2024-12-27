package com.brainexpensescontrol.controle_de_despesas;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
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
public class AddCartaoCredito extends AppCompatActivity {
    EditText title_input51,prices_input51;
    Button add_button51;
    TextView textViewSelectedDate;
    final Calendar calendar = Calendar.getInstance();
    private String data_venc_cartao,limite_utilizado_do_cartao,limite_disponivel_do_cartao;
    private Locale userLocale;
    private Context context;
    private FirebaseAnalytics mFirebaseAnalytics;

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cartao_credito);

        // INIT - ANUNCIO DO BANNER
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // END - ANUNCIO DO BANNER

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("AddCartaoCredito");
        }

        title_input51 = findViewById(R.id.title_input51);

        // -----------------FORMATAÇÃO DO TEXTO TITLE INPUT CONFORME DIGITA----------------------//
        title_input51.addTextChangedListener(new TextWatcher() {
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
                    title_input51.setText(formattedText.toString());
                    title_input51.setSelection(formattedText.length());
                }
            }

        });
        // ----------------- FIM FORMATAÇÃO DO TEXTO TITLE INPUT CONFORME DIGITA----------------------//

        prices_input51 = findViewById(R.id.prices_input51);
        add_button51 = findViewById(R.id.add_button51);


//        calendarView51 = findViewById(R.id.calendarView51);
//
//        calendarView51.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                String formattedMonth = String.format("%02d", month + 1);
//                String formattedDay = String.format("%02d", dayOfMonth);
//                data_venc_cartao = year + "-" + formattedMonth + "-" + formattedDay;
//            }
//        });

        // Inicializar o TextView e definir a data atual no formato DD/MMM/YYYY
        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        textViewSelectedDate.setText(displayDateFormat.format(calendar.getTime()));

        // Ao clicar no TextView, abrir o DatePickerDialog
        textViewSelectedDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obter o ano, mês e dia atuais
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // Criar e mostrar o DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        new ContextThemeWrapper(AddCartaoCredito.this, R.style.CustomDatePickerDialog),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Formatar a data e salvar nas variáveis
                                String formattedMonth = String.format("%02d", selectedMonth + 1);
                                String formattedDay = String.format("%02d", selectedDay);

                                data_venc_cartao = selectedYear + "-" + formattedMonth + "-" + formattedDay;

                                // Atualizar o calendário com a nova data
                                calendar.set(Calendar.YEAR, selectedYear);
                                calendar.set(Calendar.MONTH, selectedMonth);
                                calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                                // Atualizar o TextView com a nova data no formato DD/MMM/YYYY
                                textViewSelectedDate.setText(displayDateFormat.format(calendar.getTime()));
                            }
                        }, year, month, day);

                datePickerDialog.show();
            }
        });


        add_button51.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDatabaseHelperCartoesCredito myDB_cartoes = new MyDatabaseHelperCartoesCredito(AddCartaoCredito.this);

                if (data_venc_cartao == null || data_venc_cartao.isEmpty()) {

                    String textToDisplay_data = AddCartaoCredito.this.getString(R.string.translate_selecione_o_vencimento);
                    Toast.makeText(AddCartaoCredito.this, textToDisplay_data, Toast.LENGTH_SHORT).show();
                    return;

//                    // Obtenha o ano, mês e dia da currentDate.
//                    Calendar calendar = Calendar.getInstance();
//
//                    int year = calendar.get(Calendar.YEAR);
//                    int month = calendar.get(Calendar.MONTH) + 1; // Note que os meses são base 0, então adicionamos 1.
//                    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                    // Formate o mês e o dia com dois dígitos
//                    String formattedMonth = String.format("%02d", month);
//                    String formattedDayOfMonth = String.format("%02d", dayOfMonth);
//
//                    data_venc_cartao = year + "-" + formattedMonth + "-" + formattedDayOfMonth;
                }

                String nome_do_cartao = title_input51.getText().toString().trim();
                String limite_do_cartao = prices_input51.getText().toString();

                if (nome_do_cartao.isEmpty() || nome_do_cartao.equals(".") ) {

                    String textToDisplay_tit = AddCartaoCredito.this.getString(R.string.translate_preencha_titulo);
                    Toast.makeText(AddCartaoCredito.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                }
                else if (limite_do_cartao.isEmpty() || limite_do_cartao.equals(".") || Double.parseDouble(limite_do_cartao) == 0.0) {

                    String textToDisplay_price = AddCartaoCredito.this.getString(R.string.translate_preco_nao_pode_ser_vazio_ou_zero);
                    Toast.makeText(AddCartaoCredito.this, textToDisplay_price, Toast.LENGTH_SHORT).show();

                } else if ( !isValidNumber(limite_do_cartao) ) {

                    String textToDisplay_invalid = AddCartaoCredito.this.getString(R.string.translate_o_numero_invalido);
                    Toast.makeText(AddCartaoCredito.this, textToDisplay_invalid, Toast.LENGTH_SHORT).show();

                } else {
                        try {

                             //Crie uma cópia da data inicial para evitar modificá-la diretamente
                            Calendar calendar = Calendar.getInstance();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date dataInicial = sdf.parse(data_venc_cartao);
                            //calendar.setTime(dataInicial);

//                            int year = calendar.get(Calendar.YEAR);
//                            int month = calendar.get(Calendar.MONTH) + 1; // Note que os meses são base 0, então adicionamos 1.
//                            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//
//                            // Formate o mês e o dia com dois dígitos
//                            String formattedMonth = String.format("%02d", month);
//                            String formattedDayOfMonth = String.format("%02d", dayOfMonth);
//
//                            data_venc_cartao = year + "-" + formattedMonth + "-" + formattedDayOfMonth;

                            limite_utilizado_do_cartao = "0.00";
                            limite_disponivel_do_cartao = limite_do_cartao;

                            myDB_cartoes.addBook(
                                    nome_do_cartao,
                                    limite_do_cartao,
                                    limite_utilizado_do_cartao,
                                    limite_disponivel_do_cartao,
                                    data_venc_cartao
                            );

                            // Incremente a data em um mês para a próxima parcela
                            //calendar.add(Calendar.MONTH, 1);

                            if (!BuildConfig.DEBUG) {
                                logFirebaseEvent("adicionou_cartao_de_credito");
                            }
                        } catch (NumberFormatException e) {
                            // Lidar com a situação em que a conversão falhou (entrada inválida)
                            // Por exemplo, mostre uma mensagem de erro ao usuário
                        }
                        catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    Intent intent = new Intent(AddCartaoCredito.this, MeusCartoesDeCredito.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    // Método de validação para garantir que o valor seja um número válido
    private boolean isValidNumber(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
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









