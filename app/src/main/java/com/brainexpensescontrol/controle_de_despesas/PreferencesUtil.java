package com.brainexpensescontrol.controle_de_despesas;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;


// Exemplo em PreferencesUtil.java
public class PreferencesUtil {

    private static final String KEY_MOEDA_SELECIONADA = "moedaSelecionada";
    private static final String KEY_PRIMEIRA_DESPESA_ADICIONADA = "primeiraDespesaAdicionada";
    private static final String KEY_LINEAR_LAYOUT_VISIBILITY = "linearLayoutVisibility"; // Nova chave

    // Método para obter a moeda selecionada
    public static String getMoedaSelecionada(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        return preferences.getString(KEY_MOEDA_SELECIONADA, "POP100");
    }

    // Método para verificar se a primeira despesa foi adicionada
    public static boolean isFirstExpenseAdded(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_PRIMEIRA_DESPESA_ADICIONADA, false);
    }

    // Método para marcar a primeira despesa como adicionada
    public static void markFirstExpenseAdded(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_PRIMEIRA_DESPESA_ADICIONADA, true);
        editor.apply();
    }

    // Método para obter a visibilidade do LinearLayout
    public static boolean getLinearLayoutVisibility(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_LINEAR_LAYOUT_VISIBILITY, true);
    }

    // Método para definir a visibilidade do LinearLayout
    public static void setLinearLayoutVisibility(Context context, boolean isVisible) {
        SharedPreferences preferences = context.getSharedPreferences("MeuAppPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_LINEAR_LAYOUT_VISIBILITY, isVisible);
        editor.apply();
    }
}



