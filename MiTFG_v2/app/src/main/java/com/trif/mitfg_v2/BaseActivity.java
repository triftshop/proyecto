package com.trif.mitfg_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import java.util.Locale;

public abstract class BaseActivity extends AppCompatActivity {

    protected static final String PREFS_NAME = "prefs";
    protected static final String KEY_LANGUAGE = "language";
    protected static final String KEY_DARK_MODE = "dark_mode";

    protected abstract int getLayoutId();

    protected void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected void setupUI() {
        // Por defecto no hace nada
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyUserPreferences();

        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        setupToolbar();
        setupUI();
    }

    private void applyUserPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        boolean darkModeOn = prefs.getBoolean(KEY_DARK_MODE, false);
        if (darkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setLocale(getSavedLanguage());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_language) {
            showLanguageDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_dark_mode) {
            showDarkModeDialog();
            return true;
        } else if (item.getItemId() == R.id.menu_terms) {
            Intent intent = new Intent(this, TermsActivity.class);
            intent.putExtra("first_time", false);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.language_select_title));

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_language_selector, null);
        final Spinner spinner = dialogView.findViewById(R.id.spinner_languages);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[] {
                        getString(R.string.language_spanish),
                        getString(R.string.language_english)
                });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String currentLang = getSavedLanguage();
        if ("en".equals(currentLang)) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(0);
        }

        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(getString(R.string.done), (dialog, which) -> {
            int position = spinner.getSelectedItemPosition();
            String selectedLang = (position == 1) ? "en" : "es";

            saveLanguage(selectedLang);
            setLocale(selectedLang);

            recreate();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        builder.create().show();
    }

    private void showDarkModeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dark_mode_select_title));

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_dark_mode_selector, null);
        final Spinner spinner = dialogView.findViewById(R.id.spinner_dark_mode);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[] {
                        getString(R.string.theme_light),
                        getString(R.string.theme_dark)
                });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        boolean currentDarkMode = getSavedDarkMode();
        if (currentDarkMode) {
            spinner.setSelection(1);
        } else {
            spinner.setSelection(0);
        }

        builder.setView(dialogView);
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss());
        builder.setPositiveButton(getString(R.string.done), (dialog, which) -> {
            int position = spinner.getSelectedItemPosition();
            boolean selectedDarkMode = (position == 1);

            saveDarkMode(selectedDarkMode);
            applyDarkMode(selectedDarkMode);

            recreate();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        });

        builder.create().show();
    }

    protected void saveLanguage(String lang) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_LANGUAGE, lang).apply();
    }

    protected String getSavedLanguage() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_LANGUAGE, "es");
    }

    protected void saveDarkMode(boolean darkMode) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DARK_MODE, darkMode).apply();
    }

    protected boolean getSavedDarkMode() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    protected void applyDarkMode(boolean darkMode) {
        if (darkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    protected void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    protected void finishWithTransition() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    protected void startActivityWithTransition(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}
