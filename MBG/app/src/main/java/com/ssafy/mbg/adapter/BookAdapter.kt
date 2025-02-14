package com.ssafy.mbg.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.ItemCardBinding
import com.ssafy.mbg.ui.book.CardPopupFragment
import com.bumptech.glide.Glide
import com.ssafy.mbg.data.book.dao.CardCollection

class BookAdapter(
    private val fragmentManager: FragmentManager
) : RecyclerView.Adapter<BookAdapter.CardViewHolder>() {
    private var bookCards: List<CardCollection> = emptyList()

    fun setCards(newBookCards: List<CardCollection>?) {
        bookCards = newBookCards ?: emptyList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(bookCards[position], fragmentManager)
    }

    override fun getItemCount(): Int = bookCards.size

    class CardViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookCard: CardCollection, fragmentManager: FragmentManager) {
            // collectedAt이 null이 아니면 카드가 수집된 것으로 간주
            val isUnlocked = bookCard.collectedAt != null

            binding.imageView.apply {
                if (isUnlocked) {
                    // 실제 카드 이미지 로드
                    Glide.with(this)
                        .load(bookCard.imageUrl)
                        .placeholder(R.drawable.card_back)
                        .error(R.drawable.card_back)
                        .into(this)
                } else {
                    // 잠긴 카드는 카드 뒷면 이미지 표시
                    setImageResource(R.drawable.card_back)
                }
                scaleType = ImageView.ScaleType.FIT_XY
            }

            // 잠금 해제된 카드만 클릭 가능
            if (isUnlocked) {
                itemView.setOnClickListener {
                    animateCardFlip(itemView, bookCard, fragmentManager)
                }
            }
        }

        private fun animateCardFlip(itemView: View, bookCard: CardCollection, fragmentManager: FragmentManager) {
            val flipSound = MediaPlayer.create(itemView.context, R.raw.card_flip)

            itemView.cameraDistance = 8000 * itemView.resources.displayMetrics.density

            itemView.animate()
                .rotationY(180f)
                .setDuration(300)
                .setInterpolator(AccelerateInterpolator())
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        flipSound.start()
                        itemView.postDelayed({
                            binding.imageView.setImageResource(R.drawable.card_back)
                        }, 150)
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        itemView.animate()
                            .rotationY(360f)
                            .setDuration(300)
                            .setInterpolator(DecelerateInterpolator())
                            .setListener(object : AnimatorListenerAdapter() {
                                override fun onAnimationStart(animation: Animator) {
                                    flipSound.start()
                                    itemView.postDelayed({
                                        // 실제 카드 이미지 다시 로드
                                        Glide.with(binding.imageView)
                                            .load(bookCard.imageUrl)
                                            .into(binding.imageView)
                                    }, 150)
                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    itemView.rotationY = 0f
                                    showPopupDirectly(bookCard, fragmentManager)
                                    flipSound.release()
                                }
                            })
                            .start()
                    }
                })
                .start()
        }

        private fun showPopupDirectly(bookCard: CardCollection, fragmentManager: FragmentManager) {
            val popupFragment = CardPopupFragment.newInstance(
                cardId = bookCard.cardId,
                imageUrl = bookCard.imageUrl  // 이미지 URL 추가
            )
            popupFragment.show(fragmentManager, "CardPopupFragment")
        }
    }
}