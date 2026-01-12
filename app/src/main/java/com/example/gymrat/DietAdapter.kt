package com.example.gymrat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.gymrat.databinding.ItemDietPlanBinding

class DietAdapter(
    private val plans: List<DietPlan>,
    private val onClick: (DietPlan) -> Unit
) : RecyclerView.Adapter<DietAdapter.DietViewHolder>() {

    inner class DietViewHolder(private val binding: ItemDietPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plan: DietPlan) {
            binding.tvDietTitle.text = plan.title
            binding.tvDietType.text = plan.type
            binding.chipCalories.text = plan.calories
            binding.ivDietImage.setImageResource(plan.imageRes)

            binding.root.setOnClickListener {
                onClick(plan)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val binding = ItemDietPlanBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return DietViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        holder.bind(plans[position])
    }

    override fun getItemCount(): Int = plans.size
}