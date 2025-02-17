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
import com.ssafy.mbg.data.book.response.BookResponse
import com.ssafy.mbg.databinding.FragmentBookListBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookListFragment : Fragment() {
    private var _binding: FragmentBookListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookViewModel by viewModels()
    private lateinit var bookAdapter: BookAdapter

    private var cardType: String = "M001"  // 기본값

    companion object {
        private const val ARG_CARD_TYPE = "card_type"

        fun newInstance(cardType: String): BookListFragment {
            return BookListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CARD_TYPE, cardType)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cardType = arguments?.getString(ARG_CARD_TYPE) ?: "M001"
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
        bookAdapter = BookAdapter(parentFragmentManager, cardType)
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
                        bookAdapter.setData(state.bookResponse)
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

    fun updateData(response: BookResponse) {
        if (!isAdded) return
        bookAdapter.setData(response)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}