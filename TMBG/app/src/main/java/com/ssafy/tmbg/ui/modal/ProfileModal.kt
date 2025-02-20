package com.ssafy.mbg.ui.modal

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import com.ssafy.tmbg.databinding.ModalConfirmWithdrawBinding
import com.ssafy.tmbg.databinding.ModalProfileBinding

class ProfileModal(
    context: Context,
    private val onLogout: () -> Unit,
    private val onWithdraw: () -> Unit,
    private val userName : String,
    private val userEmail : String
) : Dialog(context) {

    private var _binding: ModalProfileBinding? = null
    private val binding get() = _binding!!

    private var withdrawConfirmDialog: Dialog? = null
    private var _withdrawBinding: ModalConfirmWithdrawBinding? = null
    private val withdrawBinding get() = _withdrawBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        _binding = ModalProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        initializeViews()

        window?.let { window ->
            val params = window.attributes
            params.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
            window.attributes = params
        }
    }



    private fun initializeViews() {
        with(binding) {
            // 초기값 설정

            // 닉네임 입력 필드 설정
            profileName.text = userName
            profileEmail.text = userEmail

            // 클릭 리스너 설정


            logoutButton.setOnClickListener {
                onLogout()
                dismiss()
            }

            withdrawButton.setOnClickListener {
                showWithdrawConfirmDialog()
            }
        }
    }

    private fun showWithdrawConfirmDialog() {
        withdrawConfirmDialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)

            _withdrawBinding = ModalConfirmWithdrawBinding.inflate(layoutInflater)
            setContentView(withdrawBinding.root)

            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            withdrawBinding.confirmWithdrawButton.setOnClickListener {
                onWithdraw()
                dismiss()
                this@ProfileModal.dismiss()
            }

            window?.let { window ->
                val params = window.attributes
                params.width = (context.resources.displayMetrics.widthPixels * 0.9).toInt()
                window.attributes = params
            }

            show()
        }
    }

    override fun dismiss() {
        withdrawConfirmDialog?.dismiss()
        super.dismiss()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
        _withdrawBinding = null
    }
}