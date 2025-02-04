package com.ssafy.tmbg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.Team
import com.ssafy.tmbg.databinding.ItemTeamBinding


// Team 전체 어뎁터
class TeamAdapter(
    private val teams: List<Team>,
    // 각 조를 클릭 했을 때의 이벤트 처리를 위한 콜백
    private val onTeamClick: (Team) -> Unit
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

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
        holder.bind(teams[position])
    }

    // 아이템의 개수는 Team 리스트의 길이
    override fun getItemCount(): Int = teams.size

    inner class TeamViewHolder(
        private val binding: ItemTeamBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(team: Team) = with(binding) {
            // 몇 조인지 보여줌
            btnTeam.text = "${team.number}조"
            // 각 조별로 클릭 했을 때의 이벤트 처리
            btnTeam.setOnClickListener {
                onTeamClick(team)
            }
        }
    }
}