package com.example.gymrat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymrat.databinding.ItemWorkoutPlanBinding

class WorkoutAdapter(
    private val plans: List<WorkoutPlan>,
    private val onClick: (WorkoutPlan) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(private val binding: ItemWorkoutPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: WorkoutPlan) {
            binding.tvPlanTitle.text = plan.title
            binding.tvDifficulty.text = plan.difficulty
            binding.tvDuration.text = plan.duration
            // Ideally use Glide/Picasso for real images. For now:
            binding.ivPlanImage.setImageResource(plan.imageRes)

            binding.root.setOnClickListener {
                onClick(plan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutPlanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(plans[position])
    }

    override fun getItemCount(): Int = plans.size
}