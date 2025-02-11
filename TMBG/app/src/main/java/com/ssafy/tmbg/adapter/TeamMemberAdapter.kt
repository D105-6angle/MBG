package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.dao.MemberDto
import com.ssafy.tmbg.databinding.ItemTeamMemberBinding


// TeamMember Recycler View 용 어댑터
class TeamMemberAdapter(
    private val members: List<MemberDto>
) : RecyclerView.Adapter<TeamMemberAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTeamMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    inner class ViewHolder(
        private val binding: ItemTeamMemberBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: MemberDto) {
            binding.tvNickname.text = member.nickname
            // 조장인 경우 표시
            binding.tvLeaderBadge.visibility = 
                if (member.codeId == "J001") View.VISIBLE else View.GONE
        }
    }
}