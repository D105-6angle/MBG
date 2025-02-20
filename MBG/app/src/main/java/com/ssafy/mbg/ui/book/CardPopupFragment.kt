package com.ssafy.mbg.ui.book

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.ssafy.mbg.R
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
        setStyle(STYLE_NO_FRAME, R.style.FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = FragmentCardPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardId = arguments?.getLong(ARG_CARD_ID) ?: return
        val imageUrl = arguments?.getString(ARG_IMAGE_URL) ?: return

        Log.d("CardPopupFragment", "Loading image with URL: $imageUrl")

        updatePopupContent(imageUrl)

        dialog?.window?.decorView?.setOnClickListener {
            dismiss()
        }
        binding.root.setOnClickListener { }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun updatePopupContent(imageUrl: String) {
        binding.popupCardImage.scaleType = ImageView.ScaleType.FIT_XY

        Glide.with(requireContext())
            .load(imageUrl)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.popupCardImage)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_CARD_ID = "card_id"
        private const val ARG_IMAGE_URL = "image_url"

        fun newInstance(cardId: Long, imageUrl: String): CardPopupFragment {
            return CardPopupFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CARD_ID, cardId)
                    putString(ARG_IMAGE_URL, imageUrl)
                }
            }
        }
    }
}