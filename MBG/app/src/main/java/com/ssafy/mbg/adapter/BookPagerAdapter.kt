package com.ssafy.mbg.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.mbg.data.book.dao.CardCollection
import com.ssafy.mbg.data.book.response.BookResponse
import com.ssafy.mbg.ui.book.BookListFragment

class BookPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = mutableMapOf<Int, BookListFragment>()
    private var heritageCardsCount: Int = 0
    private var storyCardsCount: Int = 0

    fun updateData(response: BookResponse) {
        heritageCardsCount = response.totalHeritageCards
        storyCardsCount = response.totalStoryCards
        // 현재 생성된 프래그먼트들에 데이터 전달
        fragments.forEach { (_, fragment) ->
            fragment.updateData(response)
        }
    }

    // 추가: heritage_cards 개수 가져오기
    fun getHeritageCardsCount(): Int = heritageCardsCount

    // 추가: story_cards 개수 가져오기
    fun getStoryCardsCount(): Int = storyCardsCount

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val codeId = if (position == 0) "M001" else "M002"
        return BookListFragment.newInstance(codeId).also {
            fragments[position] = it
        }
    }
}