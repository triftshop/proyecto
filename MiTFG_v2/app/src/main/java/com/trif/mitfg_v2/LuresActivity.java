package com.trif.mitfg_v2;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class LuresActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_lures;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.lures_title);
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();
        setupLuresList();
    }

    private void setupLuresList() {
        RecyclerView recyclerView = findViewById(R.id.recyclerLures);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<LureItem> lures = getLuresList();
        LuresAdapter adapter = new LuresAdapter(lures, lure -> {
            // Al hacer click, abrir pantalla de detalle (NO tutorial)
            android.content.Intent intent = new android.content.Intent(this, LureDetailActivity.class);
            intent.putExtra("lure_id", lure.getId());
            intent.putExtra("lure_name", lure.getName());
            intent.putExtra("lure_type", lure.getType());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        recyclerView.setAdapter(adapter);
    }

    private List<LureItem> getLuresList() {
        List<LureItem> lures = new ArrayList<>();

        // Lista de señuelos con sus tipos
        lures.add(new LureItem(1, getString(R.string.lure_cucharilla_name), getString(R.string.lure_cucharilla_description), "lure_cucharilla_icon", "cucharilla"));
        lures.add(new LureItem(2, getString(R.string.lure_popper_name), getString(R.string.lure_popper_description), "lure_popper_icon", "popper"));
        lures.add(new LureItem(3, getString(R.string.lure_jig_name), getString(R.string.lure_jig_description), "lure_jig_icon", "jig"));
        lures.add(new LureItem(4, getString(R.string.lure_spinnerbait_name), getString(R.string.lure_spinnerbait_description), "lure_spinnerbait_icon", "spinnerbait"));
        lures.add(new LureItem(5, getString(R.string.lure_crankbait_name), getString(R.string.lure_crankbait_description), "lure_crankbait_icon", "crankbait"));
        lures.add(new LureItem(6, getString(R.string.lure_soft_plastic_name), getString(R.string.lure_soft_plastic_description), "lure_soft_plastic_icon", "soft_plastic"));

        return lures;
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