package com.brainexpensescontrol.controle_de_despesas;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;    // Classe que contém os métodos de manipulação dos dados no banco, por isso temos que importar
import android.database.sqlite.SQLiteOpenHelper; // Classe responsável pela criação do banco e também responsável pelo versionamento do mesmo.
import android.icu.text.CaseMap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class MyDatabaseHelper extends SQLiteOpenHelper {  // Primeiro passo então, é herdar todos os métodos da classe SQLiteOpenHelper, para acessar seus métodos.
    // Defina as constantes para nome do banco, versão, tabelas, etc.
    private Context context;
    private static final String DATABASE_NAME = "Banco_De_Dados_Meu_Controle_Financeiro.db";
    //Certifique-se de aumentar o número de versão sempre que fizer alterações no esquema do banco de dados que precisem
    //ser refletidas em versões futuras do aplicativo. Isso permitirá que os usuários atualizem o aplicativo sem perder os dados existentes.
    private static final int DATABASE_VERSION = 2; // Aumente o número da versão sempre que adicionar novas colunas e coloque esssa nova coluna no onUpgrade.
    public static final String TABLE_NAME = "my_library";
    private static final String COLUMN_ID = "_id";
    public  static final String COLUMN_TITLE = "book_title";
    private static final String COLUMN_AUTHOR = "book_author";
    private static final String COLUMN_PRICES = "prices";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_YEAR_MONTH = "year_month";
    public static final String COLUMN_YEAR = "year";
    public static final String COLUMN_GASTO_FIXO = "gasto_fixo";
    public static final String COLUMN_DESPESA_OU_RECEITA = "Receita_ou_despesa";
    public static final String COLUMN_PARCELADO = "parcelado_ou_nao_parcelado";
    public static final String COLUMN_CATEGORIA = "categoria";
    public static final String COLUMN_PAGO_NAO_PAGO = "pago_ou_nao_pago";
    public static final String COLUMN_HASH = "hash";
    public static final String COLUMN_RED_FLAG_NOTIFICATION = "red_flag_notification";

    private FirebaseAnalytics mFirebaseAnalytics;

    MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    // Ao estender sua classe SQLiteOpenHelper, o Android Studio obriga o desenvolvedor a implementar dois métodos que são
    // de suma importância para o correto funcionamento da criação do banco de dados:

    // Método onCreate(): é chamado quando a aplicação cria o banco de dados pela primeira vez.
    // Nesse método devem ter todas as diretrizes de criação e população inicial do banco.

    // Método onUpgrade(): é o método responsável por atualizar o banco de dados com alguma informação estrutural
    // que tenha sido alterada. Ele sempre é chamado quando uma atualização é necessária, para não ter nenhum tipo
    // de inconsistência de dados entre o banco existente no aparelho e o novo que a aplicação irá utilizar.

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crie as tabelas conforme necessário
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_PRICES + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_YEAR_MONTH + " TEXT, " +
                COLUMN_YEAR + " TEXT, " +
                COLUMN_GASTO_FIXO + " TEXT, " +
                COLUMN_DESPESA_OU_RECEITA + " TEXT, " +
                COLUMN_PARCELADO + " TEXT, " +
                COLUMN_CATEGORIA + " TEXT, " +
                COLUMN_PAGO_NAO_PAGO + " TEXT, " +
                COLUMN_HASH + " TEXT, " +
                COLUMN_RED_FLAG_NOTIFICATION + " TEXT);";
        db.execSQL(query);
    }

//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Lide com atualizações do banco, se necessário
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Este trecho de código está excluindo a tabela especificada (TABLE_NAME) se ela existir. Em outras palavras, ele está descartando a tabela inteira e todos os dados dentro dela. Isso é geralmente usado quando você precisa fazer uma atualização de banco de dados que envolve uma mudança significativa na estrutura da tabela ou quando você deseja recriar a tabela do zero. No entanto, isso resultará na perda de todos os dados existentes na tabela.
//        onCreate(db); // Após excluir a tabela (se ela existir), este código chama o método onCreate para criar novamente a tabela de acordo com a definição fornecida dentro do método onCreate. Isso efetivamente cria uma nova tabela no banco de dados, substituindo a tabela antiga que foi excluída na etapa anterior.
//    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Se a versão antiga for menor que 2, adicione todas as novas colunas
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_RED_FLAG_NOTIFICATION + " TEXT;");
        }
    }

    void addBook(String title, String author, String prices, String date, String year_month, String year, String gasto_fixo,String receita_ou_despesa, String parcelado, String categoria, String pago_ou_nao_pago, String hash, String red_flag_notification){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_AUTHOR,author);
        cv.put(COLUMN_PRICES,prices);
        cv.put(COLUMN_DATE,date);
        cv.put(COLUMN_YEAR_MONTH,year_month);
        cv.put(COLUMN_YEAR,year);
        cv.put(COLUMN_GASTO_FIXO,gasto_fixo);
        cv.put(COLUMN_DESPESA_OU_RECEITA,receita_ou_despesa);
        cv.put(COLUMN_PARCELADO,parcelado);
        cv.put(COLUMN_CATEGORIA,categoria);
        cv.put(COLUMN_PAGO_NAO_PAGO,pago_ou_nao_pago);
        cv.put(COLUMN_HASH,hash);
        cv.put(COLUMN_RED_FLAG_NOTIFICATION,red_flag_notification);
        long result = db.insert(TABLE_NAME, null, cv);

        if(result == -1){
            String textToDisplay_falhou = context.getString(R.string.translate_falhou);
            Toast.makeText(context,textToDisplay_falhou,Toast.LENGTH_SHORT).show();
        }
        db.close();
    }



    Cursor readAllData(){
            String query = "SELECT * FROM " + TABLE_NAME;
            SQLiteDatabase db = this.getReadableDatabase();

            Cursor cursor = null;
            if(db != null){
                cursor = db.rawQuery(query,null);
            }
            return cursor;
        }

    Cursor readDataFilter(String targetDate) {
        SQLiteDatabase db = this.getReadableDatabase();

        //String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ?";
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ? ORDER BY date ASC";

        String[] selectionArgs = { targetDate };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
        //O cursor retornado pelo método readDataFilter contém os resultados da consulta SQL que seleciona todas
        //as colunas da tabela especificada (TABLE_NAME) onde o valor da coluna year_month corresponde ao valor passado como targetDate.

        //O cursor contém os registros que atendem aos critérios de filtragem definidos na consulta SQL. Cada registro
        //do cursor representa uma linha da tabela, e você pode iterar sobre o cursor para acessar os valores de cada coluna desse registro.
    }

    Cursor readDataFilter_cartao(String targetDate, String bookAuthor) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Modifique a consulta para incluir a condição para a segunda coluna (book_author)
        // Adicione um operador lógico (AND) para combinar ambas as condições
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ? AND book_author = ? ORDER BY date ASC";

        // Passe ambos os valores como argumentos de seleção
        String[] selectionArgs = { targetDate, bookAuthor };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }


    Cursor readDataFilter_gastos_do_mes_por_categoria(String year_month, String nome_da_categoria) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Modifique a consulta para incluir a condição para a segunda coluna (book_author)
        // Adicione um operador lógico (AND) para combinar ambas as condições
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ? AND categoria = ? ORDER BY date ASC";

        // Passe ambos os valores como argumentos de seleção
        String[] selectionArgs = { year_month, nome_da_categoria };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    double GastoDoMesPorcategoria(String year_month, String nome_da_categoria) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta SQL para filtrar year_month, gasto_fixo e Receita_ou_despesa
        String query = "SELECT SUM(prices) FROM " + TABLE_NAME +
                " WHERE year_month = ? AND categoria = ? ";

        // Passe os valores como argumentos de seleção
        String[] selectionArgs = { year_month, nome_da_categoria };

        Cursor cursor = null;
        double total = 0;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);

            // Move para o primeiro resultado (se houver)
            if (cursor != null && cursor.moveToFirst()) {
                // Obtém a soma da coluna "prices"
                total = cursor.getDouble(0);
            }

            // Fecha o cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }


    Cursor readDataFilter_despesas_por_categoria(String year_month, String categoria) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Modifique a consulta para incluir a condição para a segunda coluna (book_author)
        // Use o operador IN para comparar com uma lista de valores possíveis
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ? AND categoria = ? AND Receita_ou_despesa IN (?, ?)";

        // Passe ambos os valores como argumentos de seleção
        String[] selectionArgs = { year_month, categoria, "DESPESA", "DESPESA COM CARTÃO" };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    Cursor readDataFilter_gasto_fixo_do_mes(String year_month, String gasto_fixo) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ? AND gasto_fixo = ?";
        String[] selectionArgs = { year_month, gasto_fixo };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    Cursor readDataFilter_nao_pagos_e_nao_recebidos_do_mes(String year_month, String pago_ou_nao_pago) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE year_month = ? AND pago_ou_nao_pago = ? ORDER BY date ASC";

        String[] selectionArgs = { year_month, pago_ou_nao_pago };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    Cursor readDataFilter_vencendo_hoje( String date_today ) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME + " WHERE date = ? ";
        String[] selectionArgs = { date_today };

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    Cursor readDataFilter_vencendo_nos_proximos_7_dias(String formattedDate_day, String formattedDate_sixDaysLater) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query SQL para selecionar os dados entre as datas e com 'pago_nao_pago' = 'NAO_PAGO'
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE date BETWEEN ? AND ? AND pago_ou_nao_pago = ? ORDER BY date ASC";
        String[] selectionArgs = {formattedDate_day, formattedDate_sixDaysLater, "NAO_PAGO"};

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }
        return cursor;
    }

    public double TotalReceitaVencendoHoje(String date_today) {
        // Calcula a soma dos itens da coluna prices, considerando os filtros year_month, Receita_ou_despesa e pago_ou_nao_pago
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o year_month e outros filtros especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE date = ? AND Receita_ou_despesa = 'RECEITA' ";

        Cursor cursor = db.rawQuery(query, new String[]{ date_today });

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum;
    }

    public double TotalDespesaVencendoHoje(String date_today) {
        // Calcula a soma dos itens da coluna prices, considerando os filtros year_month, Receita_ou_despesa e pago_ou_nao_pago
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o year_month e outros filtros especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE date = ? AND (Receita_ou_despesa = 'DESPESA' OR Receita_ou_despesa = 'DESPESA COM CARTÃO')";

        Cursor cursor = db.rawQuery(query, new String[]{ date_today });

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum;
    }

    public double TotalDespesaVencendoProximos6DiasNaoPagas(String formattedDate_day, String formattedDate_sixDaysLater) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        String query = "SELECT SUM(prices) FROM " + TABLE_NAME + " WHERE date BETWEEN ? AND ? AND (Receita_ou_despesa = 'DESPESA' OR Receita_ou_despesa = 'DESPESA COM CARTÃO') AND pago_ou_nao_pago = ?";
        String[] selectionArgs = {formattedDate_day, formattedDate_sixDaysLater, "NAO_PAGO"};

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }

        // Verifica se o cursor não é nulo e se há pelo menos uma linha de resultado
        if (cursor != null && cursor.moveToFirst()) {
            // Obtém a soma dos preços
            sum = cursor.getDouble(0);
            cursor.close(); // Fecha o cursor após o uso
        }

        return sum;
    }

    public double TotalReceitaVencendoProximos6DiasRecebidas(String formattedDate_day, String formattedDate_sixDaysLater) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        String query = "SELECT SUM(prices) FROM " + TABLE_NAME + " WHERE date BETWEEN ? AND ? AND Receita_ou_despesa = 'RECEITA'  AND pago_ou_nao_pago = ?";
        String[] selectionArgs = {formattedDate_day, formattedDate_sixDaysLater, "PAGO"};

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);
        }

        // Verifica se o cursor não é nulo e se há pelo menos uma linha de resultado
        if (cursor != null && cursor.moveToFirst()) {
            // Obtém a soma dos preços
            sum = cursor.getDouble(0);
            cursor.close(); // Fecha o cursor após o uso
        }

        return sum;
    }

    double ReceitaFixaDoMes(String year_month, String gasto_fixo) {

        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta SQL para filtrar year_month, gasto_fixo e Receita_ou_despesa
        String query = "SELECT SUM(prices) FROM " + TABLE_NAME +
                " WHERE year_month = ? AND gasto_fixo = ? AND Receita_ou_despesa = 'RECEITA'";

        // Passe os valores como argumentos de seleção
        String[] selectionArgs = { year_month, gasto_fixo };

        Cursor cursor = null;
        double total = 0;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);

            // Move para o primeiro resultado (se houver)
            if (cursor != null && cursor.moveToFirst()) {
                // Obtém a soma da coluna "prices"
                total = cursor.getDouble(0);
            }

            // Fecha o cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    double DespesaFixaDoMes(String year_month, String gasto_fixo) {

        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT SUM(prices) FROM " + TABLE_NAME +
                " WHERE year_month = ? AND gasto_fixo = ? AND (Receita_ou_despesa = 'DESPESA' OR Receita_ou_despesa = 'DESPESA COM CARTÃO')";

        // Passe os valores como argumentos de seleção
        String[] selectionArgs = { year_month, gasto_fixo };

        Cursor cursor = null;
        double total = 0;
        if (db != null) {
            cursor = db.rawQuery(query, selectionArgs);

            // Move para o primeiro resultado (se houver)
            if (cursor != null && cursor.moveToFirst()) {
                // Obtém a soma da coluna "prices"
                total = cursor.getDouble(0);
            }

            // Fecha o cursor
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    public List<String> getAllCategories(String year_month) {
        List<String> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT DISTINCT " + COLUMN_CATEGORIA + " FROM " + TABLE_NAME +
                " WHERE year_month = ? AND Receita_ou_despesa IN (?, ?)";

        // Passe os valores como argumentos de seleção
        String[] selectionArgs = { year_month, "DESPESA", "DESPESA COM CARTÃO" };

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORIA));
                    categories.add(category);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return categories;
    }

    void updateAuthor(String authorOld, String authorNew) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_AUTHOR, authorNew);

            long result = db.update(TABLE_NAME, cv, COLUMN_AUTHOR + "=?", new String[]{authorOld});
            if (result == -1) {
                String textToDisplay6 = context.getString(R.string.translate_falha_ao_atualizar);
                Toast.makeText(context, textToDisplay6, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            // Lidar com exceção, se houver
            e.printStackTrace();
        } finally {
            // Fechar o banco de dados
            if (db != null) {
                db.close();
            }
        }
    }

    void updateData(String row_id, String title, String author, String prices, String date, String year_month,String year,String gasto_fixo,String receita_ou_despesa, String parcelado, String categoria, String pago_ou_nao_pago, String hash, String red_flag_notification){
        // pages foi mudado para String pages ou int pages etc.. agora double pages

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_AUTHOR,author);
        cv.put(COLUMN_PRICES,prices);
        cv.put(COLUMN_DATE,date);
        cv.put(COLUMN_YEAR_MONTH,year_month);
        cv.put(COLUMN_YEAR,year);
        cv.put(COLUMN_GASTO_FIXO,gasto_fixo);
        cv.put(COLUMN_DESPESA_OU_RECEITA,receita_ou_despesa);
        cv.put(COLUMN_PARCELADO,parcelado);
        cv.put(COLUMN_CATEGORIA,categoria);
        cv.put(COLUMN_PAGO_NAO_PAGO,pago_ou_nao_pago);
        cv.put(COLUMN_HASH,hash);
        cv.put(COLUMN_RED_FLAG_NOTIFICATION,red_flag_notification);

        long result = db.update(TABLE_NAME,cv,"_id=?",new String[]{row_id});
        if ( result == -1 ){

            //Context context = getApplicationContext();
            String textToDisplay6 = context.getString(R.string.translate_falha_ao_atualizar);

            Toast.makeText(context,textToDisplay6, Toast.LENGTH_SHORT).show();
        }
        /*/
        else{

            String textToDisplay7 = context.getString(R.string.translate_atualizado_com_sucesso);
            Toast.makeText(context,textToDisplay7, Toast.LENGTH_SHORT).show();
        }
        /*/

        db.close();
    }

    void updateDataWithoutGastoFixo(String row_id, String title, String author, String prices, String date, String year_month,String year,String receita_ou_despesa, String parcelado, String categoria, String pago_ou_nao_pago, String hash, String red_flag_notification){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_TITLE,title);
        cv.put(COLUMN_AUTHOR,author);
        cv.put(COLUMN_PRICES,prices);
        cv.put(COLUMN_DATE,date);
        cv.put(COLUMN_YEAR_MONTH,year_month);
        cv.put(COLUMN_YEAR,year);
        cv.put(COLUMN_DESPESA_OU_RECEITA,receita_ou_despesa);
        cv.put(COLUMN_PARCELADO,parcelado);
        cv.put(COLUMN_CATEGORIA,categoria);
        cv.put(COLUMN_PAGO_NAO_PAGO,pago_ou_nao_pago);
        cv.put(COLUMN_HASH,hash);
        cv.put(COLUMN_RED_FLAG_NOTIFICATION,red_flag_notification);

        long result = db.update(TABLE_NAME,cv,"_id=?",new String[]{row_id});
        if ( result == -1 ){

            //Context context = getApplicationContext();
            String textToDisplay6 = context.getString(R.string.translate_falha_ao_atualizar);

            Toast.makeText(context,textToDisplay6, Toast.LENGTH_SHORT).show();
        }

        db.close();
    }

    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
            if (result == -1) {
                String textToDisplay8 = context.getString(R.string.translate_falha_ao_deletar);
                Toast.makeText(context, textToDisplay8, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            // Lidar com exceção, se houver
            e.printStackTrace();
        } finally {
            // Fechar o banco de dados
            if (db != null) {
                db.close();
            }
        }
    }


    public boolean isDatabaseVersion1() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.getVersion() == 1;
    }

    public boolean areAllColumnsNullById(long itemId) {

        // Usar essa função para preencher os databases mais antigos,pois tem 22 usuarios utilizando. e está dando erro ao apagar a função.
        // NAO PRECISO USAR MAIS. POIS AGORA É OUTRO APP.

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"gasto_fixo", "Receita_ou_despesa", "parcelado_ou_nao_parcelado", "hash"};
        String selection = "_id=?";
        String[] selectionArgs = {String.valueOf(itemId)};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        boolean allColumnsNull = true;

        if (cursor.moveToFirst()) {
            for (String columnName : columns) {
                int columnIndex = cursor.getColumnIndex(columnName);
                if (!cursor.isNull(columnIndex)) {
                    allColumnsNull = false;
                    break;
                }
            }
        }

        cursor.close();
        return allColumnsNull;
    }

    public void deleteRowsWithSameHash(String hash) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            long result = db.delete(TABLE_NAME, "hash=?", new String[]{hash});
            if (result == -1) {
                String textToDisplay8 = context.getString(R.string.translate_falha_ao_deletar);
                Toast.makeText(context, textToDisplay8, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            // Lidar com exceção, se houver
            e.printStackTrace();
        } finally {
            // Fechar o banco de dados
            if (db != null) {
                db.close();
            }
        }
    }


    public boolean hasDuplicateHash(String hashValue) {
        // pega os hash que estao duplicados e retorna true se achar algum duplicado, isso é para verificar se o item esta parcelado ou fixo.
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {"_id"};
        String selection = "hash=?";
        String[] selectionArgs = {hashValue};

        Cursor cursor = db.query(TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        int rowCount = cursor.getCount();
        cursor.close();

        // Se rowCount for maior que 1, existem duplicatas
        return rowCount > 1;
    }

    public String getHashById(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"hash"}; // Colunas que você deseja recuperar

        Cursor cursor = db.query(TABLE_NAME, columns, "_id=?", new String[]{rowId}, null, null, null);
        String hash = "";

        if (cursor.moveToFirst()) {
            hash = cursor.getString(cursor.getColumnIndex("hash"));
        }

        cursor.close();
        return hash;
    }

    public String getParceladoStatusById(String rowId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {"parcelado_ou_nao_parcelado"}; // Coluna que você deseja recuperar

        Cursor cursor = db.query(TABLE_NAME, columns, "_id=?", new String[]{rowId}, null, null, null);
        String parceladoStatus = "";

        if (cursor.moveToFirst()) {
            parceladoStatus = cursor.getString(cursor.getColumnIndex("parcelado_ou_nao_parcelado"));
        }

        cursor.close();
        return parceladoStatus;
    }

    public void deleteRowsWithSameHashAndDate(String hash, String date) {

        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();

            String selection = "hash=? AND date >= ?";
            String[] selectionArgs = {hash, date};

            long result = db.delete(TABLE_NAME, selection, selectionArgs);

            if (result == -1) {
                String textToDisplay8 = context.getString(R.string.translate_falha_ao_deletar);
                Toast.makeText(context, textToDisplay8, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            // Lidar com exceção, se houver
            e.printStackTrace();
        } finally {
            // Fechar o banco de dados
            if (db != null) {
                db.close();
            }
        }
    }


    public double sumPricesForYearMonth(String yearMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum;
    }

    public double sumPricesForYearMonthAndType(String yearMonth, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ? AND Receita_ou_despesa = ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, type});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como: double despesaTotal = sumPricesForYearMonthAndType("2023-11", "DESPESA");
        return sum;

    }

    public double DespesasDoMesPagas(String yearMonth, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ? AND Receita_ou_despesa = ? AND pago_ou_nao_pago = ?  ";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, type, "PAGO"});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como: double despesaTotal = sumPricesForYearMonthAndType("2023-11", "DESPESA");
        return sum;

    }

    public double despesasNaoPagasDoMes(String yearMonth) {
        // Calcula a soma dos itens da coluna prices, considerando os filtros year_month, Receita_ou_despesa e pago_ou_nao_pago
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o year_month e outros filtros especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ? AND (Receita_ou_despesa = ? OR Receita_ou_despesa = ?) AND pago_ou_nao_pago = ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, "DESPESA", "DESPESA COM CARTÃO", "NAO_PAGO"});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum;
    }

    public double receitasNaoRecebidasDoMes(String yearMonth) {
        // Calcula a soma dos itens da coluna prices, considerando os filtros year_month, Receita_ou_despesa e pago_ou_nao_pago
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o year_month e outros filtros especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ? AND Receita_ou_despesa = ? AND pago_ou_nao_pago = ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, "RECEITA", "NAO_PAGO"});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum;
    }

    public double DespesasAcumuladaPagas(String yearMonth, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month <= ? AND Receita_ou_despesa = ? AND pago_ou_nao_pago = ?  ";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, type, "PAGO"});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como: double despesaTotal = sumPricesForYearMonthAndType("2023-11", "DESPESA");
        return sum;

    }

    public double ReceitasRecebidasDoMes(String yearMonth, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ? AND Receita_ou_despesa = ? AND pago_ou_nao_pago = ?  ";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, type, "PAGO"});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como: double despesaTotal = sumPricesForYearMonthAndType("2023-11", "DESPESA");
        return sum;

    }

    public double ReceitasRecebidasAcumulada(String yearMonth, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month <= ? AND Receita_ou_despesa = ? AND pago_ou_nao_pago = ?  ";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, type, "PAGO"});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como: double despesaTotal = sumPricesForYearMonthAndType("2023-11", "DESPESA");
        return sum;

    }

    public double sumPricesForMonth(String yearMonth) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum2 = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month <= ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth});

        if (cursor.moveToFirst()) {
            sum2 = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum2;
    }

    public double somaPrecosAteAqui(String yearMonth, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month <= ? AND Receita_ou_despesa = ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, type});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como, por exemplo: double despesaTotal = somaPrecosAteAqui("2023-11", "DESPESA");
        return sum;

    }

    public double somaFaturaDoMesPorCartao(String yearMonth, String book_author, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year_month = ? AND book_author = ? AND Receita_ou_despesa = ?";

        Cursor cursor = db.rawQuery(query, new String[]{yearMonth, book_author, type});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como, por exemplo: double despesaTotal = somaPrecosAteAqui("2023-11", "DESPESA");
        return sum;
    }

    public double somaTotalGastoNoCartao(String book_author) {

        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE book_author = ? ";

        Cursor cursor = db.rawQuery(query, new String[]{book_author});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como, por exemplo: double despesaTotal = somaPrecosAteAqui("2023-11", "DESPESA");
        return sum;

    }

    public double obterTotalNaoPago(String book_author, String pago_nao_pago) {

        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE book_author = ? AND pago_ou_nao_pago = ? ";

        Cursor cursor = db.rawQuery(query, new String[]{book_author,pago_nao_pago});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como, por exemplo: double despesaTotal = somaPrecosAteAqui("2023-11", "DESPESA");
        return sum;

    }

    public double obterTotalNaoPagoSemGastoFixo(String book_author, String pago_nao_pago) {

        SQLiteDatabase db = this.getReadableDatabase();
        double sum1 = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE book_author = ? AND pago_ou_nao_pago = ? AND gasto_fixo = 'NAO'";

        Cursor cursor = db.rawQuery(query, new String[]{book_author, pago_nao_pago});

        if (cursor.moveToFirst()) {
            sum1 = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como, por exemplo: double despesaTotal = somaPrecosAteAqui("2023-11", "DESPESA");
        return sum1;

    }

    public double obterTotalNaoPagoComGastoFixo(String book_author, String pago_ou_nao_pago) {

        SQLiteDatabase db = this.getReadableDatabase();
        double sum2 = 0;

        // Obtém o ano, mês e dia atuais
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Os meses são indexados de 0 a 11, então somamos 1 para obter o mês atual
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Consulta principal para somar os preços dos itens mais recentes
        String query = "SELECT SUM(prices) " +
                "FROM my_library " +
                "WHERE book_author = ? AND pago_ou_nao_pago = ? AND gasto_fixo = 'SIM' " +
                "AND date(date) <= date(?, 'start of month', '+1 month', '-1 day')";

        // Formata a data atual como uma string YYYY-MM-DD
        String currentDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month, day);

        Cursor cursor = db.rawQuery(query, new String[]{book_author, pago_ou_nao_pago, currentDate});
        if (cursor.moveToFirst()) {
            sum2 = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return sum2;
    }

    public double obterSomaTotal(String book_author, String pago_nao_pago) {
        double somaTotal = 0;

        // Obtém a soma total de preços com "gasto_fixo = 'SIM'"
        double somaGastoFixo = obterTotalNaoPagoComGastoFixo(book_author, pago_nao_pago);

        // Obtém a soma total de preços sem considerar "gasto_fixo"
        double somaSemGastoFixo = obterTotalNaoPagoSemGastoFixo(book_author, pago_nao_pago);

        // Soma as duas somas parciais
        somaTotal = somaGastoFixo + somaSemGastoFixo;

        // Retorna a soma total
        return somaTotal;
    }

    public double sumPricesForYear(String year) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum_total_year = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados
        String query = "SELECT SUM(prices) FROM my_library WHERE year = ?";

        Cursor cursor = db.rawQuery(query, new String[]{year});

        if (cursor.moveToFirst()) {
            sum_total_year = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        return sum_total_year;
    }

    public double sumPricesForYearReceitaDespesa(String year, String type) {
        // Calcula a soma dos itens da coluna prices levando em conta o year_month e Receita_ou_despesa. Por exemplo, "2023-11", "DESPESA" ou "2023-11", "RECEITA"
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Crie a consulta SQL para somar os preços para o ano e mês especificados e tipo de receita/despesa
        String query = "SELECT SUM(prices) FROM my_library WHERE year = ? AND Receita_ou_despesa = ?";

        Cursor cursor = db.rawQuery(query, new String[]{year, type});

        if (cursor.moveToFirst()) {
            sum = cursor.getDouble(0);
        }
        cursor.close();
        db.close(); // Feche o banco de dados quando terminar de usá-lo

        // Chamamos como, por exemplo: double despesaTotal = somaPrecosAteAqui("2023-11", "DESPESA");
        return sum;

    }

    public void marcarComoPago(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("pago_ou_nao_pago", "PAGO");

        db.update(TABLE_NAME, values, "_id=?", new String[]{itemId});

        db.close();
    }

    public void marcarComoNaoPago(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("pago_ou_nao_pago", "NAO_PAGO");

        db.update(TABLE_NAME, values, "_id=?", new String[]{itemId});

        db.close();
    }

    public void marcarParaNotificar(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("red_flag_notification", "NOTIFICAR");

        db.update(TABLE_NAME, values, "_id=?", new String[]{itemId});

        db.close();
    }

    public void marcarParaNaoNotificar(String itemId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("red_flag_notification", "NAO_NOTIFICAR");

        db.update(TABLE_NAME, values, "_id=?", new String[]{itemId});

        db.close();
    }

    public void deleteRowsWithNullOrEmptyPrices() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_PRICES + " IS NULL OR " + COLUMN_PRICES + " = ?", new String[]{""});
        db.close();
    }

    //-------INIT FILTRANDO POR TODOS OS ANOS ------//

    // Método para obter anos distintos
    public List<String> obterAnosDistintos() {
        List<String> anos = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Consulta SQL para obter anos distintos
        String query = "SELECT DISTINCT year FROM my_library ORDER BY year";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            String ano = cursor.getString(cursor.getColumnIndex("year"));
            anos.add(ano);
        }

        cursor.close();
        db.close();

        return anos;
    }
    //------- FIM FILTRANDO POR TODOS OS ANOS ------//


}//class MyDatabaseHelper extends SQLiteOpenHelper