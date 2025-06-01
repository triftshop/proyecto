package com.trif.mitfg_v2;

import android.widget.ImageView;
import android.widget.TextView;

public class FishingTypeDetailActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_fishing_type_detail;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        String fishingType = getIntent().getStringExtra("fishing_type");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            switch (fishingType) {
                case "feeder":
                    getSupportActionBar().setTitle(R.string.feeder_title);
                    break;
                case "casting":
                    getSupportActionBar().setTitle(R.string.casting_title);
                    break;
                case "spinning":
                    getSupportActionBar().setTitle(R.string.spinning_title);
                    break;
                case "fly":
                    getSupportActionBar().setTitle(R.string.fly_title);
                    break;
            }
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        String fishingType = getIntent().getStringExtra("fishing_type");
        loadFishingTypeContent(fishingType);
    }

    private void loadFishingTypeContent(String type) {
        ImageView imgType = findViewById(R.id.imgFishingType);
        TextView txtDescription = findViewById(R.id.txtFishingTypeDescription);

        switch (type) {
            case "feeder":
                imgType.setImageResource(R.drawable.ic_feeder);
                txtDescription.setText(R.string.feeder_description);
                break;
            case "casting":
                imgType.setImageResource(R.drawable.ic_casting);
                txtDescription.setText(R.string.casting_description);
                break;
            case "spinning":
                imgType.setImageResource(R.drawable.ic_spinning);
                txtDescription.setText(R.string.spinning_description);
                break;
            case "fly":
                imgType.setImageResource(R.drawable.ic_fly);
                txtDescription.setText(R.string.fly_description);
                break;
        }
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