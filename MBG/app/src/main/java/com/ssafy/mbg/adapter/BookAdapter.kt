package com.ssafy.mbg.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.MediaPlayer
import android.util.Log
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
import com.ssafy.mbg.data.book.response.BookResponse

class BookAdapter(
    private val fragmentManager: FragmentManager,
    private val cardType: String  // "M001" 또는 "M002"
) : RecyclerView.Adapter<BookAdapter.CardViewHolder>() {
    private var collectedCards: List<CardCollection> = emptyList()
    private var totalCardCount: Int = 0

    fun setData(response: BookResponse) {
        // cardType에 따라 전체 카드 개수 설정
        totalCardCount = when (cardType) {
            "M001" -> response.totalHeritageCards
            "M002" -> response.totalStoryCards
            else -> 0
        }
        
        // 해당 타입의 획득한 카드만 필터링
        collectedCards = response.cards.filter { it.codeId == cardType }
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
        // position이 획득한 카드 범위 내에 있는지 확인
        if (position < collectedCards.size) {
            // 획득한 카드는 앞면 표시
            holder.bind(collectedCards[position], true, fragmentManager)
        } else {
            // 미획득 카드는 뒷면 표시
            holder.bind(null, false, fragmentManager)
        }
    }

    override fun getItemCount(): Int = totalCardCount

    class CardViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookCard: CardCollection?, isCollected: Boolean, fragmentManager: FragmentManager) {
            binding.imageView.apply {
                if (isCollected && bookCard != null) {
                    // 획득한 카드는 실제 이미지 로드
                    Glide.with(this)
                        .load(bookCard.imageUrl)
                        .placeholder(R.drawable.card_back)
                        .error(R.drawable.card_back)
                        .into(this)
                    
                    // 클릭 리스너 설정
                    setOnClickListener {
                        animateCardFlip(itemView, bookCard, fragmentManager)
                    }
                } else {
                    // 미획득 카드는 뒷면 이미지 표시
                    setImageResource(R.drawable.card_back)
                    setOnClickListener(null)  // 클릭 불가능하게 설정
                }
                scaleType = ImageView.ScaleType.FIT_XY
            }
        }

        private fun animateCardFlip(itemView: View, bookCard: CardCollection?, fragmentManager: FragmentManager) {
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
                            Log.d("BookAdapter", "Card Image URL: ${bookCard?.imageUrl}")
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
                                            .load(bookCard?.imageUrl)
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

        private fun showPopupDirectly(bookCard: CardCollection?, fragmentManager: FragmentManager) {
            val popupFragment = CardPopupFragment.newInstance(
                cardId = bookCard?.cardId ?: -1L,
                imageUrl = bookCard?.imageUrl ?: ""
            )
            popupFragment.show(fragmentManager, "CardPopupFragment")
        }
    }
}