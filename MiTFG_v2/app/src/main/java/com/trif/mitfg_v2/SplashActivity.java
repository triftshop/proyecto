package com.trif.mitfg_v2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;

public class SplashActivity extends BaseActivity {

    private static final int SPLASH_DELAY = 2000;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void setupToolbar() {
        // No mostrar toolbar en splash
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            checkTermsAndNavigate();
        }, SPLASH_DELAY);
    }

    private void checkTermsAndNavigate() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean termsAccepted = prefs.getBoolean("terms_accepted", false);

        Intent intent;
        if (termsAccepted) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, TermsActivity.class);
            intent.putExtra("first_time", true);
        }

        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        // No permite volver atrás desde splash
    }
}