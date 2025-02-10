package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.dao.TeamPlace
import com.ssafy.tmbg.databinding.ItemTeamPlaceBinding


// Team이 다녀간 장소에 대한 어뎁터
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
    // 장소 리스트의 인덱스 순서로 바인딩
    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position])
    }
    // 아이템의 개수는 장소 리스트의 사이즈
    override fun getItemCount() = places.size
}