package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.dao.MemberDto
import com.ssafy.tmbg.databinding.ItemTeamMemberBinding


// TeamMember Recycler View 용 어댑터
class TeamMemberAdapter(
    private val members: List<MemberDto>
) : RecyclerView.Adapter<TeamMemberAdapter.MemberViewHolder>() {

    inner class MemberViewHolder(private val binding: ItemTeamMemberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: MemberDto) {
            binding.root.apply {
                // 닉네임 설정
                text = member.nickname
                
                // 역할에 따라 다른 스타일 적용
                setTextColor(
                    if (member.isLeader == "LEADER") 
                        context.getColor(android.R.color.holo_red_light)
                    else 
                        context.getColor(android.R.color.black)
                )
            }
        }
    }
    // 팀 멤버 아이템과 바인딩
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemTeamMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MemberViewHolder(binding)
    }
    // 멤버 리스트의 인덱스 순으로 바인딩
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }
    // 아이템의 개수는 멤버 리스트의 사이즈
    override fun getItemCount() = members.size
}