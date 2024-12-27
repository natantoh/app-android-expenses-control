package com.brainexpensescontrol.controle_de_despesas;

import static android.content.ContentValues.TAG;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.File;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Verifique e armazene a versão do app aqui
        int currentVersionCode = BuildConfig.VERSION_CODE;
        SharedPreferences prefs = getSharedPreferences("MeuAppPreferences", MODE_PRIVATE);
        int lastVersionCode = prefs.getInt("last_version_code", -1);

        if (BuildConfig.DEBUG) {
            // Log para verificar o código da versão
            Log.d(TAG, "currentVersionCode é " + currentVersionCode);
            Log.d(TAG, "lastVersionCode é " + lastVersionCode);
        }

        if (currentVersionCode > lastVersionCode) {
            // Se a versão foi atualizada, realizar ações, como limpar cache
            if (BuildConfig.DEBUG) {
                // Log para verificar o código da versão
                Log.d(TAG, "Versão do app atualizada. Limpando cache...");
            }
            clearCache();
            // Salve a nova versão no SharedPreferences
            prefs.edit().putInt("last_version_code", currentVersionCode).apply();
        }
        else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "A versão do app não foi atualizada.");
            }
        }
    }

    private void clearCache() {
        // Implementação para limpar o cache
        File cacheDir = getCacheDir();
        if (cacheDir != null && cacheDir.isDirectory()) {
            deleteDir(cacheDir);
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}