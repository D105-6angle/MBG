package com.ssafy.tmbg.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.dao.MemberDto
import com.ssafy.tmbg.databinding.ItemTeamMemberBinding
import android.content.Context
import androidx.core.content.ContextCompat
import com.ssafy.tmbg.R


// TeamMember Recycler View 용 어댑터
class TeamMemberAdapter(
    private val members: List<MemberDto>,
    private val onDeleteClick: ((Long) -> Unit)? = null  // userId를 Long 타입으로 변경
) : RecyclerView.Adapter<TeamMemberAdapter.ViewHolder>() {

    private var isDeleteMode = false

    fun setDeleteMode(enabled: Boolean) {
        isDeleteMode = enabled
        notifyDataSetChanged()
    }

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
            binding.apply {
                tvNickname.text = member.nickname
                // 조장/조원 뱃지 표시
                tvLeaderBadge.visibility = when (member.codeId) {
                    "J001" -> View.VISIBLE  // 조장
                    "J002" -> View.GONE     // 조원
                    else -> View.GONE
                }
                
                // 삭제 버튼 표시 여부 설정
//                btnDelete.visibility = if (isDeleteMode) View.VISIBLE else View.GONE
//
//                // 삭제 모드일 때 전체 아이템 클릭으로도 삭제 가능하게 설정
//                itemView.setOnClickListener {
//                    if (isDeleteMode) {
//                        Log.d("TeamMemberAdapter", "아이템 클릭으로 삭제 - userId: ${member.userId}")
//                        onDeleteClick?.invoke(member.userId)
//                    }
//                }
//
//                // 삭제 버튼 클릭 리스너 (기존 코드 유지)
//                btnDelete.setOnClickListener {
//                    Log.d("TeamMemberAdapter", "삭제 버튼 클릭 - userId: ${member.userId}")
//                    onDeleteClick?.invoke(member.userId)
//                }
            }
        }
    }
}