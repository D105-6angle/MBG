package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.ssafy.mbg.R
import com.ssafy.mbg.adapter.BookPagerAdapter
import com.ssafy.mbg.databinding.FragmentBookBinding

/**
 * 도감 화면의 메인 Fragment
 *
 * ViewPager2를 사용하여 문화재/스토리 탭을 구현합니다.
 * 각 탭은 별도의 BookListFragment로 구성됩니다.
 */
class BookFragment : Fragment() {
    private var _binding: FragmentBookBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabButtons()
    }

    /**
     * ViewPager2 초기 설정
     */
    private fun setupViewPager() {
        binding.viewPager.apply {
            adapter = BookPagerAdapter(this@BookFragment)
            // 양쪽 페이지를 모두 유지
            offscreenPageLimit = 2

            // 페이지 변경 리스너 추가
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateTabSelection(position == 0)
                    // 현재 표시된 Fragment에 탭 변경 알림
                    (adapter as? BookPagerAdapter)?.notifyTabChanged(position)
                }
            })
        }
    }

    /**
     * 탭 버튼 클릭 이벤트 설정
     */
    private fun setupTabButtons() {
        binding.culturalTab.setOnClickListener {
            updateTabSelection(true)
            binding.viewPager.setCurrentItem(0, true)
        }

        binding.storyTab.setOnClickListener {
            updateTabSelection(false)
            binding.viewPager.setCurrentItem(1, true)
        }
    }

    /**
     * 탭 선택 상태 업데이트
     *
     * @param isCulturalTab 문화재 탭 선택 여부
     */
    private fun updateTabSelection(isCulturalTab: Boolean) {
        binding.culturalTab.setBackgroundResource(
            if (isCulturalTab) R.drawable.button_selected else R.drawable.button_unselected
        )
        binding.storyTab.setBackgroundResource(
            if (isCulturalTab) R.drawable.button_unselected else R.drawable.button_selected
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
