package com.brainexpensescontrol.controle_de_despesas;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.material.datepicker.MaterialDatePicker;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AddActivity extends AppCompatActivity {
    EditText title_input,prices_input;
    Button add_button;
    TextView textViewSelectedDate;
    final Calendar calendar = Calendar.getInstance();
    private String year_month,selectedDate, yearString, selectedOption_despesa_ou_receita = "", selectedOption = "", selectedOption_categoria = "";
    private String parcelado_nao_parcelado = "NAO_PARCELADO", book_pago_ou_nao_pago = "NAO_PAGO", book_red_flag_notification = "NAO_NOTIFICAR", gasto_fixo = "NAO",numero_parcelas;
    private Locale userLocale;
    private Context context;
    private Spinner spinnerOptions,spinnerOptions_categoria,spinnerOptions_despesa_ou_receita;
    private MyDatabaseHelperCartoesCredito myDB_cartoes;
    private MyDatabaseHelperCategorias myDB_Categoria;
    private String[] options,options_categoria, options_despesa_ou_receita;
    private List<String> optionsList,optionsList_categoria;
    private LinearLayout cartaoContainer,categoriaContainer;
    private FirebaseAnalytics mFirebaseAnalytics;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (!BuildConfig.DEBUG) {
            logPageViewEvent("AddActivity");
        }

        // Inicialize o array utilizando os recursos do strings.xml
        options_despesa_ou_receita = new String[] {
                getString(R.string.translate_spinnerOptions_despesa_com_cartao),
                getString(R.string.translate_spinnerOptions_despesa),
                getString(R.string.translate_spinnerOptions_receita)
        };

        myDB_cartoes = new MyDatabaseHelperCartoesCredito(AddActivity.this);
        optionsList = myDB_cartoes.getOptionsFromDatabase();
        options = optionsList.toArray(new String[0]);  // Convertendo a List<String> para um array de String

        myDB_Categoria = new MyDatabaseHelperCategorias(AddActivity.this);
        optionsList_categoria = myDB_Categoria.getOptionsFromDatabase();
        options_categoria = optionsList_categoria.toArray(new String[0]);

        cartaoContainer = findViewById(R.id.cartaoContainer);
        categoriaContainer = findViewById(R.id.categoriaContainer);

        //------------- INIT DESPESAS OU RECEITAS  ---------------------//
        spinnerOptions_despesa_ou_receita = findViewById(R.id.spinner_options_despesa_ou_receita);
        ArrayAdapter<String> adapter_despesa_ou_receita = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options_despesa_ou_receita);
        adapter_despesa_ou_receita.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions_despesa_ou_receita.setAdapter(adapter_despesa_ou_receita);

        // Defina um ouvinte para capturar a seleção do usuário
        spinnerOptions_despesa_ou_receita.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                // Verifique se a opção selecionada é "DESPESA COM CARTÃO"
                if (position == 0) {
                    cartaoContainer.setVisibility(View.VISIBLE);
                    categoriaContainer.setVisibility(View.VISIBLE);
                    selectedOption_despesa_ou_receita = "DESPESA COM CARTÃO";
                }
                else if(position == 1){
                    cartaoContainer.setVisibility(View.GONE);
                    categoriaContainer.setVisibility(View.VISIBLE);
                    selectedOption_despesa_ou_receita = "DESPESA";

                }
                else {
                    cartaoContainer.setVisibility(View.GONE);
                    categoriaContainer.setVisibility(View.GONE);
                    selectedOption_despesa_ou_receita = "RECEITA";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
            }
        });
        //------------- FIM DESPESAS OU RECEITAS  ---------------------//

        //------------- CARTÕES ---------------------------------------//

        spinnerOptions = findViewById(R.id.spinner_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions.setAdapter(adapter);

        // Defina um ouvinte para capturar a seleção do usuário
        spinnerOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedOption = options[position]; // Funcionou para a opções fixas
                //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
                //selectedOption = options.get(position);
                //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
            }
        });

        spinnerOptions.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (optionsList.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddActivity.this);

                    String text_cadastro_cartao_1 = AddActivity.this.getString(R.string.translate_cadastro_de_cartao_de_credito);
                    String text_cadastro_primeiro_cartao_1 = AddActivity.this.getString(R.string.translate_cadastrar_seu_primeiro_cartao_de_credito);
                    String text_sim_1 = AddActivity.this.getString(R.string.translate_sim);
                    String text_nao_1 = AddActivity.this.getString(R.string.translate_nao);

                    builder.setTitle(text_cadastro_cartao_1);
                    builder.setMessage(text_cadastro_primeiro_cartao_1);
                    builder.setPositiveButton(text_sim_1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(AddActivity.this, AddCartaoCredito.class));
                        }
                    });
                    builder.setNegativeButton(text_nao_1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    return true;
                }
                return false;
            }
        });

        //------------- FIM CARTÕES ------------------------------------//

        //------------- CATEGORIA DE GASTOS ---------------------//
        spinnerOptions_categoria = findViewById(R.id.spinner_options_categoria);
        ArrayAdapter<String> adapter_categoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options_categoria);
        adapter_categoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions_categoria.setAdapter(adapter_categoria);

        // Defina um ouvinte para capturar a seleção do usuário
        spinnerOptions_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                selectedOption_categoria = options_categoria[position];
                //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
            }
        });
        //------------- FIM CATEGORIA DE GASTOS ---------------------//

        title_input = findViewById(R.id.title_input);
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
                    title_input.setText(formattedText.toString());
                    title_input.setSelection(formattedText.length());
                }
            }
        });

        prices_input = findViewById(R.id.prices_input);
        add_button = findViewById(R.id.add_button);
        //calendarView50 = findViewById(R.id.calendarView50);

        TextInputLayout pricesInputLayout = findViewById(R.id.prices_input_layout);
        Context context11 = getApplicationContext();
        String textToDisplay20 = context11.getString(R.string.translate_preco_total);
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context11);
        String textoDesejado = textToDisplay20 + " " + moedaSelecionada;
        pricesInputLayout.setHint(textoDesejado);

        RadioButton parcelarCheckbox = findViewById(R.id.checkbox2);
        RadioButton gastofixoCheckbox = findViewById(R.id.checkbox3);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);

        TextInputLayout parcelarInputLayout = findViewById(R.id.parcelar_input_layout);
        EditText parcelar_input = findViewById(R.id.parcelar_input);
        parcelar_input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método não é necessário para o seu caso, mas precisa ser implementado
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Este método é chamado quando o texto está sendo alterado
                numero_parcelas = charSequence.toString();
                //Log.d("TAG", "numero_parcelas é " + numero_parcelas);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Este método não é necessário para o seu caso, mas precisa ser implementado
            }
        });

        parcelarCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView2, boolean isChecked2) {
                if (isChecked2) {
                    parcelarInputLayout.setVisibility(View.VISIBLE);
                } else {
                    parcelarInputLayout.setVisibility(View.GONE);
                    numero_parcelas = "1";
                }
            }
        });

        gastofixoCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView2, boolean isChecked3) {
                if (isChecked3) {
                    gasto_fixo = "SIM";
                } else {
                    gasto_fixo = "NÃO";
                }
            }
        });

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
                        new ContextThemeWrapper(AddActivity.this, R.style.CustomDatePickerDialog),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Formatar a data e salvar nas variáveis
                                String formattedMonth = String.format("%02d", selectedMonth + 1);
                                String formattedDay = String.format("%02d", selectedDay);

                                selectedDate = selectedYear + "-" + formattedMonth + "-" + formattedDay;
                                year_month = selectedYear + "-" + formattedMonth;

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

        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);

                if (selectedDate == null || selectedDate.isEmpty()) {
                    String textToDisplay_data = AddActivity.this.getString(R.string.translate_selecione_o_vencimento);
                    Toast.makeText(AddActivity.this, textToDisplay_data, Toast.LENGTH_SHORT).show();
                    return;
                }

                String title_2 = title_input.getText().toString().trim();
                String nome_cartao_despesa_ou_receita = selectedOption;
                String priceText44 = prices_input.getText().toString();
                String year2 = year_month.substring(0, 4);

                UUID uuid = UUID.randomUUID();
                String hash = uuid.toString();

                if (selectedOption_despesa_ou_receita == "RECEITA") {
                    selectedOption_categoria = "";
                    nome_cartao_despesa_ou_receita = "RECEITA";
                } if (selectedOption_despesa_ou_receita == "DESPESA") {
                    nome_cartao_despesa_ou_receita = "DESPESA";
                }
                if (title_2.isEmpty()) {
                    String textToDisplay_tit = AddActivity.this.getString(R.string.translate_preencha_titulo);
                    Toast.makeText(AddActivity.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                    return;
                }
                if(nome_cartao_despesa_ou_receita.isEmpty()) {

                    String textToDisplay_tit_desc = AddActivity.this.getString(R.string.translate_preencha_cartao_de_credito_ou_debito);
                    Toast.makeText(AddActivity.this, textToDisplay_tit_desc, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (priceText44.isEmpty() || priceText44.equals(".") || Double.parseDouble(priceText44) == 0.0 ) {

                    String textToDisplay_price = AddActivity.this.getString(R.string.translate_preco_nao_pode_ser_vazio_ou_zero);
                    Toast.makeText(AddActivity.this, textToDisplay_price, Toast.LENGTH_SHORT).show();
                    return;
                }
                if ( !isValidNumber(priceText44) ) {

                    String textToDisplay_invalid = AddActivity.this.getString(R.string.translate_o_numero_invalido);
                    Toast.makeText(AddActivity.this, textToDisplay_invalid, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (parcelarCheckbox.isChecked()) {
                    numero_parcelas = parcelar_input.getText().toString();
                } else {
                    numero_parcelas = "1";
                }
                if (BuildConfig.DEBUG) {
                    Log.d("TAG", "gasto_fixo é " + gasto_fixo);
                    Log.d("TAG", "numero_parcelas é " + numero_parcelas);
                }
                if ( numero_parcelas == null || numero_parcelas.isEmpty() || numero_parcelas.equals(".") || Double.parseDouble(numero_parcelas) == 0.0 ) {

                    String textToDisplay_tit = AddActivity.this.getString(R.string.translate_numero_de_parcela_nao_pode_ser_zero);
                    Toast.makeText(AddActivity.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    if (!isInteger(numero_parcelas)) {
                        String textToDisplay_tit = AddActivity.this.getString(R.string.translate_numero_de_parcela_nao_pode_ser_zero);
                        Toast.makeText(AddActivity.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int numParcelas = Integer.parseInt(numero_parcelas);

                if (numParcelas > 600) {
                    String textToDisplay_tit = AddActivity.this.getString(R.string.translate_o_numero_de_parcela_nao_pode_ser_maior_que_600);
                    Toast.makeText(AddActivity.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    if (numParcelas != 1) {
                        try {
                            parcelado_nao_parcelado = "PARCELADO";
                            Calendar calendar = Calendar.getInstance();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date dataInicial = sdf.parse(selectedDate);
                            calendar.setTime(dataInicial);

                            for (int i = 0; i < numParcelas; i++) {
                                String titulo = title_2 + " " + (i + 1) + "/" + numParcelas;

                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1;
                                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                                String formattedMonth = String.format("%02d", month);
                                String formattedDayOfMonth = String.format("%02d", dayOfMonth);

                                selectedDate = year + "-" + formattedMonth + "-" + formattedDayOfMonth;
                                year_month = year + "-" + formattedMonth;
                                yearString = String.valueOf(year);

                                myDB.addBook(
                                        titulo,
                                        nome_cartao_despesa_ou_receita,
                                        priceText44,
                                        selectedDate,
                                        year_month,
                                        yearString,
                                        gasto_fixo,
                                        selectedOption_despesa_ou_receita,
                                        parcelado_nao_parcelado,
                                        selectedOption_categoria,
                                        book_pago_ou_nao_pago,
                                        hash,
                                        book_red_flag_notification
                                );
                                calendar.add(Calendar.MONTH, 1);
                            }
                            if (!BuildConfig.DEBUG) {
                                logFirebaseEvent(selectedOption_despesa_ou_receita);
                            }

                            double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(nome_cartao_despesa_ou_receita,"NAO_PAGO");
                            String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                            myDB_cartoes.atualizarLimiteUtilizado(nome_cartao_despesa_ou_receita,string_despesa_total_do_cartao_nao_paga);
                            double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(nome_cartao_despesa_ou_receita);
                            double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                            String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                            myDB_cartoes.atualizarLimiteDisponivel(nome_cartao_despesa_ou_receita,string_limite_disponivel_do_cartao);

                            Intent intent = new Intent(AddActivity.this, AnotacoesSalvas.class);
                            startActivity(intent);
                            finishAffinity();
                        } catch (NumberFormatException e) {
                            // Lidar com a situação em que a conversão falhou (entrada inválida)
                            // Por exemplo, mostre uma mensagem de erro ao usuário
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }

                    } else if ( "SIM".equals(gasto_fixo) ){
                        try {
                            Calendar calendar = Calendar.getInstance();

                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            Date dataInicial = sdf.parse(selectedDate);
                            calendar.setTime(dataInicial);

                            for (int i = 0; i < 600; i++) {
                                String titulo = title_2 ;

                                int year = calendar.get(Calendar.YEAR);
                                int month = calendar.get(Calendar.MONTH) + 1;
                                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                                String formattedMonth = String.format("%02d", month);
                                String formattedDayOfMonth = String.format("%02d", dayOfMonth);

                                selectedDate = year + "-" + formattedMonth + "-" + formattedDayOfMonth;
                                year_month = year + "-" + formattedMonth;
                                yearString = String.valueOf(year);

                                myDB.addBook(
                                        titulo,
                                        nome_cartao_despesa_ou_receita,
                                        priceText44,
                                        selectedDate,
                                        year_month,
                                        yearString,
                                        gasto_fixo,
                                        selectedOption_despesa_ou_receita,
                                        parcelado_nao_parcelado,
                                        selectedOption_categoria,
                                        book_pago_ou_nao_pago,
                                        hash,
                                        book_red_flag_notification
                                );
                                calendar.add(Calendar.MONTH, 1);
                            }
                            if (!BuildConfig.DEBUG) {
                                logFirebaseEvent(selectedOption_despesa_ou_receita);
                            }
                            double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(nome_cartao_despesa_ou_receita,"NAO_PAGO");
                            String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                            myDB_cartoes.atualizarLimiteUtilizado(nome_cartao_despesa_ou_receita,string_despesa_total_do_cartao_nao_paga);
                            double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(nome_cartao_despesa_ou_receita);
                            double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                            String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                            myDB_cartoes.atualizarLimiteDisponivel(nome_cartao_despesa_ou_receita,string_limite_disponivel_do_cartao);

                            Intent intent = new Intent(AddActivity.this, AnotacoesSalvas.class);
                            startActivity(intent);
                            finishAffinity();

                        } catch (NumberFormatException e) {
                            // Lidar com a situação em que a conversão falhou (entrada inválida)
                            // Por exemplo, mostre uma mensagem de erro ao usuário
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else {
                        myDB.addBook(
                                title_2,
                                nome_cartao_despesa_ou_receita,
                                priceText44,
                                selectedDate,
                                year_month,
                                year2,
                                gasto_fixo,
                                selectedOption_despesa_ou_receita,
                                parcelado_nao_parcelado,
                                selectedOption_categoria,
                                book_pago_ou_nao_pago,
                                hash,
                                book_red_flag_notification
                        );

                        double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(nome_cartao_despesa_ou_receita,"NAO_PAGO");
                        String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                        myDB_cartoes.atualizarLimiteUtilizado(nome_cartao_despesa_ou_receita,string_despesa_total_do_cartao_nao_paga);

                        double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(nome_cartao_despesa_ou_receita);
                        double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                        String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                        myDB_cartoes.atualizarLimiteDisponivel(nome_cartao_despesa_ou_receita,string_limite_disponivel_do_cartao);

                        if (!BuildConfig.DEBUG) {
                            logFirebaseEvent(selectedOption_despesa_ou_receita);
                        }
                        Intent intent = new Intent(AddActivity.this, AnotacoesSalvas.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                }

                }
            }
        });

    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    private boolean isFirstExpense() {
        SharedPreferences preferences = getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        return preferences.getBoolean("primeiraDespesaAdicionada", true);
    }

    private void markFirstExpenseAdded() {
        SharedPreferences preferences = getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("primeiraDespesaAdicionada", false);
        editor.apply();
    }

    private void logFirebaseEvent(String despesa_sem_cartao_ou_com_cartao_ou_receita) {

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (mFirebaseAnalytics != null) {
            if (isFirstExpense()) {
                mFirebaseAnalytics.logEvent("adicionou_primeira_despesa", null);
                markFirstExpenseAdded();
            } else {
                if ("DESPESA".equals(despesa_sem_cartao_ou_com_cartao_ou_receita)) {
                    mFirebaseAnalytics.logEvent("adicionou_despesa_sem_cartao", null);
                } else if ("DESPESA COM CARTÃO".equals(despesa_sem_cartao_ou_com_cartao_ou_receita)) {
                    mFirebaseAnalytics.logEvent("adicionou_despesa_com_cartao", null);
                } else if ("RECEITA".equals(despesa_sem_cartao_ou_com_cartao_ou_receita)) {
                    mFirebaseAnalytics.logEvent("adicionou_receita", null);
                } else {
                    mFirebaseAnalytics.logEvent("Erro_despesa", null);
                }
            }
        } else {
            Log.e("Firebase Analytics", "Firebase Analytics não inicializado corretamente.");
        }
    }

    private boolean isValidNumber(String input) {
        try {
            Double.parseDouble(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            int number = Integer.parseInt(str);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}






