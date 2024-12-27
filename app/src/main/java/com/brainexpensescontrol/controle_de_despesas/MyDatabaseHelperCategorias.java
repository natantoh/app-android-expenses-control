package com.brainexpensescontrol.controle_de_despesas;
import android.app.DownloadManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase; // Classe que contém os métodos de manipulação dos dados no banco, por isso temos que importar
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper; // Classe responsável pela criação do banco e também responsável pelo versionamento do mesmo.
import android.icu.text.CaseMap;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

class MyDatabaseHelperCategorias extends SQLiteOpenHelper {  // Primeiro passo então, é herdar todos os métodos da classe SQLiteOpenHelper, para acessar seus métodos.
    // Defina as constantes para nome do banco, versão, tabelas, etc.
    private Context context;
    private static final String DATABASE_NAME = "Minhas_Categorias.db";

    //Certifique-se de aumentar o número de versão sempre que fizer alterações no esquema do banco de dados que precisem
    //ser refletidas em versões futuras do aplicativo. Isso permitirá que os usuários atualizem o aplicativo sem perder os dados existentes.
    private static final int DATABASE_VERSION = 1; // Aumente o número da versão

    public static final String TABLE_NAME = "my_library_3"; //  Nome do Banco de Dados.db
    private static final String COLUMN_ID = "_id"; // id da coluna, que será automaticamente incrementado.
    public  static final String COLUMN_NOME_DA_CATEGORIA= "nome_da_categoria";


    MyDatabaseHelperCategorias(@Nullable Context context) {
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
                COLUMN_NOME_DA_CATEGORIA + " TEXT);";
        db.execSQL(query);


        String textToDisplay1 = context.getString(R.string.translate_categoria_supermercado);
        String textToDisplay2 = context.getString(R.string.translate_categoria_comer_fora);
        String textToDisplay3 = context.getString(R.string.translate_categoria_viagem);
        String textToDisplay4 = context.getString(R.string.translate_categoria_combustivel);
        String textToDisplay5 = context.getString(R.string.translate_categoria_gasto_com_o_carro);
        String textToDisplay6 = context.getString(R.string.translate_categoria_festas);
        String textToDisplay7 = context.getString(R.string.translate_categoria_academia);
        String textToDisplay8 = context.getString(R.string.translate_categoria_conta_de_luz);
        String textToDisplay9 = context.getString(R.string.translate_categoria_pix_com_cartao);
        String textToDisplay10 = context.getString(R.string.translate_categoria_aluguel);
        String textToDisplay11 = context.getString(R.string.translate_categoria_outras);

        // Adicione os dados no método onCreate
        addCategoria(db, textToDisplay1);
        addCategoria(db, textToDisplay2);
        addCategoria(db, textToDisplay3);
        addCategoria(db, textToDisplay4);
        addCategoria(db, textToDisplay5);
        addCategoria(db, textToDisplay6);
        addCategoria(db, textToDisplay7);
        addCategoria(db, textToDisplay8);
        addCategoria(db, textToDisplay9);
        addCategoria(db, textToDisplay10);
        addCategoria(db, textToDisplay11);


//        // Adicione os dados no método onCreate
//        addCategoria(db, "SUPERMERCADO");
//        addCategoria(db, "COMER FORA");
//        addCategoria(db, "BARZINHO");
//        addCategoria(db, "VIAGEM");
//        addCategoria(db, "COMBUSTIVEL");
//        addCategoria(db, "GASTOS COM O CARRO");
//        addCategoria(db, "FESTAS");
//        addCategoria(db, "ROUPAS");
//        addCategoria(db, "INFORMATICA");
//        addCategoria(db, "ACADEMIA");
//        addCategoria(db, "CONTA DE LUZ");
//        addCategoria(db, "PIX COM CARTÃO");
//        addCategoria(db, "ALUGUEL");
//        addCategoria(db, "OUTROS");

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
            //db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_NOME_DA_CATEGORIA + " TEXT;");
        }
    }

    private void addCategoria(SQLiteDatabase db, String nome_da_categoria) {
        // Método para adicionar dados à tabela previamente durante a primeira instalação do app..
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOME_DA_CATEGORIA, nome_da_categoria);
        db.insert(TABLE_NAME, null, values);
    }

    long addBook(String nome_da_categoria) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar se a categoria já existe
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOME_DA_CATEGORIA + " = ?", new String[]{nome_da_categoria});

        if (cursor.getCount() > 0) {
            // Se a categoria já existe, exibir mensagem e sair
            String textToDisplay = "A categoria '" + nome_da_categoria + "' já existe.";
            Toast.makeText(context, textToDisplay, Toast.LENGTH_SHORT).show();

            cursor.close();
            db.close();
            return -1;
        }

        // Se a categoria não existe, prosseguir com a adição
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_DA_CATEGORIA, nome_da_categoria);

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

        // Fechar o cursor e o banco de dados
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


    long updateData(String row_id, String nome_da_categoria) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Verificar se a categoria já existe
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NOME_DA_CATEGORIA + " = ? AND _id != ?", new String[]{nome_da_categoria, row_id});

        if (cursor.getCount() > 0) {
            // Se a categoria já existe, exibir mensagem e sair
            String textToDisplay = "A categoria '" + nome_da_categoria + "' já existe. Não é possível atualizar com um nome de categoria que já existe.";
            Toast.makeText(context, textToDisplay, Toast.LENGTH_SHORT).show();

            cursor.close();
            db.close();
            return -1;
        }

        // Se a categoria não existe, prosseguir com a atualização
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_NOME_DA_CATEGORIA, nome_da_categoria);

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

        // Fechar o cursor e o banco de dados
        cursor.close();
        db.close();

        return result;
    }


    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
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
                db.close(); // Fechando o banco de dados
            }
        }
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


    // Método para obter opções do banco de dados
    public List<String> getOptionsFromDatabase() {
        List<String> optionsList_categoria = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_NOME_DA_CATEGORIA}; // Substitua COLUMN_NOME_DA_CATEGORIApelo nome correto da coluna no seu banco de dados

        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            String option = cursor.getString(cursor.getColumnIndex(COLUMN_NOME_DA_CATEGORIA));
            optionsList_categoria.add(option);
        }

        cursor.close();
        db.close();

        return optionsList_categoria;
    }

    public boolean isDatabaseVersion1() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.getVersion() == 1;
    }



}//class MyDatabaseHelperCategorias extends SQLiteOpenHelper
