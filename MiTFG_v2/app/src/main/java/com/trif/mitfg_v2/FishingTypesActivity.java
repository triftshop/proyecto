package com.trif.mitfg_v2;

import androidx.cardview.widget.CardView;

public class FishingTypesActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fishing_types;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.fishing_types_title);
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();
        setupFishingTypeCards();
    }

    private void setupFishingTypeCards() {
        CardView cardFeeder = findViewById(R.id.cardFeeder);
        cardFeeder.setOnClickListener(v -> showFishingTypeDetail("feeder"));

        CardView cardCasting = findViewById(R.id.cardCasting);
        cardCasting.setOnClickListener(v -> showFishingTypeDetail("casting"));

        CardView cardSpinning = findViewById(R.id.cardSpinning);
        cardSpinning.setOnClickListener(v -> showFishingTypeDetail("spinning"));

        CardView cardFly = findViewById(R.id.cardFly);
        cardFly.setOnClickListener(v -> showFishingTypeDetail("fly"));
    }

    private void showFishingTypeDetail(String type) {
        android.content.Intent intent = new android.content.Intent(this, FishingTypeDetailActivity.class);
        intent.putExtra("fishing_type", type);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finishWithTransition();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}