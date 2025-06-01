package com.trif.mitfg_v2;

import android.widget.ImageView;
import android.widget.TextView;

public class LureDetailActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lure_detail;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        String lureName = getIntent().getStringExtra("lure_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(lureName);
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        int lureId = getIntent().getIntExtra("lure_id", 1);
        String lureName = getIntent().getStringExtra("lure_name");
        String lureType = getIntent().getStringExtra("lure_type");

        loadLureContent(lureId, lureName, lureType);
    }

    private void loadLureContent(int lureId, String lureName, String lureType) {
        ImageView imgLure = findViewById(R.id.imgLureDetail);
        TextView txtLureName = findViewById(R.id.txtLureDetailName);
        TextView txtLureDescription = findViewById(R.id.txtLureDetailDescription);

        // Establecer nombre
        txtLureName.setText(lureName);

        // Cargar imagen del tipo de señuelo
        String imageResourceName = "lure_" + lureType + "_main";
        int imageResId = getResources().getIdentifier(imageResourceName, "drawable", getPackageName());

        if (imageResId != 0) {
            imgLure.setImageResource(imageResId);
        } else {
            imgLure.setImageResource(R.drawable.ic_lure_default);
        }

        // Cargar descripción según el tipo
        switch (lureType) {
            case "cucharilla":
                txtLureDescription.setText(R.string.lure_cucharilla_full_description);
                break;
            case "popper":
                txtLureDescription.setText(R.string.lure_popper_full_description);
                break;
            case "jig":
                txtLureDescription.setText(R.string.lure_jig_full_description);
                break;
            case "spinnerbait":
                txtLureDescription.setText(R.string.lure_spinnerbait_full_description);
                break;
            case "crankbait":
                txtLureDescription.setText(R.string.lure_crankbait_full_description);
                break;
            case "soft_plastic":
                txtLureDescription.setText(R.string.lure_soft_plastic_full_description);
                break;
            default:
                txtLureDescription.setText(R.string.lure_default_description);
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