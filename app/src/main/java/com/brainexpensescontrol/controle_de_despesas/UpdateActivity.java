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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class UpdateActivity extends AppCompatActivity {
    EditText title_input,prices_input;
    Button update_button,delete_button;
    private String id, title, author,nome_cartao_antigo,prices,date,categoria,despesa_ou_receita, prices_Formatado_virgula,year2;
    private String selectedOption_despesa_ou_receita = "", selectedOption = "", selectedOption_categoria = "";
    private String year_month,selectedDate,parcelado_nao_parcelado,hash;

    private String gasto_fixo = "NAO", book_pago_ou_nao_pago = "NAO_PAGO", book_red_flag_notification = "NAO_NOTIFICAR";
    private CalendarView calendarView2;
    private Spinner spinnerOptions2, spinnerOptions_categoria, spinnerOptions_despesa_ou_receita;
    private MyDatabaseHelperCartoesCredito myDB_cartoes;
    private MyDatabaseHelperCategorias myDB_Categoria;
    private String[] options,options_categoria;
    private List<String> optionsList,optionsList_categoria;
    private String[] options_despesa_ou_receita;
    private String[] options_despesa_ou_receita_database = {"DESPESA COM CARTÃO","DESPESA","RECEITA"};
    private LinearLayout cartaoContainer2, categoriaContainer2;
    private FirebaseAnalytics mFirebaseAnalytics;
    private TextView textViewSelectedDate;
    private Calendar calendar = Calendar.getInstance();  // Calendário para manipulação de datas

    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);


        // INIT - ANUNCIO DO BANNER
        MobileAds.initialize(this, initializationStatus -> {});
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        // END - ANUNCIO DO BANNER

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (!BuildConfig.DEBUG) {
            logPageViewEvent("UpdateActivity");
        }

        // Inicialize o array utilizando os recursos do strings.xml
        options_despesa_ou_receita = new String[] {
                getString(R.string.translate_spinnerOptions_despesa_com_cartao),
                getString(R.string.translate_spinnerOptions_despesa),
                getString(R.string.translate_spinnerOptions_receita)
        };

        myDB_cartoes = new MyDatabaseHelperCartoesCredito(UpdateActivity.this);
        optionsList = myDB_cartoes.getOptionsFromDatabase();   // Lista com opções pegas do banco de dados
        options = optionsList.toArray(new String[0]);     // Convertendo a List<String> para um array de String

        myDB_Categoria = new MyDatabaseHelperCategorias(UpdateActivity.this);
        optionsList_categoria = myDB_Categoria.getOptionsFromDatabase();   // Lista com opções pegas do banco de dados
        options_categoria = optionsList_categoria.toArray(new String[0]);  // Convertendo a List<String> para um array de String

        cartaoContainer2 = findViewById(R.id.cartaoContainer2);
        categoriaContainer2 = findViewById(R.id.categoriaContainer2);

        title_input = findViewById(R.id.title_input2);

        spinnerOptions2 = findViewById(R.id.spinner_options2);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions2.setAdapter(adapter);

        spinnerOptions_categoria = findViewById(R.id.spinner_options_categoria);
        ArrayAdapter<String> adapter_categoria = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options_categoria);
        adapter_categoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions_categoria.setAdapter(adapter_categoria);

        spinnerOptions_despesa_ou_receita = findViewById(R.id.spinner_options_despesa_ou_receita);
        ArrayAdapter<String> adapter_despesa_ou_receita = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options_despesa_ou_receita);
        adapter_despesa_ou_receita.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerOptions_despesa_ou_receita.setAdapter(adapter_despesa_ou_receita);

        prices_input = findViewById(R.id.prices_input2);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        textViewSelectedDate = findViewById(R.id.textViewSelectedDate);

        getAndSetIntentData();
        String[] dateParts = selectedDate.split("-");

        int year = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int day = Integer.parseInt(dateParts[2]);
        int month2 = month + 1;
        String formattedMonth = String.format("%02d", month2);
        year_month = year + "-" + formattedMonth;

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
                        new ContextThemeWrapper(UpdateActivity.this, R.style.CustomDatePickerDialog),
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
                    title_input.setText(formattedText.toString());
                    title_input.setSelection(formattedText.length());
                }
            }
        });

        //Definir o título da barra de ação após o método getAndSetIntentData
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(getString(R.string.translate_dados_do_item));  // Usando a string do strings.xml
        }

        TextInputLayout pricesInputLayout = findViewById(R.id.prices_input_layout2);
        Context context19 = getApplicationContext();
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context19);
        String textToDisplay20 = context19.getString(R.string.translate_preco_total);
        String textoDesejado = textToDisplay20 + " " + moedaSelecionada;
        pricesInputLayout.setHint(textoDesejado);

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);

                if (selectedDate == null || selectedDate.isEmpty()) {
                    String textToDisplay_data = "Selecionar a data";
                    Toast.makeText(UpdateActivity.this, textToDisplay_data, Toast.LENGTH_SHORT).show();
                    return;
                }

                title = title_input.getText().toString().trim();

                // Defina um ouvinte para capturar a seleção do usuário
                spinnerOptions2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        selectedOption = options[position];
                        //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
                    }
                });

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

                // Defina um ouvinte para capturar a seleção do usuário
                spinnerOptions_despesa_ou_receita.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                        // Verifique se a opção selecionada é "DESPESA COM CARTÃO"
                        if (position == 0) {
                            // Se "DESPESA" estiver selecionado, torne os contêiners cartaoContainer e categoriaContainer visíveis.
                            cartaoContainer2.setVisibility(View.VISIBLE);
                            categoriaContainer2.setVisibility(View.VISIBLE);
                            selectedOption_despesa_ou_receita = "DESPESA COM CARTÃO";

                        }
                        else if(position == 1){
                            // Se "DESPESA" estiver selecionado, torne somente o conteiner categoriaContainer visível.
                            cartaoContainer2.setVisibility(View.GONE);
                            categoriaContainer2.setVisibility(View.VISIBLE);
                            selectedOption_despesa_ou_receita = "DESPESA";
                        }
                        else {
                            // Se "Receita" estiver selecionada, torne o contêiner do cartão invisível
                            cartaoContainer2.setVisibility(View.GONE);
                            categoriaContainer2.setVisibility(View.GONE);
                            selectedOption_despesa_ou_receita = "RECEITA";
                        }

                        //selectedOption_despesa_ou_receita = options_despesa_ou_receita[position];
                        //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
                    }
                });

                author = selectedOption;

                //author = author_input.getText().toString().trim();

                // FUNCIONAL prices = prices_input.getText().toString().trim();

                //---------INICIO FORMATAÇÃO DOS VALORES-----------------------------------------------------------------
                prices_Formatado_virgula = prices_input.getText().toString();

                //---------FIM FORMATAÇÃO DOS VALORES-----------------------------------------------------------------

                year2 = year_month.substring(0, 4);

                if (selectedOption_despesa_ou_receita == "RECEITA")
                {
                    selectedOption_categoria = "";
                    author = "RECEITA";

                } else if (selectedOption_despesa_ou_receita == "DESPESA")
                {
                    author = "DESPESA";
                }

                // Verifique se o título ou descrição não são vazios
                if (title.isEmpty()) {
                    String textToDisplay_tit = UpdateActivity.this.getString(R.string.translate_preencha_titulo);
                    Toast.makeText(UpdateActivity.this, textToDisplay_tit, Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (author.isEmpty()) {
                    String textToDisplay_tit_desc = UpdateActivity.this.getString(R.string.translate_preencha_cartao_de_credito_ou_debito);
                    Toast.makeText(UpdateActivity.this,textToDisplay_tit_desc,Toast.LENGTH_SHORT).show();
                    return;
                } else if (  prices_Formatado_virgula.isEmpty() || prices_Formatado_virgula.equals(".") || Double.parseDouble(prices_Formatado_virgula) == 0.0  ) {
                    String textToDisplay_price = UpdateActivity.this.getString(R.string.translate_preco_nao_pode_ser_vazio_ou_zero);
                    Toast.makeText(UpdateActivity.this,textToDisplay_price,Toast.LENGTH_SHORT).show();
                    return;
                } else if ( !isValidNumber(prices_Formatado_virgula) ) {
                    String textToDisplay_invalid = UpdateActivity.this.getString(R.string.translate_o_numero_invalido);
                    Toast.makeText(UpdateActivity.this,textToDisplay_invalid,Toast.LENGTH_SHORT).show();
                    return;
                }
                else {

                    book_pago_ou_nao_pago = "NAO_PAGO";
                    book_red_flag_notification = "NAO_NOTIFICAR";

                    myDB.updateDataWithoutGastoFixo(id,
                            title,
                            author,
                            prices_Formatado_virgula,
                            selectedDate,
                            year_month,
                            year2,
                            selectedOption_despesa_ou_receita,
                            parcelado_nao_parcelado,
                            selectedOption_categoria,
                            book_pago_ou_nao_pago,
                            hash,
                            book_red_flag_notification
                    );
                }

                // Atualizando a soma com nome_cartao_antigo
                MyDatabaseHelper myDB_geral20 = new MyDatabaseHelper(UpdateActivity.this);
                double despesa_total_do_cartao_nao_paga20 = myDB_geral20.obterSomaTotal(nome_cartao_antigo,"NAO_PAGO");

                //Log.d("TAG", "O valor de despesa_total_do_cartao_nao_paga é: " + despesa_total_do_cartao_nao_paga);
                String string_despesa_total_do_cartao_nao_paga20 = String.valueOf(despesa_total_do_cartao_nao_paga20);
                myDB_cartoes.atualizarLimiteUtilizado(nome_cartao_antigo,string_despesa_total_do_cartao_nao_paga20);

                double limite_do_cartao20 = myDB_cartoes.obterLimiteDoCartao(nome_cartao_antigo);
                double limite_disponivel_do_cartao20 = limite_do_cartao20 - despesa_total_do_cartao_nao_paga20;
                String string_limite_disponivel_do_cartao20 = String.valueOf(limite_disponivel_do_cartao20);
                myDB_cartoes.atualizarLimiteDisponivel(nome_cartao_antigo,string_limite_disponivel_do_cartao20);


                // Atualizando a soma com novo nome do cartão
                MyDatabaseHelper myDB_geral2 = new MyDatabaseHelper(UpdateActivity.this);
                double despesa_total_do_cartao_nao_paga = myDB_geral2.obterSomaTotal(author,"NAO_PAGO");

                //Log.d("TAG", "O valor de despesa_total_do_cartao_nao_paga é: " + despesa_total_do_cartao_nao_paga);
                String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                myDB_cartoes.atualizarLimiteUtilizado(author,string_despesa_total_do_cartao_nao_paga);

                double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(author);
                double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                myDB_cartoes.atualizarLimiteDisponivel(author,string_limite_disponivel_do_cartao);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("atualizou_um_item");
                }
                finish();
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);

                boolean hasDuplicates = myDB.hasDuplicateHash(hash);
                boolean check_database_version = myDB.isDatabaseVersion1();

                long itemId = Long.parseLong(id); // Converte a String para um long
                boolean allColumnsNull = myDB.areAllColumnsNullById(itemId);
                //Log.d("TAG", "O valor de allColumnsNull é " + allColumnsNull);
                //Log.d("TAG", "O valor de check_database_version é " + check_database_version);
                if (hasDuplicates) {
                    showCustomDialog();
                } else {
                    confirmDialog();
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

    void getAndSetIntentData(){
        if( getIntent().hasExtra("id") && getIntent().hasExtra("title") && getIntent().hasExtra("author") && getIntent().hasExtra("prices") && getIntent().hasExtra("book_date") && getIntent().hasExtra("book_categoria") && getIntent().hasExtra("book_despesa_ou_receita") && getIntent().hasExtra("book_pago_ou_nao_pago") && getIntent().hasExtra("book_red_flag_notification") ){

            // Getting Data from Intent
            id = getIntent().getStringExtra("id");
            title = getIntent().getStringExtra("title");
            author = getIntent().getStringExtra("author");
            prices = getIntent().getStringExtra("prices");
            selectedDate = getIntent().getStringExtra("book_date");
            despesa_ou_receita = getIntent().getStringExtra("book_despesa_ou_receita");
            categoria = getIntent().getStringExtra("book_categoria");
            book_pago_ou_nao_pago = getIntent().getStringExtra("book_pago_ou_nao_pago");
            book_red_flag_notification = getIntent().getStringExtra("book_red_flag_notification");

            nome_cartao_antigo = author;
            //selectedOption_categoria = myDB_cartoes.getCategoriaById(id);

            //Log.d("TAG", "O valor de categoria eh " + categoria);

            // Setting Intent Data
            title_input.setText(title);

            //////--------------------------------INIT-----------------------------------------/////
            // Defina um ouvinte para capturar a seleção do usuário
            spinnerOptions2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    selectedOption = options[position];
                    //selectedOption = options.get(position);
                    //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
                }

            });

            // Obter a opção do banco de dados
            String optionFromDatabase = author;

            // Encontre a correspondência no array de opções
            int initialOptionIndex = -1; // Inicialize como -1 para verificar se encontramos uma correspondência
            for (int i = 0; i < options.length; i++) {
                if (options[i].equals(optionFromDatabase)) {
                    initialOptionIndex = i;
                    break; // Parar a iteração assim que uma correspondência for encontrada
                }
            }

            if (initialOptionIndex >= 0) {
                spinnerOptions2.setSelection(initialOptionIndex);
                selectedOption = optionFromDatabase; // Defina a opção do banco de dados como selecionada
            } else {
                // Não foi encontrada uma correspondência, lide com isso de acordo com sua lógica
            }
            //////--------------------------------FIM-----------------------------------------/////
           // author_input.setText(author);
            //////--------------------------------INIT-----------------------------------------/////
            // Defina um ouvinte para capturar a seleção do usuário
            spinnerOptions_categoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    selectedOption_categoria = options_categoria[position];
                    //selectedOption = options.get(position);
                    //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
                }

            });

            // Obter a opção do banco de dados
            String optionFromDatabase2 = categoria;

            // Encontre a correspondência no array de opções
            int initialOptionIndex2 = -1; // Inicialize como -1 para verificar se encontramos uma correspondência
            for (int i = 0; i < options_categoria.length; i++) {
                if (options_categoria[i].equals(optionFromDatabase2)) {
                    initialOptionIndex2 = i;
                    break; // Parar a iteração assim que uma correspondência for encontrada
                }
            }

            if (initialOptionIndex2 >= 0) {
                spinnerOptions_categoria.setSelection(initialOptionIndex2);
                selectedOption_categoria = optionFromDatabase2; // Defina a opção do banco de dados como selecionada
            } else {
                // Não foi encontrada uma correspondência, lide com isso de acordo com sua lógica
            }
            //////--------------------------------FIM-----------------------------------------/////



            //////--------------------------------INIT-----------------------------------------/////
            // Defina um ouvinte para capturar a seleção do usuário
            spinnerOptions_despesa_ou_receita.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    // Verifique se a opção selecionada é "Despesa"
                    if (position == 0) {
                        // Se "DESPESA COM CARTÃO" estiver selecionado, torne os contêiners cartaoContainer e categoriaContainer visíveis.
                        cartaoContainer2.setVisibility(View.VISIBLE);
                        categoriaContainer2.setVisibility(View.VISIBLE);
                        selectedOption_despesa_ou_receita = "DESPESA COM CARTÃO";

                    }
                    else if(position == 1){
                        // Se "DESPESA" estiver selecionado, torne somente o conteiner categoriaContainer visível.
                        cartaoContainer2.setVisibility(View.GONE);
                        categoriaContainer2.setVisibility(View.VISIBLE);
                        selectedOption_despesa_ou_receita = "DESPESA";

                    }
                    else {
                        // Se "Receita" estiver selecionada, torne o contêiner do cartão invisível
                        cartaoContainer2.setVisibility(View.GONE);
                        categoriaContainer2.setVisibility(View.GONE);
                        selectedOption_despesa_ou_receita = "RECEITA";
                    }


                    //selectedOption_despesa_ou_receita = options_despesa_ou_receita[position];
                    //selectedOption = options.get(position);
                    //Toast.makeText(AddActivity.this, "Opção selecionada: " + selectedOption, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // Não é necessário fazer nada aqui, mas você pode adicionar lógica se desejar.
                }

            });

            // Obter a opção do banco de dados
            String optionFromDatabase4 = despesa_ou_receita;

            // Encontre a correspondência no array de opções
            int initialOptionIndex4 = -1; // Inicialize como -1 para verificar se encontramos uma correspondência
            for (int i = 0; i < options_despesa_ou_receita_database.length; i++) {
                if (options_despesa_ou_receita_database[i].equals(optionFromDatabase4)) {
                    initialOptionIndex4 = i;
                    break; // Parar a iteração assim que uma correspondência for encontrada
                }
            }

            if (initialOptionIndex4 >= 0) {
                spinnerOptions_despesa_ou_receita.setSelection(initialOptionIndex4);
                selectedOption_despesa_ou_receita = optionFromDatabase4; // Defina a opção do banco de dados como selecionada
            } else {
                // Não foi encontrada uma correspondência, lide com isso de acordo com sua lógica
            }
            //////--------------------------------FIM-----------------------------------------/////


            prices_input.setText(String.valueOf(prices));

            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
            long itemId = Long.parseLong(id); // Converte a String para um long
            boolean allColumnsNull = myDB.areAllColumnsNullById(itemId);

            //Log.d("TAG", "O valor de allColumnsNull é " + allColumnsNull);

            if (allColumnsNull) {  // SOLUCAO TEMPORARIA
                // Se todas as colunas forem 1 é por que estão no database antigo. essa é uma solução temporária.
                hash = "old40ol0-old7-4old-o647-25oo78480o6o";
                parcelado_nao_parcelado = "NAO_PARCELADO";
            }else{
                hash = myDB.getHashById(id);
                parcelado_nao_parcelado = myDB.getParceladoStatusById(id);
            }
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
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);

                double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(author,"NAO_PAGO");
                //Log.d("TAG", "O valor de despesa_total_do_cartao_nao_paga é: " + despesa_total_do_cartao_nao_paga);
                String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                myDB_cartoes.atualizarLimiteUtilizado(author,string_despesa_total_do_cartao_nao_paga);
                double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(author);
                double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                myDB_cartoes.atualizarLimiteDisponivel(author,string_limite_disponivel_do_cartao);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("deletou_um_item_de_item_unico");
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

    public void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        Button buttonOnlyThis = dialogView.findViewById(R.id.button_only_this);
        Button buttonAll = dialogView.findViewById(R.id.button_all);
        Button buttonThisAndFuture = dialogView.findViewById(R.id.button_this_and_future);

        buttonOnlyThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para APENAS ESTA
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(id);

                double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(author,"NAO_PAGO");
                //Log.d("TAG", "O valor de despesa_total_do_cartao_nao_paga é: " + despesa_total_do_cartao_nao_paga);
                String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                myDB_cartoes.atualizarLimiteUtilizado(author,string_despesa_total_do_cartao_nao_paga);
                double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(author);
                double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                myDB_cartoes.atualizarLimiteDisponivel(author,string_limite_disponivel_do_cartao);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("deletou_um_item_da_lista");
                }
                finish();
                dialog.dismiss();
            }
        });

        buttonAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteRowsWithSameHash(hash);

                double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(author,"NAO_PAGO");
                //Log.d("TAG", "O valor de despesa_total_do_cartao_nao_paga é: " + despesa_total_do_cartao_nao_paga);
                String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                myDB_cartoes.atualizarLimiteUtilizado(author,string_despesa_total_do_cartao_nao_paga);
                double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(author);
                double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                myDB_cartoes.atualizarLimiteDisponivel(author,string_limite_disponivel_do_cartao);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("deletou_todos_items_da_lista");
                }

                finish();
                dialog.dismiss();
            }
        });

        buttonThisAndFuture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lógica para ESTA E PRÓXIMAS
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);

                myDB.deleteRowsWithSameHashAndDate(hash,selectedDate);

                double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(author,"NAO_PAGO");
                //Log.d("TAG", "O valor de despesa_total_do_cartao_nao_paga é: " + despesa_total_do_cartao_nao_paga);
                String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                myDB_cartoes.atualizarLimiteUtilizado(author,string_despesa_total_do_cartao_nao_paga);
                double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(author);
                double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                myDB_cartoes.atualizarLimiteDisponivel(author,string_limite_disponivel_do_cartao);

                if (!BuildConfig.DEBUG) {
                    logFirebaseEvent("deleted_this_and_next_items");
                }
                finish();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void logPageViewEvent(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, pageName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public interface AtualizacaoBancoDadosListener {
        void onAtualizacaoConcluida();
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