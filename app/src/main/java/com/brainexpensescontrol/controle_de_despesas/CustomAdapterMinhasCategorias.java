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

public class CustomAdapterMinhasCategorias extends RecyclerView.Adapter<CustomAdapterMinhasCategorias.MyViewHolder> {

    private Context context;
    Activity activity;
    private ArrayList book_id, book_title;

    CustomAdapterMinhasCategorias(Activity activity, Context context, ArrayList book_id, ArrayList book_title){
        this.activity = activity;
        this.context = context;
        this.book_id = book_id;
        this.book_title = book_title;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from((context));
        View view = inflater.inflate(R.layout.my_row_minhas_categorias,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.book_id_txt_categoria.setText(String.valueOf(book_id.get(position)));
        holder.book_title_txt_categoria.setText(String.valueOf(book_title.get(position)));

        holder.mainLayoutMinhasCategorias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Passe os valores como extras para a UpdateCategoria
                Intent intent = new Intent(context, UpdateCategoria.class);
                intent.putExtra("id",String.valueOf(book_id.get(position)));
                intent.putExtra("title",String.valueOf(book_title.get(position)));
                activity.startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() {

        return book_id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView book_id_txt_categoria, book_title_txt_categoria;
        LinearLayout mainLayoutMinhasCategorias;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            book_id_txt_categoria = itemView.findViewById(R.id.book_id_txt_categoria);
            book_title_txt_categoria = itemView.findViewById(R.id.book_title_txt_categoria);
            mainLayoutMinhasCategorias = itemView.findViewById(R.id.mainLayoutMinhasCategorias);
        }
    }
}

