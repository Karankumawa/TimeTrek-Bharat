package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.R
import com.example.timetrekbharat.databinding.ItemStateCardBinding
import com.example.timetrekbharat.model.State

class StateAdapter(
    private val onStateClickListener: (State) -> Unit
) : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    private var stateList: List<State> = ArrayList()
    private var lastPosition = -1

    fun setStates(states: List<State>?) {
        if (states != null) {
            this.stateList = states
            this.lastPosition = -1
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StateViewHolder {
        val binding = ItemStateCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StateViewHolder, position: Int) {
        holder.bind(stateList[position])
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(viewToAnimate.context, R.anim.item_animation_fall_down)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = stateList.size

    inner class StateViewHolder(private val binding: ItemStateCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(state: State) {
            binding.tvStateName.text = state.name
            binding.tvRegionBadge.text = state.region
            binding.tvCapital.text = "Capital: ${state.capital ?: "N/A"}"
            binding.tvShortDescription.text = state.shortDescription

            val eraCount = state.timeline?.size ?: 0
            binding.tvTimelineCount.text = "$eraCount Historical Eras"

            if (!state.imageUrl.isNullOrBlank()) {
                Glide.with(itemView.context)
                    .load(state.imageUrl)
                    .placeholder(R.drawable.placeholder_heritage)
                    .error(R.drawable.placeholder_heritage)
                    .centerCrop()
                    .into(binding.ivStateImage)
            } else {
                binding.ivStateImage.setImageResource(R.drawable.placeholder_heritage)
            }

            itemView.setOnClickListener {
                // Interactive Card Press Scale Animation
                itemView.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(120)
                    .withEndAction {
                        itemView.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(120)
                            .withEndAction {
                                onStateClickListener(state)
                            }
                            .start()
                    }
                    .start()
            }
        }
    }
}
