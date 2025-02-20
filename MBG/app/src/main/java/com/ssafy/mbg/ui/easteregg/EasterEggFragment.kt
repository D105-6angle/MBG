package com.ssafy.mbg.ui.easteregg

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import androidx.fragment.app.DialogFragment
import com.ssafy.mbg.databinding.FragmentEasterEggBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EasterEggDialog : DialogFragment() {
    private var _binding: FragmentEasterEggBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEasterEggBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setDimAmount(0.3f)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startMainAnimation()
        startSparkleAnimations()
        animateText()
    }

    private fun startMainAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding.ivEasterEgg, "scaleX", 0.8f, 1.2f).apply {
            duration = 1500
            repeatCount = -1
            repeatMode = ObjectAnimator.REVERSE
        }

        val scaleY = ObjectAnimator.ofFloat(binding.ivEasterEgg, "scaleY", 0.8f, 1.2f).apply {
            duration = 1500
            repeatCount = -1
            repeatMode = ObjectAnimator.REVERSE
        }

        val rotation = ObjectAnimator.ofFloat(binding.ivEasterEgg, "rotation", 0f, 360f).apply {
            duration = 1500
            repeatCount = -1
            repeatMode = ObjectAnimator.RESTART
        }

        AnimatorSet().apply {
            playTogether(scaleX, scaleY, rotation)
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
    }

    private fun startSparkleAnimations() {
        fun animateSparkle(view: View, delay: Long) {
            val translateY = ObjectAnimator.ofFloat(view, "translationY", 0f, 500f).apply {
                duration = 2000
                repeatCount = -1
                repeatMode = ObjectAnimator.RESTART
            }

            val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).apply {
                duration = 2000
                repeatCount = -1
                repeatMode = ObjectAnimator.RESTART
            }

            val rotation = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f).apply {
                duration = 2000
                repeatCount = -1
                repeatMode = ObjectAnimator.RESTART
            }

            val scale = ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.5f).apply {
                duration = 2000
                repeatCount = -1
                repeatMode = ObjectAnimator.RESTART
            }

            val scaleY = ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.5f).apply {
                duration = 2000
                repeatCount = -1
                repeatMode = ObjectAnimator.RESTART
            }

            AnimatorSet().apply {
                playTogether(translateY, alpha, rotation, scale, scaleY)
                startDelay = delay
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        }

        animateSparkle(binding.ivSparkle1, 0)
        animateSparkle(binding.ivSparkle2, 1000)
    }

    private fun animateText() {
        ObjectAnimator.ofFloat(binding.tvMessage, "alpha", 0.5f, 1f).apply {
            duration = 800
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}