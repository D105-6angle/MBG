package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.ssafy.mbg.adapter.BookAdapter
import com.ssafy.mbg.data.book.dao.CardCollection
import com.ssafy.mbg.databinding.FragmentBookListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    private var isCultural: Boolean = false

    companion object {
        private const val ARG_IS_CULTURAL = "is_cultural"

        fun newInstance(isCultural: Boolean): BookListFragment {
            return BookListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(ARG_IS_CULTURAL, isCultural)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getBoolean(ARG_IS_CULTURAL)?.let {
            isCultural = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeState()
        loadCards()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter(parentFragmentManager)
        binding.bookRecyclerView.apply {
            adapter = bookAdapter
            layoutManager = GridLayoutManager(context, 3)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookState.collect { state ->
                when (state) {
                    is BookState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.bookRecyclerView.visibility = View.GONE
                    }
                    is BookState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        binding.bookRecyclerView.visibility = View.VISIBLE

                        val cards = state.bookResponse.cards.filter { card ->
                            if (state.bookResponse.cards.isEmpty()) false
                            else {
                                // isCultural이 true면 M001, false면 M002 코드를 가진 카드들 필터링
                                card.codeId == if (isCultural) "M001" else "M002"
                            }
                        }

                        bookAdapter.setCards(cards)
                    }
                    is BookState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.bookRecyclerView.visibility = View.GONE
                        Snackbar.make(
                            binding.root,
                            state.message,
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                    BookState.Initial -> Unit
                }
            }
        }
    }

    fun setCards(newBookCards: List<CardCollection>) {
        if (!isAdded) return

        // 문화재(M001) 또는 이야기(M002) 카드 필터링
        val filteredCards = newBookCards.filter {
            it.codeId == if (isCultural) "M001" else "M002"
        }

        bookAdapter.setCards(filteredCards)
    }

    private fun loadCards() {
        if (!isAdded) return

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserId()?.let { userId ->
                viewModel.getBook(userId)
            }
        }
    }

    fun refreshCards() {
        if (!isAdded) return
        loadCards()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}