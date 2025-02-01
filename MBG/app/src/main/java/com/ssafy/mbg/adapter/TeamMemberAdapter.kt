package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.databinding.ItemTeamMemberBinding

class TeamMemberAdapter : RecyclerView.Adapter<TeamMemberAdapter.MemberViewHolder>() {
    // member의 리스트
    private var members = listOf<String>()

    // Member 리사이클러 뷰 홀더
    class MemberViewHolder(private val binding: ItemTeamMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        // memberName 묶기
        fun bind(memberName: String) {
            binding.memberName.text = memberName
        }
    }

    // 리사이클러 뷰 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemTeamMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    // member 리스트 순서로 묶기
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount() = members.size

    fun updateMembers(newMembers: List<String>) {
        members = newMembers
        notifyDataSetChanged()
    }

}