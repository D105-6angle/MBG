package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.tmbg.data.team.dao.TeamPhoto
import com.ssafy.tmbg.databinding.ItemTeamPhotoBinding

class TeamPhotoAdapter(
    private val photos: List<TeamPhoto>,
    private val onPhotoClick: (TeamPhoto) -> Unit  // 사진 클릭 시 처리를 위한 콜백
) : RecyclerView.Adapter<TeamPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: ItemTeamPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: TeamPhoto) {
            binding.imageView.apply {
                // 실제 이미지 로딩 (여기서는 임시로 drawable 리소스 사용)
                setImageResource(photo.photoUrl.toInt())  // 현재는 R.drawable 리소스를 직접 사용

                // 클릭 리스너 설정
                setOnClickListener {
                    onPhotoClick(photo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemTeamPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    override fun getItemCount() = photos.size
}