package com.example.timetrekbharat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.timetrekbharat.databinding.ItemStateCardBinding
import com.example.timetrekbharat.model.State

class StateAdapter(
    private val onStateClickListener: (State) -> Unit
) : RecyclerView.Adapter<StateAdapter.StateViewHolder>() {

    private var stateList: List<State> = ArrayList()

    fun setStates(states: List<State>?) {
        if (states != null) {
            this.stateList = states
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
                    .centerCrop()
                    .into(binding.ivStateImage)
            }

            itemView.setOnClickListener {
                onStateClickListener(state)
            }
        }
    }
}
