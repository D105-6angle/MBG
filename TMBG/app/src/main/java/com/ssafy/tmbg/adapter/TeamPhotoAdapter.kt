package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.tmbg.R
import com.ssafy.tmbg.data.team.dao.VerificationPhotos
import com.ssafy.tmbg.databinding.ItemTeamPhotoBinding
import java.text.SimpleDateFormat
import java.util.*

class TeamPhotoAdapter(
    private val photos: List<VerificationPhotos>,
    private val onPhotoClick: (VerificationPhotos) -> Unit
) : RecyclerView.Adapter<TeamPhotoAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemTeamPhotoBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(photo: VerificationPhotos) {
            binding.apply {
                // Glide를 사용하여 이미지 로드
                Glide.with(itemView.context)
                    .load(photo.pictureUrl)
                    .centerCrop()
                    .into(ivPhoto)

                // 완료 시간 표시
                tvCompletionTime.text = formatDateTime(photo.completionTime)

                // 클릭 리스너 설정
                root.setOnClickListener {
                    onPhotoClick(photo)
                }
            }
        }
    }

    private fun formatDateTime(dateTimeStr: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateTimeStr) ?: return dateTimeStr
            
            val outputFormat = SimpleDateFormat("M월 d일 a h시 mm분", Locale.KOREAN)
            return outputFormat.format(date)
        } catch (e: Exception) {
            return dateTimeStr
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTeamPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size
}