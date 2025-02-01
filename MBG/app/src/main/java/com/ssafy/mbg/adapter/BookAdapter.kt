package com.ssafy.mbg.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.mbg.R
import com.ssafy.mbg.databinding.ItemCardBinding
import com.ssafy.mbg.data.Card
import com.ssafy.mbg.ui.book.CardPopupFragment
import android.widget.ImageView

/**
 * 도감 화면의 카드 목록을 표시하기 위한 RecyclerView 어댑터
 * 
 * 카드의 잠금 상태에 따라 다른 이미지를 표시합니다:
 * - 잠금 해제된 카드: 각 카드별 고유 이미지
 * - 잠긴 카드: 카드 뒷면 이미지
 */
class BookAdapter(private val fragmentManager: FragmentManager) : RecyclerView.Adapter<BookAdapter.CardViewHolder>() {

    /** 표시할 카드 목록 */
    private val cards = mutableListOf<Card>()

    /**
     * 카드 목록을 업데이트합니다.
     *
     * @param newCards 새로운 카드 목록
     */
    fun setCards(newCards: List<Card>) {
        cards.clear()
        cards.addAll(newCards)
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
        holder.bind(cards[position], fragmentManager)
    }

    override fun getItemCount(): Int = cards.size

    /**
     * 카드 아이템을 표시하기 위한 ViewHolder
     * 
     * @property binding 카드 아이템 레이아웃 바인딩
     */
    class CardViewHolder(
        private val binding: ItemCardBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * 카드 정보를 뷰에 바인딩합니다.
         * 
         * @param card 표시할 카드 정보
         * @param fragmentManager 팝업 표시를 위한 FragmentManager
         */
        fun bind(card: Card, fragmentManager: FragmentManager) {
            val imageResource = when (card.id) {
                1 -> R.drawable.card_1
                2 -> R.drawable.card_2
                3 -> R.drawable.card_3
                else -> R.drawable.card_back
            }
            
            binding.imageView.apply {
                setImageResource(if (card.isUnlocked) imageResource else R.drawable.card_back)
                scaleType = ImageView.ScaleType.FIT_XY
            }

            // 잠금 해제된 카드만 클릭 가능
            if (card.isUnlocked) {
                itemView.setOnClickListener {
                    animateCardFlip(itemView, imageResource, fragmentManager)
                }
            }
        }

        /**
         * 카드 뒤집기 애니메이션을 실행합니다.
         */
        private fun animateCardFlip(itemView: View, imageResId: Int, fragmentManager: FragmentManager) {
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
                            binding.imageView.apply {
                                setImageResource(R.drawable.card_back)
                                scaleType = ImageView.ScaleType.FIT_XY
                            }
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
                                        binding.imageView.apply {
                                            setImageResource(imageResId)
                                            scaleType = ImageView.ScaleType.FIT_XY
                                        }
                                    }, 150)
                                }

                                override fun onAnimationEnd(animation: Animator) {
                                    itemView.rotationY = 0f
                                    showPopupDirectly(imageResId, fragmentManager)
                                    flipSound.release()
                                }
                            })
                            .start()
                    }
                })
                .start()
        }

        /**
         * 카드 상세 정보 팝업을 표시합니다.
         */
        private fun showPopupDirectly(imageResId: Int, fragmentManager: FragmentManager) {
            val popupFragment = CardPopupFragment.newInstance(imageResId)
            popupFragment.show(fragmentManager, "CardPopupFragment")
        }
    }
} 