package com.ssafy.mbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.databinding.ItemTeamMemberBinding

class TeamMemberAdapter : RecyclerView.Adapter<TeamMemberAdapter.MemberViewHolder>() {
    // 팀 멤버 데이터를 저장할 리스트
    private var members = listOf<String>()

    // ViewHolder 클래스 정의: 개별 아이템 뷰를 관리
    class MemberViewHolder(private val binding: ItemTeamMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        // 아이템 뷰에 데이터를 설정하는 함수
        fun bind(memberName: String) {
            binding.memberName.text = memberName // 멤버 이름 설정
        }
    }

    // ViewHolder 생성 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemTeamMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    // ViewHolder와 데이터를 연결하는 함수
    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    // 아이템 개수를 반환하는 함수
    override fun getItemCount() = members.size

    // 새로운 팀 멤버 목록으로 업데이트하는 함수
    fun updateMembers(newMembers: List<String>) {
        members = newMembers
        notifyDataSetChanged() // 변경 사항을 RecyclerView에 알림
    }
}