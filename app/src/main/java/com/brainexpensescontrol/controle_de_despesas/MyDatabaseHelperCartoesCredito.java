package com.brainexpensescontrol.controle_de_despesas;

import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; // Classe que contém os métodos de manipulação dos dados no banco, por isso temos que importar
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper; // Classe responsável pela criação do banco e também responsável pelo versionamento do mesmo.
import android.icu.text.CaseMap;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class MyDatabaseHelperCartoesCredito extends SQLiteOpenHelper {  // Primeiro passo então, é herdar todos os métodos da classe SQLiteOpenHelper, para acessar seus métodos.
    private Context context;
    private static final String DATABASE_NAME = "Meus_Cartoes_De_Credito.db";

    MyDatabaseHelper myDB;

    //Certifique-se de aumentar o número de versão sempre que fizer alterações no esquema do banco de dados que precisem
    //ser refletidas em versões futuras do aplicativo. Isso permitirá que os usuários atualizem o aplicativo sem perder os dados existentes.
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "my_library_2";
    private static final String COLUMN_ID = "_id";
    public  static final String COLUMN_NOME_DO_CARTAO = "nome_do_cartao";
    private static final String COLUMN_LIMITE_DO_CARTAO = "limite_do_cartao";
    private static final String COLUMN_LIMITE_UTILIZADO_DO_CARTAO = "limite_utilizado_do_cartao";
    private static final String COLUMN_LIMITE_DISPONIVEL_DO_CARTAO = "limite_disponivel_do_cartao";
    public static final String COLUMN_DATA_DE_VENC_FATURA = "data_venc_fatura";

    MyDatabaseHelperCartoesCredito(@Nullable Context context) {
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
                COLUMN_NOME_DO_CARTAO + " TEXT, " +
                COLUMN_LIMITE_DO_CARTAO + " TEXT, " +
                COLUMN_LIMITE_UTILIZADO_DO_CARTAO + " TEXT, " +
                COLUMN_LIMITE_DISPONIVEL_DO_CARTAO + " TEXT, " +
                COLUMN_DATA_DE_VENC_FATURA + " TEXT);";
        db.execSQL(query);


        // Adicione dados iniciais ao banco de dados. Linha com problema!!!!!!!!!!!!!!!!!!!!!!!!
        //addBook("SEM CARTÃO", "20.00","20.00","20.00","2023-01-02");

        //addBook("Cartão Samsung Itaucard", "$20.00", "2023-01-02");
        //addBook("Cartão Santander Elite", "$30.00", "2023-01-03");

    }
    /*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Lide com atualizações do banco, se necessário
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Este trecho de código está excluindo a tabela especificada (TABLE_NAME) se ela existir. Em outras palavras, ele está descartando a tabela inteira e todos os dados dentro dela. Isso é geralmente usado quando você precisa fazer uma atualização de banco de dados que envolve uma mudança significativa na estrutura da tabela ou quando você deseja recriar a tabela do zero. No entanto, isso resultará na perda de todos os dados existentes na tabela.
        onCreate(db); // Após excluir a tabela (se ela existir), este código chama o método onCreate para criar novamente a tabela de acordo com a definição fornecida dentro do método onCreate. Isso efetivamente cria uma nova tabela no banco de dados, substituindo a tabela antiga que foi excluída na etapa anterior.
    }
    /*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            // Se a versão antiga for menor que 1, adicione todas as novas colunas ao banco de dados
            //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_GASTO_FIXO + " TEXT;");
            //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_DESPESA_OU_RECEITA + " TEXT;");
            //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_PARCELADO + " TEXT;");
            //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NOME_DO_CARTAO + " TEXT;");
        }
    }

    long addBook(String nome_do_cartao, String limite_do_cartao, String limite_utilizado_do_cartao, String limite_disponivel_do_cartao, String data_venc_cartao) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar se o cartão já existe
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOME_DO_CARTAO + " = ?", new String[]{nome_do_cartao});

        if (cursor.getCount() > 0) {
            // Se o cartão já existe, exibir mensagem e não adicionar
            String textToDisplay = "O cartão com o nome '" + nome_do_cartao + "' já existe. Não é possível adicionar um cartão com o mesmo nome.";
            Toast.makeText(context, textToDisplay, Toast.LENGTH_SHORT).show();
            cursor.close();
            db.close();
            return -1;
        }

        // Se o cartão não existe, prosseguir com a adição
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_DO_CARTAO, nome_do_cartao);
        cv.put(COLUMN_LIMITE_DO_CARTAO, limite_do_cartao);
        cv.put(COLUMN_LIMITE_UTILIZADO_DO_CARTAO, limite_utilizado_do_cartao);
        cv.put(COLUMN_LIMITE_DISPONIVEL_DO_CARTAO, limite_disponivel_do_cartao);
        cv.put(COLUMN_DATA_DE_VENC_FATURA, data_venc_cartao);

        long result = db.insert(TABLE_NAME, null, cv);
        if (result == -1) {
            String textToDisplay_falhou = context.getString(R.string.translate_falhou);
            Toast.makeText(context, textToDisplay_falhou, Toast.LENGTH_SHORT).show();

            cursor.close();
            db.close();
            return -1;
        } else {
            String textToDisplay_added_sucesso = context.getString(R.string.translate_adicionado_com_sucesso);
            Toast.makeText(context, textToDisplay_added_sucesso, Toast.LENGTH_SHORT).show();
        }

        cursor.close();
        db.close();
        return result;
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

    long updateData(String row_id, String nome_do_cartao, String limite_do_cartao, String data_venc_cartao) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar se o cartão já existe
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOME_DO_CARTAO + " = ? AND _id != ?", new String[]{nome_do_cartao, row_id});

        if (cursor.getCount() > 0) {
            // Se o cartão já existe, exibir mensagem e não atualizar
            String textToDisplay = "O cartão com o nome '" + nome_do_cartao + "' já existe. Não é possível atualizar o cartão com o mesmo nome.";
            Toast.makeText(context, textToDisplay, Toast.LENGTH_SHORT).show();

            cursor.close();
            db.close();
            return -1;
        }

        // Se o cartão não existe, prosseguir com a atualização
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_DO_CARTAO, nome_do_cartao);
        cv.put(COLUMN_LIMITE_DO_CARTAO, limite_do_cartao);
        cv.put(COLUMN_DATA_DE_VENC_FATURA, data_venc_cartao);

        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            String textToDisplay6 = context.getString(R.string.translate_falha_ao_atualizar);
            Toast.makeText(context, textToDisplay6, Toast.LENGTH_SHORT).show();

            cursor.close();
            db.close();
            return -1;
        } else {
            String textToDisplay7 = context.getString(R.string.translate_atualizado_com_sucesso);
            Toast.makeText(context, textToDisplay7, Toast.LENGTH_SHORT).show();
        }


        cursor.close();
        db.close();
        return result;
    }

    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
            if (result == -1) {
                String textToDisplay8 = context.getString(R.string.translate_falha_ao_deletar);
                Toast.makeText(context, textToDisplay8, Toast.LENGTH_SHORT).show();
            } else{
            String textToDisplay9 = context.getString(R.string.translate_deletado_com_sucesso);
            Toast.makeText(context, textToDisplay9, Toast.LENGTH_SHORT).show();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    public void atualizarLimiteUtilizado(String author_2, String despesa_total_do_cartao_nao_paga) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("limite_utilizado_do_cartao", despesa_total_do_cartao_nao_paga);

        db.update(TABLE_NAME, values, "nome_do_cartao=?", new String[]{author_2});
        db.close();
    }

    public void atualizarLimiteDisponivel(String author_2, String string_limite_disponivel_do_cartao) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("limite_disponivel_do_cartao", string_limite_disponivel_do_cartao);

        db.update(TABLE_NAME, values, "nome_do_cartao=?", new String[]{author_2});
        db.close();
    }

    public double obterLimiteDoCartao(String nomeDoCartao) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Colunas que você deseja recuperar
        String[] colunas = {"limite_do_cartao"};

        // Condição WHERE para filtrar pelo nome_do_cartao
        String whereClause = "nome_do_cartao = ?";

        // Argumentos para a condição WHERE
        String[] whereArgs = {nomeDoCartao};

        // Realize a consulta SELECT
        Cursor cursor = db.query(TABLE_NAME, colunas, whereClause, whereArgs, null, null, null);

        double limiteDoCartao = 0;

        // Verifique se há resultados e obtenha o valor da coluna "limite_do_cartao"
        if (cursor.moveToFirst()) {
            limiteDoCartao = cursor.getDouble(cursor.getColumnIndex("limite_do_cartao"));
        }

        cursor.close();
        db.close();
        return limiteDoCartao;
    }

    public double calculaSomaDosItensDaColuna(String nome_da_coluna) {
        SQLiteDatabase db = this.getReadableDatabase();
        double sum = 0;

        // Utilize a função SUM diretamente na consulta SQL
        String query = "SELECT SUM(" + nome_da_coluna + ") FROM my_library_2";

        try (Cursor cursor = db.rawQuery(query, null)) {
            if (cursor.moveToFirst()) {
                sum = cursor.getDouble(0);
            }
        } finally {
            db.close();
        }
        return sum;
    }

    public String getCategoriaById(String id) {
        String categoria = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Ajuste o nome da tabela e o nome da coluna conforme necessário
        String query = "SELECT categoria FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id});

        if (cursor.moveToFirst()) {
            categoria = cursor.getString(cursor.getColumnIndex("categoria"));
        }

        cursor.close();
        db.close();
        return categoria;
    }

    public List<String> getOptionsFromDatabase() {
        List<String> optionsList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NOME_DO_CARTAO}; // Substitua COLUMN_NOME_DO_CARTAO pelo nome correto da coluna no seu banco de dados

        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String option = cursor.getString(cursor.getColumnIndex(COLUMN_NOME_DO_CARTAO));
            optionsList.add(option);
        }

        cursor.close();
        db.close();
        return optionsList;
    }

    public boolean isDatabaseVersion1() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.getVersion() == 1;
    }

    public void deleteRowsWithNullOrEmptyPrices() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_LIMITE_DO_CARTAO + " IS NULL OR " + COLUMN_LIMITE_DO_CARTAO + " = ?", new String[]{""});
        db.close();
    }
}
