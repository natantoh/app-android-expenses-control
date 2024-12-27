package com.brainexpensescontrol.controle_de_despesas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import android.content.Context;
import android.widget.ArrayAdapter;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    Activity activity;
    private ArrayList book_id, book_title, book_author, prices, book_date, book_categoria,book_despesa_ou_receita,book_pago_ou_nao_pago,book_red_flag_notification;
    private static final int COR_PURPLE_700 = R.color.purple_700;
    private static final int COR_RED = R.color.dark_red;
    private static final int COR_TRANSPARENT = android.R.color.transparent;
    private String dataAtualFormatada42;
    private SimpleDateFormat formatoOriginal42 = new SimpleDateFormat("yyyy-MM-dd");
    private List<Pair<Integer, Integer>> positionsAndColors = new ArrayList<>();
    private List<Pair<Integer, Integer>> positionsAndColors3 = new ArrayList<>();

    CustomAdapter(Activity activity, Context context, ArrayList book_id, ArrayList book_title, ArrayList book_author, ArrayList prices, ArrayList book_date, ArrayList book_despesa_ou_receita,  ArrayList book_categoria,ArrayList book_pago_ou_nao_pago,ArrayList book_red_flag_notification){
        this.activity = activity;
        this.context = context;
        this.book_id = book_id;
        this.book_title = book_title;
        this.book_author = book_author;
        this.prices = prices;
        this.book_date = book_date;
        this.book_despesa_ou_receita = book_despesa_ou_receita;
        this.book_categoria = book_categoria;
        this.book_pago_ou_nao_pago = book_pago_ou_nao_pago;
        this.book_red_flag_notification = book_red_flag_notification;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((context));
        View view = inflater.inflate(R.layout.my_row,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        String receita_ou_despesa = String.valueOf(book_author.get(position));
        String dataNoFormatoOriginal42 = String.valueOf(book_date.get(position));
        String pago_ou_nao_pago = String.valueOf(book_pago_ou_nao_pago.get(position));
        String red_flag_notification = String.valueOf(book_red_flag_notification.get(position));

        if ("RECEITA".equals( receita_ou_despesa) ) {
            holder.book_author_txt.setText(    context.getString(R.string.translate_spinnerOptions_receita)  );
            holder.book_author_txt.setTextColor(ContextCompat.getColor(context, R.color.verde));
        } else if("DESPESA".equals(  receita_ou_despesa    ) )  {

            holder.book_author_txt.setText(    context.getString(R.string.translate_spinnerOptions_despesa)  );
            holder.book_author_txt.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        else {
            holder.book_author_txt.setText( receita_ou_despesa   );
            holder.book_author_txt.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        if ("PAGO".equals(pago_ou_nao_pago)) {
            holder.paidIcon.setVisibility(View.VISIBLE);
            holder.book_date_txt.setTextColor(Color.BLACK);
        } else {
            holder.paidIcon.setVisibility(View.INVISIBLE);

            Date dataAtual42 = new Date();
            dataAtualFormatada42 = formatoOriginal42.format(dataAtual42);
            if (dataAtualFormatada42.compareTo(dataNoFormatoOriginal42) > 0  ) {
                holder.book_date_txt.setTextColor(Color.RED);
            } else{
                holder.book_date_txt.setTextColor(Color.BLACK);
            }
        }

        if ("NOTIFICAR".equals(red_flag_notification)) {
            holder.notificationIcon.setVisibility(View.VISIBLE);
        } else {
            holder.notificationIcon.setVisibility(View.INVISIBLE);
        }

        holder.book_id_txt.setText(String.valueOf(book_id.get(position)));
        holder.book_title_txt.setText(String.valueOf(book_title.get(position)));
        holder.book_categoria_txt.setText(String.valueOf(book_categoria.get(position)));

        String moedaSelecionada = PreferencesUtil.getMoedaSelecionada(context);
        moedaSelecionada = moedaSelecionada + ": ";
        holder.book_moeda_txt.setText(moedaSelecionada);

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        String prices_Formatado_virgula = String.valueOf(prices.get(position));
        double valor = Double.parseDouble(prices_Formatado_virgula);
        String formattedtotalSum20 = numberFormat.format(valor);
        holder.prices_txt.setText(formattedtotalSum20);
        String dataNoFormatoOriginal = String.valueOf(book_date.get(position));

        try {
            SimpleDateFormat formatoDesejado = new SimpleDateFormat("dd/MMM/yyyy");
            Date data = formatoOriginal42.parse(dataNoFormatoOriginal);
            String dataFormatada = formatoDesejado.format(data);
            holder.book_date_txt.setText(dataFormatada);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showPopupMenu(v, position);
                return true;
            }
        });

        for (Pair<Integer, Integer> pair : positionsAndColors) {
            if (pair.first == position) {
                // Obtém a cor associada à posição
                int cor = pair.second;
                // Verifica a cor e define o background
                if (cor == COR_PURPLE_700) {

                    holder.paidIcon.setVisibility(View.VISIBLE);
                    holder.book_date_txt.setTextColor(Color.BLACK);
                } else if (cor == COR_TRANSPARENT) {
                    holder.paidIcon.setVisibility(View.INVISIBLE);

                    Date dataAtual43 = new Date();
                    dataAtualFormatada42 = formatoOriginal42.format(dataAtual43);
                    if (dataAtualFormatada42.compareTo(dataNoFormatoOriginal42) > 0  ) {
                        holder.book_date_txt.setTextColor(Color.RED);
                    } else{
                        holder.book_date_txt.setTextColor(Color.BLACK);
                    }
                }
                break;
            }
        }

        for (Pair<Integer, Integer> pair10 : positionsAndColors3) {
            if (pair10.first == position) {

                int cor3 = pair10.second;
                if (cor3 == COR_RED) {
                    holder.notificationIcon.setVisibility(View.VISIBLE);
                } else if (cor3 == COR_TRANSPARENT) {
                    holder.notificationIcon.setVisibility(View.INVISIBLE);
                }
                break;
            }
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(context,UpdateActivity.class);

                intent.putExtra("id",String.valueOf(book_id.get(position)));
                intent.putExtra("title",String.valueOf(book_title.get(position)));
                intent.putExtra("author",String.valueOf(book_author.get(position)));
                intent.putExtra("prices",String.valueOf(prices.get(position)));
                intent.putExtra("book_date",String.valueOf(book_date.get(position)));
                intent.putExtra("book_despesa_ou_receita",String.valueOf(book_despesa_ou_receita.get(position)));
                intent.putExtra("book_categoria",String.valueOf(book_categoria.get(position)));
                intent.putExtra("book_pago_ou_nao_pago",String.valueOf(book_pago_ou_nao_pago.get(position)));
                intent.putExtra("book_red_flag_notification",String.valueOf(book_red_flag_notification.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });



    }

    @Override
    public int getItemCount() {
        return book_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView book_id_txt, book_title_txt, book_author_txt, prices_txt,book_date_txt,book_categoria_txt,book_moeda_txt ;
        LinearLayout mainLayout;
        FrameLayout notificationIcon, paidIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializa as visualizações
            book_id_txt = itemView.findViewById(R.id.book_id_txt);
            book_title_txt = itemView.findViewById(R.id.book_title_txt);
            book_author_txt = itemView.findViewById(R.id.book_author_txt);
            prices_txt = itemView.findViewById(R.id.prices_txt);
            book_date_txt = itemView.findViewById(R.id.book_date_txt);
            book_categoria_txt = itemView.findViewById(R.id.book_categoria_txt);
            book_moeda_txt = itemView.findViewById(R.id.book_moeda_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            notificationIcon = itemView.findViewById(R.id.notification_icon);
            paidIcon = itemView.findViewById(R.id.paid_icon);
        }
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.popup_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_pago:

                        FrameLayout paidIcon = view.findViewById(R.id.paid_icon);
                        if (paidIcon != null) {
                            paidIcon.setVisibility(View.VISIBLE);
                        }

                        TextView bookDateTextView = view.findViewById(R.id.book_date_txt);
                        if (bookDateTextView != null) {
                            bookDateTextView.setTextColor(Color.BLACK);
                        }

                        String itemId = String.valueOf(book_id.get(position));
                        String author = String.valueOf(book_author.get(position));
                        MyDatabaseHelper myDB = new MyDatabaseHelper(context);
                        myDB.marcarComoPago(itemId);

                        MyDatabaseHelperCartoesCredito myDB_cartoes = new MyDatabaseHelperCartoesCredito(context);
                        double despesa_total_do_cartao_nao_paga = myDB.obterSomaTotal(author,"NAO_PAGO");
                        String string_despesa_total_do_cartao_nao_paga = String.valueOf(despesa_total_do_cartao_nao_paga);
                        myDB_cartoes.atualizarLimiteUtilizado(author,string_despesa_total_do_cartao_nao_paga);
                        double limite_do_cartao = myDB_cartoes.obterLimiteDoCartao(author);
                        double limite_disponivel_do_cartao = limite_do_cartao - despesa_total_do_cartao_nao_paga;
                        String string_limite_disponivel_do_cartao = String.valueOf(limite_disponivel_do_cartao);
                        myDB_cartoes.atualizarLimiteDisponivel(author,string_limite_disponivel_do_cartao);

                        int positionToChange = position;
                        int colorToChange = COR_PURPLE_700;

                        for (int i = 0; i < positionsAndColors.size(); i++) {
                            Pair<Integer, Integer> pair = positionsAndColors.get(i);
                            if (pair.first.equals(positionToChange)) {
                                positionsAndColors.set(i, new Pair<>(positionToChange, colorToChange));
                                break;
                            }
                        }
                        positionsAndColors.add(new Pair<>(positionToChange, colorToChange));
                        return true;

                    case R.id.menu_nao_pago:

                        FrameLayout paidIcon4 = view.findViewById(R.id.paid_icon);
                        if (paidIcon4 != null) {
                            paidIcon4.setVisibility(View.INVISIBLE);
                        }

                        Date dataAtual44 = new Date();
                        String dataNoFormatoOriginal42 = String.valueOf(book_date.get(position));
                        dataAtualFormatada42 = formatoOriginal42.format(dataAtual44);

                        TextView bookDateTextView8 = view.findViewById(R.id.book_date_txt);
                        if (bookDateTextView8 != null) {
                            if (dataAtualFormatada42.compareTo(dataNoFormatoOriginal42) > 0) {
                                bookDateTextView8.setTextColor(Color.RED);
                            } else {
                                bookDateTextView8.setTextColor(Color.BLACK);
                            }
                        }

                        String itemId2 = String.valueOf(book_id.get(position));
                        String author2 = String.valueOf(book_author.get(position));
                        MyDatabaseHelper myDB2 = new MyDatabaseHelper(context);
                        myDB2.marcarComoNaoPago(itemId2);

                        MyDatabaseHelperCartoesCredito myDB_cartoes2 = new MyDatabaseHelperCartoesCredito(context);
                        double despesa_total_do_cartao_nao_paga2 = myDB2.obterSomaTotal(author2,"NAO_PAGO");
                        String string_despesa_total_do_cartao_nao_paga2 = String.valueOf(despesa_total_do_cartao_nao_paga2);
                        myDB_cartoes2.atualizarLimiteUtilizado(author2,string_despesa_total_do_cartao_nao_paga2);
                        double limite_do_cartao2 = myDB_cartoes2.obterLimiteDoCartao(author2);
                        double limite_disponivel_do_cartao2 = limite_do_cartao2 - despesa_total_do_cartao_nao_paga2;
                        String string_limite_disponivel_do_cartao2 = String.valueOf(limite_disponivel_do_cartao2);
                        myDB_cartoes2.atualizarLimiteDisponivel(author2,string_limite_disponivel_do_cartao2);

                        int positionToChange2 = position;
                        int colorToChange2 = COR_TRANSPARENT;

                        for (int i = 0; i < positionsAndColors.size(); i++) {
                            Pair<Integer, Integer> pair = positionsAndColors.get(i);
                            if (pair.first.equals(positionToChange2)) {
                                positionsAndColors.set(i, new Pair<>(positionToChange2, colorToChange2));
                                break;
                            }
                        }
                        positionsAndColors.add(new Pair<>(positionToChange2, colorToChange2));
                        return true;


//                    case R.id.menu_red_flag_notification:
//
//                        FrameLayout notificationIcon = view.findViewById(R.id.notification_icon);
//                        if (notificationIcon != null) {
//                            notificationIcon.setVisibility(View.VISIBLE);
//                        }
//
//                        String itemId3 = String.valueOf(book_id.get(position));
//                        MyDatabaseHelper myDB3 = new MyDatabaseHelper(context);
//                        myDB3.marcarParaNotificar(itemId3);
//
//                        int positionToChange3 = position;
//                        int colorToChange3 = COR_RED;
//
//                        for (int i3 = 0; i3 < positionsAndColors3.size(); i3++) {
//                            Pair<Integer, Integer> pair3 = positionsAndColors3.get(i3);
//                            if (pair3.first.equals(positionToChange3)) {
//                                positionsAndColors3.set(i3, new Pair<>(positionToChange3, colorToChange3));
//                                break;
//                            }
//                        }
//                        positionsAndColors3.add(new Pair<>(positionToChange3, colorToChange3));
//
//                        return true;

//                    case R.id.menu_nao_red_flag_notification:
//
//                        FrameLayout notificationIcon4 = view.findViewById(R.id.notification_icon);
//                        if (notificationIcon4 != null) {
//                            notificationIcon4.setVisibility(View.INVISIBLE);
//                        }
//
//                        String itemId4 = String.valueOf(book_id.get(position));
//                        MyDatabaseHelper myDB4 = new MyDatabaseHelper(context);
//                        myDB4.marcarParaNaoNotificar(itemId4);
//
//                        int positionToChange4 = position;
//                        int colorToChange4 = COR_TRANSPARENT;
//
//                        for (int i = 0; i < positionsAndColors3.size(); i++) {
//                            Pair<Integer, Integer> pair = positionsAndColors3.get(i);
//                            if (pair.first.equals(positionToChange4)) {
//                                positionsAndColors3.set(i, new Pair<>(positionToChange4, colorToChange4));
//                                break;
//                            }
//                        }
//                        positionsAndColors3.add(new Pair<>(positionToChange4, colorToChange4));
//                        return true;


                    default:
                        return false;
                }
            }
        });
        popupMenu.show();
    }
}