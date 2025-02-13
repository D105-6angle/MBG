package com.ssafy.mbg.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.mbg.data.book.dao.CardCollection
import com.ssafy.mbg.ui.book.BookListFragment

class BookPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private var cardCollections: List<CardCollection> = emptyList()
    private val fragments = mutableMapOf<Int, BookListFragment>()

    fun updateData(newData: List<CardCollection>) {
        cardCollections = newData

        // 현재 생성된 프래그먼트들에 데이터 전달
        fragments.forEach { (position, fragment) ->
            val filteredCards = when {
                cardCollections.isEmpty() -> emptyList()
                position == 0 -> cardCollections.filter { it.codeId == "M001" }
                else -> cardCollections.filter { it.codeId == "M002" }
            }

            fragment.setCards(filteredCards)
        }
    }

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return BookListFragment.newInstance(position == 0).also {
            fragments[position] = it
        }
    }
}