package com.ssafy.tmbg.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ssafy.tmbg.data.team.dao.VerificationPhotos
import com.ssafy.tmbg.databinding.ItemTeamPhotoBinding

class TeamPhotoAdapter(
    private val photos: List<VerificationPhotos>,
    private val onPhotoClick: (VerificationPhotos) -> Unit
) : RecyclerView.Adapter<TeamPhotoAdapter.PhotoViewHolder>() {

    inner class PhotoViewHolder(private val binding: ItemTeamPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: VerificationPhotos) {
            binding.imageView.apply {
                // Glide를 사용하여 이미지 로딩
                Glide.with(context)
                    .load(photo.pictureUrl)
                    .centerCrop()
                    .into(this)

                // 클릭 리스너 설정
                setOnClickListener {
                    onPhotoClick.invoke(photo)
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