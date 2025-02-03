package com.ssafy.tmbg.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.TeamMember
import com.ssafy.tmbg.databinding.ItemTeamMemberBinding

class TeamMemberAdapter(
    private val members: List<TeamMember>
) : RecyclerView.Adapter<TeamMemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(private val binding: ItemTeamMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: TeamMember) {
            // Button으로 만든 레이아웃이므로 text 속성으로 직접 설정
            binding.root.text = member.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemTeamMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size
}