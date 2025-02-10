package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.databinding.ItemTeamBinding


// Team 전체 어뎁터
class TeamAdapter(
    private var numOfGroups: Int = 0,
    private val onTeamClick: (Int) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    fun updateData(numOfGroups: Int) {
        this.numOfGroups = numOfGroups
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        return TeamViewHolder(
            ItemTeamBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    // Team 리스트의 인덱스 순으로 바인딩
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        holder.bind(position + 1)  // 1부터 시작하는 그룹 번호
    }

    // 아이템의 개수는 Team 리스트의 길이
    override fun getItemCount(): Int = numOfGroups

    inner class TeamViewHolder(
        private val binding: ItemTeamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(groupNumber: Int) = with(binding) {
            // 그룹 번호 표시
            btnTeam.text = "${groupNumber}조"
            
            // 그룹 클릭 이벤트
            btnTeam.setOnClickListener {
                onTeamClick(groupNumber)
            }
        }
    }
}