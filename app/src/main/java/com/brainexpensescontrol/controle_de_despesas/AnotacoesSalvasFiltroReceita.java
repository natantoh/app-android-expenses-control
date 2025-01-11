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
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
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

public class AnotacoesSalvasFiltroReceita extends AppCompatActivity {

    RecyclerView recyclerView_Filtro_Receita;
    MyDatabaseHelper myDB10;
    private Context context10;
    ArrayList<String> book_id10, book_title10, book_author10,book_pages10,book_date10,book_year_month10,book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10;
    CustomAdapter customAdapter;
    private TextView txtMonth10;
    private Calendar currentCalendar10;
    private FirebaseAnalytics mFirebaseAnalytics;
    private String year_monthh10,gasto_fixo = "SIM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anotacoes_salvas_filtro_receita);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (!BuildConfig.DEBUG) {
            logPageViewEvent("AnotacoesSalvasFiltroReceita");
        }

        context10 = this;

        txtMonth10 = findViewById(R.id.txtMonth10);
        Button btnNext = findViewById(R.id.btnNext_receita);
        Button btnBefore = findViewById(R.id.btnBefore_receita);
        currentCalendar10 = Calendar.getInstance();
        recyclerView_Filtro_Receita = findViewById(R.id.recyclerView_Filtro_receita);

        myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroReceita.this);

        Date currentDate = currentCalendar10.getTime();
        int year = currentCalendar10.get(Calendar.YEAR);
        int month = currentCalendar10.get(Calendar.MONTH) + 1;

        String formattedMonth2 = String.format("%02d", month);
        year_monthh10 = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth2);

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        String monthText = sdf.format(currentDate);

        monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
        txtMonth10.setText(monthText);

        book_id10 = new ArrayList<>();
        book_title10 = new ArrayList<>();
        book_author10 = new ArrayList<>();
        book_pages10 = new ArrayList<>();
        book_date10 = new ArrayList<>();
        book_year_month10 = new ArrayList<>();
        book_categoria10 = new ArrayList<>();
        book_pago_ou_nao_pago10 = new ArrayList<>();
        book_red_flag_notification10 = new ArrayList<>();

        customAdapter = new CustomAdapter(AnotacoesSalvasFiltroReceita.this,AnotacoesSalvasFiltroReceita.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
        recyclerView_Filtro_Receita.setAdapter(customAdapter);

        storeDataInArrays_using_filter_receita(year_monthh10);
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context10);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);

        double total_receita = myDB10.ReceitaDoMes(year_monthh10);
        String formattedtotalSum = numberFormat.format(total_receita);
        TextView resultTextView1 = findViewById(R.id.textViewReceitaMes);

        Context context01 = getApplicationContext();
        String income_total = context01.getString(R.string.translate_receitas);
        String concatenatedText = income_total + ": " + moedaSelecionada + " " + formattedtotalSum;
        resultTextView1.setText(concatenatedText);

        customAdapter = new CustomAdapter(AnotacoesSalvasFiltroReceita.this,AnotacoesSalvasFiltroReceita.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
        recyclerView_Filtro_Receita.setAdapter(customAdapter);
        recyclerView_Filtro_Receita.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroReceita.this));

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentCalendar10.add(Calendar.MONTH, 1);
                Date currentDate = currentCalendar10.getTime();
                int year = currentCalendar10.get(Calendar.YEAR);
                int month = currentCalendar10.get(Calendar.MONTH) + 1;

                String formattedMonth4 = String.format("%02d", month);
                String year_month10 = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth4);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String monthText = sdf.format(currentDate);

                monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);
                txtMonth10.setText(monthText);

                book_id10 = new ArrayList<>();
                book_title10 = new ArrayList<>();
                book_author10 = new ArrayList<>();
                book_pages10 = new ArrayList<>();
                book_date10 = new ArrayList<>();
                book_year_month10 = new ArrayList<>();
                book_despesa_ou_receita10 = new ArrayList<>();
                book_categoria10 = new ArrayList<>();
                book_pago_ou_nao_pago10 = new ArrayList<>();
                book_red_flag_notification10 = new ArrayList<>();

                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroReceita.this,AnotacoesSalvasFiltroReceita.this, book_id10, book_title10,book_author10,book_pages10,book_date10,book_despesa_ou_receita10,book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Receita.setAdapter(customAdapter);

                storeDataInArrays_using_filter_receita(year_month10);

                myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroReceita.this);
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                double total_receita = myDB10.ReceitaDoMes(year_month10);
                String formattedtotalSum11 = numberFormat.format(total_receita);

                Context context011 = getApplicationContext();
                String texto_receita = context011.getString(R.string.translate_receitas);
                String concatenatedText11 = texto_receita + ": " + moedaSelecionada + " " + formattedtotalSum11;
                TextView resultTextView11 = findViewById(R.id.textViewReceitaMes);
                resultTextView11.setText(concatenatedText11);

                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroReceita.this,AnotacoesSalvasFiltroReceita.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Receita.setAdapter(customAdapter);
                recyclerView_Filtro_Receita.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroReceita.this));
            }
        });

        btnBefore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                currentCalendar10.add(Calendar.MONTH, -1);
                Date currentDate = currentCalendar10.getTime();

                int year = currentCalendar10.get(Calendar.YEAR);
                int month = currentCalendar10.get(Calendar.MONTH) + 1;

                String formattedMonth5 = String.format("%02d", month);
                String year_month = String.format(Locale.getDefault(), "%d-%s", year, formattedMonth5);
                SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
                String monthText = sdf.format(currentDate);
                monthText = monthText.substring(0, 1).toUpperCase() + monthText.substring(1);

                txtMonth10.setText(monthText);

                book_id10 = new ArrayList<>();
                book_title10 = new ArrayList<>();
                book_author10 = new ArrayList<>();
                book_pages10 = new ArrayList<>();
                book_date10 = new ArrayList<>();
                book_year_month10 = new ArrayList<>();
                book_despesa_ou_receita10 = new ArrayList<>();
                book_categoria10 = new ArrayList<>();
                book_pago_ou_nao_pago10 = new ArrayList<>();
                book_red_flag_notification10 = new ArrayList<>();

                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroReceita.this,AnotacoesSalvasFiltroReceita.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Receita.setAdapter(customAdapter);

                storeDataInArrays_using_filter_receita(year_month);

                myDB10 = new MyDatabaseHelper(AnotacoesSalvasFiltroReceita.this);
                NumberFormat numberFormat = NumberFormat.getNumberInstance();
                numberFormat.setMinimumFractionDigits(2);

                double total_receita = myDB10.ReceitaDoMes(year_month);
                String formattedtotalSum44 = numberFormat.format(total_receita);

                Context context0111 = getApplicationContext();
                String texto_receitas = context0111.getString(R.string.translate_receitas);
                String concatenatedText44 = texto_receitas + ": " + moedaSelecionada + " " + formattedtotalSum44;
                TextView resultTextView44 = findViewById(R.id.textViewReceitaMes);
                resultTextView44.setText(concatenatedText44);

                customAdapter = new CustomAdapter(AnotacoesSalvasFiltroReceita.this,AnotacoesSalvasFiltroReceita.this, book_id10, book_title10,book_author10,book_pages10,book_date10, book_despesa_ou_receita10, book_categoria10,book_pago_ou_nao_pago10,book_red_flag_notification10);
                recyclerView_Filtro_Receita.setAdapter(customAdapter);
                recyclerView_Filtro_Receita.setLayoutManager(new LinearLayoutManager(AnotacoesSalvasFiltroReceita.this));
            }
        });

    }

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
    void storeDataInArrays_using_filter_receita(String year_monthhh ){

        MyDatabaseHelper myDB_geral = new MyDatabaseHelper(AnotacoesSalvasFiltroReceita.this);
        Cursor cursor = myDB_geral.readDataFilter_receita_mes(year_monthhh);

        try {

            book_id10 = new ArrayList<>();
            book_title10 = new ArrayList<>();
            book_author10 = new ArrayList<>();
            book_pages10 = new ArrayList<>();
            book_date10 = new ArrayList<>();
            book_year_month10 = new ArrayList<>();
            book_despesa_ou_receita10 = new ArrayList<>();
            book_categoria10 = new ArrayList<>();
            book_pago_ou_nao_pago10 = new ArrayList<>();
            book_red_flag_notification10 = new ArrayList<>();

            if(cursor.getCount() == 0 ){
            }else{
                while(cursor.moveToNext()){
                    book_id10.add(cursor.getString(0));
                    book_title10.add(cursor.getString(1));
                    book_author10.add(cursor.getString(2));
                    book_pages10.add(cursor.getString(3));
                    book_date10.add(cursor.getString(4));
                    book_despesa_ou_receita10.add(cursor.getString(8));
                    book_categoria10.add(cursor.getString(10));
                    book_pago_ou_nao_pago10.add(cursor.getString(11));
                    book_red_flag_notification10.add(cursor.getString(13));
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (myDB_geral != null) {
                myDB_geral.close();
            }
        }
    }

    @Override
    public void onBackPressed() {
        updateRecyclerView();
        super.onBackPressed();
    }

    private void updateRecyclerView() {

        Intent intent33 = new Intent(AnotacoesSalvasFiltroReceita.this, AnotacoesSalvas.class);
        startActivity(intent33);
    }
}