package com.ssafy.mbg.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.data.task.dao.Member
import com.ssafy.mbg.databinding.ItemTeamMemberBinding

class TeamMemberAdapter : RecyclerView.Adapter<TeamMemberAdapter.MemberViewHolder>() {
    private val members = mutableListOf<Member>()

    class MemberViewHolder(private val binding: ItemTeamMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: Member) {
            Log.d("TeamMemberAdapter", "Binding member: $member")
            binding.memberName.text = member.nickname
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
        Log.d("TeamMemberAdapter", "onBindViewHolder: position $position")
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    fun updateMembers(newMembers: List<Member>) {
        Log.d("TeamMemberAdapter", "updateMembers called with ${newMembers.size} members")
        members.clear()
        members.addAll(newMembers)
        notifyDataSetChanged()
    }
}