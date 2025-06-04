package com.trif.mitfg_v2;

import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import java.util.ArrayList;
import java.util.List;

public class LureTutorialActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private int lureId;
    private String lureName;
    private List<TutorialStep> steps;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_knot_tutorial; // Mismo layout que nudos
    }

    @Override
    protected void setupToolbar() {
        super.setupToolbar();

        lureId = getIntent().getIntExtra("lure_id", 1);
        lureName = getIntent().getStringExtra("lure_name");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(lureName);
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
        steps = getTutorialSteps(lureId);

        TutorialStepsAdapter adapter = new TutorialStepsAdapter(steps);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(getString(R.string.step_number, position + 1));
        }).attach();
    }

    private List<TutorialStep> getTutorialSteps(int lureId) {
        List<TutorialStep> stepsList = new ArrayList<>();

        switch (lureId) {
            case 1: // Cucharilla
                stepsList.add(new TutorialStep("lure_1_step_1", getString(R.string.lure_1_step_1_description)));
                stepsList.add(new TutorialStep("lure_1_step_2", getString(R.string.lure_1_step_2_description)));
                break;

            case 2: // Popper
                stepsList.add(new TutorialStep("lure_2_step_1", getString(R.string.lure_2_step_1_description)));
                stepsList.add(new TutorialStep("lure_2_step_2", getString(R.string.lure_2_step_2_description)));
                break;

            case 3: // Jig
                stepsList.add(new TutorialStep("lure_3_step_1", getString(R.string.lure_3_step_1_description)));
                stepsList.add(new TutorialStep("lure_3_step_2", getString(R.string.lure_3_step_2_description)));
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