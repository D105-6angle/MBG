package com.ssafy.mbg.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ssafy.mbg.ui.book.BookListFragment

class BookPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    private val fragments = mutableMapOf<Int, BookListFragment>()

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return BookListFragment(position == 0).also {
            fragments[position] = it
        }
    }

    fun notifyTabChanged(position: Int) {
        fragments[position]?.refreshCards()
    }
} 