package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.ssafy.mbg.R
import com.ssafy.mbg.data.book.response.BookDetailResponse
import com.ssafy.mbg.databinding.FragmentCardPopupBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardPopupFragment : DialogFragment() {
    private var _binding: FragmentCardPopupBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.TransparentDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCardPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardId = arguments?.getLong(ARG_CARD_ID) ?: return
        val userId = viewModel.getUserId() ?: return

        observeBookDetailState()
        viewModel.getBookDetail(userId, cardId)

        // 팝업 외부 클릭 시 닫기
        dialog?.window?.decorView?.setOnClickListener {
            dismiss()
        }
        // 팝업 내부 클릭은 이벤트 소비
        binding.root.setOnClickListener { }
    }

    private fun observeBookDetailState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookDetailState.collect { state ->
                when (state) {
                    is BookDetailState.Loading -> {
                        // 로딩 상태 처리 (필요한 경우)
                    }
                    is BookDetailState.Success -> {
                        updatePopupContent(state.data)
                    }
                    is BookDetailState.Error -> {
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    BookDetailState.Initial -> Unit
                }
            }
        }
    }

    private fun updatePopupContent(bookDetail: BookDetailResponse) {
        // Glide를 사용하여 이미지 로드
        Glide.with(this)
            .load(bookDetail.imageUrl)
            .into(binding.popupCardImage)

        // 여기에 추가 정보 표시 로직 구현 가능
        // 예: binding.cardNameTextView.text = bookDetail.cardName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CARD_ID = "card_id"

        fun newInstance(cardId: Long): CardPopupFragment {
            return CardPopupFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CARD_ID, cardId)
                }
            }
        }
    }
}