package com.trif.mitfg_v2;

import androidx.cardview.widget.CardView;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setupUI() {
        super.setupUI();
        setupCardClickListeners();
    }

    private void setupCardClickListeners() {
        CardView cardDocumentation = findViewById(R.id.cardDocumentation);
        cardDocumentation.setOnClickListener(v -> startActivityWithTransition(DocumentationActivity.class));

        CardView cardFishingTypes = findViewById(R.id.cardFishingTypes);
        cardFishingTypes.setOnClickListener(v -> startActivityWithTransition(FishingTypesActivity.class));

        CardView cardKnots = findViewById(R.id.cardKnots);
        cardKnots.setOnClickListener(v -> startActivityWithTransition(KnotsActivity.class));

        CardView cardLures = findViewById(R.id.cardLures);
        cardLures.setOnClickListener(v -> startActivityWithTransition(LuresActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}