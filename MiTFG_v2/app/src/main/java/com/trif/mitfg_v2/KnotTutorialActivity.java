package com.trif.mitfg_v2;

import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.trif.mitfg_v2.BaseActivity;
import com.trif.mitfg_v2.TutorialStep;
import com.trif.mitfg_v2.TutorialStepsAdapter;

import java.util.ArrayList;
import java.util.List;

public class KnotTutorialActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private int knotId;
    private String knotName;
    private List<TutorialStep> steps;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_knot_tutorial;
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        knotId = getIntent().getIntExtra("knot_id", 1);
        knotName = getIntent().getStringExtra("knot_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(knotName);
        }
    }

    @Override
    protected void setupUI() {
        super.setupUI();

        viewPager = findViewById(R.id.viewPagerSteps);
        tabLayout = findViewById(R.id.tabLayoutSteps);

        setupTutorialSteps();
    }

    private void setupTutorialSteps() {
        steps = getTutorialSteps(knotId);

        TutorialStepsAdapter adapter = new TutorialStepsAdapter(steps);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(getString(R.string.step_number, position + 1));
        }).attach();
    }

    private List<TutorialStep> getTutorialSteps(int knotId) {
        List<TutorialStep> stepsList = new ArrayList<>();

        switch (knotId) {
            case 1: // Nudo Palomar - 4 pasos
                stepsList.add(new TutorialStep("knot_1_step_1", getString(R.string.knot_1_step_1_description)));
                stepsList.add(new TutorialStep("knot_1_step_2", getString(R.string.knot_1_step_2_description)));
                stepsList.add(new TutorialStep("knot_1_step_3", getString(R.string.knot_1_step_3_description)));
                stepsList.add(new TutorialStep("knot_1_step_4", getString(R.string.knot_1_step_4_description)));
                break;

            case 2: // Nudo de Bucle - 3 pasos
                stepsList.add(new TutorialStep("knot_2_step_1", getString(R.string.knot_2_step_1_description)));
                stepsList.add(new TutorialStep("knot_2_step_2", getString(R.string.knot_2_step_2_description)));
                stepsList.add(new TutorialStep("knot_2_step_3", getString(R.string.knot_2_step_3_description)));
                break;

            case 3: // Nudo de Doble Lazo - 3 pasos
                stepsList.add(new TutorialStep("knot_3_step_1", getString(R.string.knot_3_step_1_description)));
                stepsList.add(new TutorialStep("knot_3_step_2", getString(R.string.knot_3_step_2_description)));
                stepsList.add(new TutorialStep("knot_3_step_3", getString(R.string.knot_3_step_3_description)));
                break;

            case 4: // Nudo Davy - 3 pasos
                stepsList.add(new TutorialStep("knot_4_step_1", getString(R.string.knot_4_step_1_description)));
                stepsList.add(new TutorialStep("knot_4_step_2", getString(R.string.knot_4_step_2_description)));
                stepsList.add(new TutorialStep("knot_4_step_3", getString(R.string.knot_4_step_3_description)));
                break;

            case 5: // Nudo de Pescador Doble - 3 pasos
                stepsList.add(new TutorialStep("knot_5_step_1", getString(R.string.knot_5_step_1_description)));
                stepsList.add(new TutorialStep("knot_5_step_2", getString(R.string.knot_5_step_2_description)));
                stepsList.add(new TutorialStep("knot_5_step_3", getString(R.string.knot_5_step_3_description)));
                break;

            case 6: // Nudo Trilene - 5 pasos
                stepsList.add(new TutorialStep("knot_6_step_1", getString(R.string.knot_6_step_1_description)));
                stepsList.add(new TutorialStep("knot_6_step_2", getString(R.string.knot_6_step_2_description)));
                stepsList.add(new TutorialStep("knot_6_step_3", getString(R.string.knot_6_step_3_description)));
                stepsList.add(new TutorialStep("knot_6_step_4", getString(R.string.knot_6_step_4_description)));
                stepsList.add(new TutorialStep("knot_6_step_5", getString(R.string.knot_6_step_5_description)));
                break;
        }

        return stepsList;
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