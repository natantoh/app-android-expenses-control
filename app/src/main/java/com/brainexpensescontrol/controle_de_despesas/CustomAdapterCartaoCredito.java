package com.brainexpensescontrol.controle_de_despesas;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import android.content.Context;
import android.widget.ArrayAdapter;

public class CustomAdapterCartaoCredito extends RecyclerView.Adapter<CustomAdapterCartaoCredito.MyViewHolder> {

    MyDatabaseHelper myDB;
    private Context context;
    Activity activity;
    private ArrayList book_id, book_title, prices,prices2,prices3, book_date;

    CustomAdapterCartaoCredito(Activity activity, Context context, ArrayList book_id, ArrayList book_title, ArrayList prices, ArrayList prices2, ArrayList prices3, ArrayList book_date){
        this.activity = activity;
        this.context = context;
        this.book_id = book_id;
        this.book_title = book_title;
        this.prices = prices;
        this.prices2 = prices2;
        this.prices3 = prices3;
        this.book_date = book_date;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((context));
        View view = inflater.inflate(R.layout.my_row_cartao_credito,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.book_id_txt.setText(String.valueOf(book_id.get(position)));
        holder.book_title_txt.setText(String.valueOf(book_title.get(position)));
        NumberFormat numberFormat = NumberFormat.getNumberInstance();

        numberFormat.setMinimumFractionDigits(2);
        String prices_Formatado_virgula = String.valueOf(prices.get(position));
        double valor = Double.parseDouble(prices_Formatado_virgula);
        String formattedtotalSum20 = numberFormat.format(valor);
        holder.prices_txt.setText(formattedtotalSum20);


        numberFormat.setMinimumFractionDigits(2);
        String prices_Formatado_virgula2 = String.valueOf(prices2.get(position));
        double valor2 = Double.parseDouble(prices_Formatado_virgula2);
        String formattedtotalSum2 = numberFormat.format(valor2);
        holder.prices_txt2.setText(formattedtotalSum2);


        numberFormat.setMinimumFractionDigits(2);
        String prices_Formatado_virgula3 = String.valueOf(prices3.get(position));
        double valor3 = Double.parseDouble(prices_Formatado_virgula3);
        String formattedtotalSum3 = numberFormat.format(valor3);
        holder.prices_txt3.setText(formattedtotalSum3);


        // Obtenha a moeda selecionada usando PreferencesUtil
        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context);

        String textToDisplay80 = context.getString(R.string.translate_total);
        String textToDisplay81 = context.getString(R.string.translate_utilizado);
        String textToDisplay82 = context.getString(R.string.translate_disponivel);

        String text_saldo_total = textToDisplay80 + " " + moedaSelecionada + ": ";

        holder.moeda.setText(text_saldo_total);

        String text_saldo_utilizado2 = textToDisplay81 + " " + moedaSelecionada + ": ";
        holder.moeda2.setText(text_saldo_utilizado2);

        String text_saldo_disponivel3 = textToDisplay82 + " " + moedaSelecionada + ": ";
        holder.moeda3.setText(text_saldo_disponivel3);




        //double despesa_deste_mes = myDB.somaTotalGastoNoCartao(String.valueOf(book_title.get(position)));

        //holder.prices_txt2.setText("EAE");

        //holder.prices_txt2.setText(String.valueOf(prices2.get(position)));
        //holder.prices_txt3.setText(String.valueOf(prices3.get(position)));

        //-------------------------INICIO SETANDO A DATA----------------------------------
        // Supondo que book_date.get(position) seja uma String no formato "2023-09-15"
        String dataNoFormatoOriginal = String.valueOf(book_date.get(position));
        try {
            // Formate a data no formato desejado para exibir para o usuario. assim na tela fica apresentando no formato 22/set/2023  enquando no banco de dados fica 2023-09-22
            SimpleDateFormat formatoOriginal = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MMM/yyyy");

            Date data = formatoOriginal.parse(dataNoFormatoOriginal);
            String dataFormatada = formatoDesejado.format(data);

            // Agora, você pode definir o texto no TextView
            holder.book_date_txt.setText(dataFormatada);
        } catch (ParseException e) {
            // Lidar com exceções de formatação de data, se necessário
            e.printStackTrace();
        }
        //-------------------------FIM SETANDO A DATA----------------------------------

        holder.mainLayoutCartaoCredito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Passe os valores como extras para a UpdateActivity
                Intent intent = new Intent(context,UpdateCartaoCredito.class);
                intent.putExtra("id",String.valueOf(book_id.get(position)));
                intent.putExtra("title",String.valueOf(book_title.get(position)));
                intent.putExtra("prices",String.valueOf(prices.get(position)));
                intent.putExtra("book_date",String.valueOf(book_date.get(position)));

                activity.startActivityForResult(intent, 1);
            }
        });



    }

    @Override
    public int getItemCount() {

        return book_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView book_id_txt, book_title_txt, prices_txt,prices_txt2, prices_txt3,book_date_txt , moeda, moeda2, moeda3;
        LinearLayout mainLayoutCartaoCredito;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_id_txt = itemView.findViewById(R.id.book_id_txt);
            book_title_txt = itemView.findViewById(R.id.book_title_txt);
            prices_txt = itemView.findViewById(R.id.prices_txt);
            prices_txt2 = itemView.findViewById(R.id.prices_txt2);
            prices_txt3 = itemView.findViewById(R.id.prices_txt3);
            book_date_txt = itemView.findViewById(R.id.book_date_txt);

            moeda = itemView.findViewById(R.id.moeda);
            moeda2 = itemView.findViewById(R.id.moeda2);
            moeda3 = itemView.findViewById(R.id.moeda3);


            mainLayoutCartaoCredito = itemView.findViewById(R.id.mainLayoutCartaoCredito);
        }
    }
}
