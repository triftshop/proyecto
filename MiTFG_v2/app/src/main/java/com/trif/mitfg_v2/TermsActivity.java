package com.trif.mitfg_v2;

import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class TermsActivity extends BaseActivity {

    private CheckBox checkboxAccept;
    private Button btnContinue;
    private boolean isFirstTime;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        isFirstTime = getIntent().getBooleanExtra("first_time", false);

        if (getSupportActionBar() != null) {
            if (isFirstTime) {
                getSupportActionBar().setTitle(R.string.terms_title_first_time);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                getSupportActionBar().setTitle(R.string.terms_title_menu);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        TextView termsText = findViewById(R.id.terms_text);
        checkboxAccept = findViewById(R.id.checkbox_accept);
        btnContinue = findViewById(R.id.btn_continue);

        termsText.setText(getString(R.string.terms_content));

        if (isFirstTime) {
            checkboxAccept.setVisibility(android.view.View.VISIBLE);
            btnContinue.setVisibility(android.view.View.VISIBLE);
            setupFirstTimeLogic();
        } else {
            checkboxAccept.setVisibility(android.view.View.GONE);
            btnContinue.setVisibility(android.view.View.GONE);
        }
    }

    private void setupFirstTimeLogic() {
        btnContinue.setEnabled(false);
        btnContinue.setAlpha(0.5f);

        checkboxAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
            btnContinue.setEnabled(isChecked);
            btnContinue.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        btnContinue.setOnClickListener(v -> {
            if (checkboxAccept.isChecked()) {
                markTermsAccepted();
                startActivityWithTransition(MainActivity.class);
                finish();
            }
        });
    }

    private void markTermsAccepted() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putBoolean("terms_accepted", true).apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (!isFirstTime) {
            finishWithTransition();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!isFirstTime) {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}