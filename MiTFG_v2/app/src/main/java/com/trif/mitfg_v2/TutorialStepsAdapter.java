package com.trif.mitfg_v2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TutorialStepsAdapter extends RecyclerView.Adapter<TutorialStepsAdapter.StepViewHolder> {

    private List<TutorialStep> steps;

    public TutorialStepsAdapter(List<TutorialStep> steps) {
        this.steps = steps;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tutorial_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        TutorialStep step = steps.get(position);
        holder.bind(step);
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        private ImageView imgStep;
        private TextView txtDescription;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStep = itemView.findViewById(R.id.imgTutorialStep);
            txtDescription = itemView.findViewById(R.id.txtStepDescription);
        }

        public void bind(TutorialStep step) {
            int imageResId = itemView.getContext().getResources()
                    .getIdentifier(step.getImageName(), "drawable", itemView.getContext().getPackageName());

            if (imageResId != 0) {
                imgStep.setImageResource(imageResId);
            } else {
                imgStep.setImageResource(R.drawable.ic_image_placeholder);
            }

            txtDescription.setText(step.getDescription());
        }
    }
}