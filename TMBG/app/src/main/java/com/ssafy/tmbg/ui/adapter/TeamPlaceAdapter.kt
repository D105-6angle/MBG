package com.ssafy.tmbg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.TeamPlace
import com.ssafy.tmbg.databinding.ItemTeamPlaceBinding

class TeamPlaceAdapter(
    private val places: List<TeamPlace>
) : RecyclerView.Adapter<TeamPlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(private val binding: ItemTeamPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: TeamPlace) {
            binding.apply {
                placeName.text = place.name
                visitTime.text = "오후 ${place.visitedTime.split(" ")[1]}"  // "2024-02-03 10:30" -> "오후 10:30"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemTeamPlaceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }

    override fun getItemCount() = places.size
}